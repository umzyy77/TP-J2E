package org.example.tpj2eannonces.features.role.repository;

import java.util.Optional;

import org.example.tpj2eannonces.features.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
