package swapify;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class MainDashboardController implements Initializable {

    @FXML
    private TilePane itemCatalogPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;

    private ItemDAO itemDAO;
    private UserDAO userDAO;

    public MainDashboardController() {
        itemDAO = new ItemDAO();
        userDAO = new UserDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadItems();
    }

    @FXML
    private void handleLogoutAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText("Anda akan keluar dari sesi ini.");
        alert.setContentText("Apakah Anda yakin ingin logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.getInstance().clearSession();
            try {
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
                Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Swapify - Login");

                // --- PERUBAHAN DI SINI ---
                loginStage.setScene(new Scene(root)); // Ukuran manual dihapus
                loginStage.setMaximized(true);      // Jendela diatur ke mode maksimal

                loginStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleProfileAction() {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();

        if (loggedInUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();

                ProfileController profileController = loader.getController();
                profileController.initData(loggedInUser, this);

                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + loggedInUser.getNama());
                profileStage.setScene(new Scene(root));
                profileStage.setMaximized(true);
                profileStage.showAndWait();
                
                loadItems();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Sesi Tidak Ditemukan", "Tidak dapat menemukan data pengguna yang login. Silakan coba login kembali.");
        }
    }

    public void refreshItems() {
        loadItems();
    }

    private void loadItems() {
        ObservableList<Item> availableItems = itemDAO.getAllAvailableItems();
        itemCatalogPane.getChildren().clear();
        for (Item item : availableItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCard.fxml"));
                Parent itemCardNode = loader.load();
                ItemCardController itemCardController = loader.getController();
                itemCardController.setData(item);
                itemCatalogPane.getChildren().add(itemCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}