package swapify;

import java.io.File;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ItemDetailController {

    @FXML
    private ImageView itemImageView;
    @FXML
    private Label namaBarangLabel;
    @FXML
    private Label jenisTransaksiLabel;
    @FXML
    private Label kategoriLabel;
    @FXML
    private Label deskripsiArea; // Tipe data sudah benar (Label)

    @FXML
    private Label uploaderNameLabel;
    @FXML
    private Label tanggalUnggahLabel;
    @FXML
    private Button tutupButton;

    public void initData(Item item) {
        if (item != null) {
            // Mengatur data teks
            namaBarangLabel.setText(item.getNamaBarang());
            jenisTransaksiLabel.setText(item.getJenisTransaksi());
            kategoriLabel.setText(item.getKategori());
            deskripsiArea.setText(item.getDeskripsi());
            uploaderNameLabel.setText(item.getNamaUploader());

            // Mengatur format tanggal
            if (item.getCreatedAt() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
                tanggalUnggahLabel.setText(formatter.format(item.getCreatedAt()));
            }

            // Memuat gambar barang
            String imagePath = item.getGambarPath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    // --- PERUBAHAN DI SINI ---
                    // Menggunakan konstruktor Image yang meminta kualitas 'smooth' (halus)
                    // new Image(url, reqWidth, reqHeight, preserveRatio, smooth, backgroundLoading)
                    Image image = new Image(imageFile.toURI().toString(), 400, 500, true, true, true);
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