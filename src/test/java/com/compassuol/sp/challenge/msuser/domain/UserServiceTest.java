package com.compassuol.sp.challenge.msuser.domain;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.exception.UserDataIntegrityViolationException;
import com.compassuol.sp.challenge.msuser.domain.exception.UserEntityNotFoundException;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.repository.UserRepository;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.mqueue.publisher.UserRequestNotificationPublisher;
import com.compassuol.sp.challenge.msuser.infra.openfeign.client.AddressClientConsumer;
import com.compassuol.sp.challenge.msuser.infra.openfeign.exception.AddressBadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.compassuol.sp.challenge.msuser.common.UserConstants.VALID_PASSWORD;
import static com.compassuol.sp.challenge.msuser.common.UserUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;
    @Mock
    private AddressClientConsumer addressConsumer;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRequestNotificationPublisher notificationPublisher;

    @Test
    public void createUser_WithValidData_ReturnsUserResponse() throws JsonProcessingException {
        final User validUser = mockValidUser();

        when(repository.save(any(User.class))).thenReturn(validUser);
        when(addressConsumer.getAddressByCep(anyString(), anyString()))
                .thenReturn(mockAddressResponseDTO(validUser.getAddress()));
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_PASSWORD);
        doNothing().when(notificationPublisher).sendNotification(anyString(), eq(EventType.CREATE));

        User sutUser = service.createUser(mockCreateUserRequestDTO(validUser));

        assertThat(sutUser).isNotNull();
        assertThat(sutUser).isEqualTo(validUser);

        verify(repository, times(1)).save(any(User.class));
        verify(addressConsumer, times(1)).getAddressByCep(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.CREATE));
    }

    @Test
    public void createUser_WithConflictData_ThrowsException() throws JsonProcessingException {
        final User sutUser = mockValidUser();

        when(repository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        when(addressConsumer.getAddressByCep(anyString(), anyString()))
                .thenReturn(mockAddressResponseDTO(sutUser.getAddress()));
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_PASSWORD);

        assertThatThrownBy(() -> service.createUser(mockCreateUserRequestDTO(sutUser)))
                .isInstanceOf(UserDataIntegrityViolationException.class);

        verify(repository, times(1)).save(any(User.class));
        verify(addressConsumer, times(1)).getAddressByCep(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    public void createUser_WithNonExistingAddress_ThrowsException() {
        final User sutUser = mockValidUser();
        sutUser.getAddress().setCep("00000-000");

        when(addressConsumer.getAddressByCep(eq("00000-000"), anyString())).thenThrow(FeignException.NotFound.class);
        assertThatThrownBy(() -> service.createUser(mockCreateUserRequestDTO(sutUser)))
                .isInstanceOf(AddressBadRequestException.class);

        verify(addressConsumer, times(1)).getAddressByCep(eq("00000-000"), anyString());
    }

    @Test
    public void createUser_WithInvalidAddressResponse_ThrowsException() {
        final User sutUser = mockValidUser();
        sutUser.getAddress().setCep("00000-000");

        when(addressConsumer.getAddressByCep(eq("00000-000"), anyString())).thenThrow(FeignException.class);
        assertThatThrownBy(() -> service.createUser(mockCreateUserRequestDTO(sutUser)))
                .isInstanceOf(AddressBadRequestException.class);

        verify(addressConsumer, times(1)).getAddressByCep(eq("00000-000"), anyString());
    }

    @Test
    public void createUser_WithInvalidData_ThrowsException() {
        final User sutUser = mockInvalidUser();

        assertThatThrownBy(() -> service.createUser(mockCreateUserRequestDTO(sutUser)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createUser_WithNotificationEventError_ThrowsException() throws JsonProcessingException {
        final User sutUser = mockValidUser();

        when(repository.save(any(User.class))).thenReturn(sutUser);
        when(addressConsumer.getAddressByCep(anyString(), anyString()))
                .thenReturn(mockAddressResponseDTO(sutUser.getAddress()));
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_PASSWORD);
        doThrow(JsonProcessingException.class).when(notificationPublisher).sendNotification(anyString(), eq(EventType.CREATE));

        assertThatThrownBy(() -> service.createUser(mockCreateUserRequestDTO(sutUser)))
                .isInstanceOf(NotificationBadRequestException.class);

        verify(repository, times(1)).save(any(User.class));
        verify(addressConsumer, times(1)).getAddressByCep(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.CREATE));
    }

    @Test
    public void findUserById_WithExistingId_ReturnsUserResponse() {
        final User validUser = mockValidUser();

        when(repository.findById(anyLong())).thenReturn(Optional.of(validUser));

        User sutUser = service.findUserById(1L);

        assertThat(sutUser).isNotNull();
        assertThat(sutUser).isEqualTo(validUser);

        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void findUserById_WithNonExistingId_ThrowsException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUserById(1L))
                .isInstanceOf(UserEntityNotFoundException.class);

        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void findUserByEmail_WithExistingEmail_ReturnsUserResponse() {
        final User validUser = mockValidUser();

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(validUser));

        User sutUser = service.findUserByEmail(anyString());

        assertThat(sutUser).isNotNull();
        assertThat(sutUser).isEqualTo(validUser);

        verify(repository, times(1)).findByEmail(anyString());
    }

    @Test
    public void findUserByEmail_WithNonExistingEmail_ThrowsException() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUserByEmail(anyString()))
                .isInstanceOf(UserEntityNotFoundException.class);

        verify(repository, times(1)).findByEmail(anyString());
    }

    @Test
    public void findUserRoleByEmail_WithExistingEmail_ReturnsUserRoleResponse() {
        final UserRole validUserRole = UserRole.ROLE_USER;

        when(repository.findUserRoleByEmail(anyString())).thenReturn(Optional.of(validUserRole));

        UserRole sutUserRole = service.findRoleByEmail(anyString());

        assertThat(sutUserRole).isNotNull();
        assertThat(sutUserRole).isEqualTo(validUserRole);

        verify(repository, times(1)).findUserRoleByEmail(anyString());
    }

    @Test
    public void findUserRoleByEmail_WithNonExistingEmail_ThrowsException() {
        when(repository.findUserRoleByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findRoleByEmail(anyString()))
                .isInstanceOf(UserEntityNotFoundException.class);

        verify(repository, times(1)).findUserRoleByEmail(anyString());
    }

    @Test
    public void updateUserPassword_WithExistingUser() throws JsonProcessingException {
        final User validUser = mockValidUser();
        validUser.setPassword("oldPassword");

        when(repository.findById(1L)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_PASSWORD);
        doNothing().when(notificationPublisher).sendNotification(anyString(), eq(EventType.UPDATE_PASSWORD));

        service.updateUserPassword(1L, VALID_PASSWORD);

        assertThat(validUser.getPassword()).isEqualTo(VALID_PASSWORD);

        verify(repository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.UPDATE_PASSWORD));
    }

    @Test
    public void updateUserPassword_WithNonExistingUser_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUserPassword(1L, VALID_PASSWORD))
                .isInstanceOf(UserEntityNotFoundException.class);

        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void updateUserPassword_WithNotificationEventError_ThrowsException() throws JsonProcessingException {
        final User validUser = mockValidUser();
        validUser.setPassword("oldPassword");

        when(repository.findById(1L)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_PASSWORD);
        doThrow(JsonProcessingException.class).when(notificationPublisher).sendNotification(anyString(), eq(EventType.UPDATE_PASSWORD));

        assertThatThrownBy(() -> service.updateUserPassword(1L, VALID_PASSWORD))
                .isInstanceOf(NotificationBadRequestException.class);

        verify(repository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.UPDATE_PASSWORD));
    }

    @Test
    public void updateUser_WithExistingUser_ReturnsUserResponse() throws JsonProcessingException {
        final User validUser = mockValidUser();
        final User updatedUser = mockValidUser();
        updatedUser.setFirstName("Jane");
        updatedUser.setEmail("jane@hotmail.com");
        updatedUser.setCpf("714.324.150-42");

        when(repository.findById(1L)).thenReturn(Optional.of(validUser));
        when(repository.saveAndFlush(any(User.class))).thenReturn(updatedUser);
        doNothing().when(notificationPublisher).sendNotification(anyString(), eq(EventType.UPDATE));

        User sutUser = service.updateUser(1L, mockUserUpdateRequestDTO(updatedUser));

        assertThat(sutUser).isNotNull();
        assertThat(sutUser).isEqualTo(updatedUser);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(any(User.class));
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.UPDATE));
    }

    @Test
    public void updateUser_WithNonExistingUser_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(1L, mockUserUpdateRequestDTO(mockValidUser())))
                .isInstanceOf(UserEntityNotFoundException.class);

        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void updateUser_WithConflictData_ThrowsException() {
        final User validUser = mockValidUser();
        final User updatedUser = mockValidUser();
        updatedUser.setCpf("714.324.150-42");
        updatedUser.setEmail("jane@hotmail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(validUser));
        when(repository.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.updateUser(1L, mockUserUpdateRequestDTO(updatedUser)))
                .isInstanceOf(UserDataIntegrityViolationException.class);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    public void updateUser_WithNotificationEventError_ThrowsException() throws JsonProcessingException {
        final User validUser = mockValidUser();
        final User updatedUser = mockValidUser();
        updatedUser.setFirstName("Jane");
        updatedUser.setEmail("jane@hotmail.com");
        updatedUser.setCpf("714.324.150-42");

        when(repository.findById(1L)).thenReturn(Optional.of(validUser));
        when(repository.saveAndFlush(any(User.class))).thenReturn(updatedUser);
        doThrow(JsonProcessingException.class).when(notificationPublisher).sendNotification(anyString(), eq(EventType.UPDATE));

        assertThatThrownBy(() -> service.updateUser(1L, mockUserUpdateRequestDTO(updatedUser)))
                .isInstanceOf(NotificationBadRequestException.class);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(any(User.class));
        verify(notificationPublisher, times(1)).sendNotification(anyString(), eq(EventType.UPDATE));
    }
}
