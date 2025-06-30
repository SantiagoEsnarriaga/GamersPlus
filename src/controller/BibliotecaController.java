package controller;

import database.VideojuegoDAO;
import database.VideojuegoDAOImpl;
import model.Usuario;
import model.Videojuego;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de la biblioteca personal de videojuegos de un usuario.
 */
public class BibliotecaController {
    
    // Referencias a dependencias
    private final Usuario usuario;
    private final VideojuegoDAO videojuegoDAO;
    
    /**
     * Constructor principal
     * Inicializa el controlador para un usuario específico
     * 
     * @param usuario Usuario propietario de la biblioteca (no puede ser nulo)
     * @throws NullPointerException si el usuario es nulo
     */
    public BibliotecaController(Usuario usuario) {
        this.usuario = Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        this.videojuegoDAO = new VideojuegoDAOImpl();
    }
    
    /**
     * Constructor con inyección de dependencias
     * Permite inyectar un DAO personalizado (útil para testing)
     * 
     * @param usuario Usuario propietario de la biblioteca
     * @param videojuegoDAO Implementación del DAO a utilizar
     */
    public BibliotecaController(Usuario usuario, VideojuegoDAO videojuegoDAO) {
        this.usuario = Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        this.videojuegoDAO = Objects.requireNonNull(videojuegoDAO, "El VideojuegoDAO no puede ser nulo");
    }
    
    // ================== OPERACIONES BÁSICAS ==================
    
    /**
     * Agrega un nuevo videojuego a la biblioteca del usuario
     * El juego se marca automáticamente como propiedad del usuario actual
     * 
     * @param juego Videojuego a agregar (no puede ser nulo)
     * @throws NullPointerException si el juego es nulo
     * @throws RuntimeException si ocurre un error al guardar en la base de datos
     */
    public void agregarJuego(Videojuego juego) {
        Objects.requireNonNull(juego, "El juego no puede ser nulo");
        
        // Asignar la propiedad al usuario actual
        juego.setIdPropietario(usuario.getId());
        
        // Guardar en la base de datos
        videojuegoDAO.save(juego, usuario.getId());
    }
    
    /**
     * Elimina un videojuego de la biblioteca del usuario
     * Solo se pueden eliminar juegos que pertenezcan al usuario
     * 
     * @param juego Videojuego a eliminar (no puede ser nulo)
     * @throws NullPointerException si el juego es nulo
     * @throws IllegalArgumentException si el usuario no posee el juego
     * @throws RuntimeException si ocurre un error al eliminar de la base de datos
     */
    public void eliminarJuego(Videojuego juego) {
        Objects.requireNonNull(juego, "El juego no puede ser nulo");
        
        if (!tieneJuego(juego)) {
            throw new IllegalArgumentException("El usuario no posee este juego");
        }
        
        videojuegoDAO.delete(juego.getId());
    }
    
    /**
     * Verifica si el usuario posee un videojuego específico en su biblioteca
     * 
     * @param juego Videojuego a verificar
     * @return true si el usuario posee el juego, false en caso contrario
     */
    public boolean tieneJuego(Videojuego juego) {
        if (juego == null) return false;
        return obtenerTodosLosJuegos().contains(juego);
    }
    
    // ================== GESTIÓN DE DISPONIBILIDAD ==================
    
    /**
     * Marca un videojuego como disponible para intercambio
     * El juego debe pertenecer al usuario actual
     * 
     * @param juego Videojuego a marcar como disponible
     * @throws IllegalArgumentException si el juego no está en la biblioteca del usuario
     */
    public void marcarJuegoComoDisponible(Videojuego juego) {
        validarJuegoEnBiblioteca(juego);
        juego.marcarDisponible(); // Usar método del modelo para consistencia
        videojuegoDAO.update(juego);
    }
    
    /**
     * Marca un videojuego como intercambiado (proceso completado)
     * El juego debe pertenecer al usuario actual y estar previamente reservado
     * 
     * @param juego Videojuego a marcar como intercambiado
     * @throws IllegalArgumentException si el juego no está en la biblioteca del usuario
     * @throws IllegalStateException si el juego no estaba reservado
     */
    public void marcarJuegoComoIntercambiado(Videojuego juego) {
        validarJuegoEnBiblioteca(juego);
        juego.marcarComoIntercambiado(); // Usar método del modelo que valida el estado
        videojuegoDAO.update(juego);
    }
    
    /**
     * Marca un videojuego como reservado (en proceso de intercambio)
     * El juego debe pertenecer al usuario actual y estar disponible
     * 
     * @param juego Videojuego a marcar como reservado
     * @throws IllegalArgumentException si el juego no está en la biblioteca del usuario
     * @throws IllegalStateException si el juego no está disponible
     */
    public void marcarJuegoComoReservado(Videojuego juego) {
        validarJuegoEnBiblioteca(juego);
        juego.marcarReservado(); // Usar método del modelo que valida el estado
        videojuegoDAO.update(juego);
    }
    
    /**
     * Método privado para validar que un juego pertenece a la biblioteca del usuario
     * Centraliza la validación común en los métodos de gestión de estado
     * 
     * @param juego Videojuego a validar
     * @throws NullPointerException si el juego es nulo
     * @throws IllegalArgumentException si el juego no está en la biblioteca
     */
    private void validarJuegoEnBiblioteca(Videojuego juego) {
        Objects.requireNonNull(juego, "El juego no puede ser nulo");
        
        if (!tieneJuego(juego)) {
            throw new IllegalArgumentException("El juego no está en la biblioteca del usuario");
        }
    }
    
    // ================== CONSULTAS Y OBTENCIÓN DE DATOS ==================
    
    /**
     * Obtiene todos los videojuegos de la biblioteca del usuario
     * 
     * @return Lista completa de videojuegos del usuario (puede estar vacía)
     */
    public List<Videojuego> obtenerTodosLosJuegos() {
        return videojuegoDAO.findByUsuario(usuario.getId());
    }
    
    /**
     * Obtiene solo los videojuegos disponibles para intercambio
     * 
     * @return Lista de videojuegos con estado DISPONIBLE
     */
    public List<Videojuego> obtenerJuegosDisponibles() {
        return obtenerTodosLosJuegos().stream()
            .filter(Videojuego::estaDisponible)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los videojuegos que NO están disponibles (reservados o intercambiados)
     * 
     * @return Lista de videojuegos con estado RESERVADO o INTERCAMBIADO
     */
    public List<Videojuego> obtenerJuegosNoDisponibles() {
        return obtenerTodosLosJuegos().stream()
            .filter(juego -> !juego.estaDisponible())
            .collect(Collectors.toList());
    }
    
    /**
     * Busca un videojuego específico por su ID dentro de la biblioteca del usuario
     * 
     * @param id ID del videojuego a buscar
     * @return Optional con el videojuego si se encuentra, Optional.empty() si no existe
     */
    public Optional<Videojuego> buscarJuegoPorId(int id) {
        return obtenerTodosLosJuegos().stream()
            .filter(juego -> juego.getId() == id)
            .findFirst();
    }
    
    /**
     * Cuenta el total de videojuegos en la biblioteca
     * 
     * @return Número total de videojuegos del usuario
     */
    public int contarTotalJuegos() {
        return obtenerTodosLosJuegos().size();
    }
    
    /**
     * Cuenta los videojuegos disponibles para intercambio
     * 
     * @return Número de videojuegos con estado DISPONIBLE
     */
    public int contarJuegosDisponibles() {
        return obtenerJuegosDisponibles().size();
    }
    
    // ================== OPERACIONES DE BÚSQUEDA Y FILTRADO ==================
    
    /**
     * Busca videojuegos por título (búsqueda parcial, case-insensitive)
     * 
     * @param titulo Texto a buscar en el título (no puede ser nulo)
     * @return Lista de videojuegos que contienen el texto en su título
     * @throws NullPointerException si el título es nulo
     */
    public List<Videojuego> buscarJuegosPorTitulo(String titulo) {
        Objects.requireNonNull(titulo, "El título no puede ser nulo");
        String tituloLower = titulo.toLowerCase().trim();
        
        if (tituloLower.isEmpty()) {
            return obtenerTodosLosJuegos(); // Si está vacío, devolver todos
        }
        
        return obtenerTodosLosJuegos().stream()
            .filter(juego -> juego.getTitulo().toLowerCase().contains(tituloLower))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca videojuegos por género (búsqueda parcial, case-insensitive)
     * 
     * @param genero Texto a buscar en el género (no puede ser nulo)
     * @return Lista de videojuegos que contienen el texto en su género
     * @throws NullPointerException si el género es nulo
     */
    public List<Videojuego> buscarJuegosPorGenero(String genero) {
        Objects.requireNonNull(genero, "El género no puede ser nulo");
        String generoLower = genero.toLowerCase().trim();
        
        if (generoLower.isEmpty()) {
            return obtenerTodosLosJuegos(); // Si está vacío, devolver todos
        }
        
        return obtenerTodosLosJuegos().stream()
            .filter(juego -> juego.getGenero().toLowerCase().contains(generoLower))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca videojuegos por múltiples criterios
     * Todos los parámetros son opcionales (pueden ser null o vacíos)
     * 
     * @param titulo Texto a buscar en el título (opcional)
     * @param genero Texto a buscar en el género (opcional)
     * @param soloDisponibles Si true, solo devuelve juegos disponibles
     * @return Lista de videojuegos que cumplen todos los criterios especificados
     */
    public List<Videojuego> buscarJuegos(String titulo, String genero, boolean soloDisponibles) {
        List<Videojuego> juegos = soloDisponibles ? obtenerJuegosDisponibles() : obtenerTodosLosJuegos();
        
        // Aplicar filtro de título si se especifica
        if (titulo != null && !titulo.trim().isEmpty()) {
            String tituloLower = titulo.toLowerCase().trim();
            juegos = juegos.stream()
                .filter(juego -> juego.getTitulo().toLowerCase().contains(tituloLower))
                .collect(Collectors.toList());
        }
        
        // Aplicar filtro de género si se especifica
        if (genero != null && !genero.trim().isEmpty()) {
            String generoLower = genero.toLowerCase().trim();
            juegos = juegos.stream()
                .filter(juego -> juego.getGenero().toLowerCase().contains(generoLower))
                .collect(Collectors.toList());
        }
        
        return juegos;
    }
    
    // ================== MÉTODOS DE UTILIDAD ==================
    
    /**
     * Obtiene el usuario propietario de esta biblioteca
     * 
     * @return Usuario propietario
     */
    public Usuario getUsuario() {
        return usuario;
    }
    
    /**
     * Verifica si la biblioteca está vacía
     * 
     * @return true si no hay videojuegos, false en caso contrario
     */
    public boolean estaVacia() {
        return contarTotalJuegos() == 0;
    }
}