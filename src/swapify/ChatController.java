package swapify;

import java.io.IOException;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatController {

    // Deklarasi FXML
    @FXML private ListView<Proposal> activeConversationListView;
    @FXML private ListView<Proposal> historyConversationListView;
    @FXML private Button rejectButton;
    @FXML private Button acceptButton;
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

    // DAOs dan Data
    private ProposalDAO proposalDAO;
    private MessageDAO messageDAO;
    private ItemDAO itemDAO;
    private User currentUser;
    private Proposal selectedProposal;

    public void initialize() {
        this.proposalDAO = new ProposalDAO();
        this.messageDAO = new MessageDAO();
        this.itemDAO = new ItemDAO();
        this.currentUser = UserSession.getInstance().getLoggedInUser();

        setupListView(activeConversationListView);
        setupListView(historyConversationListView);
        
        loadAllConversations();
    }
    
    private void setupListView(ListView<Proposal> listView) {
        listView.setCellFactory(lv -> new ConversationListCell(currentUser));
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (listView == activeConversationListView) {
                    historyConversationListView.getSelectionModel().clearSelection();
                } else {
                    activeConversationListView.getSelectionModel().clearSelection();
                }
                selectedProposal = newSelection;
                displayConversation(newSelection);
            }
        });
    }

    private void loadAllConversations() {
        if (currentUser != null) {
            ObservableList<Proposal> activeProposals = proposalDAO.getProposalsForUser(currentUser.getId(), true);
            activeConversationListView.setItems(activeProposals);

            ObservableList<Proposal> historyProposals = proposalDAO.getProposalsForUser(currentUser.getId(), false);
            historyConversationListView.setItems(historyProposals);
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
        
        boolean isPending = "Pending".equals(proposal.getStatus());
        
        // Tombol aksi hanya muncul untuk ajuan yang statusnya 'Pending' dan dilihat oleh pemilik
        boolean showActionButtons = isPending && currentUser.getId() == proposal.getOwnerId();
        actionButtonsBox.setVisible(showActionButtons);
        actionButtonsBox.setManaged(showActionButtons);
        
        // --- LOGIKA BARU UNTUK MODE READ-ONLY ---
        // Input pesan hanya aktif jika status proposal 'Pending'
        messageField.setDisable(!isPending);
        sendButton.setDisable(!isPending);
        if (!isPending) {
            messageField.setPromptText("Transaksi ini sudah selesai.");
        } else {
            messageField.setPromptText("Ketik pesan...");
        }
        
        messageContainer.getChildren().clear();

        if ("Tukar".equals(proposal.getProposalType())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProposedItemView.fxml"));
                Node proposedItemNode = loader.load();
                ProposedItemController controller = loader.getController();
                controller.setData(proposal);
                messageContainer.getChildren().add(proposedItemNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        ObservableList<Message> messages = messageDAO.getMessagesForProposal(proposal.getId());
        for (Message msg : messages) {
            addMessageBubble(msg);
        }
        
        scrollToBottom();
    }
    
    private void addMessageBubble(Message msg) {
        HBox messageWrapper = new HBox();
        Label messageLabel = new Label(msg.getMessageText());
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(8, 12, 8, 12));
        
        if (msg.isSystemMessage()) {
            messageWrapper.setAlignment(javafx.geometry.Pos.CENTER);
            messageLabel.setStyle("-fx-background-radius: 15; -fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-font-style: italic;");
        } else if (msg.getSenderId() == currentUser.getId()) {
            messageWrapper.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            messageLabel.setStyle("-fx-background-radius: 15; -fx-background-color: #007bff; -fx-text-fill: white;");
        } else {
            messageWrapper.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            messageLabel.setStyle("-fx-background-radius: 15; -fx-background-color: #e9ecef; -fx-text-fill: black;");
        }
        
        messageWrapper.getChildren().add(messageLabel);
        messageContainer.getChildren().add(messageWrapper);
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
            addMessageBubble(newMessage);
            messageField.clear();
            scrollToBottom();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengirim pesan.");
        }
    }
    
    private void scrollToBottom() {
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
    private void handleRejectAction() {
        if (confirmAction("Tolak Ajuan", "Apakah Anda yakin ingin menolak ajuan ini? Aksi ini tidak dapat dibatalkan.")) {
            if (proposalDAO.updateProposalStatus(selectedProposal.getId(), "Rejected")) {
                sendSystemMessage("Pengajuan telah ditolak oleh pemilik.");
                refreshAfterAction();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui status ajuan.");
            }
        }
    }

    @FXML
    private void handleAcceptAction() {
        if (confirmAction("Terima Ajuan", "Apakah Anda yakin ingin menerima ajuan ini? Barang ini akan ditandai sebagai 'Selesai'.")) {
            boolean proposalSuccess = proposalDAO.updateProposalStatus(selectedProposal.getId(), "Accepted");
            boolean itemSuccess = itemDAO.updateItemStatus(selectedProposal.getItemId(), "Selesai");

            if (proposalSuccess && itemSuccess) {
                sendSystemMessage("Pengajuan telah diterima! Silakan lanjutkan transaksi secara pribadi.");
                refreshAfterAction();
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
    
    private void refreshAfterAction() {
        chatAreaPane.setTop(null);
        chatAreaPane.setCenter(placeholderLabel);
        chatAreaPane.setBottom(null);
        actionButtonsBox.setVisible(false);
        actionButtonsBox.setManaged(false);
        
        loadAllConversations();
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