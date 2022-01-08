package com.u1tramarinet.imageclustering;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class BaseApplication extends Application {

    protected Stage mainStage;
    private File defaultFile;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(getTitle());
        stage.setOnCloseRequest(windowEvent -> exit());
        stage.setScene(initializeScene(stage));
        stage.show();
        mainStage = stage;
    }

    public abstract Scene initializeScene(Stage stage) throws Exception;

    protected abstract String getTitle();

    public void exit() {
        mainStage = null;
        Platform.exit();
        System.exit(0);
    }

    public void resize() {
        if (mainStage != null) {
            mainStage.sizeToScene();
        }
    }

    public File showOpenDialog(String title, List<FileChooser.ExtensionFilter> filters) {
        File file = getBasicFileChooser(title, filters).showOpenDialog(mainStage);
        if (file != null) {
            defaultFile = file.getParentFile();
        }
        return file;
    }

    public File showSaveDialog(String title, List<FileChooser.ExtensionFilter> filters) {
        File file = getBasicFileChooser(title, filters).showSaveDialog(mainStage);
        if (file != null) {
            defaultFile = file.getParentFile();
        }
        return file;
    }

    private FileChooser getBasicFileChooser(String title, List<FileChooser.ExtensionFilter> filters) {
        FileChooser chooser = new FileChooser();
        if (title == null) {
            title = "";
        }
        chooser.setTitle(title);
        if (filters != null && !filters.isEmpty()) {
            chooser.getExtensionFilters().addAll(filters);
        }
        if (defaultFile != null) {
            chooser.setInitialDirectory(defaultFile);
        }
        return chooser;
    }
}
