package com.example.matthew.newapplication;

/**
 * Created by Matthew on 2/28/2015.
 */
public class Point {
    float x, y;
    float dx, dy;

    public void Point(int X, int Y) {
        x = X;
        y = Y;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}

