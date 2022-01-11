package com.u1tramarinet.imageclustering.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Clustering {

    private final Random random = new Random();
    private static final boolean LOG_ENABLED = true;
    private static final boolean DETAIL_LOG_ENABLED = false;

    public ImageData execute(ImageData imageData) {
        List<Pixel> pixels = imageData.pixels;
        if (pixels == null || pixels.isEmpty()) {
            return imageData;
        }
        List<Pixel> transparentPixels = new ArrayList<>();
        for (Pixel pixel : pixels) {
            if (pixel.getColor().getOpacity() == 0d) {
                transparentPixels.add(pixel);
            }
        }
        pixels.removeAll(transparentPixels);
        pixels = executeInternal(pixels);
        imageData.pixels.clear();
        imageData.pixels.addAll(pixels);
        imageData.pixels.addAll(transparentPixels);
        return imageData;
    }

    private List<Pixel> executeInternal(List<Pixel> pixels) {
        final int k = 4;
        final int trialCount = 10;
        final int d = getDegree(pixels);
        List<Cluster> clusters = null;
        List<Point> centroids = createInitialCentroids(k, d, pixels);
        for (int i = 0; i < trialCount; i++) {
            outputLog("clustering progress(" + (i + 1) + "/" + trialCount + ") start");
            clusters = assignPixelsToClusters(centroids, pixels);
            centroids = createCentroids(k, d, clusters);
            outputLog("clustering progress(" + (i + 1) + "/" + trialCount + ") finish");
        }
        return createPixelsFromCentroids(clusters);
    }

    private int getDegree(List<Pixel> points) {
        int degree = Integer.MAX_VALUE;
        for (Point point : points) {
            degree = Math.min(degree, point.getDegree());
        }
        return degree;
    }

    private List<Point> createInitialCentroids(int k, int d, List<Pixel> pixels) {
        outputLog("createInitialCluster() start");
        List<Range<Double>> ranges = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            ranges.add(new Range<>(Double.MAX_VALUE, Double.MIN_VALUE));
        }
        for (Pixel pixel : pixels) {
            for (int i = 0; i < d; i++) {
                ranges.get(i).min = Math.min(ranges.get(i).min, pixel.get(i));
                ranges.get(i).max = Math.max(ranges.get(i).max, pixel.get(i));
            }
        }

        List<Point> centroids = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            Point centroid = new Point(d);
            for (int j = 0; j < d; j++) {
                Range<Double> r = ranges.get(j);
                double value = random.nextDouble() * (r.max - r.min) + r.min;
                centroid.set(j, value);
            }
            centroids.add(centroid);
        }
        outputLog("createInitialCluster() finish");
        return centroids;
    }

    private List<Point> createCentroids(int k, int d, List<Cluster> clusters) {
        outputLog("createCentroids() start");
        List<Point> centroids = new ArrayList<>();
        List<Double> averages = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            averages.add(0d);
        }
        for (int i = 0; i < k; i++) {
            outputDetailLog("createCentroids() create (" + (i + 1) + "/" + k + ") start");
            Cluster cluster = clusters.get(i);

            int size = cluster.getPixels().size();
            for (Pixel pixel : cluster.getPixels()) {
                for (int j = 0; j < d; j++) {
                    double oldValue = averages.get(j);
                    double value = pixel.get(j);
                    averages.set(j, oldValue + value);
                }
            }

            Point centroid = new Point(d);
            for (int j = 0; j < d; j++) {
                double sum = averages.get(j);
                centroid.set(j, sum / size);
            }
            centroids.add(centroid);
            outputDetailLog("createCentroids() create (" + (i + 1) + "/" + k + ") =" + centroid);
            outputDetailLog("createCentroids() create (" + (i + 1) + "/" + k + ") finish");
        }
        outputLog("createCentroids() finish");
        return centroids;
    }

    private List<Cluster> assignPixelsToClusters(List<Point> centroids, List<Pixel> pixels) {
        outputDetailLog("assignPixelsToClusters() start");
        List<Cluster> clusters = new ArrayList<>();
        for (Point centroid : centroids) {
            clusters.add(new Cluster(centroid));
        }
        for (Pixel pixel : pixels) {
            int nearestIndex = findNearestCluster(pixel, clusters);
            clusters.get(nearestIndex).addPixel(pixel);
        }
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            outputDetailLog("assignPixelsToClusters() cluster-" + (i + 1) + ": centroid=" + cluster.getCentroid() + ", pixel count=" + cluster.getPixels().size());
        }
        outputDetailLog("assignPixelsToClusters() finish");
        return clusters;
    }

    private int findNearestCluster(Pixel pixel, List<Cluster> clusters) {
        outputDetailLog("findNearestCluster() start");
        double minDistance = Double.MAX_VALUE;
        int minCentroidIndex = 0;
        for (int i = 0; i < clusters.size(); i++) {
            double distance = Distance.calculateDistance(pixel, clusters.get(i).getCentroid());
            if (minDistance > distance) {
                minDistance = distance;
                minCentroidIndex = i;
            }
        }
        outputDetailLog("findNearestCluster() finish");
        return minCentroidIndex;
    }

    private List<Pixel> createPixelsFromCentroids(List<Cluster> clusters) {
        List<Pixel> newPixels = new ArrayList<>();
        for (Cluster cluster : clusters) {
            Point centroid = cluster.getCentroid();
            System.out.println("centroid r=" + centroid.get(0) + ", g=" + centroid.get(1) + ", b=" + centroid.get(2));
            System.out.println("cluster size=" + cluster.getPixels().size());
            for (Pixel pixel : cluster.getPixels()) {
                for (int d = 0; d < pixel.getDegree(); d++) {
                    pixel.set(d, centroid.get(d));
                }
                newPixels.add(pixel);
            }
        }
        return newPixels;
    }

    private void outputLog(String message) {
        if (!LOG_ENABLED) return;
        System.out.println(message);
    }

    private void outputDetailLog(String message) {
        if (!DETAIL_LOG_ENABLED) return;
        outputLog(message);
    }
}
