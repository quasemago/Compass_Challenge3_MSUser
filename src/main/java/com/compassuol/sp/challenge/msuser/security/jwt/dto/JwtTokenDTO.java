package com.compassuol.sp.challenge.msuser.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class JwtTokenDTO {
    private String email;
    private Date created;
    private Date expiration;
    private String accessToken;
}
