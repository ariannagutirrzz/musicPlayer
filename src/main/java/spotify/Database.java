package spotify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;

    public Database() throws SQLException {
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Spotify", "postgres", "password");
    }

    // Método para obtener la conexión
    public Connection getConnection() {
        return conn;
    }

    // Cierra la conexión cuando ya no se necesite
    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}

