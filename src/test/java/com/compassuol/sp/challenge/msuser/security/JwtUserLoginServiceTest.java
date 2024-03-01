package com.compassuol.sp.challenge.msuser.security;

import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.mqueue.publisher.UserRequestNotificationPublisher;
import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.exception.JwtAuthenticationException;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserDetailsService;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import static com.compassuol.sp.challenge.msuser.common.LoginUtils.mockJwtTokenDTO;
import static com.compassuol.sp.challenge.msuser.common.LoginUtils.mockUserLoginRequestDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class JwtUserLoginServiceTest {
    @InjectMocks
    private JwtUserLoginService service;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUserDetailsService userDetailsService;
    @Mock
    private UserRequestNotificationPublisher notificationPublisher;

    @Test
    public void login_WithValidCredentials_ReturnsJwtTokenResponse() throws JsonProcessingException {
        final JwtTokenDTO validJwtToken = mockJwtTokenDTO();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.getTokenAuthenticated(anyString())).thenReturn(validJwtToken);
        doNothing().when(notificationPublisher).sendNotification(anyString(), eq(EventType.LOGIN));

        JwtTokenDTO sutJwtToken = service.login(mockUserLoginRequestDTO());

        assertThat(sutJwtToken).isNotNull();
        assertThat(sutJwtToken).isEqualTo(validJwtToken);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).getTokenAuthenticated(anyString());
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.LOGIN));
    }

    @Test
    public void login_WithInvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> service.login(mockUserLoginRequestDTO()))
                .isInstanceOf(JwtAuthenticationException.class);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void login_WithNotificationError_ThrowsException() throws JsonProcessingException {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.getTokenAuthenticated(anyString())).thenReturn(mockJwtTokenDTO());

        doThrow(JsonProcessingException.class).when(notificationPublisher).sendNotification(anyString(), eq(EventType.LOGIN));

        assertThatThrownBy(() -> service.login(mockUserLoginRequestDTO()))
                .isInstanceOf(NotificationBadRequestException.class);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).getTokenAuthenticated(anyString());
    }
}
