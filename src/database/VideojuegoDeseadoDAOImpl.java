package database;

import model.VideojuegoDeseado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideojuegoDeseadoDAOImpl implements VideojuegoDeseadoDAO {

    @Override
    public boolean agregarAListaDeseados(int usuarioId, String titulo, String genero, String plataforma, String descripcion) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Insertar o obtener el videojuego deseado
            String sqlVideojuego = "INSERT INTO VideojuegosDeseados (Titulo, Genero, Plataforma, Descripcion) " +
                                  "VALUES (?, ?, ?, ?) " +
                                  "ON DUPLICATE KEY UPDATE ID_VID_DESEADO = LAST_INSERT_ID(ID_VID_DESEADO)";
            
            pstmt1 = conn.prepareStatement(sqlVideojuego, Statement.RETURN_GENERATED_KEYS);
            pstmt1.setString(1, titulo);
            pstmt1.setString(2, genero);
            pstmt1.setString(3, plataforma != null ? plataforma : "");
            pstmt1.setString(4, descripcion != null ? descripcion : "");
            
            pstmt1.executeUpdate();
            
            // Obtener el ID del videojuego deseado
            int idVideojuegoDeseado;
            rs = pstmt1.getGeneratedKeys();
            if (rs.next()) {
                idVideojuegoDeseado = rs.getInt(1);
            } else {
                // Si no se generó clave, obtener el ID existente
                String sqlGetId = "SELECT ID_VID_DESEADO FROM VideojuegosDeseados WHERE Titulo = ? AND Genero = ? AND Plataforma = ?";
                PreparedStatement pstmtGet = conn.prepareStatement(sqlGetId);
                pstmtGet.setString(1, titulo);
                pstmtGet.setString(2, genero);
                pstmtGet.setString(3, plataforma != null ? plataforma : "");
                ResultSet rsGet = pstmtGet.executeQuery();
                if (rsGet.next()) {
                    idVideojuegoDeseado = rsGet.getInt("ID_VID_DESEADO");
                } else {
                    throw new SQLException("No se pudo obtener el ID del videojuego deseado");
                }
                rsGet.close();
                pstmtGet.close();
            }
            
            // 2. Agregar a la lista de deseados del usuario
            String sqlLista = "INSERT INTO ListasDeseados (ID_USUARIO, ID_VID_DESEADO) VALUES (?, ?)";
            pstmt2 = conn.prepareStatement(sqlLista);
            pstmt2.setInt(1, usuarioId);
            pstmt2.setInt(2, idVideojuegoDeseado);
            
            pstmt2.executeUpdate();
            
            conn.commit(); // Confirmar transacción
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Revertir cambios en caso de error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            // Manejar error de duplicado (usuario ya tiene este juego en su lista)
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                System.out.println("El videojuego ya está en tu lista de deseados");
                return false;
            }
            
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) rs.close();
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<VideojuegoDeseado> obtenerListaDeseados(int usuarioId) {
        List<VideojuegoDeseado> listaDeseados = new ArrayList<>();
        String sql = "SELECT vd.*, ld.Fecha_Agregado FROM VideojuegosDeseados vd " +
                    "JOIN ListasDeseados ld ON vd.ID_VID_DESEADO = ld.ID_VID_DESEADO " +
                    "WHERE ld.ID_USUARIO = ? " +
                    "ORDER BY ld.Fecha_Agregado DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaDeseados.add(mapVideojuegoDeseado(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaDeseados;
    }

    @Override
    public boolean eliminarDeListaDeseados(int usuarioId, int idVideojuegoDeseado) {
        String sql = "DELETE FROM ListasDeseados WHERE ID_USUARIO = ? AND ID_VID_DESEADO = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, idVideojuegoDeseado);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public VideojuegoDeseado findById(int id) {
        String sql = "SELECT * FROM VideojuegosDeseados WHERE ID_VID_DESEADO = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapVideojuegoDeseado(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<VideojuegoDeseado> buscarPorTitulo(String titulo) {
        List<VideojuegoDeseado> resultados = new ArrayList<>();
        String sql = "SELECT * FROM VideojuegosDeseados WHERE Titulo LIKE ? ORDER BY Titulo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapVideojuegoDeseado(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    /**
     * Verifica si un usuario ya tiene un videojuego en su lista de deseados
     */
    public boolean usuarioTieneJuegoDeseado(int usuarioId, int idVideojuegoDeseado) {
        String sql = "SELECT COUNT(*) FROM ListasDeseados WHERE ID_USUARIO = ? AND ID_VID_DESEADO = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, idVideojuegoDeseado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private VideojuegoDeseado mapVideojuegoDeseado(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_VID_DESEADO");
        String titulo = rs.getString("Titulo");
        String genero = rs.getString("Genero");
        String plataforma = rs.getString("Plataforma");
        String descripcion = rs.getString("Descripcion");
        
        return new VideojuegoDeseado(id, titulo, genero, plataforma, descripcion);
    }
}