package com.compassuol.sp.challenge.msuser.security.jwt;

import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserDetailsService.resolveAccessToken;

public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, JwtException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final var tokenRequest = resolveAccessToken(
                    authorizationHeader.substring(7));

            if (tokenRequest != null) {
                final String subject = tokenRequest.getPayload().getSubject();
                final var authToken = userDetailsService.getAuthorizationToken(subject);

                if (authToken != null) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
