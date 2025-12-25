package com.example.miseventos.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitarios para InputValidator
 */
public class InputValidatorTest {

    // ==================== Tests de validateUsername ====================
    
    @Test
    public void testValidateUsername_ValidUsername_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validateUsername("usuario123");
        assertTrue("Un username válido debe pasar la validación", result.isSuccess());
        assertNull("No debe haber mensaje de error", result.getErrorMessage());
    }

    @Test
    public void testValidateUsername_NullUsername_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateUsername(null);
        assertFalse("Un username nulo debe fallar", result.isSuccess());
        assertNotNull("Debe haber mensaje de error", result.getErrorMessage());
    }

    @Test
    public void testValidateUsername_EmptyUsername_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateUsername("");
        assertFalse("Un username vacío debe fallar", result.isSuccess());
        assertNotNull("Debe haber mensaje de error", result.getErrorMessage());
    }

    @Test
    public void testValidateUsername_WhitespaceUsername_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateUsername("   ");
        assertFalse("Un username con solo espacios debe fallar", result.isSuccess());
    }

    @Test
    public void testValidateUsername_TooShort_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateUsername("ab");
        assertFalse("Username de 2 caracteres debe fallar", result.isSuccess());
        assertTrue("Debe mencionar longitud mínima", 
                   result.getErrorMessage().contains("3"));
    }

    @Test
    public void testValidateUsername_MinLength_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validateUsername("abc");
        assertTrue("Username de 3 caracteres (mínimo) debe pasar", result.isSuccess());
    }

    @Test
    public void testValidateUsername_TooLong_ReturnsError() {
        String longUsername = "a".repeat(51);
        InputValidator.ValidationResult result = InputValidator.validateUsername(longUsername);
        assertFalse("Username de 51 caracteres debe fallar", result.isSuccess());
        assertTrue("Debe mencionar longitud máxima", 
                   result.getErrorMessage().contains("50"));
    }

    @Test
    public void testValidateUsername_MaxLength_ReturnsSuccess() {
        String maxUsername = "a".repeat(50);
        InputValidator.ValidationResult result = InputValidator.validateUsername(maxUsername);
        assertTrue("Username de 50 caracteres (máximo) debe pasar", result.isSuccess());
    }

    // ==================== Tests de validatePassword ====================
    
    @Test
    public void testValidatePassword_ValidPassword_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validatePassword("password123");
        assertTrue("Una contraseña válida debe pasar", result.isSuccess());
    }

    @Test
    public void testValidatePassword_NullPassword_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validatePassword(null);
        assertFalse("Una contraseña nula debe fallar", result.isSuccess());
    }

    @Test
    public void testValidatePassword_EmptyPassword_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validatePassword("");
        assertFalse("Una contraseña vacía debe fallar", result.isSuccess());
    }

    @Test
    public void testValidatePassword_TooShort_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validatePassword("12345");
        assertFalse("Contraseña de 5 caracteres debe fallar", result.isSuccess());
        assertTrue("Debe mencionar longitud mínima", 
                   result.getErrorMessage().contains("6"));
    }

    @Test
    public void testValidatePassword_MinLength_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validatePassword("123456");
        assertTrue("Contraseña de 6 caracteres (mínimo) debe pasar", result.isSuccess());
    }

    @Test
    public void testValidatePassword_TooLong_ReturnsError() {
        String longPassword = "a".repeat(101);
        InputValidator.ValidationResult result = InputValidator.validatePassword(longPassword);
        assertFalse("Contraseña de 101 caracteres debe fallar", result.isSuccess());
    }

    // ==================== Tests de validateDate ====================
    
    @Test
    public void testValidateDate_ValidDate_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validateDate("2025-12-25");
        assertTrue("Una fecha válida debe pasar", result.isSuccess());
    }

    @Test
    public void testValidateDate_NullDate_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateDate(null);
        assertFalse("Una fecha nula debe fallar", result.isSuccess());
    }

    @Test
    public void testValidateDate_EmptyDate_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateDate("");
        assertFalse("Una fecha vacía debe fallar", result.isSuccess());
    }

    @Test
    public void testValidateDate_InvalidFormat_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateDate("25/12/2025");
        assertFalse("Formato incorrecto debe fallar", result.isSuccess());
        assertTrue("Debe mencionar el formato correcto",
                   result.getErrorMessage().toLowerCase().contains("aaaa-mm-dd"));
    }

    @Test
    public void testValidateDate_InvalidDay_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateDate("2025-02-30");
        assertFalse("Día inválido (30 de febrero) debe fallar", result.isSuccess());
    }

    @Test
    public void testValidateDate_InvalidMonth_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateDate("2025-13-01");
        assertFalse("Mes inválido (13) debe fallar", result.isSuccess());
    }

    @Test
    public void testValidateDate_LeapYear_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validateDate("2024-02-29");
        assertTrue("29 de febrero en año bisiesto debe pasar", result.isSuccess());
    }

    // ==================== Tests de validateNotEmpty ====================
    
    @Test
    public void testValidateNotEmpty_ValidValue_ReturnsSuccess() {
        InputValidator.ValidationResult result = InputValidator.validateNotEmpty("valor", "Campo");
        assertTrue("Un valor no vacío debe pasar", result.isSuccess());
    }

    @Test
    public void testValidateNotEmpty_NullValue_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateNotEmpty(null, "Campo");
        assertFalse("Un valor nulo debe fallar", result.isSuccess());
        assertTrue("Debe mencionar el nombre del campo",
                   result.getErrorMessage().contains("Campo"));
    }

    @Test
    public void testValidateNotEmpty_EmptyValue_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateNotEmpty("", "Título");
        assertFalse("Un valor vacío debe fallar", result.isSuccess());
        assertTrue("Debe mencionar el nombre del campo",
                   result.getErrorMessage().contains("Título"));
    }

    @Test
    public void testValidateNotEmpty_WhitespaceValue_ReturnsError() {
        InputValidator.ValidationResult result = InputValidator.validateNotEmpty("   ", "Descripción");
        assertFalse("Un valor con solo espacios debe fallar", result.isSuccess());
    }
}
