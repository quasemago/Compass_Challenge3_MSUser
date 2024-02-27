package com.compassuol.sp.challenge.msuser.web.dto;

import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String cpf;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private String email;
    private AddressResponseDTO address;
    private Boolean active;

    public static UserResponseDTO toDTO(User user, AddressResponseDTO address) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCpf(),
                user.getBirthDate(),
                user.getEmail(),
                address,
                user.getActive()
        );
    }
}
