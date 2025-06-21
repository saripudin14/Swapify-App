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
            pstmt.setString(3, password);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
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
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODE DIPERBARUI UNTUK MEMBACA `profile_image_path` ---
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("profile_image_path") // <-- Membaca kolom baru
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // --- METODE DIPERBARUI UNTUK MEMBACA `profile_image_path` ---
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("profile_image_path") // <-- Membaca kolom baru
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // --- METODE DIPERBARUI UNTUK MENG-UPDATE NAMA, PASSWORD, DAN FOTO PROFIL ---
    public boolean updateUserProfile(int userId, String newName, String newPassword, String profileImagePath) {
        // Membangun query SQL secara dinamis
        StringBuilder sql = new StringBuilder("UPDATE users SET nama = ?, profile_image_path = ?");
        
        boolean isPasswordChanged = newPassword != null && !newPassword.isEmpty();
        if (isPasswordChanged) {
            sql.append(", password = ?");
        }
        
        sql.append(" WHERE id = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int parameterIndex = 1;
            pstmt.setString(parameterIndex++, newName);
            pstmt.setString(parameterIndex++, profileImagePath);

            if (isPasswordChanged) {
                pstmt.setString(parameterIndex++, newPassword);
            }
            
            pstmt.setInt(parameterIndex, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}