package com.u1tramarinet.imageclustering.model;

public class Range<T extends Number> {
    public T min;
    public T max;

    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }
}
