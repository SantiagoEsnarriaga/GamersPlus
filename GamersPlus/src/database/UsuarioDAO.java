package database;

import model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAll();
    boolean save(Usuario usuario);
    boolean update(Usuario usuario);
    boolean delete(int id);
}