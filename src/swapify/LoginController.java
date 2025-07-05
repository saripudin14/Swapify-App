package swapify;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLoginAction() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Mohon isi email dan password.");
            return;
        }

        if (userDAO.loginUser(email, password)) {
            User loggedInUser = userDAO.getUserByEmail(email);
            UserSession.getInstance().setLoggedInUser(loggedInUser);
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Login sukses! Selamat datang, " + loggedInUser.getNama() + ".");
            
            try {
                // Menutup jendela login
                Stage loginStage = (Stage) emailField.getScene().getWindow();
                loginStage.close();

                // Membuka jendela dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainDashboardView.fxml"));
                Parent root = loader.load();
                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Swapify - Beranda");
                
                // ================================================================
                // === PERUBAHAN UTAMA YANG MEMPERBAIKI MASALAH ADA DI SINI ===
                // ================================================================
                // 1. Buat Scene baru
                Scene scene = new Scene(root);
                
                // 2. Hubungkan file "styles.css" ke Scene tersebut
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                
                // 3. Pasang Scene yang sudah memiliki style ke Jendela (Stage)
                dashboardStage.setScene(scene);
                // ================================================================
                
                dashboardStage.setMaximized(true); // Memaksimalkan jendela dashboard
                dashboardStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Email atau password salah.");
        }
    }

    @FXML
    private void handleRegisterLinkAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));
            Parent root = loader.load();
            Stage registerStage = new Stage();
            registerStage.setTitle("Registrasi Akun Baru");
            registerStage.setScene(new Scene(root));
            registerStage.setMaximized(true);
            registerStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}