package com.compassuol.sp.challenge.msuser.infra.mqueue.dto;

import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class UserRequestEventDTO {
    private String email;
    private EventType event;
    private Date date;
}
