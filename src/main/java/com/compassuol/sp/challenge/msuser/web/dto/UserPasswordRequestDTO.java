package com.compassuol.sp.challenge.msuser.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPasswordRequestDTO {
    @NotBlank
    @Length(min = 6, message = "A senha precisa ter pelo menos 6 caracteres.")
    private String password;
}
