package swapify;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ProfileController {

    @FXML private Button logoutButton;
    @FXML private Label namaLengkapLabel;
    @FXML private Label emailLabel;
    @FXML private Button editProfileButton;
    @FXML private Button tambahBarangButton;
    @FXML private TilePane myItemsPane;
    @FXML private Button backButton;
    @FXML private Button chatButton;

    // --- FXML Fields Diperbarui ---
    @FXML private Circle profileCircle;
    @FXML private Label profileInitialLabel;


    private UserDAO userDAO;
    private ItemDAO itemDAO; // Deklarasi sudah benar
    private User currentUser;
    private MainDashboardController dashboardController;

    public ProfileController() {
        userDAO = new UserDAO();
        // --- PERBAIKAN DI SINI ---
        // Sebelumnya: itemDAO = new itemDAO(); (Salah)
        // Seharusnya:
        itemDAO = new ItemDAO(); // (Benar, dengan 'I' besar)
    }

    public void initData(User user, MainDashboardController controller) {
        this.currentUser = user;
        this.dashboardController = controller;
        if (currentUser != null) {
            namaLengkapLabel.setText(currentUser.getNama());
            emailLabel.setText(currentUser.getEmail());
            setupProfileAvatar(); // Panggil metode baru
            loadUserItems(currentUser.getId());
        }
    }

    private void setupProfileAvatar() {
        String imagePath = currentUser.getProfileImagePath();

        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                javafx.scene.image.Image profileImage = new javafx.scene.image.Image(imageFile.toURI().toString());
                profileCircle.setFill(new ImagePattern(profileImage));
                profileInitialLabel.setVisible(false);
                return;
            }
        }
        
        displayInitial();
    }
    
    private void displayInitial() {
        profileCircle.setFill(Color.web("#495057"));
        profileInitialLabel.setVisible(true);
        if (currentUser.getNama() != null && !currentUser.getNama().isEmpty()) {
            profileInitialLabel.setText(currentUser.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
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

            this.currentUser = UserSession.getInstance().getLoggedInUser();
            namaLengkapLabel.setText(currentUser.getNama());
            setupProfileAvatar();

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
                Stage currentStage = (Stage) backButton.getScene().getWindow();
                currentStage.close();
                
                if (dashboardController != null) {
                    Stage dashboardStage = (Stage) dashboardController.getProfileCircle().getScene().getWindow();
                    dashboardStage.close();
                }

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