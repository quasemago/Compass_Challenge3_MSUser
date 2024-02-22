package com.compassuol.sp.challenge.msuser.domain.model;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.web.dto.UserResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "cpf", nullable = false, unique = true)
    @CPF(message = "CPF precisa estar no formato correto.")
    private String cpf;

    @Column(name = "birth_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email precisa estar no formato correto.")
    private String email;

    @Column(name = "cep", nullable = false)
    private String cep;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    public User(String firstName, String lastName, String cpf, Date birthDate, String email, String cep, String password, Boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.email = email;
        this.cep = cep;
        this.password = password;
        this.active = active;
    }

    public UserResponseDTO toDTO() {
        return new UserResponseDTO(
                this.id,
                this.firstName,
                this.lastName,
                this.cpf,
                this.birthDate,
                this.email,
                this.cep,
                this.password,
                this.active
        );
    }
}
