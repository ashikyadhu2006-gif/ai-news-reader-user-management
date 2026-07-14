module com.newsreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.newsreader to javafx.fxml;
    opens com.newsreader.controller to javafx.fxml;
    opens com.newsreader.model to javafx.base;

    exports com.newsreader;
    exports com.newsreader.controller;
    exports com.newsreader.model;
}
