package org.example.tpj2eannonces.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

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
}
