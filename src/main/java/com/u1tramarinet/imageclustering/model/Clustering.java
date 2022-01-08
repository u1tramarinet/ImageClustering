package com.u1tramarinet.imageclustering.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.util.ArrayList;

public class Clustering {
    public Image execute(Image original) {
        return original;
    }

    private ImageData convertToImageData(Image image) {
        ImageData imageData = new ImageData();
        imageData.width = image.getWidth();
        imageData.height = image.getHeight();
        imageData.pixels = new ArrayList<>();
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < imageData.height; y++) {
            for (int x = 0; x < imageData.width; x++) {
                Pixel pixel = new Pixel();
                pixel.x = x;
                pixel.y = y;
                pixel.color = reader.getColor(x, y);
                imageData.pixels.add(pixel);
            }
        }
        return imageData;
    }
}
