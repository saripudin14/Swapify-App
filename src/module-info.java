module swapify {
	// 'transitive' mengizinkan modul lain yang memakai 'swapify'
	// untuk juga bisa "melihat" tipe data dari java.sql (seperti Connection).
	requires transitive java.sql;
	
	// DITAMBAHKAN: Diperlukan karena kelas 'Stage' berada di modul ini.
	// Dibuat 'transitive' karena metode start(Stage) mengeksposnya ke publik.
	requires transitive javafx.graphics;
	
	// Modul-modul ini tetap diperlukan seperti sebelumnya.
	requires javafx.controls;
	requires javafx.fxml;
	
	
	// Bagian ini tidak perlu diubah.
	opens swapify to javafx.fxml;
	exports swapify;
}