package com.u1tramarinet.imageclustering;

import com.u1tramarinet.imageclustering.controller.MainViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends BaseApplication {

    @Override
    public Scene initializeScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Parent parent = fxmlLoader.load();
        MainViewController controller = fxmlLoader.getController();
        controller.setApplication(this);
        return new Scene(parent, 320, 240);
    }

    @Override
    protected String getTitle() {
        return "Image Clustering";
    }

    public static void main(String[] args) {
        launch();
    }
}