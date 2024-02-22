package com.compassuol.sp.challenge.msuser.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCreateRequestDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @CPF(message = "CPF precisa ser válido e estar no formato correto. (xxx-xxx-xxx.xx)")
    @Length(min = 14, max = 14, message = "O CPF precisa ter 14 caracteres.")
    private String cpf;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @Email(message = "Email precisa estar no formato correto.")
    private String email;
    @Valid
    @NotNull
    private AddressCreateRequestDTO address;
    @NotBlank
    @Length(min = 6, message = "A senha precisa ter pelo menos 6 caracteres.")
    private String password;
    @NotNull
    private Boolean active;
}
