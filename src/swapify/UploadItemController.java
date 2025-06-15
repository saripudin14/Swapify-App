package swapify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadItemController implements Initializable {

    @FXML
    private TextField namaBarangField;
    @FXML
    private TextArea deskripsiArea;
    @FXML
    private ComboBox<String> kategoriComboBox;
    @FXML
    private ComboBox<String> jenisTransaksiComboBox;
    @FXML
    private Button pilihGambarButton;
    @FXML
    private Label namaFileGambarLabel;
    @FXML
    private Button unggahButton;

    private File selectedImageFile;
    private ItemDAO itemDAO;

    public UploadItemController() {
        itemDAO = new ItemDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mengisi pilihan untuk ComboBox Kategori
        kategoriComboBox.setItems(FXCollections.observableArrayList(
            "Pakaian", "Buku", "Alat Rumah Tangga", "Elektronik", "Lainnya"
        ));

        // Mengisi pilihan untuk ComboBox Jenis Transaksi
        jenisTransaksiComboBox.setItems(FXCollections.observableArrayList(
            "Donasi", "Tukar"
        ));
    }

    @FXML
    private void handlePilihGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Barang");
        // Filter agar hanya file gambar yang bisa dipilih
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Menampilkan dialog untuk memilih file
        File file = fileChooser.showOpenDialog(pilihGambarButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            namaFileGambarLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleUnggahBarang() {
        String namaBarang = namaBarangField.getText();
        String deskripsi = deskripsiArea.getText();
        String kategori = kategoriComboBox.getValue();
        String jenisTransaksi = jenisTransaksiComboBox.getValue();

        // Validasi input
        if (namaBarang.isEmpty() || deskripsi.isEmpty() || kategori == null || jenisTransaksi == null || selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Mohon isi semua kolom dan pilih gambar.");
            return;
        }

        String gambarPath = selectedImageFile.getAbsolutePath();
        int currentUserId = 1; // PENTING: Untuk sementara, kita hardcode ID user. Nanti ini akan diganti dengan ID user yang sedang login.

        // Memanggil metode addItem dari DAO (akan kita buat di langkah selanjutnya)
        boolean success = itemDAO.addItem(namaBarang, deskripsi, kategori, jenisTransaksi, gambarPath, currentUserId);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Barang Anda telah berhasil diunggah!");
            // Menutup jendela setelah berhasil
            Stage stage = (Stage) unggahButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat mengunggah barang.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}