module com.example.project_i {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.example.project_i to javafx.fxml;
    exports com.example.project_i;
   opens com.example.project_i.resources to javafx.fxml;
    exports com.example.project_i.files;
    opens com.example.project_i.files to javafx.fxml;
}