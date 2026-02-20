package org.example.tpj2eannonces.core.aop;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* org.example.tpj2eannonces.features..service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = formatArgs(joinPoint.getArgs());

        logger.info("[ENTER] {}.{}({})", className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            logger.info("[EXIT]  {}.{} - {}ms", className, methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            logger.error("[ERROR] {}.{} - {}ms - {}: {}",
                    className, methodName, duration, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return Arrays.stream(args)
                .map(this::sanitizeArg)
                .collect(Collectors.joining(", "));
    }

    private String sanitizeArg(Object arg) {
        if (arg == null) {
            return "null";
        }
        String className = arg.getClass().getSimpleName();
        // Eviter de logger les objets JPA (lazy loading) et les secrets
        if (className.contains("$$") || className.contains("Proxy")) {
            return className + "@proxy";
        }
        String value = arg.toString();
        if (value.length() > 100) {
            return className + "(...)";
        }
        return value;
    }
}
