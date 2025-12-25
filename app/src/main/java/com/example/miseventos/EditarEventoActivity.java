package com.example.miseventos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miseventos.models.Evento;
import com.example.miseventos.utils.DatePickerHelper;
import com.example.miseventos.utils.InputValidator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class EditarEventoActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etFecha, etObservacion, etLugar;
    private AutoCompleteTextView spImportancia;
    private MaterialButton btnGuardar, btnCancelar;
    private MaterialToolbar toolbar;
    
    private BDActivity dbHelper;
    private int eventoId;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);

        dbHelper = new BDActivity(this);
        eventoId = getIntent().getIntExtra("eventoId", -1);

        if (eventoId == -1) {
            showError("Error al obtener evento");
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupImportanciaSpinner();
        setupListeners();
        cargarEvento();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etObservacion = findViewById(R.id.etObservacion);
        etLugar = findViewById(R.id.etLugar);
        spImportancia = findViewById(R.id.spImportancia);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImportanciaSpinner() {
        String[] importancias = getResources().getStringArray(R.array.importancia_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                importancias
        );
        spImportancia.setAdapter(adapter);
    }

    private void setupListeners() {
        // DatePicker para fecha
        etFecha.setOnClickListener(v -> 
            DatePickerHelper.showDatePicker(this, etFecha)
        );

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarCambios());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarEvento() {
        evento = dbHelper.getEventoById(eventoId);
        
        if (evento == null) {
            showError("Evento no encontrado");
            finish();
            return;
        }

        // Rellenar campos con los datos del evento
        etTitulo.setText(evento.getTitulo());
        etFecha.setText(evento.getFecha());
        etLugar.setText(evento.getLugar());
        etObservacion.setText(evento.getObservacion());
        spImportancia.setText(evento.getImportancia(), false);
    }

    private void guardarCambios() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String observacion = etObservacion.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String importancia = spImportancia.getText().toString();

        // Validaciones
        InputValidator.ValidationResult tituloResult = InputValidator.validateNotEmpty(titulo, "El título");
        if (!tituloResult.isSuccess()) {
            showError(tituloResult.getErrorMessage());
            etTitulo.requestFocus();
            return;
        }

        InputValidator.ValidationResult fechaResult = InputValidator.validateDate(fecha);
        if (!fechaResult.isSuccess()) {
            showError(fechaResult.getErrorMessage());
            etFecha.requestFocus();
            return;
        }

        InputValidator.ValidationResult lugarResult = InputValidator.validateNotEmpty(lugar, "El lugar");
        if (!lugarResult.isSuccess()) {
            showError(lugarResult.getErrorMessage());
            etLugar.requestFocus();
            return;
        }

        // Actualizar evento en la base de datos
        if (dbHelper.updateEvento(eventoId, titulo, fecha, observacion, lugar, importancia)) {
            showSuccess(getString(R.string.evento_actualizado));
            
            // Esperar un momento para que se vea el mensaje y luego cerrar
            etTitulo.postDelayed(this::finish, 1000);
        } else {
            showError("Error al actualizar el evento");
        }
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark))
                .show();
    }

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
