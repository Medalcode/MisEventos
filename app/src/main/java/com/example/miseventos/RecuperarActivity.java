package com.example.miseventos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecuperarActivity extends AppCompatActivity {

    // Declaración de variables para los elementos de la interfaz de usuario
    private EditText etNombre2, etPistaRecu2, etNuevaClave3, etNuevaClave4;
    private Button btnRecuClave, btnVolver;
    private BDActivity dbHelper;

    // Método que se ejecuta al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        // Aqui hacemos la Inicialización de la base de datos
        dbHelper = new BDActivity(this);

        // Referencias a los elementos de la interfaz de usuario
        etNombre2 = findViewById(R.id.etNombre2);
        etPistaRecu2 = findViewById(R.id.etPistaRecu2);
        etNuevaClave3 = findViewById(R.id.etNuevaClave3);
        etNuevaClave4 = findViewById(R.id.etNuevaClave4);
        btnRecuClave = findViewById(R.id.btnRecuClave);
        btnVolver = findViewById(R.id.btnVolver);

        // Ajustar los márgenes para evitar la superposición con las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración del evento para el botón "Volver"
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecuperarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Esta es la función que se ejecuta cuando se hace clic en el botón "Volver"Configuración del evento para el botón "Recuperar Clave"
        btnRecuClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarClave();
            }
        });
    }

    // Este es el método que se ejecuta cuando se hace clic en el botón "Recuperar Clave"
    private void recuperarClave() {
        String nombreUsuario = etNombre2.getText().toString();
        String pistaRecuperacion = etPistaRecu2.getText().toString();
        String nuevaClave1 = etNuevaClave3.getText().toString();
        String nuevaClave2 = etNuevaClave4.getText().toString();

        // Validación de que todos los campos estén llenos
        if (nombreUsuario.isEmpty() || pistaRecuperacion.isEmpty() || nuevaClave1.isEmpty() || nuevaClave2.isEmpty()) {
            Toast.makeText(RecuperarActivity.this, getString(R.string.todos_los_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de que las nuevas contraseñas coincidan
        if (!nuevaClave1.equals(nuevaClave2)) {
            Toast.makeText(RecuperarActivity.this, getString(R.string.contrasena_no_coincide), Toast.LENGTH_SHORT).show();
            return;
        }

        // Aqui Verificacamos la pista de recuperación
        if (dbHelper.checkPistaRecuperacion(nombreUsuario, pistaRecuperacion)) {


            // Actualización de la contraseña en la base de datos
            if (dbHelper.updateContrasena(nombreUsuario, nuevaClave1)) {
                Toast.makeText(RecuperarActivity.this, getString(R.string.contrasena_cambiada_exitosamente), Toast.LENGTH_SHORT).show();

                // Aqui Redirigimos al usuario a la pantalla de inicio de sesión
                Intent intent = new Intent(RecuperarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            // Si no se cumple la actualización de la contraseña, se muestra un mensaje de error
            } else {
                Toast.makeText(RecuperarActivity.this, getString(R.string.error_cambiar_contrasena), Toast.LENGTH_SHORT).show();
            }

            // Si la pista de recuperación no coincide, se muestra un mensaje de error
        } else {
            Toast.makeText(RecuperarActivity.this, getString(R.string.pista_recuperacion_incorrecta), Toast.LENGTH_SHORT).show();
        }
    }
}