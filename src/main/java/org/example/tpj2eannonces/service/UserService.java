package org.example.tpj2eannonces.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.exception.user.DuplicateUserException;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.repository.UserRepository;
import org.example.tpj2eannonces.utils.JpaPersistenceExecutor;
import org.example.tpj2eannonces.utils.PasswordUtils;
import org.example.tpj2eannonces.utils.PersistenceExecutor;

public class UserService {

    private final UserRepository repository;
    private final PersistenceExecutor persistenceExecutor;

    public UserService() {
        this(new UserRepository(), new JpaPersistenceExecutor());
    }

    public UserService(UserRepository repository) {
        this(repository, new JpaPersistenceExecutor());
    }

    public UserService(UserRepository repository, PersistenceExecutor persistenceExecutor) {
        this.repository = Objects.requireNonNull(repository);
        this.persistenceExecutor = Objects.requireNonNull(persistenceExecutor);
    }

    public User create(User user) {
        return persistenceExecutor.inTransaction(em -> {
            if (repository.existsByUsername(em, user.getUsername())) {
                throw new DuplicateUserException("Le nom d'utilisateur", user.getUsername());
            }
            if (repository.existsByEmail(em, user.getEmail())) {
                throw new DuplicateUserException("L'email", user.getEmail());
            }
            user.setPassword(PasswordUtils.hash(user.getPassword()));
            return repository.save(em, user);
        });
    }

    public User update(User user) {
        return persistenceExecutor.inTransaction(em -> repository.update(em, user));
    }

    public boolean delete(UUID userId) {
        return persistenceExecutor.inTransaction(em -> repository.deleteById(em, userId));
    }

    public Optional<User> findById(UUID id) {
        return persistenceExecutor.inReadOnly(em -> repository.findById(em, id));
    }

    public Optional<User> findByUsername(String username) {
        return persistenceExecutor.inReadOnly(em -> repository.findByUsername(em, username));
    }

    public Optional<User> authenticate(String username, String password) {
        return persistenceExecutor.inReadOnly(em -> {
            Optional<User> userOpt = repository.findByUsername(em, username);
            if (userOpt.isPresent() && PasswordUtils.verify(password, userOpt.get().getPassword())) {
                return userOpt;
            }
            return Optional.empty();
        });
    }

    public List<User> findAll() {
        return persistenceExecutor.inReadOnly(repository::findAllOrderByCreatedAt);
    }
}
