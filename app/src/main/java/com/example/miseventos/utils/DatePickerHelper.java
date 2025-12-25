package com.example.miseventos.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utilidad para mostrar DatePicker y formatear fechas
 */
public class DatePickerHelper {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    
    /**
     * Muestra un DatePicker y actualiza un EditText con la fecha seleccionada
     * @param context El contexto de la Activity
     * @param editText El EditText donde se mostrará la fecha
     */
    public static void showDatePicker(Context context, EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        
        // Si el EditText ya tiene una fecha, usarla como inicial
        String currentDate = editText.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                calendar.setTime(dateFormatter.parse(currentDate));
            } catch (Exception e) {
                // Usar fecha actual si hay error
            }
        }
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(selectedYear, selectedMonth, selectedDay);
                String formattedDate = dateFormatter.format(calendar.getTime());
                editText.setText(formattedDate);
            },
            year, month, day
        );
        
        datePickerDialog.show();
    }
    
    /**
     * Formatea una fecha a String en formato yyyy-MM-dd
     */
    public static String formatDate(Calendar calendar) {
        return dateFormatter.format(calendar.getTime());
    }
    
    /**
     * Obtiene la fecha actual en formato yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return dateFormatter.format(Calendar.getInstance().getTime());
    }
}
