package com.example.matthew.newapplication;

/**
 * Created by Matthew on 1/22/2015.
 */
public class QRCodeLocation {
    private int pos, points;
    private String code, build, flo;
    private String full;

    public QRCodeLocation(String full, int points) {
        this.full = full;
        this.points = points;
        build = full.split(":")[0];
        flo = full.split(":")[1];
//        code = id;
        pos = Integer.parseInt(full.split(":")[2]);
    }

    public String printLocation() {
        return ("Building: " + build + " , Floor: " + flo);
    }

    public String getID() {
        return code;
    }

    public String getBuilding() {
        return build;
    }

    public String getFloor() {
        return flo;
    }

    public int getPosition() {
        return pos;
    }

    public int getPoints() {
        return points;
    }

    public String getFullPosition() {
        return full;
    }

    public void removePoints() {
        points = 0;
    }

}
