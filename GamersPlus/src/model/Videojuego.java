package model;

public class Videojuego {
    private int id;
    private String titulo;
    private String genero;
    private String estado; // Disponible, Reservado, Intercambiado

    // Constructor
    public Videojuego(int id, String titulo, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.estado = "Disponible"; // Estado inicial
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEstado() {
        return estado;
    }

    // Métodos funcionales
    public boolean estaDisponible() {
        return estado.equalsIgnoreCase("Disponible");
    }

    public void marcarReservado() {
        this.estado = "Reservado";
    }

    public void marcarComoIntercambiado() {
        this.estado = "Intercambiado";
    }

    public void marcarDisponible() {
        this.estado = "Disponible";
    }

    @Override
    public String toString() {
        return titulo + " (" + genero + ") - " + estado;
    }
    
}
