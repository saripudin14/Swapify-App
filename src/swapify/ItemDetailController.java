package swapify;

import java.io.File;
import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ItemDetailController {

    // Komponen FXML
    @FXML private ImageView itemImageView;
    @FXML private Label namaBarangLabel;
    @FXML private Label deskripsiArea;
    @FXML private Label uploaderNameLabel;
    @FXML private Label tanggalUnggahLabel;
    @FXML private Button tutupButton;

    // Label untuk detail tambahan
    @FXML private Label kategoriIconLabel;
    @FXML private Label kategoriTextLabel;
    @FXML private Label jenisTransaksiIconLabel;
    @FXML private Label jenisTransaksiTextLabel;
    @FXML private Label statusIconLabel;
    @FXML private Label statusTextLabel;

    // Metode untuk mengisi data ke tampilan
    public void initData(Item item) {
        if (item != null) {
            namaBarangLabel.setText(item.getNamaBarang());
            deskripsiArea.setText(item.getDeskripsi());

            // --- KATEGORI
            kategoriIconLabel.setText("🏷️");
            kategoriTextLabel.setText("Kategori : " + item.getKategori());
            if (kategoriIconLabel.getParent() instanceof HBox kategoriBox) {
                kategoriBox.getStyleClass().add("tag-transaksi-tukar");
            }

            // --- JENIS TRANSAKSI
            jenisTransaksiTextLabel.setText("Jenis Transaksi : " + item.getJenisTransaksi());
            if (jenisTransaksiIconLabel.getParent() instanceof HBox jenisTransaksiBox) {
                if ("Donasi".equals(item.getJenisTransaksi())) {
                    jenisTransaksiIconLabel.setText("❤");
                    jenisTransaksiBox.getStyleClass().add("tag-transaksi-donasi");
                } else {
                    jenisTransaksiIconLabel.setText("⇄");
                    jenisTransaksiBox.getStyleClass().add("tag-transaksi-tukar");
                }
            }

            // --- STATUS
            statusTextLabel.setText("Status : " + item.getStatus());
            if (statusIconLabel.getParent() instanceof HBox statusBox) {
                if ("Tersedia".equals(item.getStatus())) {
                    statusIconLabel.setText("●");
                    statusBox.getStyleClass().add("tag-status-tersedia");
                } else {
                    statusBox.setVisible(false);
                    statusBox.setManaged(false);
                }
            }

            // --- UPLOADER & TANGGAL
            uploaderNameLabel.setText("Oleh : " + item.getNamaUploader());
            if (item.getCreatedAt() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                tanggalUnggahLabel.setText("Diunggah : " + formatter.format(item.getCreatedAt()));
            } else {
                tanggalUnggahLabel.setText("Diunggah : -");
            }

            // --- GAMBAR
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

    // Tombol tutup
    @FXML
    private void handleTutupAction() {
        Stage stage = (Stage) tutupButton.getScene().getWindow();
        stage.close();
    }
}
