package com.u1tramarinet.imageclustering.controller;

import com.u1tramarinet.imageclustering.FileUtils;
import com.u1tramarinet.imageclustering.MainApplication;
import com.u1tramarinet.imageclustering.model.AsyncTask;
import com.u1tramarinet.imageclustering.model.Clustering;
import com.u1tramarinet.imageclustering.model.ImageData;
import com.u1tramarinet.imageclustering.model.Pixel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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

    @FXML
    public Label labelClusterNum;

    @FXML
    public Slider sliderClusterNum;

    @FXML
    public Label labelTrialCount;

    @FXML
    public Slider sliderTrialCount;

    private final Clustering clustering = new Clustering();

    private boolean executing = false;

    private double mouseX = 0;
    private double mouseY = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabImages.widthProperty().addListener((observable, oldValue, newValue) -> updateTabWidth());
        tabImages.getTabs().addListener((ListChangeListener<Tab>) change -> updateTabWidth());
        tabImages.getTabs().get(0).disableProperty().bind(Bindings.isNull(beforeImage.imageProperty()));
        tabImages.getTabs().get(1).disableProperty().bind(Bindings.isNull(afterImage.imageProperty()));
        beforeScrollPane.hvalueProperty().bindBidirectional(afterScrollPane.hvalueProperty());
        beforeScrollPane.vvalueProperty().bindBidirectional(afterScrollPane.vvalueProperty());
        sliderClusterNum.valueProperty().addListener((observable, oldValue, newValue) -> labelClusterNum.setText("Number of clusters:\n" + newValue.intValue()));
        sliderClusterNum.setValue(4);
        sliderTrialCount.valueProperty().addListener((observable, oldValue, newValue) -> labelTrialCount.setText("Trial count:\n" + newValue.intValue()));
        sliderTrialCount.setValue(10);
        beforeImage.setOnScroll(scrollEvent -> updateImageScale(scrollEvent.getDeltaY()));
        beforeImage.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
            }
        });
        beforeImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                updateImagePosition(mouseEvent.getX() - mouseX, mouseEvent.getY() - mouseY);
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
            }
        });
        beforeImage.scaleXProperty().bindBidirectional(afterImage.scaleXProperty());
        beforeImage.scaleYProperty().bindBidirectional(afterImage.scaleYProperty());
        beforeImage.translateXProperty().bindBidirectional(afterImage.translateXProperty());
        beforeImage.translateYProperty().bindBidirectional(afterImage.translateYProperty());
        beforeImage.fitWidthProperty().bindBidirectional(afterImage.fitWidthProperty());
        beforeImage.fitHeightProperty().bindBidirectional(afterImage.fitHeightProperty());
        afterImage.setImage(null);
        afterImage.setOnScroll(scrollEvent -> updateImageScale(scrollEvent.getDeltaY()));
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
        beforeImage.setScaleX(1d);
        beforeImage.setScaleY(1d);
        beforeImage.setTranslateX(0d);
        beforeImage.setTranslateY(0d);

        application.resize();
        runKMeans(beforeImage.getImage());
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

    @FXML
    public void onKMeansClicked(ActionEvent event) {
        if (beforeImage.getImage() == null) {
            return;
        }
        afterImage.setImage(null);
        runKMeans(beforeImage.getImage());
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

    private void updateImagePosition(double deltaX, double deltaY) {
        double oldTranslateX = beforeImage.getTranslateX();
        double newTranslateX = oldTranslateX + deltaX;
        beforeImage.setTranslateX(newTranslateX);
        double oldTranslateY = beforeImage.getTranslateY();
        double newTranslateY = oldTranslateY + deltaY;
        beforeImage.setTranslateY(newTranslateY);
    }

    private void updateImageScale(double scrollDelta) {
        double oldScale = beforeImage.getScaleX();
        double diff = Math.signum(scrollDelta) * 0.1;
        double newScale = oldScale + diff;
        if (newScale > 0) {
            beforeImage.setScaleX(newScale);
            beforeImage.setScaleY(newScale);
            System.out.println("oldScale=" + oldScale + ", newScale=" + newScale + ", diff=" + (newScale - oldScale));
            double scaleRatio = newScale / oldScale;
            double oldWidth = beforeImage.getFitWidth();
            double newWidth = oldWidth * scaleRatio;
//            double newWidth = originalWidth + newScale;
            beforeImage.setFitWidth(newWidth);
            System.out.println("oldFitWidth=" + oldWidth + ", newFitWidth=" + newWidth + ", diff=" + (newWidth - oldWidth));
            double oldTranslateX = beforeImage.getTranslateX();
            double newTranslateX = oldTranslateX + (newWidth - oldWidth);
//            double newTranslateX = (newWidth - originalWidth) / 2;
//            beforeImage.setTranslateX(newTranslateX);
            System.out.println("oldTranslateX=" + oldTranslateX + ", newTranslateX=" + newTranslateX + ", diff=" + (newTranslateX - oldTranslateX));
            double oldHeight = beforeImage.getFitHeight();
            double newHeight = oldHeight * scaleRatio;
//            double newHeight = originalHeight * newScale;
            beforeImage.setFitHeight(newHeight);
            System.out.println("oldFitHeight=" + oldHeight + ", newHeight=" + newHeight + ", diff=" + (newHeight - oldHeight));
            double oldTranslateY = beforeImage.getTranslateY();
            double newTranslateY = oldTranslateY + (newHeight - oldHeight);
//            double newTranslateY = (newHeight - originalHeight) / 2;
//            beforeImage.setTranslateY(newTranslateY);
            System.out.println("oldTranslateY=" + oldTranslateY + ", newTranslateY=" + newTranslateY + ", diff=" + (newTranslateY - oldTranslateY));
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

    private void runKMeans(Image image) {
        if (image == null || executing) {
            return;
        }
        executing = true;
        new AsyncTask<Image, Image>() {
            @Override
            public Image doInBackground(Image input) {
                System.out.println("doInBackground() input=" + input);
                return createImage(
                        clustering.execute(createImageData(input), (int) sliderClusterNum.getValue(), (int) sliderTrialCount.getValue()));
            }

            @Override
            public void onPostExecute(Image output) {
                System.out.println("onPostExecute() output=" + output);
                afterImage.setImage(output);
                executing = false;
            }
        }.execute(image);
    }
}