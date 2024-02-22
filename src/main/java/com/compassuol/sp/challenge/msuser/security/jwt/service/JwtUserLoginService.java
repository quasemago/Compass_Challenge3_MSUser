package com.compassuol.sp.challenge.msuser.security.jwt.service;

import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.mqueue.publisher.UserRequestNotificationPublisher;
import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.exception.JwtAuthenticationException;
import com.compassuol.sp.challenge.msuser.web.dto.UserLoginRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final UserRequestNotificationPublisher notificationPublisher;

    public JwtTokenDTO login(UserLoginRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            final JwtTokenDTO jwtToken = userDetailsService.getTokenAuthenticated(request.getEmail());

            notificationPublisher.sendNotification(request.getEmail(), EventType.LOGIN);

            return jwtToken;
        } catch (AuthenticationException ex) {
            throw new JwtAuthenticationException("Credenciais de Login Inválidas");
        } catch (JsonProcessingException ex) {
            throw new NotificationBadRequestException("Não foi possível processar a notificação do evento de atualização do usuário.");
        }
    }
}
