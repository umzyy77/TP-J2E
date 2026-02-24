package org.example.tpj2eannonces.core.filter;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldUseHeaderCorrelationId_whenProvided() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.addHeader("X-Correlation-ID", "test-correlation-id");

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getHeader("X-Correlation-ID")).isEqualTo("test-correlation-id");
        assertThat(MDC.get("correlationId")).isNull(); // cleaned up in finally
    }

    @Test
    void shouldGenerateCorrelationId_whenNotProvided() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getHeader("X-Correlation-ID")).isNotBlank();
        assertThat(MDC.get("correlationId")).isNull(); // cleaned up in finally
    }

    @Test
    void shouldGenerateCorrelationId_whenHeaderIsBlank() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        request.addHeader("X-Correlation-ID", "   ");

        filter.doFilterInternal(request, response, chain);

        String correlationId = response.getHeader("X-Correlation-ID");
        assertThat(correlationId)
                .isNotBlank()
                .isNotEqualTo("   ");
    }
}
