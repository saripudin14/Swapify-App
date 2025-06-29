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

    private Item currentItem;
    private UserDAO userDAO;

    public ItemDetailController() {
        userDAO = new UserDAO();
    }

    public void initData(Item item) {
        this.currentItem = item;
        if (item != null) {
            namaBarangLabel.setText(item.getNamaBarang());
            deskripsiArea.setText(item.getDeskripsi());
            kategoriTextLabel.setText(item.getKategori());
            jenisTransaksiTextLabel.setText(item.getJenisTransaksi());
            uploaderNameLabel.setText("Oleh : " + item.getNamaUploader());
            uploaderNameLabel.setStyle("-fx-cursor: hand; -fx-underline: true;");
            uploaderNameLabel.setOnMouseClicked(this::handleUploaderProfileClick);
            
            if (item.getCreatedAt() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                tanggalUnggahLabel.setText("Diunggah pada " + formatter.format(item.getCreatedAt()));
            }

            String imagePath = item.getGambarPath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
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

    private void handleUploaderProfileClick(MouseEvent event) {
        try {
            User uploader = userDAO.getUserById(currentItem.getUserId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfileView.fxml"));
            Parent root = loader.load();
            UserProfileController controller = loader.getController();
            controller.initData(uploader);
            Stage stage = new Stage();
            stage.setTitle("Profil " + uploader.getNama());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka profil pengguna.");
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