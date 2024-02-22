package com.compassuol.sp.challenge.msuser.web.controller;

import com.compassuol.sp.challenge.msuser.domain.service.UserService;
import com.compassuol.sp.challenge.msuser.web.dto.UserPasswordRequestDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserRequestDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        final UserResponseDTO response = service.createUser(request).toDTO();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable("id") Long id) {
        final UserResponseDTO response = service.findUserById(id).toDTO();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDTO request) {
        final UserResponseDTO response = service.updateUser(id, request).toDTO();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

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
