package swapify;

import java.io.File;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class ItemDetailController {

    @FXML private ImageView itemImageView;
    @FXML private Label namaBarangLabel;
    @FXML private Label deskripsiArea;
    @FXML private Label uploaderNameLabel;
    @FXML private Label tanggalUnggahLabel;
    @FXML private Button tutupButton;

    // FXML untuk label nilai di dalam GridPane
    @FXML private Label kategoriTextLabel;
    @FXML private Label jenisTransaksiTextLabel;
    @FXML private Label statusTextLabel;
    
    // FXML untuk menargetkan baris status agar bisa disembunyikan
    @FXML private RowConstraints statusRow;

    public void initData(Item item) {
        if (item != null) {
            namaBarangLabel.setText(item.getNamaBarang());
            deskripsiArea.setText(item.getDeskripsi());

            // --- Atur Teks dan Style untuk Label Info ---

            // KATEGORI
            kategoriTextLabel.setText(item.getKategori());
            // Terapkan style baru kita dari styles.css
            kategoriTextLabel.getStyleClass().setAll("info-label", "info-label-tukar");

            // JENIS TRANSAKSI
            jenisTransaksiTextLabel.setText(item.getJenisTransaksi());
            if ("Donasi".equals(item.getJenisTransaksi())) {
                jenisTransaksiTextLabel.getStyleClass().setAll("info-label", "info-label-donasi");
            } else {
                jenisTransaksiTextLabel.getStyleClass().setAll("info-label", "info-label-tukar");
            }

            // STATUS
            if ("Tersedia".equals(item.getStatus())) {
                statusTextLabel.setText(item.getStatus());
                statusTextLabel.getStyleClass().setAll("info-label", "info-label-tersedia");
                // Tampilkan baris status
                statusRow.setPrefHeight(-1); // Atur ke ukuran default
                statusRow.setMinHeight(10);
            } else {
                // Sembunyikan baris status sepenuhnya
                statusRow.setPrefHeight(0);
                statusRow.setMinHeight(0);
                // Sembunyikan labelnya juga untuk keamanan
                statusTextLabel.getParent().setVisible(false);
                statusTextLabel.getParent().setManaged(false);
            }
            
            // ... Sisa metode tetap sama ...
            uploaderNameLabel.setText("Oleh : " + item.getNamaUploader());
            if (item.getCreatedAt() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                tanggalUnggahLabel.setText("Diunggah pada " + formatter.format(item.getCreatedAt()));
            } else {
                tanggalUnggahLabel.setText("Diunggah : -");
            }

            String imagePath = item.getGambarPath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 350, 450, true, true, true);
                    itemImageView.setImage(image);
                }
            }
        }
    }

    @FXML
    private void handleTutupAction() {
        Stage stage = (Stage) tutupButton.getScene().getWindow();
        stage.close();
    }
}