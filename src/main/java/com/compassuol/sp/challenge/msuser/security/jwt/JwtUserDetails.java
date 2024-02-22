package com.compassuol.sp.challenge.msuser.security.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
    private final com.compassuol.sp.challenge.msuser.domain.model.User user;

    public JwtUserDetails(com.compassuol.sp.challenge.msuser.domain.model.User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getRole() {
        return this.user.getRole().name();
    }
}
