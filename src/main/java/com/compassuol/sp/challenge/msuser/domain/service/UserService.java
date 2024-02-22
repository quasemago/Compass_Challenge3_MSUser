package com.compassuol.sp.challenge.msuser.domain.service;

import com.compassuol.sp.challenge.msuser.domain.enums.UserRole;
import com.compassuol.sp.challenge.msuser.domain.exception.UserDataIntegrityViolationException;
import com.compassuol.sp.challenge.msuser.domain.exception.UserEntityNotFoundException;
import com.compassuol.sp.challenge.msuser.domain.model.User;
import com.compassuol.sp.challenge.msuser.domain.repository.UserRepository;
import com.compassuol.sp.challenge.msuser.infra.mqueue.enums.EventType;
import com.compassuol.sp.challenge.msuser.infra.mqueue.exception.NotificationBadRequestException;
import com.compassuol.sp.challenge.msuser.infra.mqueue.publisher.UserRequestNotificationPublisher;
import com.compassuol.sp.challenge.msuser.infra.openfeign.client.AddressClientConsumer;
import com.compassuol.sp.challenge.msuser.infra.openfeign.exception.AddressBadRequestException;
import com.compassuol.sp.challenge.msuser.web.dto.AddressResponseDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserCreateRequestDTO;
import com.compassuol.sp.challenge.msuser.web.dto.UserUpdateRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
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
    private final AddressClientConsumer addressConsumer;
    private final UserRequestNotificationPublisher notificationPublisher;

    @Transactional
    public User createUser(UserCreateRequestDTO request) {
        try {
            AddressResponseDTO address;
            try {
                address = addressConsumer.getAddressByCep(request
                        .getAddress()
                        .getCep());
            } catch (FeignException ex) {
                if (ex instanceof FeignException.NotFound) {
                    throw new AddressBadRequestException("O CEP do endereço informado não foi encontrado!");
                }
                throw new AddressBadRequestException("Não foi possível processar o CEP do endereço informado!");
            }

            address.setNumber(request.getAddress().getNumber());
            address.setComplement(request.getAddress().getComplement());

            final User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setCpf(request.getCpf());
            user.setBirthDate(request.getBirthDate());
            user.setEmail(request.getEmail());
            user.setAddress(address.toModel());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setActive(request.getActive());

            final User createdUser = repository.save(user);
            notificationPublisher.sendNotification(createdUser.getEmail(), EventType.CREATE);

            return createdUser;
        } catch (DataIntegrityViolationException ex) {
            throw new UserDataIntegrityViolationException("Já existe um usuário com o e-mail ou CPF informado.");
        } catch (JsonProcessingException ex) {
            throw new NotificationBadRequestException("Não foi possível processar a notificação do evento de criação do usuário.");
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
            user.setActive(request.getActive());

            final User updatedUser = repository.saveAndFlush(user);
            notificationPublisher.sendNotification(updatedUser.getEmail(), EventType.CREATE);

            return updatedUser;
        } catch (DataIntegrityViolationException ex) {
            throw new UserDataIntegrityViolationException("Já existe um usuário com o e-mail ou CPF informado.");
        } catch (JsonProcessingException ex) {
            throw new NotificationBadRequestException("Não foi possível processar a notificação do evento de atualização do usuário.");
        }
    }

    @Transactional
    public void updateUserPassword(Long id, String password) {
        try {
            final User user = findUserById(id);
            user.setPassword(passwordEncoder.encode(password));

            repository.save(user);
            notificationPublisher.sendNotification(user.getEmail(), EventType.UPDATE_PASSWORD);
        } catch (JsonProcessingException ex) {
            throw new NotificationBadRequestException("Não foi possível processar a notificação do evento de atualização do usuário.");
        }
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
