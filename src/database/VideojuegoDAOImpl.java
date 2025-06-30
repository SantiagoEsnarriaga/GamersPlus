package database;

import model.Videojuego;
import model.Videojuego.Estado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideojuegoDAOImpl implements VideojuegoDAO {

    @Override
    public List<Videojuego> findByUsuario(int usuarioId) {
        List<Videojuego> videojuegos = new ArrayList<>();
        String sql = "SELECT * FROM Videojuegos WHERE ID_US_PROPIETARIO = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    videojuegos.add(mapVideojuego(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videojuegos;
    }

    @Override
    public boolean save(Videojuego videojuego, int usuarioId) {
        String sql = "INSERT INTO Videojuegos (Titulo, Genero, Estado, ID_US_PROPIETARIO) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, videojuego.getTitulo());
            stmt.setString(2, videojuego.getGenero());
            stmt.setString(3, videojuego.getEstado().name());
            stmt.setInt(4, usuarioId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        videojuego.setId(rs.getInt(1));
                        // Establecer el propietario en el objeto
                        videojuego.setIdPropietario(usuarioId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Videojuego videojuego) {
        String sql = "UPDATE Videojuegos SET Titulo = ?, Genero = ?, Estado = ? WHERE ID_VID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, videojuego.getTitulo());
            stmt.setString(2, videojuego.getGenero());
            stmt.setString(3, videojuego.getEstado().name());
            stmt.setInt(4, videojuego.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Videojuegos WHERE ID_VID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Videojuego findById(int id) {
        String sql = "SELECT * FROM Videojuegos WHERE ID_VID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapVideojuego(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Videojuego mapVideojuego(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_VID");
        String titulo = rs.getString("Titulo");
        String genero = rs.getString("Genero");
        Estado estado = Estado.valueOf(rs.getString("Estado"));
        int idPropietario = rs.getInt("ID_US_PROPIETARIO");

        // Usar el constructor de 4 parámetros
        Videojuego juego = new Videojuego(id, titulo, genero, idPropietario);
        juego.setEstado(estado);
        return juego;
    }
}