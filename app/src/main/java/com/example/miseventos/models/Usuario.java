package com.example.miseventos.models;

/**
 * Modelo de datos para un Usuario
 */
public class Usuario {
    private int id;
    private String nombre;
    private String contrasenaHash;
    private String pistaRecuperacion;

    public Usuario(int id, String nombre, String contrasenaHash, String pistaRecuperacion) {
        this.id = id;
        this.nombre = nombre;
        this.contrasenaHash = contrasenaHash;
        this.pistaRecuperacion = pistaRecuperacion;
    }

    // Constructor sin ID para nuevos usuarios
    public Usuario(String nombre, String contrasenaHash, String pistaRecuperacion) {
        this(0, nombre, contrasenaHash, pistaRecuperacion);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public String getPistaRecuperacion() {
        return pistaRecuperacion;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public void setPistaRecuperacion(String pistaRecuperacion) {
        this.pistaRecuperacion = pistaRecuperacion;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
