package com.compassuol.sp.challenge.msuser.web;

import com.compassuol.sp.challenge.msuser.domain.exception.UserEntityNotFoundException;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.web.controller.UserController;
import com.compassuol.sp.challenge.msuser.web.dto.UserResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.compassuol.sp.challenge.msuser.common.UserUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(UserController.class)
public class UserControllerWithAuthTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService service;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void findUserById_WithExistingId_ReturnsUserResponse() throws Exception {
        final User sutUser = mockValidUser();
        sutUser.setId(1L);

        when(service.findUserById(1L)).thenReturn(sutUser);
        when(service.findAddressByCep(anyString())).thenReturn(mockAddressResponseDTO());
        final UserResponseDTO validResponseBody = mockUserResponseDTO(sutUser, mockAddressResponseDTO());

        mockMvc.perform(
                        get("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validResponseBody)));

        verify(service, times(1)).findUserById(1L);
    }

    @Test
    @WithMockUser
    public void findUserById_WithNonExistingId_ReturnsNotFound() throws Exception {
        when(service.findUserById(1L)).thenThrow(UserEntityNotFoundException.class);

        mockMvc.perform(
                        get("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(service, times(1)).findUserById(1L);
    }

    @Test
    public void findByUserId_WIthNonAuthenticatedUser_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateUserPassword_WithValidData_ReturnsNoContent() throws Exception {
        doNothing().when(service).updateUserPassword(1L, "123456");

        mockMvc.perform(
                        put("/v1/users/{id}/password", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserPasswordRequestDTO("123456")))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isNoContent());

        verify(service, times(1)).updateUserPassword(1L, "123456");
    }

    @Test
    @WithMockUser
    public void updateUserPassword_WithNonExistingId_ReturnsNotFound() throws Exception {
        doThrow(UserEntityNotFoundException.class).when(service).updateUserPassword(1L, "123456");

        mockMvc.perform(
                        put("/v1/users/{id}/password", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserPasswordRequestDTO("123456")))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isNotFound());

        verify(service, times(1)).updateUserPassword(1L, "123456");
    }

    @Test
    @WithMockUser
    public void updateUserPassword_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        put("/v1/users/{id}/password", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserPasswordRequestDTO("123")))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserPassword_WithNonAuthenticatedUser_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/v1/users/{id}/password", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserPasswordRequestDTO("123456")))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateUser_WithValidData_ReturnsUserResponse() throws Exception {
        final User sutUser = mockValidUser();
        sutUser.setId(1L);

        final UserResponseDTO validResponseBody = mockUserResponseDTO(sutUser, mockAddressResponseDTO());
        when(service.updateUser(eq(1L), any())).thenReturn(validResponseBody);

        mockMvc.perform(
                        put("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserUpdateRequestDTO(sutUser)))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validResponseBody)));
    }

    @Test
    @WithMockUser
    public void updateUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        put("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserUpdateRequestDTO(mockInvalidUser())))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_WithNonAuthenticatedUser_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/v1/users/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserUpdateRequestDTO(mockValidUser())))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isUnauthorized());
    }
}
