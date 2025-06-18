package swapify;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

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
    private TilePane myItemsPane;

    private UserDAO userDAO;
    private ItemDAO itemDAO;

    public ProfileController() {
        userDAO = new UserDAO();
        itemDAO = new ItemDAO();
    }
    
    public void initData(User user) {
        if (user != null) {
            namaLengkapLabel.setText(user.getNama());
            emailLabel.setText(user.getEmail());
            loadUserItems(user.getId());
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
                
                // --- INI BARIS KUNCI YANG DITAMBAHKAN ---
                // Memberikan "perintah" untuk me-refresh halaman ini jika item dihapus
                itemCardController.setOnDeleteCallback(() -> loadUserItems(userId));

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