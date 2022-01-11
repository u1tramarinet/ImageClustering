package com.u1tramarinet.imageclustering.model;

public class Distance {

    private Distance() {
    }

    public static double calculateDistance(Point one, Point another) {
        double sum = 0;
        for (int i = 0; i < one.getDegree(); i++) {
            double sub = one.get(i) - another.get(i);
            sum += Math.pow(sub, 2);
        }
        return Math.sqrt(sum);
    }
}
