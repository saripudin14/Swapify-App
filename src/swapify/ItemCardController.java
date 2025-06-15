package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemCardController {

    @FXML
    private ImageView itemImageView;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label itemCategoryLabel;
    @FXML
    private Label itemTypeLabel;
    
    // --- 1. Menambahkan variabel FXML untuk label nama pengunggah ---
    @FXML
    private Label uploaderNameLabel;
    
    @FXML
    private Button detailButton;

    private Item currentItem;

    public void setData(Item item) {
        this.currentItem = item;
        
        itemNameLabel.setText(item.getNamaBarang());
        itemCategoryLabel.setText(item.getKategori());
        itemTypeLabel.setText(item.getJenisTransaksi());

        // --- 2. Mengatur teks untuk label nama pengunggah ---
        uploaderNameLabel.setText("oleh: " + item.getNamaUploader());

        // Mengatur warna label berdasarkan jenis transaksi
        if ("Donasi".equals(item.getJenisTransaksi())) {
            itemTypeLabel.setStyle("-fx-background-color: #DCEDC8; -fx-text-fill: #33691E; -fx-background-radius: 5; -fx-padding: 2 8 2 8;");
        } else { // Tukar
            itemTypeLabel.setStyle("-fx-background-color: #BBDEFB; -fx-text-fill: #0D47A1; -fx-background-radius: 5; -fx-padding: 2 8 2 8;");
        }

        // Memuat gambar
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

    @FXML
    private void handleDetailAction() {
        System.out.println("Tombol detail diklik untuk item: " + currentItem.getNamaBarang());
    }
}