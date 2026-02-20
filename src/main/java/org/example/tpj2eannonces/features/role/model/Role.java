package org.example.tpj2eannonces.features.role.model;

import java.io.Serial;
import java.util.LinkedHashSet;
import java.util.Set;

import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.shared.model.BaseEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du role est obligatoire")
    @Size(max = 50, message = "Le nom du role ne doit pas depasser 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "authority", nullable = false, length = 64)
    private Set<String> authorities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = (authorities == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(authorities);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = (users == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(users);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
