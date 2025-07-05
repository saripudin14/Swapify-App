package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditProfileController {

    // --- FXML Fields Diperbarui ---
    @FXML private Circle profileCircle;
    @FXML private Label profileInitialLabel;
    @FXML private Button pilihFotoButton;
    @FXML private TextField namaField;
    @FXML private PasswordField passwordBaruField;
    @FXML private PasswordField konfirmasiPasswordField;
    @FXML private Button batalButton;
    @FXML private Button simpanButton;

    private UserDAO userDAO;
    private User currentUser;
    private File selectedImageFile;

    public EditProfileController() {
        userDAO = new UserDAO();
    }

    public void initData(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            namaField.setText(currentUser.getNama());
            setupProfileAvatar(); // Panggil metode untuk setup avatar
        }
    }

    private void setupProfileAvatar() {
        String imagePath = currentUser.getProfileImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileCircle.setFill(new ImagePattern(image));
                profileInitialLabel.setVisible(false);
                return;
            }
        }
        displayInitial();
    }

    private void displayInitial() {
        profileCircle.setFill(Color.web("#495057")); // Warna latar default
        profileInitialLabel.setVisible(true);
        if (currentUser.getNama() != null && !currentUser.getNama().isEmpty()) {
            profileInitialLabel.setText(currentUser.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
        }
    }

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
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            // Tampilkan pratinjau di avatar
            profileCircle.setFill(new ImagePattern(image));
            profileInitialLabel.setVisible(false);
        }
    }

    @FXML
    private void handleSimpanAction() {
        String namaBaru = namaField.getText();
        String passwordBaru = passwordBaruField.getText();
        String konfirmasiPassword = konfirmasiPasswordField.getText();

        if (namaBaru.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Nama tidak boleh kosong.");
            return;
        }

        if (!passwordBaru.isEmpty() && !passwordBaru.equals(konfirmasiPassword)) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Password", "Kolom kata sandi tidak cocok.");
            return;
        }

        String newProfileImagePath = currentUser.getProfileImagePath();
        if (selectedImageFile != null) {
            newProfileImagePath = selectedImageFile.getAbsolutePath();
        }
        
        boolean success = userDAO.updateUserProfile(currentUser.getId(), namaBaru, passwordBaru, newProfileImagePath);

        if (success) {
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