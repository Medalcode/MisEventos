package com.example.miseventos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClaveActivity extends AppCompatActivity {

    // Declaración de variables para los botones y campos de texto
    private Button btnCambClave, btnCrearEvento1, btnEliminarCuenta;
    private EditText etClaveActual, etNuevaClave1, etNuevaClave2;
    private String nombreUsuario;
    private BDActivity dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave);

        // Inicialización de la base de datos y obtención del nombre de usuario del Intent
        dbHelper = new BDActivity(this);
        nombreUsuario = getIntent().getStringExtra("usuario");

        // Referencias a los elementos de la interfaz de usuario
        referencias();
        // Configuración de los eventos de los botones
        eventos();
    }

    private void referencias() {
        // Asignación de los elementos de la interfaz a las variables
        btnCambClave = findViewById(R.id.btnCambClave);
        btnCrearEvento1 = findViewById(R.id.btnCrearEvento1);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        etClaveActual = findViewById(R.id.etClaveActual);
        etNuevaClave1 = findViewById(R.id.etNuevaClave1);
        etNuevaClave2 = findViewById(R.id.etNuevaClave2);
    }

    private void eventos() {
        // Configuración del evento para el botón de cambiar clave
        btnCambClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarClave();
            }
        });

        // Configuración del evento para el botón de crear evento
        btnCrearEvento1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegación a la actividad de eventos
                Intent intent = new Intent(ClaveActivity.this, EventosActivity.class);
                intent.putExtra("usuario", nombreUsuario);
                startActivity(intent);
            }
        });

        // Configuración del evento para el botón de eliminar cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarCuenta();
            }
        });
    }

    private void cambiarClave() {
        // Obtención de los valores de los campos de texto
        String claveActual = etClaveActual.getText().toString();
        String nuevaClave1 = etNuevaClave1.getText().toString();
        String nuevaClave2 = etNuevaClave2.getText().toString();

        // Validación de que todos los campos estén llenos
        if (claveActual.isEmpty() || nuevaClave1.isEmpty() || nuevaClave2.isEmpty()) {
            Toast.makeText(ClaveActivity.this, getString(R.string.todos_los_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de que las nuevas contraseñas coincidan
        if (!nuevaClave1.equals(nuevaClave2)) {
            Toast.makeText(ClaveActivity.this, getString(R.string.nuevas_contrasenas_no_coinciden), Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtención del ID del usuario
        int usuarioId = dbHelper.getUsuarioId(nombreUsuario);
        if (usuarioId != -1) {
            // Consulta para obtener la contraseña actual del usuario
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT " + BDActivity.COLUMN_USUARIO_CONTRASENA + " FROM " + BDActivity.TABLE_USUARIOS + " WHERE " + BDActivity.COLUMN_USUARIO_ID + " = ?",
                    new String[]{String.valueOf(usuarioId)}
            );
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(BDActivity.COLUMN_USUARIO_CONTRASENA);
                if (columnIndex != -1) {
                    String contrasenaActual = cursor.getString(columnIndex);
                    // Verificación de que la contraseña actual sea correcta
                    if (contrasenaActual.equals(claveActual)) {
                        // Actualización de la contraseña en la base de datos
                        ContentValues values = new ContentValues();
                        values.put(BDActivity.COLUMN_USUARIO_CONTRASENA, nuevaClave1);
                        dbHelper.getWritableDatabase().update(BDActivity.TABLE_USUARIOS, values, BDActivity.COLUMN_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
                        Toast.makeText(ClaveActivity.this, getString(R.string.contrasena_cambiada_exitosamente), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ClaveActivity.this, getString(R.string.contraseña_actual_incorrecta), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            cursor.close();
        } else {
            Toast.makeText(ClaveActivity.this, getString(R.string.usuario_no_encontrado), Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarCuenta() {
        // Obtención del ID del usuario
        int usuarioId = dbHelper.getUsuarioId(nombreUsuario);
        if (usuarioId != -1) {
            // Eliminación lógica del usuario
            boolean usuarioEliminado = dbHelper.deleteUsuarioLogico(usuarioId);
            // Eliminación lógica de los eventos del usuario
            boolean eventosEliminados = dbHelper.deleteEventosLogicoPorUsuario(usuarioId);

            if (usuarioEliminado && eventosEliminados) {
                Toast.makeText(ClaveActivity.this, getString(R.string.cuenta_y_eventos_eliminados), Toast.LENGTH_SHORT).show();
                // Redirigir al usuario a la pantalla de inicio de sesión o cerrar la actividad actual
                Intent intent = new Intent(ClaveActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ClaveActivity.this, getString(R.string.error_eliminar_cuenta_eventos), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ClaveActivity.this, getString(R.string.usuario_no_encontrado), Toast.LENGTH_SHORT).show();
        }
    }
}