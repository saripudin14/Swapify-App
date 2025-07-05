package swapify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ItemDAO {

    /**
     * Metode baru yang lebih fleksibel untuk mencari dan memfilter barang.
     * @param keyword Kata kunci untuk mencari nama barang (bisa null atau kosong).
     * @param category Kategori untuk filter (bisa null atau "Semua Kategori").
     * @return Daftar barang yang sesuai dengan kriteria.
     */
    public ObservableList<Item> getFilteredItems(String keyword, String category) {
        ObservableList<Item> itemList = FXCollections.observableArrayList();
        
        // Query dasar untuk mengambil semua item yang tersedia
        StringBuilder sql = new StringBuilder(
            "SELECT i.*, u.nama as nama_uploader FROM items i " +
            "JOIN users u ON i.user_id = u.id WHERE i.status = 'Tersedia' "
        );

        // Menambahkan kondisi filter secara dinamis
        boolean keywordExists = keyword != null && !keyword.trim().isEmpty();
        boolean categoryExists = category != null && !category.equals("Semua Kategori");

        if (keywordExists) {
            sql.append("AND i.nama_barang LIKE ? ");
        }
        if (categoryExists) {
            sql.append("AND i.kategori = ? ");
        }
        
        sql.append("ORDER BY i.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keywordExists) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            if (categoryExists) {
                pstmt.setString(paramIndex++, category);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("id"),
                    rs.getString("nama_barang"),
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    rs.getString("jenis_transaksi"),
                    rs.getString("status"),
                    rs.getString("gambar_path"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at"),
                    rs.getString("nama_uploader")
                );
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }
    
    // ... sisa metode di ItemDAO (addItem, getItemsByUserId, dll.) tetap sama ...
    // Pastikan untuk tidak menghapus metode lainnya.
    // Kode ini hanya menggantikan metode getAllAvailableItems().
    // Untuk lebih mudah, kamu bisa hapus metode getAllAvailableItems() yang lama 
    // dan tambahkan metode getFilteredItems() di atasnya.
    
    // Metode lama yang bisa kamu hapus atau ganti
    public ObservableList<Item> getAllAvailableItems() {
        return getFilteredItems("", "Semua Kategori"); // Sekarang memanggil metode baru
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
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODE INI DIMODIFIKASI ---
    public ObservableList<Item> getItemsByUserId(int userId) {
        ObservableList<Item> itemList = FXCollections.observableArrayList();
        // Menambahkan filter "AND i.status = 'Tersedia'"
        String sql = "SELECT i.*, u.nama as nama_uploader FROM items i JOIN users u ON i.user_id = u.id WHERE i.user_id = ? AND i.status = 'Tersedia' ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                 Item item = new Item(
                    rs.getInt("id"),
                    rs.getString("nama_barang"),
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    rs.getString("jenis_transaksi"),
                    rs.getString("status"),
                    rs.getString("gambar_path"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at"),
                    rs.getString("nama_uploader")
                );
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    public boolean deleteItemById(int itemId) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateItem(int itemId, String namaBarang, String deskripsi, String kategori, String jenisTransaksi, String gambarPath) {
        String sql = "UPDATE items SET nama_barang = ?, deskripsi = ?, kategori = ?, jenis_transaksi = ?, gambar_path = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaBarang);
            pstmt.setString(2, deskripsi);
            pstmt.setString(3, kategori);
            pstmt.setString(4, jenisTransaksi);
            pstmt.setString(5, gambarPath);
            pstmt.setInt(6, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Item getItemById(int itemId) {
        String sql = "SELECT i.*, u.nama as nama_uploader FROM items i JOIN users u ON i.user_id = u.id WHERE i.id = ?";
        Item item = null;
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                item = new Item(
                    rs.getInt("id"),
                    rs.getString("nama_barang"),
                    rs.getString("deskripsi"),
                    rs.getString("kategori"),
                    rs.getString("jenis_transaksi"),
                    rs.getString("status"),
                    rs.getString("gambar_path"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("created_at"),
                    rs.getString("nama_uploader")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    // --- METODE BARU UNTUK MENGUBAH STATUS BARANG ---
    public boolean updateItemStatus(int itemId, String newStatus) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}