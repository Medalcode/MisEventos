package com.example.miseventos.models;

/**
 * Modelo de datos para un Evento
 */
public class Evento {
    private int id;
    private String titulo;
    private String fecha;
    private String observacion;
    private String lugar;
    private String importancia;
    private int usuarioId;

    public Evento(int id, String titulo, String fecha, String observacion, 
                  String lugar, String importancia, int usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.observacion = observacion;
        this.lugar = lugar;
        this.importancia = importancia;
        this.usuarioId = usuarioId;
    }

    // Constructor sin ID para nuevos eventos
    public Evento(String titulo, String fecha, String observacion, 
                  String lugar, String importancia, int usuarioId) {
        this(0, titulo, fecha, observacion, lugar, importancia, usuarioId);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getLugar() {
        return lugar;
    }

    public String getImportancia() {
        return importancia;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setImportancia(String importancia) {
        this.importancia = importancia;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", fecha='" + fecha + '\'' +
                ", importancia='" + importancia + '\'' +
                '}';
    }
}
