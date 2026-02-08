package org.example.tpj2eannonces.utils;

import org.example.tpj2eannonces.exception.ValidationException;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final int TITLE_MAX = 64;
    private static final int DESCRIPTION_MAX = 256;
    private static final int ADRESS_MAX = 64;
    private static final int MAIL_MAX = 64;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtils() {
    }

    public static String validateTitle(String title) {
        String value = normalize(title);
        if (value.isEmpty()) {
            throw new ValidationException("Titre obligatoire");
        }
        if (value.length() > TITLE_MAX) {
            throw new ValidationException("Titre trop long (max " + TITLE_MAX + ")");
        }
        return value;
    }

    public static String validateDescription(String description) {
        String value = normalize(description);
        if (value.isEmpty()) {
            throw new ValidationException("Description obligatoire");
        }
        if (value.length() > DESCRIPTION_MAX) {
            throw new ValidationException("Description trop longue (max " + DESCRIPTION_MAX + ")");
        }
        return value;
    }

    public static String validateAdress(String adress) {
        String value = normalize(adress);
        if (value.isEmpty()) {
            throw new ValidationException("Adresse obligatoire");
        }
        if (value.length() > ADRESS_MAX) {
            throw new ValidationException("Adresse trop longue (max " + ADRESS_MAX + ")");
        }
        return value;
    }

    public static String validateEmail(String mail) {
        String value = normalize(mail);
        if (value.isEmpty()) {
            throw new ValidationException("Email obligatoire");
        }
        if (value.length() > MAIL_MAX) {
            throw new ValidationException("Email trop long (max " + MAIL_MAX + ")");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ValidationException("Format email invalide");
        }
        return value;
    }

    public static <ID> ID validateId(String idValue, Function<String, ID> parser) {
        String value = normalize(idValue);
        if (value.isEmpty()) {
            throw new ValidationException("Identifiant manquant");
        }
        try {
            return parser.apply(value);
        } catch (Exception _) {
            throw new ValidationException("Identifiant invalide");
        }
    }

    public static Long validateLongId(String idValue) {
        Long id = validateId(idValue, Long::parseLong);
        if (id <= 0) {
            throw new ValidationException("Identifiant invalide");
        }
        return id;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
