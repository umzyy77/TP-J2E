package org.example.tpj2eannonces.features.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.features.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"role", "role.authorities"})
    Optional<User> findWithRoleByUsername(String username);
}
