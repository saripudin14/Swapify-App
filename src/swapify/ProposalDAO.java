package swapify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProposalDAO {

    public boolean createProposal(Proposal proposal) {
        String sql = "INSERT INTO proposals (item_id, proposer_id, owner_id, proposal_type, " +
                     "proposed_item_name, proposed_item_description, proposed_item_image_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proposal.getItemId());
            pstmt.setInt(2, proposal.getProposerId());
            pstmt.setInt(3, proposal.getOwnerId());
            pstmt.setString(4, proposal.getProposalType());
            pstmt.setString(5, proposal.getProposedItemName());
            pstmt.setString(6, proposal.getProposedItemDescription());
            pstmt.setString(7, proposal.getProposedItemImagePath());
            pstmt.setString(8, proposal.getStatus());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODE INI SEKARANG DIPERBARUI TOTAL ---
    /**
     * Mengambil proposal untuk pengguna berdasarkan status yang diinginkan.
     * @param userId ID pengguna yang sedang login.
     * @param isActive true untuk mengambil ajuan aktif ('Pending'), false untuk riwayat ('Accepted', 'Rejected').
     * @return Daftar proposal yang relevan.
     */
    public ObservableList<Proposal> getProposalsForUser(int userId, boolean isActive) {
        ObservableList<Proposal> proposals = FXCollections.observableArrayList();
        
        // Membangun query SQL secara dinamis
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, i.nama_barang, i.gambar_path, u_proposer.nama AS proposer_name, u_owner.nama AS owner_name " +
            "FROM proposals p " +
            "JOIN items i ON p.item_id = i.id " +
            "JOIN users u_proposer ON p.proposer_id = u_proposer.id " +
            "JOIN users u_owner ON p.owner_id = u_owner.id " +
            "WHERE (p.proposer_id = ? OR p.owner_id = ?) "
        );

        if (isActive) {
            sql.append("AND p.status = 'Pending' ");
        } else {
            sql.append("AND (p.status = 'Accepted' OR p.status = 'Rejected') ");
        }
        
        sql.append("ORDER BY p.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Proposal p = new Proposal();
                p.setId(rs.getInt("id"));
                p.setItemId(rs.getInt("item_id"));
                p.setProposerId(rs.getInt("proposer_id"));
                p.setOwnerId(rs.getInt("owner_id"));
                p.setProposalType(rs.getString("proposal_type"));
                p.setStatus(rs.getString("status"));
                
                p.setOriginalItemName(rs.getString("nama_barang"));
                p.setOriginalItemImagePath(rs.getString("gambar_path"));
                p.setProposerName(rs.getString("proposer_name"));
                p.setOwnerName(rs.getString("owner_name"));

                p.setProposedItemName(rs.getString("proposed_item_name"));
                p.setProposedItemDescription(rs.getString("proposed_item_description"));
                p.setProposedItemImagePath(rs.getString("proposed_item_image_path"));

                proposals.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proposals;
    }

    public boolean updateProposalStatus(int proposalId, String newStatus) {
        String sql = "UPDATE proposals SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, proposalId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}