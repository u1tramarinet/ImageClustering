package com.u1tramarinet.imageclustering.controller;

import com.u1tramarinet.imageclustering.FileUtils;
import com.u1tramarinet.imageclustering.MainApplication;
import com.u1tramarinet.imageclustering.model.AsyncTask;
import com.u1tramarinet.imageclustering.model.Clustering;
import com.u1tramarinet.imageclustering.model.ImageData;
import com.u1tramarinet.imageclustering.model.Pixel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
        afterImage.setImage(null);
        String originalUrlStr;
        try {
            originalUrlStr = file.toURI().toURL().toString();
            beforeImage.setImage(new Image(originalUrlStr));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            beforeImage.setImage(null);
            return;
        }

//        String tempFileUrlStr = FileUtils.createTempFileUrl(originalUrlStr);
//        if (tempFileUrlStr == null) return;
//        Image tempImage = new Image(tempFileUrlStr);
        application.resize();

        new AsyncTask<Image, Image>() {
            @Override
            public Image doInBackground(Image input) {
                System.out.println("doInBackground() input=" + input);
                return createImage(
                        clustering.execute(createImageData(input)));
            }

            @Override
            public void onPostExecute(Image output) {
                System.out.println("onPostExecute() output=" + output);
                afterImage.setImage(output);
            }
        }.execute(beforeImage.getImage());
    }

    @FXML
    public void onSaveClicked(ActionEvent event) {
        if (application == null) return;
        if (afterImage.getImage() == null) return;
        File file = application.showSaveDialog("画像保存", getImageFilters());

        if (file != null) {
            String extension = FileUtils.getExtensionName(file.getName());
            if ("png".equals(extension) || "jpg".equals(extension)) {
                FileUtils.save(file, afterImage.getImage());
            }
        }
    }

    @FXML
    public void onResetClicked(ActionEvent event) {
        beforeImage.setImage(null);
        Image tempImage = afterImage.getImage();
        if (tempImage == null) return;
        FileUtils.deleteFile(tempImage.getUrl());
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

    private ImageData createImageData(Image image) {
        ImageData imageData = new ImageData();
        imageData.width = (int) image.getWidth();
        imageData.height = (int) image.getHeight();
        imageData.pixels = new ArrayList<>();
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < imageData.height; y++) {
            for (int x = 0; x < imageData.width; x++) {
                Pixel pixel = new Pixel(x, y, reader.getColor(x, y));
                imageData.pixels.add(pixel);
            }
        }
        return imageData;
    }

    private Image createImage(ImageData imageData) {
        WritableImage image = new WritableImage(imageData.width, imageData.height);
        PixelWriter writer = image.getPixelWriter();
        for (Pixel pixel : imageData.pixels) {
            writer.setColor(pixel.getX(), pixel.getY(), pixel.getColor());
        }
        return image;
    }
}