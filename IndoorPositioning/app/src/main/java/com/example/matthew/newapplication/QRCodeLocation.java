package com.example.matthew.newapplication;

/**
 * Created by Matthew on 1/22/2015.
 */
public class QRCodeLocation {
    private int pos;
    private String code,build,flo;
    private String id;

    public QRCodeLocation(String id,String building, String floor, int position) {
        build=building;
        flo=floor;
        code=id;
        pos=position;
    }

    public String printLocation(){
        return ("Building: "+build+" , Floor: "+flo);
    }

    public String getID(){return code;}

    public String getBuilding(){return build;}

    public String getFloor(){return flo;}

    public int getPosition(){return pos;}

}
