package org.example.tpj2eannonces.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Test
    void logServiceMethods_shouldProceedAndReturnResult() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingAspect.logServiceMethods(joinPoint);

        assertThat(result).isEqualTo("result");
    }

    @Test
    void logServiceMethods_shouldRethrowException() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("test error"));

        assertThatThrownBy(() -> loggingAspect.logServiceMethods(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("test error");
    }

    @Test
    void logServiceMethods_shouldHandleNullArgs() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(null);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);

        assertThat(result).isEqualTo("ok");
    }

    @Test
    void logServiceMethods_shouldHandleNullArgValue() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{null});
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);

        assertThat(result).isEqualTo("ok");
    }

    @Test
    void logServiceMethods_shouldTruncateLongArgs() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        String longArg = "a".repeat(200);
        when(joinPoint.getArgs()).thenReturn(new Object[]{longArg});
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);

        assertThat(result).isEqualTo("ok");
    }

    @Test
    void logServiceMethods_shouldHandleEmptyArgs() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);
        assertThat(result).isEqualTo("ok");
    }

    @Test
    void logServiceMethods_shouldSanitizeProxyArg() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{new SomeProxy$$Enhanced()});
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);
        assertThat(result).isEqualTo("ok");
    }

    @Test
    void logServiceMethods_shouldSanitizeArgWithProxyInClassName() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{new FakeProxy()});
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = loggingAspect.logServiceMethods(joinPoint);
        assertThat(result).isEqualTo("ok");
    }

    // Inner class whose simple name contains "$$" to trigger the $$ branch
    private static class SomeProxy$$Enhanced {
        @Override
        public String toString() {
            return "some-proxy-object";
        }
    }

    // Inner class whose simple name contains "Proxy" (without $$) to trigger the Proxy branch
    private static class FakeProxy {
        @Override
        public String toString() {
            return "fake-proxy-object";
        }
    }
}
