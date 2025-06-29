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
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class UserProfileController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label namaLengkapLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private TilePane userItemsPane;
    @FXML
    private Button backButton;

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
            loadProfileImage(viewedUser.getProfileImagePath());
            loadUserItems(viewedUser.getId());
        }
    }
    
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
            } else {
                profileImageView.setImage(null); // Atur gambar default jika path tidak valid
            }
        } else {
            profileImageView.setImage(null); // Atur gambar default jika tidak ada foto
        }
    }

    @FXML
    private void handleBackButtonAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void loadUserItems(int userId) {
        // Metode ini akan menampilkan barang yang berstatus "Tersedia"
        ObservableList<Item> userItems = itemDAO.getItemsByUserId(userId);
        userItemsPane.getChildren().clear();

        for (Item item : userItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCard.fxml"));
                Parent itemCardNode = loader.load();

                ItemCardController itemCardController = loader.getController();
                itemCardController.setData(item);
                
                // Kita tidak menampilkan tombol edit/hapus di profil orang lain
                
                userItemsPane.getChildren().add(itemCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}