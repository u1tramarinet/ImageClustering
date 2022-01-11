package com.u1tramarinet.imageclustering.model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private final Point centroid;
    private final List<Pixel> pixels = new ArrayList<>();

    public Cluster(Point centroid) {
        this.centroid = centroid;
    }

    public Cluster(Point centroid, List<Pixel> pixels) {
        this.centroid = centroid;
        this.pixels.addAll(pixels);
    }

    public Point getCentroid() {
        return this.centroid;
    }

    public void addPixel(Pixel pixel) {
        pixels.add(pixel);
    }

    public void addPixels(List<Pixel> pixels) {
        this.pixels.addAll(pixels);
    }

    public List<Pixel> getPixels() {
        return this.pixels;
    }

    public void clear() {
        this.pixels.clear();
    }
}
