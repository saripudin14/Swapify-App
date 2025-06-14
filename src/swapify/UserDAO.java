package swapify;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
	public boolean registerUser(String nama, String email, String password) {
        String sql = "INSERT INTO users (nama, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Seharusnya password di-hash, tapi kita buat simpel dulu
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Error code 1062 adalah untuk duplicate entry (email sudah ada)
            if (e.getErrorCode() == 1062) {
                System.out.println("Error: Email sudah terdaftar.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Jika ada baris data, berarti login berhasil
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
