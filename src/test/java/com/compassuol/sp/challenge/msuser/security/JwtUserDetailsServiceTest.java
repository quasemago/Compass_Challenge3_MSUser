package com.compassuol.sp.challenge.msuser.security;

import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserDetailsService;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static com.compassuol.sp.challenge.msuser.common.LoginUtils.VALID_JWT_SECRETKEY;
import static com.compassuol.sp.challenge.msuser.common.UserUtils.mockValidUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class JwtUserDetailsServiceTest {
    @InjectMocks
    private JwtUserDetailsService service;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        service.setEncryptedSecretKey(Keys.hmacShaKeyFor(VALID_JWT_SECRETKEY.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void loadUserByUsername_WithValidData_ReturnsUserDetailsResponse() {
        final User validUser = mockValidUser();
        when(userService.findUserByEmail(anyString())).thenReturn(validUser);

        var sut = service.loadUserByUsername(validUser.getEmail());

        assertThat(sut).isNotNull();
        assertThat(sut.getUsername()).isEqualTo(validUser.getEmail());
    }

    @Test
    public void getTokenAuthenticated_WithValidData_ReturnsJwtTokenResponse() {
        final User validUser = mockValidUser();
        when(userService.findRoleByEmail(anyString())).thenReturn(validUser.getRole());

        var sut = service.getTokenAuthenticated(validUser.getEmail());

        assertThat(sut).isNotNull();
        assertThat(sut.getAccessToken()).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(validUser.getEmail());

        verify(userService).findRoleByEmail(anyString());
    }

    @Test
    public void getAuthorizationToken_WithValidData_ReturnsUsernamePasswordAuthenticationTokenResponse() {
        final User validUser = mockValidUser();
        when(userService.findUserByEmail(anyString())).thenReturn(validUser);

        var sut = service.getAuthorizationToken(validUser.getEmail());

        assertThat(sut).isNotNull();
        assertThat(sut.getPrincipal()).isNotNull();
    }

    @Test
    public void createAccessToken_WithValidData_ReturnsJwtTokenResponse() {
        final User validUser = mockValidUser();
        final String roleName = validUser.getRole().name().substring("ROLE_".length());

        var sut = service.createAccessToken(validUser.getEmail(), roleName);

        assertThat(sut).isNotNull();
        assertThat(sut.getAccessToken()).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(validUser.getEmail());
    }

    @Test
    public void resolveToken_WithValidData_ReturnsJwsClaimsResponse() {
        final User validUser = mockValidUser();
        final String roleName = validUser.getRole().name().substring("ROLE_".length());

        var accessToken = service.createAccessToken(validUser.getEmail(), roleName).getAccessToken();
        var sut = service.resolveToken(accessToken);

        assertThat(sut).isNotNull();
        assertThat(sut.getPayload()).isNotNull();
        assertThat(sut.getPayload().getSubject()).isEqualTo(validUser.getEmail());
    }
}
