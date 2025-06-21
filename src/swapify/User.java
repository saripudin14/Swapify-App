package swapify;

public class User {
    private int id;
    private String nama;
    private String email;
    private String password;
    private String profileImagePath; // <-- Field baru untuk path foto profil

    // Constructor kosong
    public User() {
    }

    // Constructor diperbarui untuk menerima profileImagePath
    public User(int id, String nama, String email, String password, String profileImagePath) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }
    
    // Constructor lama untuk kompatibilitas (opsional)
    public User(int id, String nama, String email, String password) {
        this(id, nama, email, password, null);
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

    // --- Getter dan Setter untuk field baru ---
    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}