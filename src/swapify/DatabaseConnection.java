package swapify;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	// GANTI DENGAN PENGATURAN DATABASE ANDA
    private static final String DB_URL = "jdbc:mysql://localhost:3306/swapify";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Koneksi ke database gagal!");
            e.printStackTrace();
        }
        return connection;
    }
}
