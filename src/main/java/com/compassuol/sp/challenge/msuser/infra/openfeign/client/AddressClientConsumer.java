package com.compassuol.sp.challenge.msuser.infra.openfeign.client;

import com.compassuol.sp.challenge.msuser.web.dto.AddressResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "address-consumer", url = "http://localhost:8081/v1/address")
public interface AddressClientConsumer {
    @GetMapping("/{cep}")
    AddressResponseDTO getAddressByCep(@PathVariable("cep") String cep);
}