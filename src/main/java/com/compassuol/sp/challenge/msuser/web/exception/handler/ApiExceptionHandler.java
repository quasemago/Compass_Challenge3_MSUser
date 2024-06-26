package com.compassuol.sp.challenge.msuser.web.exception.handler;

import com.compassuol.sp.challenge.msuser.domain.exception.UserDataIntegrityViolationException;
import com.compassuol.sp.challenge.msuser.domain.exception.UserEntityNotFoundException;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.openfeign.exception.AddressBadRequestException;
import com.compassuol.sp.challenge.msuser.security.jwt.exception.JwtAuthenticationException;
import com.compassuol.sp.challenge.msuser.web.dto.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentNotValidException(BindingResult result) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, "Campo(s) inválido(s).", result));
    }

    @ExceptionHandler(UserDataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleDataIntegrityViolationException(UserDataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(UserEntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserEntityNotFoundException(UserEntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorMessageDTO> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(AddressBadRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleAddressBadRequestException(AddressBadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(NotificationBadRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleNotificationBadRequestException(NotificationBadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGenericException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado."));
    }
}
