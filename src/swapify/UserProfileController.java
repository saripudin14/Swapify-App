package swapify;

import java.io.File;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class UserProfileController {

    // --- FXML Fields Diperbarui ---
    @FXML private Circle profileImageCircle;
    @FXML private Label profileInitialLabel; // Ditambahkan
    @FXML private Label namaLengkapLabel;
    @FXML private Label emailLabel;
    @FXML private TilePane userItemsPane;
    @FXML private Button backButton;

    private ItemDAO itemDAO;
    private User viewedUser;

    public UserProfileController() {
        itemDAO = new ItemDAO();
    }

    public void initData(User user) {
        this.viewedUser = user;
        if (viewedUser != null) {
            namaLengkapLabel.setText(viewedUser.getNama());
            emailLabel.setText(viewedUser.getEmail());
            setupProfileAvatar(); // Panggil metode baru
            loadUserItems(viewedUser.getId());
        }
    }

    // --- METODE LAMA loadProfileImage DIGANTI DENGAN INI ---
    private void setupProfileAvatar() {
        String imagePath = viewedUser.getProfileImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageCircle.setFill(new ImagePattern(image));
                profileInitialLabel.setVisible(false);
                return;
            }
        }
        displayInitial();
    }
    
    // --- METODE BARU UNTUK MENAMPILKAN INISIAL ---
    private void displayInitial() {
        profileImageCircle.setFill(Color.web("#6c757d")); // Warna abu-abu
        profileInitialLabel.setVisible(true);
        if (viewedUser.getNama() != null && !viewedUser.getNama().isEmpty()) {
            profileInitialLabel.setText(viewedUser.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
        }
    }


    @FXML
    private void handleBackButtonAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void loadUserItems(int userId) {
        ObservableList<Item> userItems = itemDAO.getItemsByUserId(userId);
        userItemsPane.getChildren().clear();

        for (Item item : userItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCard.fxml"));
                Parent itemCardNode = loader.load();

                ItemCardController itemCardController = loader.getController();
                itemCardController.setData(item);
                
                userItemsPane.getChildren().add(itemCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}