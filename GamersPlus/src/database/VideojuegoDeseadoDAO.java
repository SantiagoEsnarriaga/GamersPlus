package database;

import model.VideojuegoDeseado;
import java.util.List;

public interface VideojuegoDeseadoDAO {
    /**
     * Agrega un videojuego a la lista de deseados de un usuario
     */
    boolean agregarAListaDeseados(int usuarioId, String titulo, String genero, String plataforma, String descripcion);
    
    /**
     * Obtiene todos los videojuegos deseados de un usuario
     */
    List<VideojuegoDeseado> obtenerListaDeseados(int usuarioId);
    
    /**
     * Elimina un videojuego de la lista de deseados de un usuario
     */
    boolean eliminarDeListaDeseados(int usuarioId, int idVideojuegoDeseado);
    
    /**
     * Busca un videojuego deseado por su ID
     */
    VideojuegoDeseado findById(int id);
    
    /**
     * Busca videojuegos deseados por título (para búsquedas)
     */
    List<VideojuegoDeseado> buscarPorTitulo(String titulo);
}
