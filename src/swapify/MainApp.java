package swapify; // Pastikan nama package ini sesuai dengan proyek Anda

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Metode ini akan dieksekusi saat aplikasi JavaFX dimulai.
        
        // 1. Memuat file FXML yang sudah kita desain
        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        
        // 2. Mengatur judul window
        primaryStage.setTitle("Swapify - Login");
        
        // 3. Menetapkan scene (tampilan) ke dalam stage (window)
        // Ukuran manual 400x350 dihapus dari sini
        primaryStage.setScene(new Scene(root));
        
        // --- PERUBAHAN DI SINI ---
        // 4. Mengatur agar jendela langsung berukuran maksimal
        primaryStage.setMaximized(true);
        
        // 5. Menampilkan window ke layar
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Metode main sekarang hanya bertugas untuk memanggil launch(),
        // yang akan menyiapkan lingkungan JavaFX dan memanggil metode start() di atas.
        launch(args);
    }
}