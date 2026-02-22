package org.example.tpj2eannonces.features.user.service;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser_whenExists() {
        UUID id = UUID.randomUUID();
        User user = new User("alice", "alice@test.com", "pass");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(userService.findById(id)).isEmpty();
    }

    @Test
    void findWithRoleByUsername_shouldReturnUser_whenExists() {
        User user = new User("alice", "alice@test.com", "pass");
        when(userRepository.findWithRoleByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findWithRoleByUsername("alice");

        assertThat(result).isPresent();
    }

    @Test
    void findWithRoleByUsername_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findWithRoleByUsername("unknown")).thenReturn(Optional.empty());

        assertThat(userService.findWithRoleByUsername("unknown")).isEmpty();
    }
}
