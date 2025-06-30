package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListaDeseadosDAOImpl implements ListaDeseadosDAO {

    @Override
    public List<Integer> getVideojuegosIds(int listaId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT ID_VID FROM ListaVideojuegos WHERE ID_LISTA = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("ID_VID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    @Override
    public boolean addVideojuegoToLista(int listaId, int videojuegoId) {
        String sql = "INSERT INTO ListaVideojuegos (ID_LISTA, ID_VID) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listaId);
            stmt.setInt(2, videojuegoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeVideojuegoFromLista(int listaId, int videojuegoId) {
        String sql = "DELETE FROM ListaVideojuegos WHERE ID_LISTA = ? AND ID_VID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listaId);
            stmt.setInt(2, videojuegoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}