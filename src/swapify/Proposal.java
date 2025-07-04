package swapify;

public class Proposal {
    private int id;
    private int itemId;
    private int proposerId;
    private int ownerId;
    private String proposalType;
    private String proposedItemName;
    private String proposedItemDescription;
    private String proposedItemImagePath;
    private String status;

    // --- Field Baru untuk Tampilan UI ---
    private String originalItemName;
    private String originalItemImagePath;
    private String proposerName;
    private String ownerName;

    // --- Getters ---
    public int getId() { return id; }
    public int getItemId() { return itemId; }
    public int getProposerId() { return proposerId; }
    public int getOwnerId() { return ownerId; }
    public String getProposalType() { return proposalType; }
    public String getProposedItemName() { return proposedItemName; }
    public String getProposedItemDescription() { return proposedItemDescription; }
    public String getProposedItemImagePath() { return proposedItemImagePath; }
    public String getStatus() { return status; }
    public String getOriginalItemName() { return originalItemName; }
    public String getOriginalItemImagePath() { return originalItemImagePath; }
    public String getProposerName() { return proposerName; }
    public String getOwnerName() { return ownerName; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setProposerId(int proposerId) { this.proposerId = proposerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public void setProposalType(String proposalType) { this.proposalType = proposalType; }
    public void setProposedItemName(String proposedItemName) { this.proposedItemName = proposedItemName; }
    public void setProposedItemDescription(String proposedItemDescription) { this.proposedItemDescription = proposedItemDescription; }
    public void setProposedItemImagePath(String proposedItemImagePath) { this.proposedItemImagePath = proposedItemImagePath; }
    public void setStatus(String status) { this.status = status; }
    public void setOriginalItemName(String originalItemName) { this.originalItemName = originalItemName; }
    public void setOriginalItemImagePath(String originalItemImagePath) { this.originalItemImagePath = originalItemImagePath; }
    public void setProposerName(String proposerName) { this.proposerName = proposerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}