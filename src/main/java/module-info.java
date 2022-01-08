module com.u1tramarinet.imageclustering {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens com.u1tramarinet.imageclustering to javafx.fxml, javafx.swing;
    exports com.u1tramarinet.imageclustering;
    exports com.u1tramarinet.imageclustering.controller;
    opens com.u1tramarinet.imageclustering.controller to javafx.fxml;
    exports com.u1tramarinet.imageclustering.view;
    opens com.u1tramarinet.imageclustering.view to javafx.fxml;
    exports com.u1tramarinet.imageclustering.model;
    opens com.u1tramarinet.imageclustering.model to javafx.fxml;
}