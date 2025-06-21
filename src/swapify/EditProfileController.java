package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditProfileController {

    // --- FXML Fields Baru ---
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button pilihFotoButton;

    @FXML
    private TextField namaField;
    @FXML
    private PasswordField passwordBaruField;
    @FXML
    private PasswordField konfirmasiPasswordField;
    @FXML
    private Button batalButton;
    @FXML
    private Button simpanButton;

    private UserDAO userDAO;
    private User currentUser;
    private File selectedImageFile; // Untuk menyimpan file gambar yang dipilih

    public EditProfileController() {
        userDAO = new UserDAO();
    }

    public void initData(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            namaField.setText(currentUser.getNama());
            // Tampilkan foto profil yang sudah ada
            loadProfileImage(currentUser.getProfileImagePath());
        }
    }

    // --- METODE BARU UNTUK MEMILIH FOTO ---
    @FXML
    private void handlePilihFotoAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) pilihFotoButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            // Simpan file yang dipilih dan tampilkan sebagai preview
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    // --- METODE SIMPAN DIPERBARUI TOTAL ---
    @FXML
    private void handleSimpanAction() {
        String namaBaru = namaField.getText();
        String passwordBaru = passwordBaruField.getText();
        String konfirmasiPassword = konfirmasiPasswordField.getText();

        if (namaBaru.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Nama tidak boleh kosong.");
            return;
        }

        if (!passwordBaru.isEmpty() || !konfirmasiPassword.isEmpty()) {
            if (!passwordBaru.equals(konfirmasiPassword)) {
                showAlert(Alert.AlertType.ERROR, "Kesalahan Password", "Kolom 'Kata Sandi Baru' dan 'Konfirmasi Sandi Baru' tidak cocok.");
                return;
            }
        }

        // Tentukan path gambar yang akan disimpan
        String newProfileImagePath = currentUser.getProfileImagePath(); // Path lama sebagai default
        if (selectedImageFile != null) {
            newProfileImagePath = selectedImageFile.getAbsolutePath(); // Jika ada file baru, gunakan path baru
        }
        
        // Memanggil metode updateUserProfile yang sudah kita perbarui di UserDAO
        boolean success = userDAO.updateUserProfile(currentUser.getId(), namaBaru, passwordBaru, newProfileImagePath);

        if (success) {
            // Perbarui juga objek currentUser di sesi
            currentUser.setNama(namaBaru);
            currentUser.setProfileImagePath(newProfileImagePath);
            if (passwordBaru != null && !passwordBaru.isEmpty()) {
                currentUser.setPassword(passwordBaru);
            }
            UserSession.getInstance().setLoggedInUser(currentUser);

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Profil Anda telah berhasil diperbarui.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat memperbarui profil.");
        }
    }

    @FXML
    private void handleBatalAction() {
        closeWindow();
    }
    
    // Metode bantuan untuk memuat gambar
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) batalButton.getScene().getWindow();
        stage.close();
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}