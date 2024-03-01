package com.compassuol.sp.challenge.msuser.common;

import com.compassuol.sp.challenge.msuser.security.jwt.dto.JwtTokenDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserLoginRequestDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUtils {
    public static String VALID_JWT_SECRETKEY = "217969690605818764702766367087001";

    public static UserLoginRequestDTO mockUserLoginRequestDTO() {
        return new UserLoginRequestDTO("testuser@hotmail.com", "123456");
    }

    public static JwtTokenDTO mockJwtTokenDTO() {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + 3600000);
        return new JwtTokenDTO("testuser@hotmail.com",
                now,
                expiration,
                "Bearer " + VALID_JWT_SECRETKEY);
    }
}
