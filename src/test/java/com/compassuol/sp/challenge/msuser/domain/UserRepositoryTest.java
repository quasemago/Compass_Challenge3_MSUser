package com.compassuol.sp.challenge.msuser.domain;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.model.Address;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.compassuol.sp.challenge.msuser.common.UserConstants.VALID_ADDRESS;
import static com.compassuol.sp.challenge.msuser.common.UserUtils.mockValidUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        VALID_ADDRESS.setId(null);
    }

    @Test
    public void findUserByEmail_WithExistingUser_ReturnsUser() {
        final User user = mockValidUser(VALID_ADDRESS);
        final Address savedAddress = testEntityManager.persistFlushFind(VALID_ADDRESS);
        user.setAddress(savedAddress);

        final User savedUser = testEntityManager.persistFlushFind(user);
        final Optional<User> sutUser = repository.findByEmail(user.getEmail());

        assertThat(sutUser).isNotEmpty();
        assertThat(sutUser.get()).isEqualTo(savedUser);
    }

    @Test
    public void findUserByEmail_WithNonExistingUser_ReturnsEmpty() {
        final Optional<User> sutUser = repository.findByEmail("test@hotmail.com");
        assertThat(sutUser).isEmpty();
    }

    @Test
    public void findUserRoleByEmail_WithExistingUser_ReturnsUserRole() {
        final User user = mockValidUser(VALID_ADDRESS);
        final Address savedAddress = testEntityManager.persistFlushFind(VALID_ADDRESS);
        user.setAddress(savedAddress);

        final User savedUser = testEntityManager.persistFlushFind(user);
        final Optional<UserRole> sutUserRole = repository.findUserRoleByEmail(user.getEmail());

        assertThat(sutUserRole).isNotEmpty();
        assertThat(sutUserRole.get()).isEqualTo(savedUser.getRole());
    }

    @Test
    public void findUserRoleByEmail_WithNonExistingUser_ReturnsEmpty() {
        final Optional<UserRole> sutUserRole = repository.findUserRoleByEmail("test@hotmail.com");
        assertThat(sutUserRole).isEmpty();
    }
}
