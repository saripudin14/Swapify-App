package swapify;

import java.io.File;
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
    @FXML
    private Label formTitleLabel;

    private File selectedImageFile;
    private ItemDAO itemDAO;
    private Item itemToEdit;

    public UploadItemController() {
        itemDAO = new ItemDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kategoriComboBox.setItems(FXCollections.observableArrayList("Pakaian", "Buku", "Alat Rumah Tangga", "Elektronik", "Lainnya"));
        jenisTransaksiComboBox.setItems(FXCollections.observableArrayList("Donasi", "Tukar"));
    }

    public void initData(Item item) {
        this.itemToEdit = item;
        
        formTitleLabel.setText("Edit Barang");
        unggahButton.setText("Simpan Perubahan");
        
        namaBarangField.setText(item.getNamaBarang());
        deskripsiArea.setText(item.getDeskripsi());
        kategoriComboBox.setValue(item.getKategori());
        jenisTransaksiComboBox.setValue(item.getJenisTransaksi());
        
        if (item.getGambarPath() != null && !item.getGambarPath().isEmpty()) {
            selectedImageFile = new File(item.getGambarPath());
            namaFileGambarLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handlePilihGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Barang");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) pilihGambarButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedImageFile = file;
            namaFileGambarLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleUnggahBarang() {
        String namaBarang = namaBarangField.getText();
        String deskripsi = deskripsiArea.getText();
        String kategori = kategoriComboBox.getValue();
        String jenisTransaksi = jenisTransaksiComboBox.getValue();

        if (namaBarang.isEmpty() || deskripsi.isEmpty() || kategori == null || jenisTransaksi == null || selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Semua kolom dan gambar wajib diisi.");
            return;
        }

        String gambarPath = selectedImageFile.getAbsolutePath();
        boolean success;

        if (itemToEdit == null) {
            // Mode Tambah Baru
            int currentUserId = 1; // ID pengguna yang sedang login (sementara di-hardcode)
            success = itemDAO.addItem(namaBarang, deskripsi, kategori, jenisTransaksi, gambarPath, currentUserId);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Barang baru telah berhasil diunggah.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat mengunggah barang.");
            }
        } else {
            // Mode Edit
            success = itemDAO.updateItem(itemToEdit.getId(), namaBarang, deskripsi, kategori, jenisTransaksi, gambarPath);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Perubahan pada barang telah berhasil disimpan.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat menyimpan perubahan.");
            }
        }

        if (success) {
            Stage stage = (Stage) unggahButton.getScene().getWindow();
            stage.close();
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