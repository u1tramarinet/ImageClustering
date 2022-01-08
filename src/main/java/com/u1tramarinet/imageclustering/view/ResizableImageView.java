package com.u1tramarinet.imageclustering.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ResizableImageView extends ImageView {
    public ResizableImageView() {
        super();
    }

    public ResizableImageView(Image image) {
        super(image);
    }

    public ResizableImageView(String url) {
        super(url);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setFitWidth(width);
        setFitHeight(height);
    }
}
