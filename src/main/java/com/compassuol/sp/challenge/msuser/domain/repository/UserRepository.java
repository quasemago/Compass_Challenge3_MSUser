package com.compassuol.sp.challenge.msuser.domain.repository;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.role FROM User u WHERE u.email = :email")
    Optional<UserRole> findUserRoleByEmail(String email);
}
