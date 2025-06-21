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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle; // Import diubah dari ImageView
import javafx.stage.Stage;

public class ItemCardController {

    @FXML private Rectangle itemImageRectangle; // Tipe diubah menjadi Rectangle
    
    @FXML private Label itemNameLabel;
    @FXML private Label itemCategoryLabel;
    @FXML private Label uploaderNameLabel;
    @FXML private Label itemTypeLabel;
    @FXML private Label statusLabel;
    @FXML private Button detailButton;
    @FXML private HBox ownerControlsBox;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Button markAsDoneButton;

    private Item currentItem;
    private ItemDAO itemDAO;
    private Runnable updateCallback;

    public ItemCardController() {
        itemDAO = new ItemDAO();
    }

    public void setData(Item item) {
        this.currentItem = item;
        itemNameLabel.setText(item.getNamaBarang());
        itemCategoryLabel.setText("Kategori: " + item.getKategori());
        uploaderNameLabel.setText("oleh: " + item.getNamaUploader());
        
        applyTagStyles(item.getJenisTransaksi(), item.getStatus());
        
        String imagePath = item.getGambarPath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Menggunakan konstruktor canggih untuk kualitas terbaik
                Image image = new Image(imageFile.toURI().toString(), 220, 160, true, true, true);
                
                image.progressProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() == 1.0) {
                        itemImageRectangle.setFill(new ImagePattern(image));
                    }
                });
                if (image.getProgress() == 1.0) {
                    itemImageRectangle.setFill(new ImagePattern(image));
                }

            } else {
                itemImageRectangle.setFill(Color.web("#e0e0e0"));
            }
        } else {
            itemImageRectangle.setFill(Color.web("#e0e0e0"));
        }
    }

    private void applyTagStyles(String jenisTransaksi, String status) {
        // Atur gaya untuk label status
        if ("Tersedia".equals(status)) {
            statusLabel.setText("● " + status); // Menambahkan ikon lingkaran
            statusLabel.getStyleClass().setAll("tag", "tag-status-tersedia");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        } else {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        }
        
        // Atur gaya dan ikon untuk label jenis transaksi
        itemTypeLabel.getStyleClass().setAll("tag"); 
        if ("Donasi".equals(jenisTransaksi)) {
            itemTypeLabel.setText("❤ " + jenisTransaksi); // Menambahkan ikon hati
            itemTypeLabel.getStyleClass().add("tag-transaksi-donasi");
        } else { // Asumsikan selain itu adalah "Tukar"
            itemTypeLabel.setText("⇄ " + jenisTransaksi); // Menambahkan ikon tukar
            itemTypeLabel.getStyleClass().add("tag-transaksi-tukar");
        }
    }
    
    // ... Sisa metode tidak berubah ...
    public void showOwnerControls() {
        ownerControlsBox.setVisible(true);
        ownerControlsBox.setManaged(true);
    }
    
    public void setOnUpdateCallback(Runnable callback) {
        this.updateCallback = callback;
    }
    
    @FXML
    private void handleMarkAsDoneAction() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Konfirmasi");
        confirmation.setHeaderText("Tandai Barang sebagai Selesai");
        confirmation.setContentText("Apakah Anda yakin ingin menyelesaikan transaksi untuk barang '" + currentItem.getNamaBarang() + "'? Barang ini akan disembunyikan dari daftar.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = itemDAO.updateItemStatus(currentItem.getId(), "Selesai");
            if (success && updateCallback != null) {
                updateCallback.run(); 
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Gagal");
                errorAlert.setContentText("Gagal memperbarui status barang.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDetailAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemDetailView.fxml"));
            Parent root = loader.load();
            ItemDetailController detailController = loader.getController();
            detailController.initData(currentItem);
            Stage detailStage = new Stage();
            detailStage.setTitle("Detail Barang - " + currentItem.getNamaBarang());
            detailStage.setScene(new Scene(root));
            detailStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAction() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Konfirmasi Hapus");
        confirmationAlert.setHeaderText("Hapus Barang: " + currentItem.getNamaBarang());
        confirmationAlert.setContentText("Apakah Anda yakin ingin menghapus barang ini secara permanen?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = itemDAO.deleteItemById(currentItem.getId());
            if (success && updateCallback != null) {
                updateCallback.run();
            }
        }
    }

    @FXML
    private void handleEditAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UploadItemView.fxml"));
            Parent root = loader.load();
            UploadItemController uploadController = loader.getController();
            uploadController.initData(currentItem);
            Stage editStage = new Stage();
            editStage.setTitle("Edit Barang");
            editStage.setScene(new Scene(root));
            editStage.showAndWait();
            if (updateCallback != null) {
                updateCallback.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}