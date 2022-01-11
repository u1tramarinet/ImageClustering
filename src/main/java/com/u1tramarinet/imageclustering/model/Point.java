package com.u1tramarinet.imageclustering.model;

import java.util.ArrayList;
import java.util.List;

public class Point {
    private final List<Double> coordinates = new ArrayList<>();

    public Point(int degree) {
        for (int i = 0; i < degree; i++) {
            coordinates.add(0d);
        }
    }

    public Point(List<Double> coordinates) {
        this.coordinates.addAll(coordinates);
    }

    public void set(int index, double value) {
        if (coordinates.size() <= index) {
            throw new IllegalArgumentException();
        }
        coordinates.set(index, value);
    }

    public double get(int index) {
        if (coordinates.size() <= index) {
            throw new IllegalArgumentException();
        }
        return coordinates.get(index);
    }

    public int getDegree() {
        return coordinates.size();
    }

    @Override
    public String toString() {
        return "Point{" +
                "coordinates=" + coordinates +
                '}';
    }
}
