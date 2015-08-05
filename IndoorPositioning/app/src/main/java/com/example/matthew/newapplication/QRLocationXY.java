package com.example.matthew.newapplication;

/**
 * Created by Matthew on 2/28/2015.
 */
public class QRLocationXY {
    public int x, y;
    public int points;
    String full;

    public QRLocationXY(String full,int X, int Y, int p) {
        this.full=full;
        x = X;
        y = Y;
        this.points = p;
    }

    @Override
    public String toString() {
        return full + "-" + points + " , ";
    }
}

