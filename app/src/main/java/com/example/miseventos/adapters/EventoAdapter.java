package com.example.miseventos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miseventos.R;
import com.example.miseventos.models.Evento;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para RecyclerView de eventos con Material Design 3
 */
public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventos;
    private final Context context;
    private final OnEventoClickListener listener;

    public interface OnEventoClickListener {
        void onEditarClick(Evento evento);
        void onEliminarClick(Evento evento);
        void onEventoClick(Evento evento);
    }

    public EventoAdapter(Context context, OnEventoClickListener listener) {
        this.context = context;
        this.eventos = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.bind(evento);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public void setEventos(List<Evento> nuevosEventos) {
        this.eventos = nuevosEventos != null ? nuevosEventos : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeEvento(int position) {
        if (position >= 0 && position < eventos.size()) {
            eventos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateEvento(int position, Evento evento) {
        if (position >= 0 && position < eventos.size()) {
            eventos.set(position, evento);
            notifyItemChanged(position);
        }
    }

    class EventoViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardEvento;
        private final View viewImportancia;
        private final TextView tvTitulo;
        private final TextView tvFecha;
        private final TextView tvLugar;
        private final TextView tvObservacion;
        private final Chip chipImportancia;
        private final MaterialButton btnEditar;
        private final MaterialButton btnEliminar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEvento = itemView.findViewById(R.id.cardEvento);
            viewImportancia = itemView.findViewById(R.id.viewImportancia);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvObservacion = itemView.findViewById(R.id.tvObservacion);
            chipImportancia = itemView.findViewById(R.id.chipImportancia);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(Evento evento) {
            tvTitulo.setText(evento.getTitulo());
            tvFecha.setText(evento.getFecha());
            tvLugar.setText(evento.getLugar());
            
            // Mostrar observación o ocultar si está vacía
            if (evento.getObservacion() != null && !evento.getObservacion().trim().isEmpty()) {
                tvObservacion.setVisibility(View.VISIBLE);
                tvObservacion.setText(evento.getObservacion());
            } else {
                tvObservacion.setVisibility(View.GONE);
            }

            // Configurar chip de importancia
            chipImportancia.setText(evento.getImportancia());
            
            // Color según importancia
            int color = getColorImportancia(evento.getImportancia());
            viewImportancia.setBackgroundColor(color);
            chipImportancia.setChipBackgroundColor(
                    android.content.res.ColorStateList.valueOf(
                            adjustAlpha(color, 0.2f)
                    )
            );

            // Click en la card (ver detalles)
            cardEvento.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoClick(evento);
                }
            });

            // Click en botón editar
            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditarClick(evento);
                }
            });

            // Click en botón eliminar
            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarClick(evento);
                }
            });
        }

        /**
         * Obtiene el color según la importancia
         */
        private int getColorImportancia(String importancia) {
            if (importancia == null) {
                return Color.GRAY;
            }

            switch (importancia.toLowerCase()) {
                case "alta":
                case "high":
                    return Color.parseColor("#F44336"); // Rojo
                case "media":
                case "medium":
                    return Color.parseColor("#FF9800"); // Naranja
                case "baja":
                case "low":
                    return Color.parseColor("#4CAF50"); // Verde
                default:
                    return Color.GRAY;
            }
        }

        /**
         * Ajusta el alpha de un color
         */
        private int adjustAlpha(int color, float factor) {
            int alpha = Math.round(Color.alpha(color) * factor);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            return Color.argb(alpha, red, green, blue);
        }
    }
}
