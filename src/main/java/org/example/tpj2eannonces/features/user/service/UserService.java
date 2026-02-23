package org.example.tpj2eannonces.features.user.service;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.features.user.exception.UserNotFoundException;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouve: " + userId));
    }

    public Optional<User> findWithRoleByUsername(String username) {
        return userRepository.findWithRoleByUsername(username);
    }
}
