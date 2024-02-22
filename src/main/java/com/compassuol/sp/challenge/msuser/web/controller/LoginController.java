package com.compassuol.sp.challenge.msuser.web.controller;

import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserLoginService;
import com.compassuol.sp.challenge.msuser.web.dto.UserLoginRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
@RequiredArgsConstructor
public class LoginController {
    private final JwtUserLoginService service;

    @PostMapping
    public ResponseEntity<JwtTokenDTO> login(@Valid @RequestBody UserLoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }
}
