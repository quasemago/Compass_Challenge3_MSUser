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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

    @Setter
    private static SecretKey encryptedSecretKey;

    @PostConstruct
    private void setup() {
        encryptedSecretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userService.findUserByEmail(email);
        return new JwtUserDetails(user);
    }

    public UsernamePasswordAuthenticationToken getAuthorizationToken(String subject) {
        final UserDetails userDetails = loadUserByUsername(subject);
        return UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
    }

    public JwtTokenDTO getTokenAuthenticated(String subject) {
        final UserRole role = userService.findRoleByEmail(subject);
        final Date issuedAt = new Date();
        final Date expiration = new Date(issuedAt.getTime() + EXPIRE_LENGTH);
        final String jwtToken = createAccessToken(issuedAt, expiration, subject, role.name().substring("ROLE_".length()));
        return new JwtTokenDTO(subject, issuedAt, expiration, jwtToken);
    }

    public static String createAccessToken(Date issuedAt,
                                           Date expiration,
                                           String subject,
                                           String role) {
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(encryptedSecretKey)
                .claim("role", role)
                .compact();
    }

    public static Jws<Claims> resolveAccessToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(encryptedSecretKey)
                .build()
                .parseSignedClaims(token);
    }
}
