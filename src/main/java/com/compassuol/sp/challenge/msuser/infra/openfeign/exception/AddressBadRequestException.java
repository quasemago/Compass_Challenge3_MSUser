package com.compassuol.sp.challenge.msuser.infra.openfeign.exception;

public class AddressBadRequestException extends RuntimeException {
    public AddressBadRequestException(String message) {
        super(message);
    }
}
