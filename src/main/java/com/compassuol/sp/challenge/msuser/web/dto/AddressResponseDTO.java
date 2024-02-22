package com.compassuol.sp.challenge.msuser.web.dto;

import com.compassuol.sp.challenge.msuser.domain.model.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddressResponseDTO {
    private String street;
    private Integer number;
    private String complement;
    private String city;
    private String state;
    private String cep;

    public Address toModel() {
        return new Address(
                this.street,
                this.number,
                this.complement,
                this.city,
                this.state,
                this.cep);
    }
}
