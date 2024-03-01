package com.compassuol.sp.challenge.msuser.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLoginRequestDTO {
    @NotBlank
    @Email(message = "Email precisa estar no formato correto.")
    private String email;
    @NotBlank
    @Length(min = 6, message = "A senha precisa ter pelo menos 6 caracteres.")
    private String password;
}