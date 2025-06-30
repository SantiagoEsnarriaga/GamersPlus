package model;

import database.ListaDeseadosDAO;
import database.ListaDeseadosDAOImpl;
import database.VideojuegoDAO;
import database.VideojuegoDAOImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase ListaDeseados - Modelo MVC para wishlist de videojuegos
 * Maneja la lista de juegos que un usuario desea intercambiar
 */
public class ListaDeseados {
    
    // Atributos inmutables - no cambian después de crear la lista
    private final int id;
    private final int idUsuario;
    
    // DAOs para acceso a datos - patrón DAO
    private final ListaDeseadosDAO listaDAO = new ListaDeseadosDAOImpl();
    private final VideojuegoDAO videojuegoDAO = new VideojuegoDAOImpl();

    public ListaDeseados(int id, int idUsuario) {
        this.id = id;
        this.idUsuario = idUsuario;
    }

    // ======== OPERACIONES PRINCIPALES ========
    
    /**
     * Agrega un videojuego a la lista de deseados
     * Persiste el cambio en la BD a través del DAO
     */
    public void agregar(Videojuego v) {
        Objects.requireNonNull(v, "El videojuego no puede ser nulo");
        listaDAO.addVideojuegoToLista(id, v.getId());
    }

    /**
     * Elimina un videojuego de la lista de deseados
     * Persiste el cambio en la BD a través del DAO
     */
    public void eliminar(Videojuego v) {
        Objects.requireNonNull(v, "El videojuego no puede ser nulo");
        listaDAO.removeVideojuegoFromLista(id, v.getId());
    }

    // ======== OPERACIONES DE CONSULTA ========
    
    /**
     * Verifica si un videojuego está en la lista de deseados
     * Consulta solo los IDs para una mejor performance
     */
    public boolean contiene(Videojuego v) {
        Objects.requireNonNull(v, "El videojuego no puede ser nulo");
        List<Integer> ids = listaDAO.getVideojuegosIds(id);
        return ids.contains(v.getId());
    }

    /**
     * Obtiene todos los videojuegos de la lista con sus datos completos
     * Hace join entre las tablas para traer la info completa
     */
    public List<Videojuego> obtenerLista() {
        List<Videojuego> juegos = new ArrayList<>();
        List<Integer> ids = listaDAO.getVideojuegosIds(id);
        
        // Buscar cada videojuego por ID en la BD
        for (int idVideojuego : ids) {
            Videojuego juego = videojuegoDAO.findById(idVideojuego);
            if (juego != null) {
                juegos.add(juego);
            }
        }
        
        return juegos;
    }

    // Getter para el ID de la lista
    public int getId() {
        return id;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }

    // Métodos de utilidad para consultas rápidas
    public boolean estaVacia() {
        return listaDAO.getVideojuegosIds(id).isEmpty();
    }

    public int cantidadJuegos() {
        return listaDAO.getVideojuegosIds(id).size();
    }

    // ======== REPRESENTACIÓN DE ESTADO ========
    
    // toString para debugging y mostrar el contenido de la lista
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de deseados (ID ").append(id).append("):\n");
        List<Videojuego> juegos = obtenerLista();
        
        if (juegos.isEmpty()) {
            sb.append("  - Vacía");
        } else {
            for (Videojuego v : juegos) {
                sb.append("  - ").append(v.getTitulo())
                  .append(" (").append(v.getGenero()).append(")\n");
            }
        }
        
        return sb.toString();
    }
    
    // equals y hashCode basados en ID (clave primaria)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaDeseados that = (ListaDeseados) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
