package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.PasswordUtils;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new ServiceException("Le nom d'utilisateur existe déjà: " + user.getUsername());
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new ServiceException("L'email existe déjà: " + user.getEmail());
        }

        user.setPassword(PasswordUtils.hash(user.getPassword()));

        return repository.save(user);
    }

    public User update(User user) {
        return repository.update(user);
    }

    public boolean delete(UUID userId) {
        return repository.deleteById(userId);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = repository.findByUsername(username);
        
        if (userOpt.isPresent() && PasswordUtils.verify(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        return repository.findAllOrderByCreatedAt();
    }
}
