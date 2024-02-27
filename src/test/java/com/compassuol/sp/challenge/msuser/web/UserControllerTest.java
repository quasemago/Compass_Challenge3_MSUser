package com.compassuol.sp.challenge.msuser.web;

import com.compassuol.sp.challenge.msuser.domain.exception.UserDataIntegrityViolationException;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.openfeign.exception.AddressBadRequestException;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.compassuol.sp.challenge.msuser.common.UserUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(UserController.class)
public class UserControllerTest {
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
                .build();
    }

    @Test
    public void createUser_WithValidData_ReturnsUserResponse() throws Exception {
        final User sutUser = mockValidUser();
        sutUser.setPassword("123456");

        final UserResponseDTO validResponseBody = mockUserResponseDTO(sutUser, mockAddressResponseDTO());
        when(service.createUser(any())).thenReturn(validResponseBody);

        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateUserRequestDTO(sutUser)))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(validResponseBody)));

        verify(service, times(1)).createUser(any());
    }

    @Test
    public void createUser_WithConflictData_ReturnsConflict() throws Exception {
        when(service.createUser(any())).thenThrow(UserDataIntegrityViolationException.class);

        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateUserRequestDTO(mockValidUser())))
                )
                .andExpect(status().isConflict());

        verify(service, times(1)).createUser(any());
    }

    @Test
    public void createUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateUserRequestDTO(mockInvalidUser())))
                )
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @Test
    public void createUser_WithNonExistingAddress_ReturnsBadRequest() throws Exception {
        when(service.createUser(any())).thenThrow(AddressBadRequestException.class);

        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateUserRequestDTO(mockValidUser())))
                )
                .andExpect(status().isBadRequest());

        verify(service, times(1)).createUser(any());
    }

    @Test
    public void createUser_WithNotificationErrorEvent_ReturnsBadRequest() throws Exception {
        when(service.createUser(any())).thenThrow(NotificationBadRequestException.class);

        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockCreateUserRequestDTO(mockValidUser())))
                )
                .andExpect(status().isBadRequest());

        verify(service, times(1)).createUser(any());
    }

    @Test
    public void createUser_WithInvalidFields_ReturnsInternalServerError() throws Exception {
        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"birthDate\": \"teste\"}")
                )
                .andExpect(status().isInternalServerError());
    }
}
