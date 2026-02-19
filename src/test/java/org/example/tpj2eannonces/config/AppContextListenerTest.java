package org.example.tpj2eannonces.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.example.tpj2eannonces.utils.JPAUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

class AppContextListenerTest {

    private static final String JAAS_CONFIG_PROPERTY = "java.security.auth.login.config";

    private String initialJaasConfigValue;

    @BeforeEach
    void setUp() {
        JPAUtil.close();
        initialJaasConfigValue = System.getProperty(JAAS_CONFIG_PROPERTY);
        System.clearProperty(JAAS_CONFIG_PROPERTY);
    }

    @AfterEach
    void tearDown() {
        JPAUtil.close();
        if (initialJaasConfigValue == null) {
            System.clearProperty(JAAS_CONFIG_PROPERTY);
            return;
        }
        System.setProperty(JAAS_CONFIG_PROPERTY, initialJaasConfigValue);
    }

    @Test
    void contextInitialized_shouldSetJaasConfigAndInitializeJpa_whenNoJvmOverride() {
        URL expectedConfig = AppContextListenerTest.class.getClassLoader().getResource("jaas.conf");
        assertThat(expectedConfig).isNotNull();

        AppContextListener listener = new AppContextListener();
        listener.contextInitialized(null);

        assertThat(System.getProperty(JAAS_CONFIG_PROPERTY)).isEqualTo(expectedConfig.toExternalForm());
        assertThat(JPAUtil.getEntityManagerFactory()).isNotNull();
        assertThat(JPAUtil.getEntityManagerFactory().isOpen()).isTrue();
    }

    @Test
    void contextInitialized_shouldKeepExistingJaasConfig_whenJvmOverrideIsPresent() {
        String existingValue = "file:/tmp/custom-jaas.conf";
        System.setProperty(JAAS_CONFIG_PROPERTY, existingValue);

        AppContextListener listener = new AppContextListener();
        listener.contextInitialized(null);

        assertThat(System.getProperty(JAAS_CONFIG_PROPERTY)).isEqualTo(existingValue);
        assertThat(JPAUtil.getEntityManagerFactory()).isNotNull();
        assertThat(JPAUtil.getEntityManagerFactory().isOpen()).isTrue();
    }

    @Test
    void contextInitialized_shouldContinueWhenJaasFileIsMissing() {
        AppContextListener listener = new AppContextListener() {
            @Override
            protected URL findJaasConfig() {
                return null;
            }
        };

        listener.contextInitialized(null);

        assertThat(System.getProperty(JAAS_CONFIG_PROPERTY)).isNull();
        assertThat(JPAUtil.getEntityManagerFactory()).isNotNull();
        assertThat(JPAUtil.getEntityManagerFactory().isOpen()).isTrue();
    }

    @Test
    void contextDestroyed_shouldCloseJpa() {
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        assertThat(emf.isOpen()).isTrue();

        AppContextListener listener = new AppContextListener();
        listener.contextDestroyed(null);

        assertThat(emf.isOpen()).isFalse();
    }
}
