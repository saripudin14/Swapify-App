package swapify;

import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; // <-- Pastikan import ini ada
import javafx.scene.text.Font;

/**
 * Versi final yang secara manual mengontrol penuh gaya visual sel
 * untuk mengatasi bug render tanpa mengorbankan tampilan.
 */
public class ConversationListCell extends ListCell<Proposal> {

    private final HBox cellContainer;
    private final ImageView itemImageView;
    private final Label itemNameLabel;
    private final Label otherUserNameLabel;
    private final Label statusLabel;
    private final VBox textVBox;
    private final User currentUser;

    public ConversationListCell(User currentUser) {
        super();
        this.currentUser = currentUser;

        // --- Membangun Komponen Tampilan ---
        cellContainer = new HBox();
        itemImageView = new ImageView();
        itemNameLabel = new Label();
        otherUserNameLabel = new Label();
        statusLabel = new Label();
        textVBox = new VBox(itemNameLabel, otherUserNameLabel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // --- Konfigurasi Awal Komponen ---
        cellContainer.setAlignment(Pos.CENTER_LEFT);
        cellContainer.setSpacing(15);
        cellContainer.setPadding(new javafx.geometry.Insets(10));
        
        itemImageView.setFitHeight(55);
        itemImageView.setFitWidth(55);
        itemImageView.setPreserveRatio(true);

        itemNameLabel.setStyle("-fx-font-weight: bold;");
        itemNameLabel.setFont(new Font(14));

        statusLabel.getStyleClass().add("tag");
        
        cellContainer.getChildren().addAll(itemImageView, textVBox, spacer, statusLabel);
    }

    @Override
    protected void updateItem(Proposal proposal, boolean empty) {
        super.updateItem(proposal, empty);

        if (empty || proposal == null) {
            setGraphic(null);
        } else {
            // --- Mengisi Data ---
            itemNameLabel.setText(proposal.getOriginalItemName());
            
            if (currentUser.getId() == proposal.getProposerId()) {
                otherUserNameLabel.setText("dengan: " + proposal.getOwnerName());
            } else {
                otherUserNameLabel.setText("dari: " + proposal.getProposerName());
            }

            String imagePath = proposal.getOriginalItemImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    itemImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    itemImageView.setImage(null);
                }
            } else {
                itemImageView.setImage(null);
            }
            
            statusLabel.setText(proposal.getStatus());
            statusLabel.getStyleClass().setAll("tag");
            switch (proposal.getStatus()) {
                case "Pending": statusLabel.getStyleClass().add("tag-transaksi-tukar"); break;
                case "Accepted": statusLabel.getStyleClass().add("tag-status-tersedia"); break;
                case "Rejected": statusLabel.getStyleClass().add("tag-transaksi-donasi"); break;
            }
            
            // =========================================================
            // =========== PERBAIKAN UTAMA DAN PALING PENTING ===========
            // =========================================================
            // Secara manual mengatur tampilan berdasarkan status 'selected'
            if (isSelected()) {
                // Atur latar belakang menjadi biru dan teks menjadi putih
                cellContainer.setStyle("-fx-background-color: #007bff; -fx-background-radius: 8;");
                itemNameLabel.setTextFill(Color.WHITE);
                otherUserNameLabel.setTextFill(Color.WHITE);
            } else {
                // Kembalikan ke tampilan normal
                cellContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
                itemNameLabel.setTextFill(Color.BLACK);
                otherUserNameLabel.setTextFill(Color.GRAY);
            }
            
            setGraphic(cellContainer);
        }
    }
}