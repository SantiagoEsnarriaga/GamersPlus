package controller;

import database.UsuarioDAO;
import database.UsuarioDAOImpl;
import model.Usuario;
import java.util.Optional;

public class UsuarioController {
    private final Usuario usuarioActual;
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public UsuarioController(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    // ================== Operaciones de persistencia ==================
    
    public boolean guardarUsuario() {
        if (usuarioActual.getId() > 0) {
            return usuarioDAO.update(usuarioActual);
        } else {
            return usuarioDAO.save(usuarioActual);
        }
    }
    
    public boolean eliminarUsuario() {
        return usuarioDAO.delete(usuarioActual.getId());
    }
    
    public static Optional<Usuario> buscarPorEmail(String email) {
        return new UsuarioDAOImpl().findByEmail(email);
    }
    
    
    public String getNombre() {
        return usuarioActual.getNombre();
    }
    
    public String getEmail() {
        return usuarioActual.getEmail();
    }
    
    public int getId() {
        return usuarioActual.getId();
    }
    
    public Usuario getUsuario() {
        return usuarioActual;
    }
    
}