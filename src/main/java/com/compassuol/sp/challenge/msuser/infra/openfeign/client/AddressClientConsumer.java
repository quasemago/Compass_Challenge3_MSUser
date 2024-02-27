package com.compassuol.sp.challenge.msuser.infra.openfeign.client;

import com.compassuol.sp.challenge.msuser.web.dto.AddressResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "address-consumer", url = "${msaddress.api-url}")
public interface AddressClientConsumer {
    @GetMapping("/{cep}")
    AddressResponseDTO getAddressByCep(@PathVariable("cep") String cep,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);
}