package com.example.miseventos.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitarios para PasswordHasher
 */
public class PasswordHasherTest {

    @Test
public void testHashPassword_NotNull() {
        String password = "miPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertNotNull("El hash no debe ser nulo", hash);
        assertFalse("El hash no debe estar vacío", hash.isEmpty());
    }

    @Test
    public void testHashPassword_DifferentPasswords_DifferentHashes() {
        String password1 = "password1";
        String password2 = "password2";
        
        String hash1 = PasswordHasher.hashPassword(password1);
        String hash2 = PasswordHasher.hashPassword(password2);
        
        assertNotEquals("Dos contraseñas diferentes deben generar hashes diferentes", hash1, hash2);
    }

    @Test
    public void testHashPassword_SamePassword_DifferentHashes() {
        // BCrypt genera un salt aleatorio, así que el mismo password genera hashes diferentes
        String password = "miPassword123";
        
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);
        
        assertNotEquals("BCrypt debe generar hashes diferentes incluso para la misma contraseña", hash1, hash2);
    }

    @Test
    public void testVerifyPassword_CorrectPassword_ReturnsTrue() {
        String password = "miPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertTrue("La verificación debe ser exitosa con la contraseña correcta", 
                   PasswordHasher.verifyPassword(password, hash));
    }

    @Test
    public void testVerifyPassword_IncorrectPassword_ReturnsFalse() {
        String password = "miPassword123";
        String wrongPassword = "passwordIncorrecta";
        String hash = PasswordHasher.hashPassword(password);
        
        assertFalse("La verificación debe fallar con contraseña incorrecta", 
                    PasswordHasher.verifyPassword(wrongPassword, hash));
    }

    @Test
    public void testVerifyPassword_NullPassword_ReturnsFalse() {
        String hash = PasswordHasher.hashPassword("test");
        
        assertFalse("La verificación debe fallar con password nulo", 
                    PasswordHasher.verifyPassword(null, hash));
    }

    @Test
    public void testVerifyPassword_NullHash_ReturnsFalse() {
        assertFalse("La verificación debe fallar con hash nulo", 
                    PasswordHasher.verifyPassword("test", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPassword_NullPassword_ThrowsException() {
        PasswordHasher.hashPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPassword_EmptyPassword_ThrowsException() {
        PasswordHasher.hashPassword("");
    }
}
