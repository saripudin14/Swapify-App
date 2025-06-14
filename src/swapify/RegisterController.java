package swapify;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;
    
    @FXML
    private Hyperlink loginLink;

    private UserDAO userDAO;

    public RegisterController() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleRegisterButtonAction() {
        String nama = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Semua kolom wajib diisi");
            return;
        }

        boolean isSuccess = userDAO.registerUser(nama, email, password);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil!", "Akun Anda telah berhasil dibuat. Silakan login.");
            // Menutup jendela register setelah berhasil
            closeWindow();
        } else {
            // Pesan error spesifik sudah ditangani di dalam DAO
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Email mungkin sudah terdaftar atau terjadi kesalahan lain.");
        }
    }
    
    @FXML
    private void handleLoginLinkAction() {
        // Cukup tutup jendela register untuk kembali ke jendela login
        closeWindow();
    }
    
    private void closeWindow() {
        // Mengambil stage (jendela) dari salah satu komponen UI
        Stage stage = (Stage) registerButton.getScene().getWindow();
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