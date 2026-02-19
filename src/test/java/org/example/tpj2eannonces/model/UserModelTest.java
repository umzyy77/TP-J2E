package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserModelTest {

    @Test
    void toString_shouldContainUsername() {
        User user = new User("john", "john@test.com", "password");
        String text = user.toString();
        assertThat(text).contains("john");
    }

    @Test
    void setAnnoncesAndGetAnnonces_shouldWork() {
        User user = new User("john", "john@test.com", "password");
        List<Annonce> annonces = new ArrayList<>();
        annonces.add(new Annonce("T1", "D1", "A1", "m1@test.com"));

        user.setAnnonces(annonces);

        assertThat(user.getAnnonces()).hasSize(1);
        assertThat(user.getAnnonces().getFirst().getTitle()).isEqualTo("T1");
    }

    @Test
    void setCreatedAt_shouldUpdateField() {
        User user = new User("john", "john@test.com", "password");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        user.setCreatedAt(createdAt);

        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void hashCode_shouldBeEqualForUsersWithSameId() {
        UUID id = UUID.randomUUID();
        User first = new User("john", "john@test.com", "password");
        User second = new User("other", "other@test.com", "password");
        first.setId(id);
        second.setId(id);

        assertThat(first)
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
    }
}
