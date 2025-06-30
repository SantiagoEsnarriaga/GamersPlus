package controller;

import database.IntercambioDAO;
import database.IntercambioDAOImpl;
import model.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class IntercambioController {
    private final Usuario usuarioActual;
    private final IntercambioDAO intercambioDAO = new IntercambioDAOImpl();
    private final BibliotecaController bibliotecaController;

    public IntercambioController(Usuario usuarioActual, BibliotecaController bibliotecaController) {
        this.usuarioActual = Objects.requireNonNull(usuarioActual, "El usuario no puede ser nulo");
        this.bibliotecaController = bibliotecaController;
    }
    
    public Intercambio proponerIntercambio(Usuario receptor, 
                                         Videojuego juegoOfrecido, 
                                         Videojuego juegoSolicitado) {
        validarParametrosIntercambio(receptor, juegoOfrecido, juegoSolicitado);
        validarIntercambio(receptor, juegoOfrecido, juegoSolicitado);
        
        Intercambio nuevo = new Intercambio(
            0, 
            usuarioActual, 
            receptor, 
            juegoOfrecido, 
            juegoSolicitado,
            new Date(),
            Intercambio.Estado.PENDIENTE,
            null,
            null,
            null
        );
        
        // Guardar en base de datos
        intercambioDAO.save(nuevo);
        
        // Reservar los juegos
        bibliotecaController.marcarJuegoComoReservado(juegoOfrecido);
        BibliotecaController receptorController = new BibliotecaController(receptor);
        receptorController.marcarJuegoComoReservado(juegoSolicitado);
        
        return nuevo;
    }
    
    private void validarParametrosIntercambio(Usuario receptor, 
                                           Videojuego juegoOfrecido,
                                           Videojuego juegoSolicitado) {
        Objects.requireNonNull(receptor, "El receptor no puede ser nulo");
        Objects.requireNonNull(juegoOfrecido, "El juego ofrecido no puede ser nulo");
        Objects.requireNonNull(juegoSolicitado, "El juego solicitado no puede ser nulo");
        
        if (usuarioActual.equals(receptor)) {
            throw new IllegalArgumentException("No puede intercambiar consigo mismo");
        }
    }
    
    private void validarIntercambio(Usuario receptor,
                                  Videojuego juegoOfrecido,
                                  Videojuego juegoSolicitado) {
        if (!bibliotecaController.tieneJuego(juegoOfrecido)) {
            throw new IllegalArgumentException("No posee el juego que desea ofrecer");
        }
        
        BibliotecaController receptorController = new BibliotecaController(receptor);
        if (!receptorController.tieneJuego(juegoSolicitado)) {
            throw new IllegalArgumentException("El usuario no posee el juego solicitado");
        }
        
        if (!juegoOfrecido.estaDisponible()) {
            throw new IllegalArgumentException("Su juego no está disponible para intercambio");
        }
        
        if (!juegoSolicitado.estaDisponible()) {
            throw new IllegalArgumentException("El juego solicitado no está disponible");
        }
    }
    
    public void aceptarIntercambio(Intercambio intercambio) {
        validarIntercambioRecibido(intercambio);
        intercambio.aceptar();
        
        // Actualizar estado en BD
        intercambio.setFechaResolucion(new Date());
        intercambioDAO.update(intercambio);
        
        // Marcar juegos como intercambiados
        bibliotecaController.marcarJuegoComoIntercambiado(intercambio.getJuegoSolicitado());
        BibliotecaController emisorController = new BibliotecaController(intercambio.getEmisor());
        emisorController.marcarJuegoComoIntercambiado(intercambio.getJuegoOfrecido());
        
        manejarContrapropuestas(intercambio);
    }
    
    private void manejarContrapropuestas(Intercambio intercambio) {
        if (intercambio.getIdIntercambioOriginal() != null) {
            Intercambio original = intercambioDAO.findById(intercambio.getIdIntercambioOriginal());
            if (original != null && !original.esFinalizado()) {
                rechazarIntercambio(original);
            }
        }
        
        if (intercambio.getIdContrapropuesta() != null) {
            Intercambio contrapropuesta = intercambioDAO.findById(intercambio.getIdContrapropuesta());
            if (contrapropuesta != null && !contrapropuesta.esFinalizado()) {
                rechazarIntercambio(contrapropuesta);
            }
        }
    }
    
    public void rechazarIntercambio(Intercambio intercambio) {
        validarIntercambioRecibido(intercambio);
        intercambio.rechazar();
        
        // Actualizar estado en BD
        intercambio.setFechaResolucion(new Date());
        intercambioDAO.update(intercambio);
        
        // Liberar juegos reservados
        bibliotecaController.marcarJuegoComoDisponible(intercambio.getJuegoSolicitado());
        BibliotecaController emisorController = new BibliotecaController(intercambio.getEmisor());
        emisorController.marcarJuegoComoDisponible(intercambio.getJuegoOfrecido());
    }
    
    private void validarIntercambioRecibido(Intercambio intercambio) {
        Objects.requireNonNull(intercambio, "El intercambio no puede ser nulo");
        
        if (!intercambio.getReceptor().equals(usuarioActual)) {
            throw new IllegalArgumentException("Solo puede gestionar intercambios recibidos");
        }
        
        if (intercambio.esFinalizado()) {
            throw new IllegalStateException("No puede modificar un intercambio finalizado");
        }
    }
    
    public Intercambio crearContrapropuesta(Intercambio intercambioOriginal,
                                          Videojuego nuevoJuegoOfrecido,
                                          Videojuego nuevoJuegoSolicitado) {
        validarContrapropuesta(intercambioOriginal, nuevoJuegoOfrecido, nuevoJuegoSolicitado);
        
        Intercambio contrapropuesta = new Intercambio(
            0,
            usuarioActual, 
            intercambioOriginal.getEmisor(), 
            nuevoJuegoOfrecido, 
            nuevoJuegoSolicitado,
            new Date(),
            Intercambio.Estado.PENDIENTE,
            null,
            intercambioOriginal.getId(),
            null
        );
        
        // Guardar en BD
        intercambioDAO.save(contrapropuesta);
        
        // Actualizar intercambio original
        intercambioOriginal.setEstado(Intercambio.Estado.CONTRAPROPUESTO);
        intercambioOriginal.setIdContrapropuesta(contrapropuesta.getId());
        intercambioDAO.update(intercambioOriginal);
        
        // Reservar juegos
        bibliotecaController.marcarJuegoComoReservado(nuevoJuegoOfrecido);
        
        return contrapropuesta;
    }
    
    private void validarContrapropuesta(Intercambio intercambioOriginal,
                                      Videojuego nuevoJuegoOfrecido,
                                      Videojuego nuevoJuegoSolicitado) {
        Objects.requireNonNull(intercambioOriginal, "El intercambio original no puede ser nulo");
        Objects.requireNonNull(nuevoJuegoOfrecido, "El juego ofrecido no puede ser nulo");
        Objects.requireNonNull(nuevoJuegoSolicitado, "El juego solicitado no puede ser nulo");
        
        if (!intercambioOriginal.getReceptor().equals(usuarioActual)) {
            throw new IllegalArgumentException("Solo puede contraproponer en intercambios recibidos");
        }
        
        if (!intercambioOriginal.puedeSerContrapropuesto()) {
            throw new IllegalStateException("Este intercambio no puede ser contrapropuesto");
        }
        
        if (!bibliotecaController.tieneJuego(nuevoJuegoOfrecido)) {
            throw new IllegalArgumentException("No posee el juego que desea ofrecer");
        }
        
        BibliotecaController emisorController = new BibliotecaController(intercambioOriginal.getEmisor());
        if (!emisorController.tieneJuego(nuevoJuegoSolicitado)) {
            throw new IllegalArgumentException("El otro usuario no posee el juego solicitado");
        }
        
        if (nuevoJuegoOfrecido.equals(intercambioOriginal.getJuegoSolicitado()) && 
            nuevoJuegoSolicitado.equals(intercambioOriginal.getJuegoOfrecido())) {
            throw new IllegalArgumentException("No puede hacer una contrapropuesta idéntica al intercambio original");
        }
        
        if (!nuevoJuegoOfrecido.estaDisponible()) {
            throw new IllegalStateException("El juego ofrecido no está disponible");
        }
        if (!nuevoJuegoSolicitado.estaDisponible()) {
            throw new IllegalStateException("El juego solicitado no está disponible");
        }
    }
    
    public List<Intercambio> obtenerIntercambiosActivos() {
        return intercambioDAO.findByUsuarioAndEstado(usuarioActual.getId(), Intercambio.Estado.PENDIENTE);
    }
    
    public List<Intercambio> obtenerHistorial() {
        return intercambioDAO.findByUsuario(usuarioActual.getId());
    }
}