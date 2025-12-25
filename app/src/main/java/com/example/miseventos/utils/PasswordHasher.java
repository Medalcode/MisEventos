package com.example.miseventos.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utilidad para el hashing y verificación de contraseñas usando BCrypt
 */
public class PasswordHasher {
    
    // Costo de BCrypt (12 es un buen balance entre seguridad y rendimiento)
    private static final int BCRYPT_COST = 12;
    
    /**
     * Genera un hash seguro de una contraseña
     * @param password La contraseña en texto plano
     * @return El hash de la contraseña
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }
    
    /**
     * Verifica si una contraseña coincide con un hash
     * @param password La contraseña en texto plano
     * @param hash El hash almacenado
     * @return true si la contraseña coincide, false en caso contrario
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }
}
