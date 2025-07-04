package swapify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProposalDAO {

    /**
     * Membuat proposal baru di database.
     * @param proposal Data proposal yang akan disimpan.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Mengambil semua proposal di mana pengguna terlibat, baik sebagai pengaju atau pemilik.
     * Kita juga mengambil nama barang dan nama pengguna lain untuk ditampilkan di list.
     * @param userId ID pengguna yang sedang login
     * @return Daftar proposal yang relevan
     */
    public ObservableList<Proposal> getProposalsForUser(int userId) {
        ObservableList<Proposal> proposals = FXCollections.observableArrayList();
        String sql = "SELECT " +
                     "p.*, " +
                     "i.nama_barang, " +
                     "i.gambar_path, " +
                     "u_proposer.nama AS proposer_name, " +
                     "u_owner.nama AS owner_name " +
                     "FROM proposals p " +
                     "JOIN items i ON p.item_id = i.id " +
                     "JOIN users u_proposer ON p.proposer_id = u_proposer.id " +
                     "JOIN users u_owner ON p.owner_id = u_owner.id " +
                     "WHERE p.proposer_id = ? OR p.owner_id = ? " +
                     "ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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

                proposals.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proposals;
    }

    // --- METODE BARU DITAMBAHKAN DI SINI ---
    /**
     * Memperbarui status sebuah proposal (misal: Pending -> Accepted).
     * @param proposalId ID dari proposal yang akan diperbarui.
     * @param newStatus Status baru ("Accepted" atau "Rejected").
     * @return true jika berhasil, false jika gagal.
     */
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