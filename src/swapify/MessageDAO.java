package swapify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageDAO {

    /**
     * Mengambil semua pesan untuk sebuah proposal_id tertentu.
     */
    public ObservableList<Message> getMessagesForProposal(int proposalId) {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        String sql = "SELECT * FROM messages WHERE proposal_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proposalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getInt("id"));
                msg.setProposalId(rs.getInt("proposal_id"));
                msg.setSenderId(rs.getInt("sender_id"));
                msg.setReceiverId(rs.getInt("receiver_id"));
                msg.setMessageText(rs.getString("message_text"));
                msg.setSystemMessage(rs.getBoolean("is_system_message"));
                msg.setCreatedAt(rs.getTimestamp("created_at"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Menyimpan pesan baru ke database.
     */
    public boolean createMessage(Message message) {
        String sql = "INSERT INTO messages (proposal_id, sender_id, receiver_id, message_text, is_system_message) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, message.getProposalId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setInt(3, message.getReceiverId());
            pstmt.setString(4, message.getMessageText());
            pstmt.setBoolean(5, message.isSystemMessage());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}