package com.compassuol.sp.challenge.msuser.common;

import com.compassuol.sp.challenge.msuser.domain.model.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstants {
    public static final String VALID_PASSWORD = "$2a$10$bJ9E9olquPZC84HYZAySoepLlD7T5.YCqrWr5GOw0Uzreik5EiRDW"; // 123456
    public static final Address VALID_ADDRESS = new Address("Praça da Sé", 123, "Ao lado da lotérica", "São Paulo", "SP", "01001-000");
    public static final Address EXISTING_ADDRESS = new Address(1L, "Praça da Sé", 123, "Ao lado da lotérica", "São Paulo", "SP", "01001-000");
}