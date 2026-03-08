module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.management;


    opens com.example.demo2 to javafx.fxml;
    exports com.example.demo2;
}