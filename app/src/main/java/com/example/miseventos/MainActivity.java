package com.example.miseventos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        String nombre = etRegUser.getText().toString();
        String password = etRegPass.getText().toString();
        String pistaRecuperacion = etPistaRecu1.getText().toString();

        // Verificación de que los campos no estén vacíos
        if (nombre.isEmpty() || password.isEmpty() || pistaRecuperacion.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.todos_los_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        // Intento de registrar el usuario en la base de datos
        if (dbHelper.addUsuario(nombre, password, pistaRecuperacion)) {
            Toast.makeText(MainActivity.this, getString(R.string.usuario_registrado_exitosamente), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.usuario_ya_registrado), Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUsuario() {
        // Obtención de los valores de los campos de texto
        String nombre = etLogUser.getText().toString();
        String password = etLogPass.getText().toString();

        // Verificación de que los campos no estén vacíos
        if (nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.todos_los_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificación de las credenciales del usuario en la base de datos
        if (dbHelper.checkUsuario(nombre, password)) {
            Toast.makeText(MainActivity.this, getString(R.string.login_exitoso), Toast.LENGTH_SHORT).show();
            // Inicio de la actividad ClaveActivity si el inicio de sesión es exitoso
            Intent intent = new Intent(MainActivity.this, ClaveActivity.class);
            intent.putExtra("usuario", nombre);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.usuario_o_contrasena_incorrectos), Toast.LENGTH_SHORT).show();
        }
    }
}
