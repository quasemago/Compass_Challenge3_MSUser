package com.compassuol.sp.challenge.msuser.security.jwt.service;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.security.jwt.JwtUserDetails;
import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Value("${security.jwt.token.secret-key:secret}")
    private String SECRET_KEY = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long EXPIRE_LENGTH = 3600000;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userService.findUserByEmail(email);
        return new JwtUserDetails(user);
    }

    public JwtTokenDTO getTokenAuthenticated(String email) {
        final UserRole role = userService.findRoleByEmail(email);
        return createAccessToken(email, role.getValue());
    }

    public UsernamePasswordAuthenticationToken getAuthorizationToken(String subject) {
        final UserDetails userDetails = loadUserByUsername(subject);
        return UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
    }

    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public JwtTokenDTO createAccessToken(String email, String role) {
        final Date issuedAt = new Date();
        final Date expiration = new Date(issuedAt.getTime() + EXPIRE_LENGTH);
        final String jwtToken = Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(generateKey())
                .claim("role", role)
                .compact();
        return new JwtTokenDTO(email, issuedAt, expiration, jwtToken);
    }

    public Jws<Claims> resolveToken(String token) {
        final String normalizedToken = token.contains("Bearer ")
                ? token.substring("Bearer ".length())
                : token;
        try {
            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(normalizedToken);
        } catch (JwtException ex) {
            //log.error(String.format("Invalid JWT Token - %s", ex.getMessage()));
        }
        return null;
    }
}
