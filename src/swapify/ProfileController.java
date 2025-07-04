package swapify;

import java.io.File;
import java.io.IOException;
import java.util.Optional; // <-- IMPORT BARU
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // <-- IMPORT BARU
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class ProfileController {

    // --- DEKLARASI FXML BARU ---
    @FXML private Button logoutButton;
    
    // Deklarasi yang sudah ada
    @FXML private ImageView profileImageView;
    @FXML private Label namaLengkapLabel;
    @FXML private Label emailLabel;
    @FXML private Button editProfileButton;
    @FXML private Button tambahBarangButton;
    @FXML private TilePane myItemsPane;
    @FXML private Button backButton;
    @FXML private Button chatButton;

    private UserDAO userDAO;
    private ItemDAO itemDAO;
    private User currentUser;
    private MainDashboardController dashboardController;

    public ProfileController() {
        userDAO = new UserDAO();
        itemDAO = new ItemDAO();
    }

    public void initData(User user, MainDashboardController controller) {
        this.currentUser = user;
        this.dashboardController = controller;
        if (currentUser != null) {
            namaLengkapLabel.setText(currentUser.getNama());
            emailLabel.setText(currentUser.getEmail());
            loadProfileImage(currentUser.getProfileImagePath());
            loadUserItems(currentUser.getId());
        }
    }
    
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
            } else {
                System.err.println("File gambar profil tidak ditemukan di: " + imagePath);
                profileImageView.setImage(null);
            }
        } else {
            profileImageView.setImage(null);
        }
    }

    @FXML
    private void handleBackButtonAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleTambahBarangAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UploadItemView.fxml"));
            Parent root = loader.load();
            
            Stage uploadStage = new Stage();
            uploadStage.setTitle("Unggah Barang Baru");
            uploadStage.setScene(new Scene(root));
            
            uploadStage.showAndWait();
            
            loadUserItems(currentUser.getId());
            if (dashboardController != null) {
                dashboardController.refreshItems();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserItems(int userId) {
        ObservableList<Item> userItems = itemDAO.getItemsByUserId(userId);
        myItemsPane.getChildren().clear();

        for (Item item : userItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCard.fxml"));
                Parent itemCardNode = loader.load();

                ItemCardController itemCardController = loader.getController();
                itemCardController.setData(item);
                itemCardController.showOwnerControls();
                
                itemCardController.setOnUpdateCallback(() -> {
                    loadUserItems(userId);
                    if (dashboardController != null) {
                        dashboardController.refreshItems();
                    }
                });

                myItemsPane.getChildren().add(itemCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditProfileView.fxml"));
            Parent root = loader.load();

            EditProfileController editController = loader.getController();
            editController.initData(currentUser);

            Stage editStage = new Stage();
            editStage.setTitle("Edit Profil");
            editStage.setScene(new Scene(root));
            editStage.showAndWait();

            namaLengkapLabel.setText(currentUser.getNama());
            loadProfileImage(currentUser.getProfileImagePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBukaChatAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajuan & Chat Saya");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- LOGIKA LOGOUT SEKARANG ADA DI SINI ---
    @FXML
    private void handleLogoutAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText("Anda akan keluar dari sesi ini.");
        alert.setContentText("Apakah Anda yakin ingin logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.getInstance().clearSession();
            try {
                // Mendapatkan stage dari tombol back/logout untuk menutup jendela profil
                Stage currentStage = (Stage) backButton.getScene().getWindow();
                currentStage.close();
                
                // Menutup juga dashboard yang mungkin masih terbuka di belakang
                if (dashboardController != null) {
                    Stage dashboardStage = (Stage) dashboardController.getProfileCircle().getScene().getWindow();
                    dashboardStage.close();
                }

                // Membuka kembali halaman login
                Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Swapify - Login");
                loginStage.setScene(new Scene(root));
                loginStage.setMaximized(true);
                loginStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}