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
    
    public boolean addItem(String namaBarang, String deskripsi, String kategori, String jenisTransaksi, String gambarPath, int userId) {
        String sql = "INSERT INTO items (nama_barang, deskripsi, kategori, jenis_transaksi, gambar_path, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, namaBarang);
            pstmt.setString(2, deskripsi);
            pstmt.setString(3, kategori);
            pstmt.setString(4, jenisTransaksi);
            pstmt.setString(5, gambarPath);
            pstmt.setInt(6, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODE BARU UNTUK HALAMAN PROFIL DITAMBAHKAN DI SINI ---
    public ObservableList<Item> getItemsByUserId(int userId) {
        ObservableList<Item> itemList = FXCollections.observableArrayList();
        // Query ini sama dengan getAllAvailableItems, tapi difilter berdasarkan user_id
        String sql = "SELECT i.*, u.nama as nama_uploader FROM items i JOIN users u ON i.user_id = u.id WHERE i.user_id = ? ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId); // Mengisi placeholder '?' dengan ID pengguna yang dicari
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String namaBarang = rs.getString("nama_barang");
                String deskripsi = rs.getString("deskripsi");
                String kategori = rs.getString("kategori");
                String jenisTransaksi = rs.getString("jenis_transaksi");
                String status = rs.getString("status");
                String gambarPath = rs.getString("gambar_path");
                String namaUploader = rs.getString("nama_uploader");
                Timestamp createdAt = rs.getTimestamp("created_at");

                Item item = new Item(id, namaBarang, deskripsi, kategori, jenisTransaksi, status, gambarPath, userId, createdAt, namaUploader);
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }
}