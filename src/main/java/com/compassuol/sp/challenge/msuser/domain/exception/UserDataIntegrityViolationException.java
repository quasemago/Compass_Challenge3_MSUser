package com.compassuol.sp.challenge.msuser.domain.exception;

public class UserDataIntegrityViolationException extends RuntimeException {
    public UserDataIntegrityViolationException(String message) {
        super(message);
    }
}
