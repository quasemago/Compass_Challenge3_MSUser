package com.compassuol.sp.challenge.msuser.domain.model;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.web.dto.AddressResponseDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
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
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email precisa estar no formato correto.")
    private String email;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    public User(String firstName, String lastName, String cpf, LocalDate birthDate, String email, Address address, String password, Boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.email = email;
        this.address = address;
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
                new AddressResponseDTO(
                        this.address.getStreet(),
                        this.address.getNumber(),
                        this.address.getComplement(),
                        this.address.getCity(),
                        this.address.getState(),
                        this.address.getCep()),
                this.active
        );
    }
}
