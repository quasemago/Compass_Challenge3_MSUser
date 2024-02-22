package com.compassuol.sp.challenge.msuser.domain.repository;

import com.compassuol.sp.challenge.msuser.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}