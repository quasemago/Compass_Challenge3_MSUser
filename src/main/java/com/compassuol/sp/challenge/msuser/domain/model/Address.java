package com.compassuol.sp.challenge.msuser.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "complement", nullable = false)
    private String complement;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "cep", nullable = false)
    private String cep;

    public Address(String street, Integer number, String complement, String city, String state, String cep) {
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.cep = cep;
    }
}
