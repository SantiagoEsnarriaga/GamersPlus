package database;

import model.Intercambio;
import model.Intercambio.Estado;
import model.Usuario;
import model.Videojuego;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IntercambioDAOImpl implements IntercambioDAO {

    @Override
    public boolean save(Intercambio intercambio) {
        String sql = "INSERT INTO Intercambios (ID_EMISOR, ID_RECEPTOR, ID_VID_OFERTADO, "
                   + "ID_VID_SOLICITADO, Fecha_Propuesta, Estado, Fecha_Resolucion, "
                   + "ID_INTERCAMBIO_ORIGINAL, ID_CONTRAPROPUESTA) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setIntercambioParameters(stmt, intercambio);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        intercambio.setId(rs.getInt(1));
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
    public boolean update(Intercambio intercambio) {
        String sql = "UPDATE Intercambios SET Estado = ?, Fecha_Resolucion = ?, "
                   + "ID_INTERCAMBIO_ORIGINAL = ?, ID_CONTRAPROPUESTA = ? WHERE ID_INT = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, intercambio.getEstado().name());
            stmt.setTimestamp(2, intercambio.getFechaResolucion() != null ? 
                new Timestamp(intercambio.getFechaResolucion().getTime()) : null);
            stmt.setObject(3, intercambio.getIdIntercambioOriginal(), Types.INTEGER);
            stmt.setObject(4, intercambio.getIdContrapropuesta(), Types.INTEGER);
            stmt.setInt(5, intercambio.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Intercambio> findByUsuario(int usuarioId) {
        List<Intercambio> intercambios = new ArrayList<>();
        String sql = "SELECT i.*, " +
                    "e.Nombre as emisor_nombre, e.Email as emisor_email, e.Contraseña as emisor_pass, " +
                    "r.Nombre as receptor_nombre, r.Email as receptor_email, r.Contraseña as receptor_pass, " +
                    "vo.Titulo as ofrecido_titulo, vo.Genero as ofrecido_genero, vo.ID_US_PROPIETARIO as ofrecido_propietario, " +
                    "vs.Titulo as solicitado_titulo, vs.Genero as solicitado_genero, vs.ID_US_PROPIETARIO as solicitado_propietario " +
                    "FROM Intercambios i " +
                    "JOIN Usuarios e ON i.ID_EMISOR = e.ID_US " +
                    "JOIN Usuarios r ON i.ID_RECEPTOR = r.ID_US " +
                    "JOIN Videojuegos vo ON i.ID_VID_OFERTADO = vo.ID_VID " +
                    "JOIN Videojuegos vs ON i.ID_VID_SOLICITADO = vs.ID_VID " +
                    "WHERE i.ID_EMISOR = ? OR i.ID_RECEPTOR = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    intercambios.add(mapIntercambioCompleto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return intercambios;
    }
    
    @Override
    public List<Intercambio> findByUsuarioAndEstado(int usuarioId, Estado estado) {
        List<Intercambio> intercambios = new ArrayList<>();
        String sql = "SELECT i.*, " +
                    "e.Nombre as emisor_nombre, e.Email as emisor_email, e.Contraseña as emisor_pass, " +
                    "r.Nombre as receptor_nombre, r.Email as receptor_email, r.Contraseña as receptor_pass, " +
                    "vo.Titulo as ofrecido_titulo, vo.Genero as ofrecido_genero, vo.ID_US_PROPIETARIO as ofrecido_propietario, " +
                    "vs.Titulo as solicitado_titulo, vs.Genero as solicitado_genero, vs.ID_US_PROPIETARIO as solicitado_propietario " +
                    "FROM Intercambios i " +
                    "JOIN Usuarios e ON i.ID_EMISOR = e.ID_US " +
                    "JOIN Usuarios r ON i.ID_RECEPTOR = r.ID_US " +
                    "JOIN Videojuegos vo ON i.ID_VID_OFERTADO = vo.ID_VID " +
                    "JOIN Videojuegos vs ON i.ID_VID_SOLICITADO = vs.ID_VID " +
                    "WHERE (i.ID_EMISOR = ? OR i.ID_RECEPTOR = ?) " +
                    "AND i.Estado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setString(3, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    intercambios.add(mapIntercambioCompleto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return intercambios;
    }
    
    @Override
    public Intercambio findById(int id) {
        String sql = "SELECT i.*, " +
                    "e.Nombre as emisor_nombre, e.Email as emisor_email, e.Contraseña as emisor_pass, " +
                    "r.Nombre as receptor_nombre, r.Email as receptor_email, r.Contraseña as receptor_pass, " +
                    "vo.Titulo as ofrecido_titulo, vo.Genero as ofrecido_genero, vo.ID_US_PROPIETARIO as ofrecido_propietario, " +
                    "vs.Titulo as solicitado_titulo, vs.Genero as solicitado_genero, vs.ID_US_PROPIETARIO as solicitado_propietario " +
                    "FROM Intercambios i " +
                    "JOIN Usuarios e ON i.ID_EMISOR = e.ID_US " +
                    "JOIN Usuarios r ON i.ID_RECEPTOR = r.ID_US " +
                    "JOIN Videojuegos vo ON i.ID_VID_OFERTADO = vo.ID_VID " +
                    "JOIN Videojuegos vs ON i.ID_VID_SOLICITADO = vs.ID_VID " +
                    "WHERE i.ID_INT = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapIntercambioCompleto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setIntercambioParameters(PreparedStatement stmt, Intercambio intercambio) throws SQLException {
        stmt.setInt(1, intercambio.getEmisor().getId());
        stmt.setInt(2, intercambio.getReceptor().getId());
        stmt.setInt(3, intercambio.getJuegoOfrecido().getId());
        stmt.setInt(4, intercambio.getJuegoSolicitado().getId());
        stmt.setTimestamp(5, new Timestamp(intercambio.getFechaPropuesta().getTime()));
        stmt.setString(6, intercambio.getEstado().name());
        stmt.setTimestamp(7, intercambio.getFechaResolucion() != null ? 
            new Timestamp(intercambio.getFechaResolucion().getTime()) : null);
        stmt.setObject(8, intercambio.getIdIntercambioOriginal(), Types.INTEGER);
        stmt.setObject(9, intercambio.getIdContrapropuesta(), Types.INTEGER);
    }


    private Intercambio mapIntercambioCompleto(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_INT");
        

        java.util.Date fechaPropuesta = new java.util.Date(rs.getTimestamp("Fecha_Propuesta").getTime());
        Estado estado = Estado.valueOf(rs.getString("Estado"));
        
        java.util.Date fechaResolucion = null;
        Timestamp timestampResolucion = rs.getTimestamp("Fecha_Resolucion");
        if (timestampResolucion != null) {
            fechaResolucion = new java.util.Date(timestampResolucion.getTime());
        }
        
        Integer idIntercambioOriginal = (Integer) rs.getObject("ID_INTERCAMBIO_ORIGINAL");
        Integer idContrapropuesta = (Integer) rs.getObject("ID_CONTRAPROPUESTA");

        
        Usuario emisor = new Usuario(
            rs.getInt("ID_EMISOR"), 
            rs.getString("emisor_nombre"), 
            rs.getString("emisor_email"),
            rs.getString("emisor_pass")
        );
        
        Usuario receptor = new Usuario(
            rs.getInt("ID_RECEPTOR"), 
            rs.getString("receptor_nombre"), 
            rs.getString("receptor_email"),
            rs.getString("receptor_pass")
        );
        
        Videojuego juegoOfrecido = new Videojuego(
            rs.getInt("ID_VID_OFERTADO"), 
            rs.getString("ofrecido_titulo"), 
            rs.getString("ofrecido_genero"), 
            rs.getInt("ofrecido_propietario")
        );
        
        Videojuego juegoSolicitado = new Videojuego(
            rs.getInt("ID_VID_SOLICITADO"), 
            rs.getString("solicitado_titulo"), 
            rs.getString("solicitado_genero"), 
            rs.getInt("solicitado_propietario")
        );

        return new Intercambio(
            id,
            emisor,
            receptor,
            juegoOfrecido,
            juegoSolicitado,
            fechaPropuesta,
            estado,
            fechaResolucion,
            idIntercambioOriginal,
            idContrapropuesta
        );
    }
}