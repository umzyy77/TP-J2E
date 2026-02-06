package org.example.tpj2eannonces.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtils {

    private static final int BCRYPT_ROUNDS = 12;

    private PasswordUtils() {
    }

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean verify(String password, String hashedPassword) {
        if (hashedPassword == null || password == null) {
            return false;
        }
        
        if (hashedPassword.startsWith("$2")) {
            try {
                return BCrypt.checkpw(password, hashedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        return password.equals(hashedPassword);
    }
}
