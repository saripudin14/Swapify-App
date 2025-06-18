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
    private Label formTitleLabel; // Pastikan variabel ini ada

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
        
        // --- PERUBAHAN VISUAL UNTUK MODE EDIT ---
        formTitleLabel.setText("Edit Barang"); // Mengubah judul form
        unggahButton.setText("Simpan Perubahan"); // Mengubah teks tombol
        // -----------------------------------------
        
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
        // ... (metode ini tidak berubah) ...
    }

    @FXML
    private void handleUnggahBarang() {
        // ... (metode ini tidak berubah) ...
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // ... (metode ini tidak berubah) ...
    }
}