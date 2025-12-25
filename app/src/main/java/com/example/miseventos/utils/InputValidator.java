package com.example.miseventos.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilidad para validación de entradas de usuario
 */
public class InputValidator {
    
    /**
     * Resultado de una validación
     */
    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        
        private ValidationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    /**
     * Valida un nombre de usuario
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("El nombre de usuario es obligatorio");
        }
        
        if (username.length() < 3) {
            return ValidationResult.error("El nombre debe tener al menos 3 caracteres");
        }
        
        if (username.length() > 50) {
            return ValidationResult.error("El nombre no puede exceder 50 caracteres");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida una contraseña
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("La contraseña es obligatoria");
        }
        
        if (password.length() < 6) {
            return ValidationResult.error("La contraseña debe tener al menos 6 caracteres");
        }
        
        if (password.length() > 100) {
            return ValidationResult.error("La contraseña es demasiado larga");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Valida una fecha en formato yyyy-MM-dd
     */
    public static ValidationResult validateDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return ValidationResult.error("La fecha es obligatoria");
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            
            if (date == null) {
                return ValidationResult.error("Fecha inválida");
            }
            
            return ValidationResult.success();
        } catch (ParseException e) {
            return ValidationResult.error("Formato de fecha inválido. Use AAAA-MM-DD");
        }
    }
    
    /**
     * Valida que un campo de texto no esté vacío
     */
    public static ValidationResult validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " es obligatorio");
        }
        return ValidationResult.success();
    }
}
