module com.example.project3rubank {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.example.project3rubank to javafx.fxml;
	exports com.example.project3rubank;
}