package swapify;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class MainDashboardController implements Initializable {

    @FXML
    private TilePane itemCatalogPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button tambahBarangButton;
    @FXML
    private Button profileButton;

    private ItemDAO itemDAO;
    private UserDAO userDAO;

    public MainDashboardController() {
        itemDAO = new ItemDAO();
        userDAO = new UserDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadItems();
    }
    
    @FXML
    private void handleProfileAction() {
        // ID pengguna yang sedang login (sementara di-hardcode)
        int loggedInUserId = 1; 

        User user = userDAO.getUserById(loggedInUserId);

        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();

                ProfileController profileController = loader.getController();
                profileController.initData(user);

                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + user.getNama());
                profileStage.setScene(new Scene(root));
                
                // --- PERUBAHAN DI SINI ---
                // Menggunakan showAndWait() agar dashboard menunggu jendela profil ditutup
                profileStage.showAndWait();

                // Setelah profil ditutup, refresh item di dashboard
                loadItems();
                // -------------------------

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Pengguna Tidak Ditemukan", "Tidak dapat menemukan data untuk pengguna dengan ID: " + loggedInUserId);
        }
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
            // Refresh item setelah jendela tambah barang ditutup
            loadItems(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}