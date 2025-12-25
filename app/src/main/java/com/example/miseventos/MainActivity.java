package com.example.miseventos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miseventos.utils.InputValidator;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // Declaración de variables para los elementos de la interfaz de usuario
    private TextView tvRegistro, tvLogin, tvOlviClave;
    private Button btnRegUser, btnLogUser;
    private EditText etRegUser, etRegPass, etPistaRecu1, etLogUser, etLogPass;
    private BDActivity dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de la base de datos
        dbHelper = new BDActivity(this);
        // Referencias a los elementos de la interfaz de usuario
        referencias();
        // Configuración de los eventos de los botones y textos
        eventos();
    }

    private void referencias() {
        // Asignación de los elementos de la interfaz de usuario a las variables
        etRegUser = findViewById(R.id.etRegUser1);
        etRegPass = findViewById(R.id.etRegPass1);
        etPistaRecu1 = findViewById(R.id.etPistaRecu1);
        etLogUser = findViewById(R.id.etLogUser1);
        etLogPass = findViewById(R.id.etLogPass1);
        btnRegUser = findViewById(R.id.btnRegUser);
        btnLogUser = findViewById(R.id.btnLogUser);
        tvOlviClave = findViewById(R.id.tvOlviClave);
    }

    private void eventos() {
        // Configuración del evento de clic para el botón de registro de usuario
        btnRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // Configuración del evento de clic para el botón de inicio de sesión
        btnLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUsuario();
            }
        });

        // Configuración del evento de clic para el texto de recuperación de contraseña
        tvOlviClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registrarUsuario() {
        // Obtención de los valores de los campos de texto
        String nombre = etRegUser.getText().toString().trim();
        String password = etRegPass.getText().toString();
        String pistaRecuperacion = etPistaRecu1.getText().toString().trim();

        // Validación del nombre de usuario
        InputValidator.ValidationResult nombreResult = InputValidator.validateUsername(nombre);
        if (!nombreResult.isSuccess()) {
            showError(nombreResult.getErrorMessage());
            etRegUser.requestFocus();
            return;
        }

        // Validación de la contraseña
        InputValidator.ValidationResult passResult = InputValidator.validatePassword(password);
        if (!passResult.isSuccess()) {
            showError(passResult.getErrorMessage());
            etRegPass.requestFocus();
            return;
        }

        // Validación de la pista de recuperación
        InputValidator.ValidationResult pistaResult = InputValidator.validateNotEmpty(
                pistaRecuperacion, "La pista de recuperación");
        if (!pistaResult.isSuccess()) {
            showError(pistaResult.getErrorMessage());
            etPistaRecu1.requestFocus();
            return;
        }

        // Intento de registrar el usuario en la base de datos
        // El hash de la contraseña se realiza automáticamente en BDActivity
        if (dbHelper.addUsuario(nombre, password, pistaRecuperacion)) {
            showSuccess(getString(R.string.usuario_registrado_exitosamente));
            
            // Limpiar campos de registro
            etRegUser.setText("");
            etRegPass.setText("");
            etPistaRecu1.setText("");
            
            // Enfocar en login
            etLogUser.setText(nombre);
            etLogUser.requestFocus();
        } else {
            showError(getString(R.string.usuario_ya_registrado));
        }
    }

    private void loginUsuario() {
        // Obtención de los valores de los campos de texto
        String nombre = etLogUser.getText().toString().trim();
        String password = etLogPass.getText().toString();

        // Validación del nombre de usuario
        InputValidator.ValidationResult nombreResult = InputValidator.validateUsername(nombre);
        if (!nombreResult.isSuccess()) {
            showError(nombreResult.getErrorMessage());
            etLogUser.requestFocus();
            return;
        }

        // Validación de la contraseña
        InputValidator.ValidationResult passResult = InputValidator.validateNotEmpty(
                password, "La contraseña");
        if (!passResult.isSuccess()) {
            showError(passResult.getErrorMessage());
            etLogPass.requestFocus();
            return;
        }

        // Verificación de las credenciales del usuario en la base de datos
        // La verificación del hash se realiza automáticamente en BDActivity
        if (dbHelper.checkUsuario(nombre, password)) {
            showSuccess(getString(R.string.login_exitoso));
            
            // Obtener ID del usuario
            int usuarioId = dbHelper.getUsuarioId(nombre);
            
            // Inicio de la actividad ClaveActivity si el inicio de sesión es exitoso
            Intent intent = new Intent(MainActivity.this, ClaveActivity.class);
            intent.putExtra("usuario", nombre);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
            
            // Limpiar campos
            etLogUser.setText("");
            etLogPass.setText("");
        } else {
            showError(getString(R.string.usuario_o_contrasena_incorrectos));
            etLogPass.setText("");
            etLogPass.requestFocus();
        }
    }

    /**
     * Muestra un mensaje de error usando Snackbar
     */
    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark))
                .show();
    }

    /**
     * Muestra un mensaje de éxito usando Snackbar
     */
    private void showSuccess(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_green_dark))
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
