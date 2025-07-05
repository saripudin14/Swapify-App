package swapify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox; // <-- IMPORT BARU
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MainDashboardController implements Initializable {

    @FXML private TilePane itemCatalogPane;
    @FXML private TextField searchField;
    @FXML private StackPane profileButtonContainer;
    @FXML private Circle profileCircle;
    @FXML private Label profileInitialLabel;
    
    // --- FXML BARU UNTUK FILTER ---
    @FXML private ComboBox<String> categoryFilterComboBox;

    private ItemDAO itemDAO;

    public MainDashboardController() {
        itemDAO = new ItemDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupProfileAvatar();
        setupFilters(); // <-- Panggil metode setup baru
        loadItems();
    }
    
    // --- METODE BARU UNTUK SETUP FILTER ---
    private void setupFilters() {
        // Isi ComboBox dengan kategori
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Semua Kategori", "Pakaian", "Buku", "Alat Rumah Tangga", "Elektronik", "Lainnya"
        );
        categoryFilterComboBox.setItems(categories);
        categoryFilterComboBox.setValue("Semua Kategori"); // Nilai default

        // Tambahkan listener ke kolom pencarian dan ComboBox
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadItems());
        categoryFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> loadItems());
    }
    
    // --- PERBARUI METODE loadItems() MENJADI SEPERTI INI ---
    private void loadItems() {
        String keyword = searchField.getText();
        String category = categoryFilterComboBox.getValue();

        // Panggil metode DAO yang sudah kita perbarui
        ObservableList<Item> items = itemDAO.getFilteredItems(keyword, category);
        
        itemCatalogPane.getChildren().clear();
        for (Item item : items) {
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
    
    // --- Sisa kode di controller ini tetap sama ---
    // (setupProfileAvatar, handleProfileAction, dll.)
    
    public void refreshItems() {
        loadItems();
    }
    
    private void setupProfileAvatar() {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            String imagePath = loggedInUser.getProfileImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image profileImage = new Image(imageFile.toURI().toString(), true);

                    profileImage.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            profileCircle.setFill(new ImagePattern(profileImage));
                            profileInitialLabel.setVisible(false);
                        }
                    });
                    
                    profileImage.errorProperty().addListener((observable, oldValue, hadError) -> {
                        if (hadError) {
                            System.err.println("Gagal memuat gambar profil: " + imagePath);
                            displayInitial(loggedInUser);
                        }
                    });

                    if (profileImage.getProgress() == 1.0 && !profileImage.isError()) {
                        profileCircle.setFill(new ImagePattern(profileImage));
                        profileInitialLabel.setVisible(false);
                    }
                    return;
                }
            }
            
            displayInitial(loggedInUser);
        }
    }

    private void displayInitial(User user) {
        profileCircle.setFill(Color.web("#495057"));
        profileInitialLabel.setVisible(true);
        if (user != null && user.getNama() != null && !user.getNama().isEmpty()) {
            profileInitialLabel.setText(user.getNama().substring(0, 1).toUpperCase());
        } else {
            profileInitialLabel.setText("?");
        }
    }
    
    @FXML
    private void handleProfileAction(MouseEvent event) {
        User loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
                Parent root = loader.load();
                
                ProfileController profileController = loader.getController();
                profileController.initData(loggedInUser, this);
                
                Stage profileStage = new Stage();
                profileStage.setTitle("Profil Pengguna - " + loggedInUser.getNama());
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                profileStage.setScene(scene);
                
                profileStage.setMaximized(true);
                profileStage.showAndWait();
                
                loadItems();
                setupProfileAvatar(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Sesi Tidak Ditemukan", "Tidak dapat menemukan data pengguna yang login.");
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Circle getProfileCircle() {
        return profileCircle;
    }
}