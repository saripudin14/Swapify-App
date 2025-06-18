package swapify;

import java.io.File;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemCardController {

    // --- DEKLARASI FXML YANG HILANG DITAMBAHKAN DI SINI ---
    @FXML
    private ImageView itemImageView;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label itemCategoryLabel;
    @FXML
    private Label uploaderNameLabel;
    @FXML
    private Label itemTypeLabel;
    @FXML
    private Button detailButton;
    @FXML
    private Button deleteButton;
    // --- AKHIR DARI DEKLARASI FXML ---

    private Item currentItem;
    private ItemDAO itemDAO;
    private Runnable deleteCallback;

    public ItemCardController() {
        itemDAO = new ItemDAO();
    }

    public void setData(Item item) {
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
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
    }

    public void setOnDeleteCallback(Runnable callback) {
        this.deleteCallback = callback;
    }

    @FXML
    private void handleDetailAction() {
        System.out.println("Tombol detail diklik untuk item: " + currentItem.getNamaBarang());
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
            if (success) {
                if (deleteCallback != null) {
                    deleteCallback.run();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Gagal");
                errorAlert.setContentText("Gagal menghapus barang dari database.");
                errorAlert.showAndWait();
            }
        }
    }
}