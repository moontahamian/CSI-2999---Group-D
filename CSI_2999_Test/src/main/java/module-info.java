module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.management;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.ikonli.typicons;
    requires org.kordamp.ikonli.carbonicons;
    requires org.kordamp.ikonli.icomoon;
    requires org.kordamp.ikonli.ionicons;
    requires org.xerial.sqlitejdbc;
    requires javafx.web;

    opens com.example.demo2 to javafx.fxml;
    exports com.example.demo2;
}