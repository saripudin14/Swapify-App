package swapify;

import java.sql.Timestamp;

public class Item {
    private int id;
    private String namaBarang;
    private String deskripsi;
    private String kategori;
    private String jenisTransaksi;
    private String status;
    private String gambarPath;
    private int userId;
    private Timestamp createdAt;
    
    // 1. Menambahkan variabel baru untuk nama pengunggah
    private String namaUploader;

    // 2. Memperbarui constructor untuk menerima parameter 'namaUploader'
    public Item(int id, String namaBarang, String deskripsi, String kategori, String jenisTransaksi, String status, String gambarPath, int userId, Timestamp createdAt, String namaUploader) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.jenisTransaksi = jenisTransaksi;
        this.status = status;
        this.gambarPath = gambarPath;
        this.userId = userId;
        this.createdAt = createdAt;
        this.namaUploader = namaUploader; //<- Parameter baru ditambahkan di sini
    }

    // --- Getters dan Setters yang sudah ada ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getJenisTransaksi() { return jenisTransaksi; }
    public void setJenisTransaksi(String jenisTransaksi) { this.jenisTransaksi = jenisTransaksi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGambarPath() { return gambarPath; }
    public void setGambarPath(String gambarPath) { this.gambarPath = gambarPath; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // 3. Menambahkan Getter dan Setter baru untuk namaUploader
    public String getNamaUploader() {
        return namaUploader;
    }

    public void setNamaUploader(String namaUploader) {
        this.namaUploader = namaUploader;
    }
}