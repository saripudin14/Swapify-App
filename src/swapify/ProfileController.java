package swapify;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label namaLengkapLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button editProfileButton;
    @FXML
    private Button tambahBarangButton;
    @FXML
    private TilePane myItemsPane;
    
    // --- FIELD BARU UNTUK TOMBOL KEMBALI ---
    @FXML
    private Button backButton;

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
            loadUserItems(currentUser.getId());
        }
    }

    // --- METODE BARU UNTUK TOMBOL KEMBALI ---
    @FXML
    private void handleBackButtonAction() {
        // Mengambil stage (jendela) dari tombol dan menutupnya
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText("Fitur Edit Profil akan dibuat selanjutnya!");
        alert.showAndWait();
    }
}