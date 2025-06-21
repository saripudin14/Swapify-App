package swapify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MainDashboardController implements Initializable {

    @FXML
    private TilePane itemCatalogPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button logoutButton;
    @FXML
    private StackPane profileButtonContainer;
    @FXML
    private Circle profileCircle;
    @FXML
    private Label profileInitialLabel;

    private ItemDAO itemDAO;
    private UserDAO userDAO;

    public MainDashboardController() {
        itemDAO = new ItemDAO();
        userDAO = new UserDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadItems();
        setupProfileAvatar();
    }
    
    // --- METODE INI DIPERBARUI TOTAL DENGAN LISTENER ---
    private void setupProfileAvatar() {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            String imagePath = loggedInUser.getProfileImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    // Muat gambar di background (asinkron)
                    Image profileImage = new Image(imageFile.toURI().toString(), true);

                    // Tambahkan listener untuk menunggu gambar selesai dimuat
                    profileImage.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            // Setelah gambar 100% dimuat, baru atur isian Circle
                            profileCircle.setFill(new ImagePattern(profileImage));
                            profileInitialLabel.setVisible(false);
                        }
                    });
                    
                    // Listener untuk error jika gambar gagal dimuat
                    profileImage.errorProperty().addListener((observable, oldValue, hadError) -> {
                        if (hadError) {
                            System.err.println("Gagal memuat gambar profil: " + imagePath);
                            displayInitial(loggedInUser); // Tampilkan inisial jika error
                        }
                    });

                    // Jika gambar sudah ada di cache, tampilkan langsung
                    if (profileImage.getProgress() == 1.0 && !profileImage.isError()) {
                        profileCircle.setFill(new ImagePattern(profileImage));
                        profileInitialLabel.setVisible(false);
                    }
                    return;
                }
            }
            
            // Jika tidak ada path gambar, tampilkan inisial
            displayInitial(loggedInUser);
        }
    }

    // --- METODE BANTUAN BARU UNTUK MENAMPILKAN INISIAL ---
    private void displayInitial(User user) {
        profileCircle.setFill(Color.web("#495057"));
        profileInitialLabel.setVisible(true);
        if (user != null && user.getNama() != null && !user.getNama().isEmpty()) {
            profileInitialLabel.setText(user.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
        }
    }

    @FXML
    private void handleLogoutAction() {
        // ... (Tidak ada perubahan di sini)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText("Anda akan keluar dari sesi ini.");
        alert.setContentText("Apakah Anda yakin ingin logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.getInstance().clearSession();
            try {
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
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

    @FXML
    private void handleProfileAction(MouseEvent event) {
        // ... (Tidak ada perubahan di sini)
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();
                ProfileController profileController = loader.getController();
                profileController.initData(loggedInUser, this);
                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + loggedInUser.getNama());
                profileStage.setScene(new Scene(root));
                profileStage.setMaximized(true);
                profileStage.showAndWait();
                loadItems();
                setupProfileAvatar(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Sesi Tidak Ditemukan", "Tidak dapat menemukan data pengguna yang login.");
        }
    }

    public void refreshItems() {
        loadItems();
    }

    private void loadItems() {
        // ... (Tidak ada perubahan di sini)
        ObservableList<Item> availableItems = itemDAO.getAllAvailableItems();
        itemCatalogPane.getChildren().clear();
        for (Item item : availableItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCard.fxml"));
                Parent itemCardNode = loader.load();
                ItemCardController itemCardController = loader.getController();
                itemCardController.setData(item);
                itemCatalogPane.getChildren().add(itemCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showAlert(String title, String header, String content) {
        // ... (Tidak ada perubahan di sini)
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}