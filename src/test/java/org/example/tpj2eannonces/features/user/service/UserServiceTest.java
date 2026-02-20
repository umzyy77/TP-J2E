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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldFindUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User("alice", "alice@example.com", "hashed");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(userId);

        assertThat(result).contains(user);
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldFindUserWithRoleByUsername() {
        User user = new User("bob", "bob@example.com", "hashed");

        when(userRepository.findWithRoleByUsername("bob")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findWithRoleByUsername("bob");

        assertThat(result).contains(user);
        verify(userRepository).findWithRoleByUsername("bob");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByUsername() {
        when(userRepository.findWithRoleByUsername("ghost")).thenReturn(Optional.empty());

        Optional<User> result = userService.findWithRoleByUsername("ghost");

        assertThat(result).isEmpty();
        verify(userRepository).findWithRoleByUsername("ghost");
    }
}
