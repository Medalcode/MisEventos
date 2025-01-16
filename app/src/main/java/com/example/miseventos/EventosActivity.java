package com.example.miseventos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EventosActivity extends AppCompatActivity {

    private EditText etTitulo, etFecha, etObservacion, etLugar;
    private Spinner spImportancia;
    private Button btnCrearEvento;
    private ListView lvEventos;
    private ArrayList<String> listaEventos;
    private ArrayAdapter<String> adapter;
    private BDActivity dbHelper;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        dbHelper = new BDActivity(this);
        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        // Referencias a los elementos de la interfaz de usuario
        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etObservacion = findViewById(R.id.etObservacion);
        etLugar = findViewById(R.id.etLugar);
        spImportancia = findViewById(R.id.spImportancia);
        btnCrearEvento = findViewById(R.id.btnCrearEvento);
        lvEventos = findViewById(R.id.lvEventos);

        listaEventos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaEventos);
        lvEventos.setAdapter(adapter);

        // Configuración del evento para el botón "Crear Evento"
        btnCrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearEvento();
            }
        });

        // Cargar los eventos existentes
        cargarEventos();
    }

    private void crearEvento() {
        String titulo = etTitulo.getText().toString();
        String fecha = etFecha.getText().toString();
        String observacion = etObservacion.getText().toString();
        String lugar = etLugar.getText().toString();
        String importancia = spImportancia.getSelectedItem().toString();

        // Verificación de que los campos no estén vacíos
        if (titulo.isEmpty() || fecha.isEmpty() || observacion.isEmpty() || lugar.isEmpty() || importancia.isEmpty()) {
            Toast.makeText(EventosActivity.this, getString(R.string.todos_los_campos_obligatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        // Inserción del evento en la base de datos y notificación al usuario
        if (dbHelper.addEvento(titulo, fecha, observacion, lugar, importancia, usuarioId)) {
            Toast.makeText(EventosActivity.this, getString(R.string.evento_creado_exitosamente), Toast.LENGTH_SHORT).show();
            // Recarga de los eventos para actualizar la lista
            cargarEventos();
        } else {
            Toast.makeText(EventosActivity.this, getString(R.string.error_crear_evento), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cargar los eventos desde la base de datos
    private void cargarEventos() {
        listaEventos.clear();
        Cursor cursor = dbHelper.getEventos(usuarioId);
        if (cursor.moveToFirst()) {
            do {
                // Obtención de los índices de las columnas
                int tituloIndex = cursor.getColumnIndex("titulo");
                int fechaIndex = cursor.getColumnIndex("fecha");
                int observacionIndex = cursor.getColumnIndex("observacion");
                int lugarIndex = cursor.getColumnIndex("lugar");
                int importanciaIndex = cursor.getColumnIndex("importancia");

                // Verificación de que los índices son válidos y creación de la cadena del evento
                if (tituloIndex != -1 && fechaIndex != -1 && observacionIndex != -1 && lugarIndex != -1 && importanciaIndex != -1) {
                    String evento = getString(R.string.titulo) + ": " + cursor.getString(tituloIndex) +
                            "\n" + getString(R.string.fecha) + ": " + cursor.getString(fechaIndex) +
                            "\n" + getString(R.string.observacion) + ": " + cursor.getString(observacionIndex) +
                            "\n" + getString(R.string.lugar) + ": " + cursor.getString(lugarIndex) +
                            "\n" + getString(R.string.importancia) + ": " + cursor.getString(importanciaIndex);
                    listaEventos.add(evento);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Notificación al adaptador de que los datos han cambiado
        adapter.notifyDataSetChanged();
    }
}