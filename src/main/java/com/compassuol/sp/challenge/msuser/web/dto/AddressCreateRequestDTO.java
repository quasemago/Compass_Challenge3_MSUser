package com.compassuol.sp.challenge.msuser.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressCreateRequestDTO {
    @NotNull
    private Integer number;
    @NotBlank
    private String complement;
    @NotBlank
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$", message = "O CEP deve conter 8 caracteres sendo apenas números no formato (00000-000).")
    private String cep;
}
