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
    private UserDAO userDAO; // Tambahkan UserDAO

    public MainDashboardController() {
        itemDAO = new ItemDAO();
        userDAO = new UserDAO(); // Inisialisasi UserDAO
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadItems();
    }
    
    // --- METODE INI YANG KITA PERBARUI ---
    @FXML
    private void handleProfileAction() {
        // PENTING: Untuk sekarang, kita hardcode ID user yang sedang login.
        // Di aplikasi nyata, ini akan didapat setelah proses login.
        int loggedInUserId = 1; 

        User user = userDAO.getUserById(loggedInUserId);

        if (user != null) {
            try {
                // 1. Memuat FXML halaman profil
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();

                // 2. Mengambil controller dari halaman profil yang baru dimuat
                ProfileController profileController = loader.getController();
                
                // 3. Mengirim data 'user' ke ProfileController
                profileController.initData(user);

                // 4. Menampilkan jendela profil
                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + user.getNama());
                profileStage.setScene(new Scene(root));
                profileStage.show();

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