package com.compassuol.sp.challenge.msuser.web.controller;

import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "API User - Tem a responsabilidade de armazenar e gerenciar os dados de usuário.")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
            security = @SecurityRequirement(name = "Bearer Token Authentication"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Recurso não processado devido a requisição inválida.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Já existe um usuário cadastrado com esse e-mail ou CPF.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateRequestDTO request) {
        final UserResponseDTO response = service.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Recuperar informações de um usuário existente",
            description = "Recurso para recuperar um usuário existente através do Id.",
            security = @SecurityRequirement(name = "Bearer Token Authentication"),
            parameters = {
                    @Parameter(name = "id", description = "Identificador (Id) do usuário no banco de dados.",
                            in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário recuperado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado para acessar esse recurso.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuário informado não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable("id") Long id) {
        final User user = service.findUserById(id);
        final AddressResponseDTO address = service.findAddressByCep(user.getCep());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserResponseDTO.toDTO(user, address));
    }

    @Operation(summary = "Atualizar um usuário existente",
            description = "Recurso para atualizar as informações de um usuário existente através do Id.",
            security = @SecurityRequirement(name = "Bearer Token Authentication"),
            parameters = {
                    @Parameter(name = "id", description = "Identificador (Id) do usuário no banco de dados.",
                            in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Recurso não processado devido a requisição inválida.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado para acessar esse recurso.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuário informado não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDTO request) {
        final UserResponseDTO response = service.updateUser(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Atualizar a senha de um usuário existente",
            description = "Recurso para atualizar a senha de um usuário existente através do Id.",
            security = @SecurityRequirement(name = "Bearer Token Authentication"),
            parameters = {
                    @Parameter(name = "id", description = "Identificador (Id) do usuário no banco de dados.",
                            in = ParameterIn.PATH, required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha do usuário alterada com sucesso.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Recurso não processado devido a requisição inválida.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado para acessar esse recurso.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuário informado não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDTO.class))
                    )
            }
    )
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserPasswordRequestDTO request) {
        service.updateUserPassword(id, request.getPassword());
        return ResponseEntity
                .noContent()
                .build();
    }
}
