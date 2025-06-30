package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/GamersPlus";
    private static final String USER = "root"; // Acá va el usuario que uno tenga en MySQL, en mi caso es root
    private static final String PASSWORD = "santiagoymariano170401";  // Acá va la contraseña que uno tenga, generalmente es "root" pero en mi caso yo lo había cambiado

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}