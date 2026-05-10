module com.example.algorithimcomparison {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens ui to javafx.fxml, javafx.graphics;
    opens model to javafx.base;
    opens algorithm to javafx.base;
    opens validation to javafx.base;

    exports ui;
}