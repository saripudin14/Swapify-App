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
    // Tombol logout dihapus dari sini
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

    private void setupProfileAvatar() {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            String imagePath = loggedInUser.getProfileImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image profileImage = new Image(imageFile.toURI().toString(), true);

                    profileImage.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            profileCircle.setFill(new ImagePattern(profileImage));
                            profileInitialLabel.setVisible(false);
                        }
                    });
                    
                    profileImage.errorProperty().addListener((observable, oldValue, hadError) -> {
                        if (hadError) {
                            System.err.println("Gagal memuat gambar profil: " + imagePath);
                            displayInitial(loggedInUser);
                        }
                    });

                    if (profileImage.getProgress() == 1.0 && !profileImage.isError()) {
                        profileCircle.setFill(new ImagePattern(profileImage));
                        profileInitialLabel.setVisible(false);
                    }
                    return;
                }
            }
            
            displayInitial(loggedInUser);
        }
    }

    private void displayInitial(User user) {
        profileCircle.setFill(Color.web("#495057"));
        profileInitialLabel.setVisible(true);
        if (user != null && user.getNama() != null && !user.getNama().isEmpty()) {
            profileInitialLabel.setText(user.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
        }
    }

    // --- METODE handleLogoutAction() DIHAPUS DARI SINI ---

    @FXML
    private void handleProfileAction(MouseEvent event) {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();
                
                ProfileController profileController = loader.getController();
                profileController.initData(loggedInUser, this);
                
                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + loggedInUser.getNama());
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                profileStage.setScene(scene);
                
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- METODE BARU UNTUK MEMBANTU PROSES LOGOUT DARI PROFIL ---
    public Circle getProfileCircle() {
        return profileCircle;
    }
}