package com.compassuol.sp.challenge.msuser.domain.service;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.exception.UserDataIntegrityViolationException;
import com.compassuol.sp.challenge.msuser.domain.exception.UserEntityNotFoundException;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.repository.UserRepository;
import com.compassuol.sp.challenge.msuser.web.dto.UserCreateRequestDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UserCreateRequestDTO request) {
        try {
            final User user = request.toModel();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            return repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserDataIntegrityViolationException("Já existe um usuário com o e-mail ou CPF informado.");
        }
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException("Usuário informado com o id {" + id + "} não encontrado."));
    }

    @Transactional()
    public User updateUser(Long id, UserUpdateRequestDTO request) {
        final User user = findUserById(id);

        if (user.getEmail().equals(request.getEmail())) {
            throw new UserDataIntegrityViolationException("Seu e-email atual é o mesmo que você está tentando atualizar.");
        }
        if (user.getCpf().equals(request.getCpf())) {
            throw new UserDataIntegrityViolationException("Seu CPF atual é o mesmo que você está tentando atualizar.");
        }

        try {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setCpf(request.getCpf());
            user.setBirthDate(request.getBirthDate());
            user.setEmail(request.getEmail());
            user.setCep(request.getCep());
            user.setActive(request.getActive());
            return repository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserDataIntegrityViolationException("Já existe um usuário com o e-mail ou CPF informado.");
        }
    }

    @Transactional
    public void updateUserPassword(Long id, String password) {
        final User user = findUserById(id);
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserEntityNotFoundException("Usuário informado com o e-mail {" + email + "} não encontrado."));
    }

    @Transactional(readOnly = true)
    public UserRole findRoleByEmail(String email) {
        return repository.findUserRoleByEmail(email)
                .orElseThrow(() -> new UserEntityNotFoundException("Role do usuário com e-mail {" + email + "} informado não encontrado."));
    }
}
