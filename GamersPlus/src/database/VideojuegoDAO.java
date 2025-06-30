package database;

import model.Videojuego;
import java.util.List;

public interface VideojuegoDAO {
	Videojuego findById(int id);
    List<Videojuego> findByUsuario(int usuarioId);
    boolean save(Videojuego videojuego, int usuarioId);
    boolean update(Videojuego videojuego);
    boolean delete(int id);
}