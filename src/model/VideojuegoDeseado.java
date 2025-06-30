package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class VideojuegoDeseado {
    
    // Atributos del videojuego deseado
    private int id;
    private String titulo;
    private String genero;
    private String plataforma;
    private String descripcion;
    private LocalDateTime fechaAgregado;

    // ================ CONSTRUCTORES ================
    
    /**
     * Constructor vacío
     * Utilizado principalmente por frameworks ORM y para inicialización básica
     */
    public VideojuegoDeseado() {}

    /**
     * Constructor completo con ID
     * Usado principalmente al cargar datos existentes desde la BD
     */
    public VideojuegoDeseado(int id, String titulo, String genero, String plataforma, String descripcion) {
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo");
        this.genero = Objects.requireNonNull(genero, "El género no puede ser nulo");
        this.plataforma = Objects.requireNonNull(plataforma, "La plataforma no puede ser nula");
        this.descripcion = descripcion; // Puede ser nula o vacía
        this.fechaAgregado = LocalDateTime.now(); // Se establece automáticamente
    }

    /**
     * Constructor sin ID para crear nuevos registros
     * El ID lo asigna automáticamente la BD
     */
    public VideojuegoDeseado(String titulo, String genero, String plataforma, String descripcion) {
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo");
        this.genero = Objects.requireNonNull(genero, "El género no puede ser nulo");
        this.plataforma = Objects.requireNonNull(plataforma, "La plataforma no puede ser nula");
        this.descripcion = descripcion; // Puede ser nula o vacía
        this.fechaAgregado = LocalDateTime.now(); // Se establece automáticamente
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

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = Objects.requireNonNull(plataforma, "La plataforma no puede ser nula");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    // ================ MÉTODOS DE UTILIDAD ================
    
    /**
     * Verifica si la descripción está presente y no está vacía
     * Útil para validaciones en la interfaz de usuario
     */
    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.trim().isEmpty();
    }

    /**
     * Verifica si el videojuego deseado coincide con los criterios de búsqueda básicos
     * Compara título, género y plataforma (case-insensitive)
     */
    public boolean coincideCon(String tituloComparar, String generoComparar, String plataformaComparar) {
        boolean tituloCoincide = titulo != null && titulo.toLowerCase().contains(tituloComparar.toLowerCase());
        boolean generoCoincide = genero != null && genero.equalsIgnoreCase(generoComparar);
        boolean plataformaCoincide = plataforma != null && plataforma.equalsIgnoreCase(plataformaComparar);
        
        return tituloCoincide && generoCoincide && plataformaCoincide;
    }

    // ================ MÉTODOS SOBRESCRITOS ================
    
    /**
     * Compara videojuegos deseados por ID (clave primaria en BD)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VideojuegoDeseado that = (VideojuegoDeseado) obj;
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
     * Incluye título, género, plataforma y descripción si está disponible
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append(" (").append(genero).append(" - ").append(plataforma).append(")");
        
        if (tieneDescripcion()) {
            sb.append(" - ").append(descripcion);
        }
        
        return sb.toString();
    }
}