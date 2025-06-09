package model;

import java.util.Date;

public class Intercambio {
    private static int contadorIntercambios = 1; // Contador estático para asignar IDs únicos
    
    private int id;
    private Usuario emisor;
    private Usuario receptor;
    private Videojuego juegoOfrecido;
    private Videojuego juegoSolicitado;
    private Date fechaPropuesta;
    private String estado; // pendiente, aceptado, rechazado, completado, fallido, contrapropuesto
    private Date fechaResolucion;
    
    // Para manejar contrapropuestas en intercambios
    private Intercambio intercambioOriginal; // Intercambio inicial (null si es el primero)
    private Intercambio contrapropuesta; // Contrapropuesta creada (null si no hay)
    
    // Constructor para crear un intercambio nuevo
    public Intercambio(Usuario emisor, Usuario receptor, Videojuego juegoOfrecido, Videojuego juegoSolicitado) {
        this.id = contadorIntercambios++;
        this.emisor = emisor;
        this.receptor = receptor;
        this.juegoOfrecido = juegoOfrecido;
        this.juegoSolicitado = juegoSolicitado;
        this.fechaPropuesta = new Date(); // Momento de creación
        this.estado = "pendiente";
        this.fechaResolucion = null;
        this.intercambioOriginal = null;
        this.contrapropuesta = null;
    }
    
    // Constructor para crear una contrapropuesta basada en un intercambio original
    public Intercambio(Usuario emisor, Usuario receptor, Videojuego juegoOfrecido, 
                      Videojuego juegoSolicitado, Intercambio intercambioOriginal) {
        this(emisor, receptor, juegoOfrecido, juegoSolicitado);
        this.intercambioOriginal = intercambioOriginal;
    }
    
    // Aceptar el intercambio si está pendiente
    public void aceptar() {
        if (estado.equals("pendiente")) {
            estado = "aceptado";
            fechaResolucion = new Date();
            juegoOfrecido.marcarComoIntercambiado();
            juegoSolicitado.marcarComoIntercambiado();
            
            // Rechazar intercambio original si aceptamos contrapropuesta
            if (intercambioOriginal != null && !intercambioOriginal.esFinalizado()) {
                intercambioOriginal.setEstado("rechazado");
                intercambioOriginal.fechaResolucion = new Date();
            }
            
            // Rechazar contrapropuesta si aceptamos intercambio original
            if (contrapropuesta != null && !contrapropuesta.esFinalizado()) {
                contrapropuesta.setEstado("rechazado");
                contrapropuesta.fechaResolucion = new Date();
            }
        } else {
            System.out.println("Este intercambio no se puede aceptar. Estado actual: " + estado);
        }
    }
    
    // Rechazar el intercambio si está pendiente
    public void rechazar() {
        if (estado.equals("pendiente")) {
            estado = "rechazado";
            fechaResolucion = new Date();
        } else {
            System.out.println("Este intercambio no se puede rechazar. Estado actual: " + estado);
        }
    }
    
    // Crear una contrapropuesta si el intercambio es válido para ello
    public Intercambio crearContrapropuesta(Videojuego nuevoJuegoOfrecido, Videojuego nuevoJuegoSolicitado) {
        if (!estado.equals("pendiente")) {
            throw new IllegalStateException("Solo se puede contraproponerr a intercambios pendientes");
        }
        
        if (contrapropuesta != null && contrapropuesta.getEstado().equals("pendiente")) {
            throw new IllegalStateException("Ya existe una contrapropuesta pendiente para este intercambio");
        }
        
        // El receptor pasa a ser emisor en la contrapropuesta
        Intercambio nuevaContrapropuesta = new Intercambio(
            this.receptor, 
            this.emisor, 
            nuevoJuegoOfrecido, 
            nuevoJuegoSolicitado,
            this
        );
        
        this.estado = "contrapropuesto"; // Estado actualizado
        this.contrapropuesta = nuevaContrapropuesta; // Referencia guardada
        
        return nuevaContrapropuesta;
    }
    
    // Indica si el intercambio ya terminó (aceptado, rechazado, etc)
    public boolean esFinalizado() {
        return estado.equals("aceptado") || estado.equals("rechazado") || 
               estado.equals("completado") || estado.equals("fallido");
    }
    
    // Verifica si un usuario participa en el intercambio (emisor o receptor)
    public boolean esConUsuario(Usuario u) {
        return emisor.equals(u) || receptor.equals(u);
    }
    
    // Indica si este intercambio es una contrapropuesta
    public boolean esContrapropuesta() {
        return intercambioOriginal != null;
    }
    
    // Indica si tiene una contrapropuesta asociada
    public boolean tieneContrapropuesta() {
        return contrapropuesta != null;
    }
    
    // Valida si puede recibir contrapropuestas (pendiente y sin contrapropuestas pendientes)
    public boolean puedeSerContrapropuesto() {
        return estado.equals("pendiente") && 
               (contrapropuesta == null || contrapropuesta.esFinalizado());
    }
    
    // Getters
    public int getId() { return id; }
    public Usuario getEmisor() { return emisor; }
    public Usuario getReceptor() { return receptor; }
    public Videojuego getJuegoOfrecido() { return juegoOfrecido; }
    public Videojuego getJuegoSolicitado() { return juegoSolicitado; }
    public Date getFechaPropuesta() { return fechaPropuesta; }
    public String getEstado() { return estado; }
    public Date getFechaResolucion() { return fechaResolucion; }
    
    // Getters nuevos
    public Intercambio getIntercambioOriginal() { return intercambioOriginal; }
    public Intercambio getContrapropuesta() { return contrapropuesta; }
    
    // Setter para estado (usar con cuidado)
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intercambio ID: ").append(id);
        
        if (esContrapropuesta()) {
            sb.append(" (CONTRAPROPUESTA de #").append(intercambioOriginal.getId()).append(")");
        }
        
        sb.append("\nEmisor: ").append(emisor.getNombre())
          .append("\nReceptor: ").append(receptor.getNombre())
          .append("\nJuego ofrecido: ").append(juegoOfrecido.getTitulo())
          .append("\nJuego solicitado: ").append(juegoSolicitado.getTitulo())
          .append("\nEstado: ").append(estado)
          .append("\nFecha propuesta: ").append(fechaPropuesta);
          
        if (fechaResolucion != null) {
            sb.append("\nFecha resolución: ").append(fechaResolucion);
        }
        
        if (tieneContrapropuesta()) {
            sb.append("\nTiene contrapropuesta activa: #").append(contrapropuesta.getId());
        }
        
        return sb.toString();
    }
}

