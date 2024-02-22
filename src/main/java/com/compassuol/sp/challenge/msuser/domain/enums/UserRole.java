package com.compassuol.sp.challenge.msuser.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_USER("USER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
