package com.compassuol.sp.challenge.msuser.infra.mqueue.enums;

import lombok.Getter;

@Getter
public enum EventType {
    LOGIN, CREATE, UPDATE, UPDATE_PASSWORD;
}
