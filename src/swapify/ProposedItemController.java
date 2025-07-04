package swapify;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProposedItemController {

    @FXML private ImageView itemImageView;
    @FXML private Label itemNameLabel;
    @FXML private Label itemDescriptionLabel;

    public void setData(Proposal proposal) {
        if (proposal != null && "Tukar".equals(proposal.getProposalType())) {
            itemNameLabel.setText(proposal.getProposedItemName());
            itemDescriptionLabel.setText(proposal.getProposedItemDescription());

            String imagePath = proposal.getProposedItemImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    itemImageView.setImage(image);
                }
            }
        }
    }
}