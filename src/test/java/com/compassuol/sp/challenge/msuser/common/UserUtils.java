package com.compassuol.sp.challenge.msuser.common;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.web.dto.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.compassuol.sp.challenge.msuser.common.UserConstants.VALID_PASSWORD;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUtils {
    public static User mockValidUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .cpf("946.801.800-80")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john@hotmail.com")
                .addressId(1L)
                .password(VALID_PASSWORD)
                .active(true)
                .role(UserRole.ROLE_USER)
                .build();
    }

    public static UserCreateRequestDTO mockCreateUserRequestDTO(User user) {
        return new UserCreateRequestDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getCpf(),
                user.getBirthDate(),
                user.getEmail(),
                "01001-000",
                user.getPassword(),
                user.getActive());
    }

    public static UserUpdateRequestDTO mockUserUpdateRequestDTO(User user) {
        return new UserUpdateRequestDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getCpf(),
                user.getBirthDate(),
                user.getEmail(),
                "01001-000",
                user.getActive());
    }

    public static AddressResponseDTO mockAddressResponseDTO() {
        return new AddressResponseDTO(1L, "Praça da Sé", "São Paulo", "SP", "01001-000");
    }

    public static UserResponseDTO mockUserResponseDTO(User user, AddressResponseDTO address) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCpf(),
                user.getBirthDate(),
                user.getEmail(),
                address,
                user.getActive());
    }

    public static UserPasswordRequestDTO mockUserPasswordRequestDTO(String password) {
        return new UserPasswordRequestDTO(password);
    }

    public static User mockInvalidUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .cpf("123-456-78901")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("@hotmail.com")
                .addressId(1L)
                .password("123456")
                .active(true)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
