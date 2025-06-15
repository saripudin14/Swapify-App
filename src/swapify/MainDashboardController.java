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

    public MainDashboardController() {
        itemDAO = new ItemDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadItems();
    }
    
    @FXML
    private void handleProfileAction() {
        System.out.println("Tombol 'Profil' diklik!");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText("Fitur untuk melihat profil akan dibuat selanjutnya.");
        alert.showAndWait();
    }

    // --- METODE INI YANG DIPERBARUI ---
    @FXML
    private void handleTambahBarangAction() {
        try {
            // 1. Memuat file FXML untuk jendela upload
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UploadItemView.fxml"));
            Parent root = loader.load();

            // 2. Membuat stage (jendela) baru untuk form upload
            Stage uploadStage = new Stage();
            uploadStage.setTitle("Unggah Barang Baru");
            uploadStage.setScene(new Scene(root));
            
            // 3. Menampilkan jendela dan menunggu sampai jendela tersebut ditutup
            uploadStage.showAndWait();
            
            // 4. Setelah jendela ditutup, muat ulang item di katalog
            //    agar barang yang baru ditambahkan langsung muncul.
            loadItems();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal Membuka Form", "Terjadi kesalahan saat mencoba membuka form unggah barang.");
        }
    }

    private void loadItems() {
        // Metode ini tidak berubah, tetap seperti sebelumnya
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
    
    // Metode bantuan untuk menampilkan notifikasi error jika diperlukan
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}