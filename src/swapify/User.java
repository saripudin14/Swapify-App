package swapify;

public class User {
    private int id;
    private String nama;
    private String email;
    private String password; // Sebaiknya tidak menyimpan password di sini dalam aplikasi nyata, tapi untuk sekarang tidak apa-apa

    // Constructor kosong (opsional, tapi baik untuk dimiliki)
    public User() {
    }

    // Constructor yang kita butuhkan untuk mengambil data dari database
    public User(int id, String nama, String email, String password) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}