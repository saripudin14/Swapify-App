package swapify;

import java.io.IOException;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets; // <-- IMPORT YANG DIPERBAIKI ADA DI SINI
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatController {

    // --- DEKLARASI FXML BARU ---
    @FXML private Button rejectButton;
    @FXML private Button acceptButton;
    
    // Deklarasi FXML yang sudah ada
    @FXML private ListView<Proposal> conversationListView;
    @FXML private BorderPane chatAreaPane;
    @FXML private Label placeholderLabel;
    @FXML private VBox chatInputContainer;
    @FXML private HBox chatHeaderBox;
    @FXML private Label chatHeaderTitle;
    @FXML private Label chatHeaderStatus;
    @FXML private ScrollPane messageScrollPane;
    @FXML private VBox messageContainer;
    @FXML private HBox actionButtonsBox;
    @FXML private TextField messageField;
    @FXML private Button sendButton;

    // --- DAO BARU & ITEMDAO ---
    private ProposalDAO proposalDAO;
    private MessageDAO messageDAO;
    private ItemDAO itemDAO; // Ditambahkan
    private User currentUser;
    private Proposal selectedProposal;

    public void initialize() {
        this.proposalDAO = new ProposalDAO();
        this.messageDAO = new MessageDAO();
        this.itemDAO = new ItemDAO(); // Diinisialisasi
        this.currentUser = UserSession.getInstance().getLoggedInUser();

        setupConversationList();
        loadConversations();
    }
    
    private void setupConversationList() {
        conversationListView.setCellFactory(lv -> new ListCell<Proposal>() {
            @Override
            protected void updateItem(Proposal proposal, boolean empty) {
                super.updateItem(proposal, empty);
                if (empty || proposal == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConversationCell.fxml"));
                        HBox cellNode = loader.load();
                        ConversationCellController controller = loader.getController();
                        controller.setData(proposal, currentUser);
                        setGraphic(cellNode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        conversationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProposal = newSelection;
                displayConversation(newSelection);
            }
        });
    }

    private void loadConversations() {
        if (currentUser != null) {
            ObservableList<Proposal> proposals = proposalDAO.getProposalsForUser(currentUser.getId());
            conversationListView.setItems(proposals);
        }
    }

    private void displayConversation(Proposal proposal) {
        placeholderLabel.setVisible(false);
        chatInputContainer.setVisible(true);
        chatInputContainer.setManaged(true);
        chatHeaderBox.setVisible(true);
        chatHeaderBox.setManaged(true);

        chatHeaderTitle.setText("Percakapan untuk: " + proposal.getOriginalItemName());
        updateStatusUI(proposal.getStatus());
        
        boolean showActionButtons = "Pending".equals(proposal.getStatus()) && currentUser.getId() == proposal.getOwnerId();
        actionButtonsBox.setVisible(showActionButtons);
        actionButtonsBox.setManaged(showActionButtons);

        loadMessages(proposal.getId());
    }
    
    private void loadMessages(int proposalId) {
        messageContainer.getChildren().clear();
        ObservableList<Message> messages = messageDAO.getMessagesForProposal(proposalId);
        for (Message msg : messages) {
            HBox messageWrapper = new HBox();
            Label messageLabel = new Label(msg.getMessageText());
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(8, 12, 8, 12));
            messageLabel.setStyle("-fx-background-radius: 15;");

            if(msg.isSystemMessage()){
                messageWrapper.setAlignment(javafx.geometry.Pos.CENTER);
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-font-style: italic;");
            } else if (msg.getSenderId() == currentUser.getId()) {
                messageWrapper.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #007bff; -fx-text-fill: white;");
            } else {
                messageWrapper.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #f1f3f5; -fx-text-fill: black;");
            }
            messageWrapper.getChildren().add(messageLabel);
            messageContainer.getChildren().add(messageWrapper);
        }
        messageScrollPane.layout();
        messageScrollPane.setVvalue(1.0);
    }
    
    private void updateStatusUI(String newStatus) {
        chatHeaderStatus.setText(newStatus);
        chatHeaderStatus.getStyleClass().setAll("tag");
        switch (newStatus) {
            case "Pending": chatHeaderStatus.getStyleClass().add("tag-transaksi-tukar"); break;
            case "Accepted": chatHeaderStatus.getStyleClass().add("tag-status-tersedia"); break;
            case "Rejected": chatHeaderStatus.getStyleClass().add("tag-transaksi-donasi"); break;
        }
    }
    
    @FXML
    private void handleSendMessage() {
        String text = messageField.getText();
        if (text.isEmpty() || selectedProposal == null) return;

        Message newMessage = new Message();
        newMessage.setProposalId(selectedProposal.getId());
        newMessage.setSenderId(currentUser.getId());
        int receiverId = (currentUser.getId() == selectedProposal.getProposerId()) ? selectedProposal.getOwnerId() : selectedProposal.getProposerId();
        newMessage.setReceiverId(receiverId);
        newMessage.setMessageText(text);
        newMessage.setSystemMessage(false);

        if (messageDAO.createMessage(newMessage)) {
            loadMessages(selectedProposal.getId());
            messageField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengirim pesan.");
        }
    }
    
    @FXML
    private void handleRejectAction() {
        if (confirmAction("Tolak Ajuan", "Apakah Anda yakin ingin menolak ajuan ini? Aksi ini tidak dapat dibatalkan.")) {
            boolean success = proposalDAO.updateProposalStatus(selectedProposal.getId(), "Rejected");
            if(success) {
                sendSystemMessage("Pengajuan telah ditolak oleh pemilik.");
                refreshCurrentConversationState("Rejected");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui status ajuan.");
            }
        }
    }

    @FXML
    private void handleAcceptAction() {
        if (confirmAction("Terima Ajuan", "Apakah Anda yakin ingin menerima ajuan ini? Barang ini akan ditandai sebagai 'Selesai' dan tidak akan terlihat lagi.")) {
            boolean proposalSuccess = proposalDAO.updateProposalStatus(selectedProposal.getId(), "Accepted");
            boolean itemSuccess = itemDAO.updateItemStatus(selectedProposal.getItemId(), "Selesai");

            if(proposalSuccess && itemSuccess) {
                sendSystemMessage("Pengajuan telah diterima! Silakan lanjutkan transaksi secara pribadi.");
                refreshCurrentConversationState("Accepted");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyelesaikan transaksi. Coba lagi.");
            }
        }
    }
    
    private void sendSystemMessage(String text) {
        Message sysMessage = new Message();
        sysMessage.setProposalId(selectedProposal.getId());
        sysMessage.setSenderId(currentUser.getId()); 
        sysMessage.setReceiverId(currentUser.getId());
        sysMessage.setMessageText(text);
        sysMessage.setSystemMessage(true);
        messageDAO.createMessage(sysMessage);
    }
    
    private void refreshCurrentConversationState(String newStatus) {
        actionButtonsBox.setVisible(false);
        actionButtonsBox.setManaged(false);
        updateStatusUI(newStatus);
        loadMessages(selectedProposal.getId());
        loadConversations();
    }
    
    private boolean confirmAction(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}