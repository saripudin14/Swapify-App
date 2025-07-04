package swapify;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int proposalId;
    private int senderId;
    private int receiverId;
    private String messageText;
    private boolean isSystemMessage;
    private Timestamp createdAt;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProposalId() { return proposalId; }
    public void setProposalId(int proposalId) { this.proposalId = proposalId; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public boolean isSystemMessage() { return isSystemMessage; }
    public void setSystemMessage(boolean isSystemMessage) { this.isSystemMessage = isSystemMessage; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}