package model;

import java.util.Date;
import java.util.Objects;

public class Intercambio {
    
    // Enum para manejar los estados del intercambio
    public enum Estado {
        PENDIENTE("pendiente"),
        ACEPTADO("aceptado"),
        RECHAZADO("rechazado"),
        COMPLETADO("completado"),
        FALLIDO("fallido"),
        CONTRAPROPUESTO("contrapropuesto");

        private final String valor;

        Estado(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    // Atributos principales
    private int id;
    private Usuario emisor;
    private Usuario receptor;
    private Videojuego juegoOfrecido;
    private Videojuego juegoSolicitado;
    private Date fechaPropuesta;
    private Estado estado;
    private Date fechaResolucion;
    
    // Para manejar contrapropuestas
    private Integer idIntercambioOriginal;
    private Integer idContrapropuesta;

    // Constructor principal
    public Intercambio(int id, Usuario emisor, Usuario receptor, 
                      Videojuego juegoOfrecido, Videojuego juegoSolicitado,
                      Date fechaPropuesta, Estado estado, Date fechaResolucion,
                      Integer idIntercambioOriginal, Integer idContrapropuesta) {
        
        // Validar datos críticos antes de crear el objeto
        validarParametros(emisor, receptor, juegoOfrecido, juegoSolicitado);
        
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.juegoOfrecido = juegoOfrecido;
        this.juegoSolicitado = juegoSolicitado;
        this.fechaPropuesta = fechaPropuesta;
        this.estado = estado;
        this.fechaResolucion = fechaResolucion;
        this.idIntercambioOriginal = idIntercambioOriginal;
        this.idContrapropuesta = idContrapropuesta;
    }
    
    // Validación de parámetros del constructor
    private void validarParametros(Usuario emisor, Usuario receptor, 
                                 Videojuego juegoOfrecido, Videojuego juegoSolicitado) {
        Objects.requireNonNull(emisor, "El emisor no puede ser nulo");
        Objects.requireNonNull(receptor, "El receptor no puede ser nulo");
        Objects.requireNonNull(juegoOfrecido, "El juego ofrecido no puede ser nulo");
        Objects.requireNonNull(juegoSolicitado, "El juego solicitado no puede ser nulo");
        
        // Un usuario no puede intercambiar consigo mismo
        if (emisor.equals(receptor)) {
            throw new IllegalArgumentException("El emisor y receptor no pueden ser el mismo usuario");
        }
    }
    
    /**
     * Acepta un intercambio pendiente
     * Cambia el estado y registra la fecha de resolución
     */
    public void aceptar() {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se puede aceptar intercambios pendientes. Estado actual: " + estado.getValor());
        }
        
        this.estado = Estado.ACEPTADO;
        this.fechaResolucion = new Date();
    }
    
    /**
     * Rechaza un intercambio pendiente
     * Cambia el estado y registra la fecha de resolución
     */
    public void rechazar() {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se puede rechazar intercambios pendientes. Estado actual: " + estado.getValor());
        }
        
        this.estado = Estado.RECHAZADO;
        this.fechaResolucion = new Date();
    }
    
    // Verifica si el intercambio ya no se puede modificar
    public boolean esFinalizado() {
        return estado == Estado.ACEPTADO || estado == Estado.RECHAZADO || 
               estado == Estado.COMPLETADO || estado == Estado.FALLIDO;
    }
    
    // Verifica si un usuario participa en este intercambio
    public boolean esConUsuario(Usuario u) {
        Objects.requireNonNull(u, "El usuario no puede ser nulo");
        return emisor.equals(u) || receptor.equals(u);
    }
    
    // Verifica si este intercambio es una contrapropuesta de otro
    public boolean esContrapropuesta() {
        return idIntercambioOriginal != null;
    }
    
    // Verifica si se puede hacer una contrapropuesta a este intercambio
    public boolean puedeSerContrapropuesto() {
        return estado == Estado.PENDIENTE && (idContrapropuesta == null);
    }
    
    // ================ Getters y Setters ================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Usuario getEmisor() { return emisor; }
    public void setEmisor(Usuario emisor) { this.emisor = emisor; }
    
    public Usuario getReceptor() { return receptor; }
    public void setReceptor(Usuario receptor) { this.receptor = receptor; }
    
    public Videojuego getJuegoOfrecido() { return juegoOfrecido; }
    public void setJuegoOfrecido(Videojuego juegoOfrecido) { this.juegoOfrecido = juegoOfrecido; }
    
    public Videojuego getJuegoSolicitado() { return juegoSolicitado; }
    public void setJuegoSolicitado(Videojuego juegoSolicitado) { this.juegoSolicitado = juegoSolicitado; }
    
    public Date getFechaPropuesta() { return fechaPropuesta; }
    public void setFechaPropuesta(Date fechaPropuesta) { this.fechaPropuesta = fechaPropuesta; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { 
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.estado = estado; 
    }
    
    public Date getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(Date fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    
    public Integer getIdIntercambioOriginal() { return idIntercambioOriginal; }
    public void setIdIntercambioOriginal(Integer idIntercambioOriginal) { this.idIntercambioOriginal = idIntercambioOriginal; }
    
    public Integer getIdContrapropuesta() { return idContrapropuesta; }
    public void setIdContrapropuesta(Integer idContrapropuesta) { this.idContrapropuesta = idContrapropuesta; }
    
    // toString para debugging y mostrar información del intercambio
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intercambio ID: ").append(id);
        
        if (esContrapropuesta()) {
            sb.append(" (CONTRAPROPUESTA de #").append(idIntercambioOriginal).append(")");
        }
        
        sb.append("\nEmisor: ").append(emisor.getNombre())
          .append("\nReceptor: ").append(receptor.getNombre())
          .append("\nJuego ofrecido: ").append(juegoOfrecido.getTitulo())
          .append("\nJuego solicitado: ").append(juegoSolicitado.getTitulo())
          .append("\nEstado: ").append(estado.getValor())
          .append("\nFecha propuesta: ").append(fechaPropuesta);
          
        if (fechaResolucion != null) {
            sb.append("\nFecha resolución: ").append(fechaResolucion);
        }
        
        if (idContrapropuesta != null) {
            sb.append("\nTiene contrapropuesta activa: #").append(idContrapropuesta);
        }
        
        return sb.toString();
    }

    // equals y hashCode basados en el ID (clave primaria en BD)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intercambio that = (Intercambio) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
