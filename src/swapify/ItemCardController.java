package swapify;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ItemCardController {

    // ... (FXML fields tetap sama) ...
    @FXML private ImageView itemImageView;
    @FXML private Label itemNameLabel;
    @FXML private Label itemCategoryLabel;
    @FXML private Label uploaderNameLabel;
    @FXML private Label itemTypeLabel;
    @FXML private Button detailButton;
    @FXML private Button deleteButton;
    @FXML private Button editButton;

    private Item currentItem;
    private ItemDAO itemDAO;
    
    // Mengubah nama callback agar lebih umum (bisa untuk hapus/edit)
    private Runnable updateCallback;

    public ItemCardController() {
        itemDAO = new ItemDAO();
    }

    public void setData(Item item) {
        // ... (Isi metode ini tetap sama) ...
        this.currentItem = item;
        itemNameLabel.setText(item.getNamaBarang());
        itemCategoryLabel.setText(item.getKategori());
        itemTypeLabel.setText(item.getJenisTransaksi());
        uploaderNameLabel.setText("oleh: " + item.getNamaUploader());
        if ("Donasi".equals(item.getJenisTransaksi())) {
            itemTypeLabel.setStyle("-fx-background-color: #DCEDC8; -fx-text-fill: #33691E; -fx-background-radius: 5; -fx-padding: 2 8 2 8;");
        } else {
            itemTypeLabel.setStyle("-fx-background-color: #BBDEFB; -fx-text-fill: #0D47A1; -fx-background-radius: 5; -fx-padding: 2 8 2 8;");
        }
        if (item.getGambarPath() != null && !item.getGambarPath().isEmpty()) {
            try {
                File file = new File(item.getGambarPath());
                Image image = new Image(file.toURI().toString());
                itemImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Gambar tidak dapat dimuat: " + item.getGambarPath());
            }
        }
    }

    public void showOwnerControls() {
        // ... (Isi metode ini tetap sama) ...
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        editButton.setVisible(true);
        editButton.setManaged(true);
    }
    
    // Mengubah nama metode callback
    public void setOnUpdateCallback(Runnable callback) {
        this.updateCallback = callback;
    }

    @FXML
    private void handleDetailAction() {
        System.out.println("Tombol detail diklik untuk item: " + currentItem.getNamaBarang());
    }

    @FXML
    private void handleDeleteAction() {
        // ... (Isi metode ini tetap sama, hanya memanggil updateCallback) ...
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Konfirmasi Hapus");
        confirmationAlert.setHeaderText("Hapus Barang: " + currentItem.getNamaBarang());
        confirmationAlert.setContentText("Apakah Anda yakin ingin menghapus barang ini secara permanen?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = itemDAO.deleteItemById(currentItem.getId());
            if (success) {
                if (updateCallback != null) {
                    updateCallback.run(); // Menggunakan nama callback baru
                }
            } else {
                // ... (Alert error tetap sama) ...
            }
        }
    }

    // --- METODE INI YANG DIPERBARUI ---
    @FXML
    private void handleEditAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UploadItemView.fxml"));
            Parent root = loader.load();

            // Ambil controller dari form yang akan dibuka
            UploadItemController uploadController = loader.getController();
            // Kirim data item yang ada ke form tersebut
            uploadController.initData(currentItem);

            Stage editStage = new Stage();
            editStage.setTitle("Edit Barang");
            editStage.setScene(new Scene(root));
            editStage.showAndWait();
            
            // Setelah jendela edit ditutup, refresh halaman profil
            if (updateCallback != null) {
                updateCallback.run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}