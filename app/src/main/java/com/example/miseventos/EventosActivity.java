package com.example.miseventos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miseventos.adapters.EventoAdapter;
import com.example.miseventos.models.Evento;
import com.example.miseventos.utils.DatePickerHelper;
import com.example.miseventos.utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EventosActivity extends AppCompatActivity implements EventoAdapter.OnEventoClickListener {

    private TextInputEditText etTitulo, etFecha, etObservacion, etLugar, etBuscar;
    private AutoCompleteTextView spImportancia;
    private MaterialButton btnCrearEvento, btnFiltro;
    private RecyclerView rvEventos;
    private TextView tvSinEventos;
    
    private EventoAdapter adapter;
    private BDActivity dbHelper;
    private int usuarioId;
    private String filtroImportancia = null; // null = todos
    private List<Evento> todosLosEventos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        dbHelper = new BDActivity(this);
        usuarioId = getIntent().getIntExtra("usuarioId", -1);

        if (usuarioId == -1) {
            showError("Error al obtener usuario");
            finish();
            return;
        }

        // Referencias a los elementos de la interfaz de usuario
        initViews();
        setupRecyclerView();
        setupImportanciaSpinner();
        setupListeners();
        
        // Cargar los eventos existentes
        cargarEventos();
    }

    private void initViews() {
        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etObservacion = findViewById(R.id.etObservacion);
        etLugar = findViewById(R.id.etLugar);
        etBuscar = findViewById(R.id.etBuscar);
        spImportancia = findViewById(R.id.spImportancia);
        btnCrearEvento = findViewById(R.id.btnCrearEvento);
        btnFiltro = findViewById(R.id.btnFiltro);
        rvEventos = findViewById(R.id.rvEventos);
        tvSinEventos = findViewById(R.id.tvSinEventos);
    }

    private void setupRecyclerView() {
        adapter = new EventoAdapter(this, this);
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        rvEventos.setAdapter(adapter);
    }

    private void setupImportanciaSpinner() {
        String[] importancias = getResources().getStringArray(R.array.importancia_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                importancias
        );
        spImportancia.setAdapter(adapter);
        spImportancia.setText(importancias[0], false); // Seleccionar "Baja" por defecto
    }

    private void setupListeners() {
        // DatePicker para fecha
        etFecha.setOnClickListener(v -> 
            DatePickerHelper.showDatePicker(this, etFecha)
        );

        // Botón crear evento
        btnCrearEvento.setOnClickListener(v -> crearEvento());

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarEventos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón de filtro
        btnFiltro.setOnClickListener(v -> mostrarDialogoFiltro());
    }

    private void crearEvento() {
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

        // Inserción del evento en la base de datos
        if (dbHelper.addEvento(titulo, fecha, observacion, lugar, importancia, usuarioId)) {
            showSuccess(getString(R.string.evento_creado_exitosamente));
            
            // Limpiar campos
            limpiarFormulario();
            
            // Recargar eventos
            cargarEventos();
        } else {
            showError(getString(R.string.error_crear_evento));
        }
    }

    private void limpiarFormulario() {
        etTitulo.setText("");
        etFecha.setText("");
        etObservacion.setText("");
        etLugar.setText("");
        // Mantener importancia en "Baja"
    }

    private void cargarEventos() {
        todosLosEventos = dbHelper.getEventosAsList(usuarioId);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<Evento> eventosFiltrados = new ArrayList<>(todosLosEventos);

        // Aplicar filtro de importancia si está activo
        if (filtroImportancia != null && !filtroImportancia.equals(getString(R.string.todos))) {
            List<Evento> temp = new ArrayList<>();
            for (Evento evento : eventosFiltrados) {
                if (evento.getImportancia().equalsIgnoreCase(filtroImportancia)) {
                    temp.add(evento);
                }
            }
            eventosFiltrados = temp;
        }

        // Aplicar búsqueda si hay texto
        String busqueda = etBuscar.getText().toString().trim();
        if (!busqueda.isEmpty()) {
            eventosFiltrados = dbHelper.searchEventos(usuarioId, busqueda);
        }

        // Actualizar adapter
        adapter.setEventos(eventosFiltrados);
        
        // Mostrar/ocultar mensaje de "sin eventos"
        if (eventosFiltrados.isEmpty()) {
            tvSinEventos.setVisibility(View.VISIBLE);
            rvEventos.setVisibility(View.GONE);
        } else {
            tvSinEventos.setVisibility(View.GONE);
            rvEventos.setVisibility(View.VISIBLE);
        }
    }

    private void buscarEventos(String query) {
        if (query.isEmpty()) {
            aplicarFiltros();
        } else {
            List<Evento> resultados = dbHelper.searchEventos(usuarioId, query);
            adapter.setEventos(resultados);
            
            if (resultados.isEmpty()) {
                tvSinEventos.setVisibility(View.VISIBLE);
                rvEventos.setVisibility(View.GONE);
            } else {
                tvSinEventos.setVisibility(View.GONE);
                rvEventos.setVisibility(View.VISIBLE);
            }
        }
    }

    private void mostrarDialogoFiltro() {
        String[] opciones = {
                getString(R.string.todos),
                getString(R.string.importancia) + ": " + getResources().getStringArray(R.array.importancia_array)[0],
                getString(R.string.importancia) + ": " + getResources().getStringArray(R.array.importancia_array)[1],
                getString(R.string.importancia) + ": " + getResources().getStringArray(R.array.importancia_array)[2]
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filtrar_por_importancia))
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            filtroImportancia = null;
                            break;
                        case 1:
                            filtroImportancia = getResources().getStringArray(R.array.importancia_array)[0];
                            break;
                        case 2:
                            filtroImportancia = getResources().getStringArray(R.array.importancia_array)[1];
                            break;
                        case 3:
                            filtroImportancia = getResources().getStringArray(R.array.importancia_array)[2];
                            break;
                    }
                    aplicarFiltros();
                })
                .show();
    }

    // ==================== Implementación de OnEventoClickListener ====================

    @Override
    public void onEditarClick(Evento evento) {
        Intent intent  = new Intent(this, EditarEventoActivity.class);
        intent.putExtra("eventoId", evento.getId());
        startActivity(intent);
    }

    @Override
    public void onEliminarClick(Evento evento) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.eliminar_evento))
                .setMessage(getString(R.string.confirmar_eliminar))
                .setPositiveButton(getString(R.string.eliminar), (dialog, which) -> {
                    if (dbHelper.deleteEventoLogico(evento.getId())) {
                        showSuccess(getString(R.string.evento_eliminado));
                        cargarEventos();
                    } else {
                        showError("Error al eliminar el evento");
                    }
                })
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }

    @Override
    public void onEventoClick(Evento evento) {
        // Por ahora, mismo comportamiento que editar
        onEditarClick(evento);
    }

    // ==================== Métodos de UI ====================

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
    protected void onResume() {
        super.onResume();
        // Recargar eventos al volver de editar
        cargarEventos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}