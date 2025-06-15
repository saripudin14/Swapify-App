package swapify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ItemDAO {

    public ObservableList<Item> getAllAvailableItems() {
        ObservableList<Item> itemList = FXCollections.observableArrayList();
        String sql = "SELECT i.*, u.nama as nama_uploader FROM items i JOIN users u ON i.user_id = u.id WHERE i.status = 'Tersedia' ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String namaBarang = rs.getString("nama_barang");
                String deskripsi = rs.getString("deskripsi");
                String kategori = rs.getString("kategori");
                String jenisTransaksi = rs.getString("jenis_transaksi");
                String status = rs.getString("status");
                String gambarPath = rs.getString("gambar_path");
                int userId = rs.getInt("user_id");
                Timestamp createdAt = rs.getTimestamp("created_at");
                String namaUploader = rs.getString("nama_uploader");

                Item item = new Item(id, namaBarang, deskripsi, kategori, jenisTransaksi, status, gambarPath, userId, createdAt, namaUploader);
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }
    
    // --- METODE BARU DITAMBAHKAN DI SINI ---
    public boolean addItem(String namaBarang, String deskripsi, String kategori, String jenisTransaksi, String gambarPath, int userId) {
        String sql = "INSERT INTO items (nama_barang, deskripsi, kategori, jenis_transaksi, gambar_path, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Mengisi placeholder (?) dengan data yang sebenarnya
            pstmt.setString(1, namaBarang);
            pstmt.setString(2, deskripsi);
            pstmt.setString(3, kategori);
            pstmt.setString(4, jenisTransaksi);
            pstmt.setString(5, gambarPath);
            pstmt.setInt(6, userId);

            // Menjalankan query
            int affectedRows = pstmt.executeUpdate();

            // Mengembalikan true jika 1 baris data berhasil dimasukkan
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Mengembalikan false jika terjadi error
        }
    }
}