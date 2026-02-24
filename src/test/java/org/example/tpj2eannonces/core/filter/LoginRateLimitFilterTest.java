package org.example.tpj2eannonces.core.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRateLimitFilterTest {

    @Test
    void shouldNotFilter_whenDisabled() {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(false, "/api/auth/login", 5, 60);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldExtractFirstIpFromXForwardedFor() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(true, "/api/auth/login", 1, 60);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader("X-Forwarded-For", "1.2.3.4, 5.6.7.8");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // First request allowed
        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);

        // Second request from same forwarded IP should be rejected
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilterInternal(request, response2, new MockFilterChain());
        assertThat(response2.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldFallbackToRemoteAddr_whenNoXForwardedFor() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(true, "/api/auth/login", 1, 60);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldFallbackToRemoteAddr_whenXForwardedForIsBlank() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(true, "/api/auth/login", 1, 60);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader("X-Forwarded-For", "   ");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldExtractSingleIpFromXForwardedFor() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(true, "/api/auth/login", 1, 60);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader("X-Forwarded-For", "9.8.7.6");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
