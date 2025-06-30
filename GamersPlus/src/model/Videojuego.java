package model;

import java.util.Objects;

public class Videojuego {
    
    /**
     * Estados posibles de un videojuego en el sistema de intercambios
     * Controla el flujo del proceso de intercambio
     */
    public enum Estado {
        DISPONIBLE("Disponible"),      // Puede ser intercambiado
        RESERVADO("Reservado"),        // En proceso de intercambio
        INTERCAMBIADO("Intercambiado"); // Ya fue intercambiado

        private final String nombre;

        Estado(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    // Atributos del videojuego
    private int id;
    private String titulo;
    private String genero;
    private Estado estado;
    private int idPropietario;

    /**
     * Constructor principal
     * Los juegos se crean siempre como DISPONIBLES por defecto
     */
    public Videojuego(int id, String titulo, String genero, int idPropietario) {
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo");
        this.genero = Objects.requireNonNull(genero, "El género no puede ser nulo");
        this.estado = Estado.DISPONIBLE; // Estado inicial siempre disponible
        this.idPropietario = idPropietario;
    }

    // ================ GETTERS & SETTERS ================
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo");
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = Objects.requireNonNull(genero, "El género no puede ser nulo");
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    // ================ OPERACIONES DE ESTADO ================
    
    // Consulta rápida para verificar disponibilidad
    public boolean estaDisponible() {
        return estado == Estado.DISPONIBLE;
    }

    /**
     * Reserva el juego para un intercambio
     */
    public void marcarReservado() {
        if (estado != Estado.DISPONIBLE) {
            throw new IllegalStateException(
                "Solo se pueden reservar juegos disponibles. Estado actual: " + estado
            );
        }
        this.estado = Estado.RESERVADO;
    }

    /**
     * Marca el juego como intercambiado (intercambio completado)
     */
    public void marcarComoIntercambiado() {
        this.estado = Estado.INTERCAMBIADO;
    }

    /**
     * Vuelve a marcar el juego como disponible
     * Útil cuando se cancela un intercambio
     */
    public void marcarDisponible() {
        this.estado = Estado.DISPONIBLE;
    }

    // ================ MÉTODOS SOBRESCRITOS ================
    
    /**
     * Compara videojuegos por ID (clave primaria en BD)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Videojuego that = (Videojuego) o;
        return id == that.id;
    }

    /**
     * Hash code basado en ID para colecciones
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representación en string para mostrar en la interfaz de usuario
     * Incluye título, género y estado actual
     */
    @Override
    public String toString() {
        return titulo + " (" + genero + ") - " + estado;
    }
}