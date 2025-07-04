package swapify;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox; // <-- IMPORT BARU
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class ItemDetailController {

    @FXML private ImageView itemImageView;
    @FXML private Label namaBarangLabel;
    @FXML private Label deskripsiArea;
    @FXML private Label uploaderNameLabel;
    @FXML private Label tanggalUnggahLabel;
    @FXML private Button tutupButton;
    @FXML private Label kategoriTextLabel;
    @FXML private Label jenisTransaksiTextLabel;
    @FXML private Label statusTextLabel;
    @FXML private RowConstraints statusRow;

    // --- DEKLARASI FXML BARU ---
    @FXML private HBox proposalButtonBox;
    @FXML private Button proposalButton;

    private Item currentItem;
    private UserDAO userDAO;

    public ItemDetailController() {
        userDAO = new UserDAO();
    }

    public void initData(Item item) {
        this.currentItem = item;
        if (item != null) {
            namaBarangLabel.setText(item.getNamaBarang()); //
            deskripsiArea.setText(item.getDeskripsi()); //
            
            kategoriTextLabel.setText(item.getKategori()); //
            jenisTransaksiTextLabel.setText(item.getJenisTransaksi()); //
            
            if ("Tersedia".equals(item.getStatus())) { //
                statusTextLabel.setText(item.getStatus()); //
                statusRow.setMinHeight(10.0); //
                statusRow.setPrefHeight(30.0); //
                statusTextLabel.getParent().setVisible(true); //
            } else {
                statusRow.setMinHeight(0); //
                statusRow.setPrefHeight(0); //
                statusTextLabel.getParent().setVisible(false); //
            }
            
            uploaderNameLabel.setText("Oleh : " + item.getNamaUploader()); //
            uploaderNameLabel.setStyle("-fx-cursor: hand; -fx-underline: true;"); //
            uploaderNameLabel.setOnMouseClicked(this::handleUploaderProfileClick); //
            
            if (item.getCreatedAt() != null) { //
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy"); //
                tanggalUnggahLabel.setText("Diunggah pada " + formatter.format(item.getCreatedAt())); //
            }

            String imagePath = item.getGambarPath(); //
            if (imagePath != null && !imagePath.isEmpty()) { //
                File imageFile = new File(imagePath); //
                if (imageFile.exists()) { //
                    Image image = new Image(imageFile.toURI().toString()); //
                    itemImageView.setImage(image); //
                }
            }
            
            // --- LOGIKA BARU UNTUK TOMBOL DINAMIS ---
            User loggedInUser = UserSession.getInstance().getLoggedInUser(); //
            if (loggedInUser != null && loggedInUser.getId() == item.getUserId()) {
                // Jika ini barang milik sendiri, sembunyikan tombol
                proposalButtonBox.setVisible(false);
                proposalButtonBox.setManaged(false);
            } else {
                // Jika ini barang orang lain, atur tombol berdasarkan jenis transaksi
                if ("Donasi".equals(item.getJenisTransaksi())) { //
                    proposalButton.setText("Ajukan Donasi");
                    proposalButton.setOnAction(e -> handleAjukanDonasiAction());
                } else if ("Tukar".equals(item.getJenisTransaksi())) { //
                    proposalButton.setText("Ajukan Tukar");
                    proposalButton.setOnAction(e -> handleAjukanTukarAction());
                    // Ganti style class agar sesuai dengan fungsi (opsional)
                    proposalButton.getStyleClass().setAll("button-secondary");
                } else {
                    // Sembunyikan tombol jika jenis transaksi tidak valid
                    proposalButtonBox.setVisible(false);
                    proposalButtonBox.setManaged(false);
                }
            }
        }
    }

    @FXML
    private void handleTutupAction() {
        Stage stage = (Stage) tutupButton.getScene().getWindow(); //
        stage.close(); //
    }

    private void handleUploaderProfileClick(MouseEvent event) {
        try {
            User uploader = userDAO.getUserById(currentItem.getUserId()); //
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfileView.fxml")); //
            Parent root = loader.load(); //
            UserProfileController controller = loader.getController(); //
            controller.initData(uploader); //
            Stage stage = new Stage(); //
            stage.setTitle("Profil " + uploader.getNama()); //
            stage.setScene(new Scene(root)); //
            stage.show(); //
        } catch (IOException | NullPointerException e) { //
            e.printStackTrace(); //
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka profil pengguna."); //
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType); //
        alert.setTitle(title); //
        alert.setHeaderText(null); //
        alert.setContentText(message); //
        alert.showAndWait(); //
    }

 // --- GANTI METODE HANDLER LAMA DENGAN INI ---
    @FXML
    private void handleAjukanDonasiAction() {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Sesi Gagal", "Sesi pengguna tidak ditemukan. Silakan login ulang.");
            return;
        }

        Proposal newProposal = new Proposal();
        newProposal.setItemId(currentItem.getId());
        newProposal.setOwnerId(currentItem.getUserId());
        newProposal.setProposerId(loggedInUser.getId());
        newProposal.setProposalType("Donasi");
        newProposal.setStatus("Pending");

        ProposalDAO proposalDAO = new ProposalDAO();
        boolean success = proposalDAO.createProposal(newProposal);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pengajuan donasi Anda telah berhasil dikirim.");
            Stage stage = (Stage) proposalButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat mengirim pengajuan donasi.");
        }
    }

    @FXML
    private void handleAjukanTukarAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProposalFormView.fxml"));
            Parent root = loader.load();

            ProposalFormController controller = loader.getController();
            controller.initData(currentItem);

            Stage stage = new Stage();
            stage.setTitle("Ajukan Penawaran untuk: " + currentItem.getNamaBarang());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form pengajuan.");
        }
    }
}