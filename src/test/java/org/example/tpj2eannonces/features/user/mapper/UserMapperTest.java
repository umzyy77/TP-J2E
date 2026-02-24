package org.example.tpj2eannonces.features.user.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.features.role.model.Role;
import org.example.tpj2eannonces.features.user.dto.UserResponseDTO;
import org.example.tpj2eannonces.features.user.model.User;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toResponseDTO_shouldMapAllFields() {
        User user = new User("alice", "alice@test.com", "secret");
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        Role role = new Role("ROLE_USER");
        role.setId(1L);
        user.setRole(role);

        UserResponseDTO dto = mapper.toResponseDTO(user);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo("alice");
        assertThat(dto.email()).isEqualTo("alice@test.com");
        assertThat(dto.role()).isEqualTo("ROLE_USER");
        assertThat(dto.createdAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
    }

    @Test
    void toResponseDTO_shouldHandleNullRole() {
        User user = new User("alice", "alice@test.com", "secret");
        user.setId(UUID.randomUUID());
        user.setCreatedAt(LocalDateTime.now());

        UserResponseDTO dto = mapper.toResponseDTO(user);

        assertThat(dto.role()).isNull();
    }

    @Test
    void toResponseDTOList_shouldMapList() {
        User user = new User("alice", "alice@test.com", "secret");
        user.setId(UUID.randomUUID());
        user.setCreatedAt(LocalDateTime.now());
        Role role = new Role("ROLE_USER");
        user.setRole(role);

        List<UserResponseDTO> dtos = mapper.toResponseDTOList(List.of(user));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().username()).isEqualTo("alice");
    }

    @Test
    void mapRole_shouldReturnName() {
        Role role = new Role("ROLE_ADMIN");
        assertThat(mapper.map(role)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void mapRole_shouldReturnNull_whenNull() {
        assertThat(mapper.map(null)).isNull();
    }
}
