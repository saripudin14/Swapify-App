package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ConversationCellController {

    @FXML private HBox cellContainer;
    @FXML private ImageView itemImageView;
    @FXML private Label itemNameLabel;
    @FXML private Label otherUserNameLabel;
    @FXML private Label statusLabel;

    /**
     * Mengisi data dari objek Proposal ke dalam elemen UI sel.
     */
    public void setData(Proposal proposal, User currentUser) {
        itemNameLabel.setText(proposal.getOriginalItemName());

        // Menentukan siapa "pengguna lain" dalam percakapan
        if (currentUser.getId() == proposal.getProposerId()) {
            otherUserNameLabel.setText("dengan: " + proposal.getOwnerName());
        } else {
            otherUserNameLabel.setText("dari: " + proposal.getProposerName());
        }

        // Mengatur gambar item
        String imagePath = proposal.getOriginalItemImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                itemImageView.setImage(image);
            }
        }
        
        // Mengatur label status dengan style yang sesuai
        statusLabel.setText(proposal.getStatus());
        statusLabel.getStyleClass().setAll("tag"); // Reset style
        switch (proposal.getStatus()) {
            case "Pending":
                statusLabel.getStyleClass().add("tag-transaksi-tukar"); // Warna abu-abu
                break;
            case "Accepted":
                statusLabel.getStyleClass().add("tag-status-tersedia"); // Warna hijau
                break;
            case "Rejected":
                statusLabel.getStyleClass().add("tag-transaksi-donasi"); // Warna merah
                break;
        }
    }

    // Metode untuk mendapatkan root node dari sel ini
    public HBox getCellContainer() {
        return cellContainer;
    }
}