package com.u1tramarinet.imageclustering.controller;

import com.u1tramarinet.imageclustering.MainApplication;
import com.u1tramarinet.imageclustering.model.AsyncTask;
import com.u1tramarinet.imageclustering.model.Clustering;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController extends BaseController<MainApplication> {

    @FXML
    public TabPane tabImages;

    @FXML
    public ImageView beforeImage;

    @FXML
    public ScrollPane beforeScrollPane;

    @FXML
    public ImageView afterImage;

    @FXML
    public ScrollPane afterScrollPane;

    private final Clustering clustering = new Clustering();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabImages.widthProperty().addListener((observable, oldValue, newValue) -> updateTabWidth());
        tabImages.getTabs().addListener((ListChangeListener<Tab>) change -> updateTabWidth());
        tabImages.getTabs().get(0).disableProperty().bind(Bindings.isNull(beforeImage.imageProperty()));
        tabImages.getTabs().get(1).disableProperty().bind(Bindings.isNull(afterImage.imageProperty()));
        beforeScrollPane.hvalueProperty().bindBidirectional(afterScrollPane.hvalueProperty());
        beforeScrollPane.vvalueProperty().bindBidirectional(afterScrollPane.vvalueProperty());
        afterImage.setImage(null);
    }

    @FXML
    public void onOpenClicked(ActionEvent event) {
        if (application == null) return;
        File file = application.showOpenDialog("画像選択", getImageFilters());
        if (file == null) return;
        Image original = new Image("file:" + file.getAbsolutePath());
        beforeImage.setImage(original);
        afterImage.setImage(null);
        application.resize();
        new AsyncTask<Image, Image>() {
            @Override
            public Image doInBackground(Image input) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return clustering.execute(input);
            }

            @Override
            public void onPostExecute(Image output) {
                afterImage.setImage(output);
            }
        }.execute(original);
    }

    @FXML
    public void onSaveClicked(ActionEvent event) {
        if (application == null) return;
        if (afterImage.getImage() == null) return;
        File file = application.showSaveDialog("画像保存", getImageFilters());
        if (file != null) {
            
        }
    }

    @FXML
    public void onResetClicked(ActionEvent event) {
        beforeImage.setImage(null);
        afterImage.setImage(null);
    }

    @FXML
    public void onExitClicked(ActionEvent event) {
        if (application != null) {
            application.exit();
        }
    }

    private List<FileChooser.ExtensionFilter> getImageFilters() {
        return List.of(new FileChooser.ExtensionFilter("画像ファイル", "*.jpg", "*.png"));
    }

    private void updateTabWidth() {
        int tabSize = tabImages.getTabs().size();
        double tabWidth = (tabSize == 0) ? 0 : tabImages.getWidth() / tabImages.getTabs().size() - 25;
        if (tabWidth >= 0) {
            tabImages.setTabMinWidth(tabWidth);
            tabImages.setTabMaxWidth(tabWidth);
        }
    }
}