package com.compassuol.sp.challenge.msuser.web.dto;

import com.compassuol.sp.challenge.msuser.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$", message = "CEP precisa estar no formato correto. (xxxxx-xxx)")
    private String cep;
    @NotBlank
    @Length(min = 6, message = "A senha precisa ter pelo menos 6 caracteres.")
    private String password;
    @NotNull
    private Boolean active;

    public User toModel() {
        return new User(this.getFirstName(),
                this.getLastName(),
                this.getCpf(),
                this.getBirthDate(),
                this.getEmail(),
                this.getCep(),
                this.getPassword(),
                this.getActive());
    }
}
