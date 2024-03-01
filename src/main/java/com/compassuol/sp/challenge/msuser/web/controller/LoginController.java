package com.compassuol.sp.challenge.msuser.web.controller;

import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.security.jwt.service.JwtUserLoginService;
import com.compassuol.sp.challenge.msuser.web.dto.ErrorMessageDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "API Login - Tem a responsabilidade de autenticar o login do usuário na API.")
@RestController
@RequestMapping("/v1/login")
@RequiredArgsConstructor
public class LoginController {
    private final JwtUserLoginService service;

    @Operation(summary = "Autenticar na API", description = "Recurso para autenticação na API.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso. Retorno de um Bearer Token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Recurso não processado devido a requisição inválida ou credenciais inválidas.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<JwtTokenDTO> login(@Valid @RequestBody UserLoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }
}
