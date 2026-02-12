package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.JPAUtil;
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
        return JPAUtil.inTransaction(em -> {
            if (repository.existsByUsername(em, user.getUsername())) {
                throw new ServiceException("Le nom d'utilisateur existe déjà: " + user.getUsername());
            }
            if (repository.existsByEmail(em, user.getEmail())) {
                throw new ServiceException("L'email existe déjà: " + user.getEmail());
            }
            user.setPassword(PasswordUtils.hash(user.getPassword()));
            return repository.save(em, user);
        });
    }

    public User update(User user) {
        return JPAUtil.inTransaction(em -> repository.update(em, user));
    }

    public boolean delete(UUID userId) {
        return JPAUtil.inTransaction(em -> repository.deleteById(em, userId));
    }

    public Optional<User> findById(UUID id) {
        return JPAUtil.inReadOnly(em -> repository.findById(em, id));
    }

    public Optional<User> findByUsername(String username) {
        return JPAUtil.inReadOnly(em -> repository.findByUsername(em, username));
    }

    public Optional<User> authenticate(String username, String password) {
        return JPAUtil.inReadOnly(em -> {
            Optional<User> userOpt = repository.findByUsername(em, username);
            if (userOpt.isPresent() && PasswordUtils.verify(password, userOpt.get().getPassword())) {
                return userOpt;
            }
            return Optional.empty();
        });
    }

    public List<User> findAll() {
        return JPAUtil.inReadOnly(repository::findAllOrderByCreatedAt);
    }
}
