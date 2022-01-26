package com.u1tramarinet.imageclustering.model;

import javafx.scene.paint.Color;

import java.util.Arrays;

public class Pixel extends Point {
    private static final int DEGREE = 3;
    private final int x;
    private final int y;
    private Color color;

    public Pixel(int x, int y, Color color) {
        super(Arrays.asList(color.getRed(), color.getGreen(), color.getBlue()));
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setColor(Color color) {
        this.color = color;
        set(0, color.getRed());
        set(1, color.getGreen());
        set(2, color.getBlue());
    }

    @Override
    public void set(int index, double value) {
        if (index >= DEGREE) {
            throw new IllegalArgumentException();
        }
        switch (index) {
            case 0:
                color = Color.color(value, color.getGreen(), color.getBlue(), color.getOpacity());
            case 1:
                color = Color.color(color.getRed(), value, color.getBlue(), color.getOpacity());
            case 2:
                color = Color.color(color.getRed(), color.getGreen(), value, color.getOpacity());
        }
        super.set(index, value);
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public int getDegree() {
        return DEGREE;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + color +
                '}';
    }
}
