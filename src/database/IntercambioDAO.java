package database;

import model.Intercambio;
import model.Intercambio.Estado;

import java.util.List;

public interface IntercambioDAO {
    boolean save(Intercambio intercambio);
    boolean update(Intercambio intercambio);
    List<Intercambio> findByUsuario(int usuarioId);
    List<Intercambio> findByUsuarioAndEstado(int usuarioId, Estado estado);
    Intercambio findById(int id);
}