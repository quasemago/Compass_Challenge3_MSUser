package com.compassuol.sp.challenge.msuser.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
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
    private String cep;
    private Boolean active;
}
