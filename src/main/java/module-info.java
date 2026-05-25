module com.example.algorithimcomparison {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    opens ui to javafx.fxml, javafx.graphics;
    opens model to javafx.base;
    opens algorithm to javafx.base;
    opens validation to javafx.base;

    exports ui;
}
