package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProposalFormController {

    // FXML Declarations
    @FXML private TextField namaBarangField;
    @FXML private TextArea deskripsiArea;
    @FXML private ImageView imagePreview;
    @FXML private Button pilihGambarButton;
    @FXML private Label namaFileGambarLabel;
    @FXML private Button batalButton;
    @FXML private Button kirimButton;

    // Data and DAO
    private Item targetItem; // Barang yang sedang ditawar
    private User loggedInUser;
    private File selectedImageFile;
    private ProposalDAO proposalDAO;

    public ProposalFormController() {
        this.proposalDAO = new ProposalDAO();
        this.loggedInUser = UserSession.getInstance().getLoggedInUser();
    }

    /**
     * Menerima data barang yang akan ditawar dari controller sebelumnya.
     */
    public void initData(Item targetItem) {
        this.targetItem = targetItem;
    }

    @FXML
    private void handlePilihGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Barang yang Ditawarkan");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) pilihGambarButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            namaFileGambarLabel.setText(file.getName());
            Image newImage = new Image(file.toURI().toString());
            imagePreview.setImage(newImage);
        }
    }

    @FXML
    private void handleKirimAjuan() {
        // 1. Validasi Input
        if (namaBarangField.getText().isEmpty() || deskripsiArea.getText().isEmpty() || selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Semua kolom dan gambar wajib diisi.");
            return;
        }
        
        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Sesi Gagal", "Sesi pengguna tidak ditemukan. Silakan login ulang.");
            return;
        }

        // 2. Buat objek Proposal
        Proposal newProposal = new Proposal();
        newProposal.setItemId(targetItem.getId());
        newProposal.setOwnerId(targetItem.getUserId());
        newProposal.setProposerId(loggedInUser.getId());
        newProposal.setProposalType("Tukar");
        newProposal.setStatus("Pending");
        newProposal.setProposedItemName(namaBarangField.getText());
        newProposal.setProposedItemDescription(deskripsiArea.getText());
        newProposal.setProposedItemImagePath(selectedImageFile.getAbsolutePath());

        // 3. Simpan ke Database
        boolean success = proposalDAO.createProposal(newProposal);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Ajuan penawaran tukar Anda telah berhasil dikirim.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat mengirim ajuan.");
        }
    }

    @FXML
    private void handleBatal() {
        closeWindow();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) batalButton.getScene().getWindow();
        stage.close();
    }
}