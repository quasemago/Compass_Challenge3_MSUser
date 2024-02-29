package com.compassuol.sp.challenge.msuser.web;

import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.exception.JwtAuthenticationException;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserLoginService;
import com.compassuol.sp.challenge.msuser.web.controller.LoginController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.compassuol.sp.challenge.msuser.common.LoginUtils.mockJwtTokenDTO;
import static com.compassuol.sp.challenge.msuser.common.LoginUtils.mockUserLoginRequestDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtUserLoginService service;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void login_WithValidCredentials_ReturnsJwtTokenResponse() throws Exception {
        final JwtTokenDTO jwtToken = mockJwtTokenDTO();

        when(service.login(any())).thenReturn(jwtToken);

        mockMvc.perform(
                        post("/v1/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserLoginRequestDTO()))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtToken)));

        verify(service, times(1)).login(any());
    }

    @Test
    public void login_WithInvalidCredentials_ReturnsBadRequest() throws Exception {
        when(service.login(any())).thenThrow(JwtAuthenticationException.class);

        mockMvc.perform(
                        post("/v1/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserLoginRequestDTO()))
                )
                .andExpect(status().isUnauthorized());

        verify(service, times(1)).login(any());
    }
}
