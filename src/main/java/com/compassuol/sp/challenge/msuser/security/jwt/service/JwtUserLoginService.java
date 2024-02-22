package com.compassuol.sp.challenge.msuser.security.jwt.service;

import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.exception.JwtAuthenticationException;
import com.compassuol.sp.challenge.msuser.web.dto.UserLoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserLoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService userDetailsService;

    public JwtTokenDTO login(UserLoginRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            return userDetailsService.getTokenAuthenticated(request.getEmail());
        } catch (AuthenticationException ex) {
            throw new JwtAuthenticationException("Credenciais de Login Inválidas");
        }
    }
}
