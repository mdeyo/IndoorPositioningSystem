package com.example.matthew.newapplication;

import java.util.ArrayList;

/**
 * Created by Matthew on 1/7/2015.
 */
public class GridData implements Comparable<GridData> {

    private RouterObject[] routers;
    private int pos;
    private String id, floor, building, stringIds = "";
    private ArrayList<String> routerIDs = new ArrayList<String>();
    private ArrayList<RouterObject> routerArray = new ArrayList<RouterObject>();
    private int count;

    public GridData(RouterObject[] listOfRouters, int position, String building, String floor, String id) {
        this.routers = listOfRouters;
        this.floor = floor;
        this.building = building;
        this.pos = position;
        this.id = id;

        for (int i = 0; i < routers.length; i++) {
            stringIds = stringIds + routers[i].printBSSID() + "=" + Integer.toString(routers[i].getStrength()) + "/";
            //Log.d("grid",Integer.toString(i) );
            routerIDs.add(routers[i].printBSSID());
            routerArray.add(routers[i]);
        }


    }

    @Override
    public int compareTo(GridData another) {
        return (pos - another.pos);
    }

    public String printPosition() {
        return Integer.toString(pos);
    }

    public String printFullLocation() {
        return this.building + ":" + this.floor + ":" + Integer.toString(pos);
    }

    public String printNodeString() {
        return this.building + "." + this.floor + "." + Integer.toString(pos);
    }

//    public void putNewRouters(RouterObject[] newAverages) {
//        this.routers = newAverages;
//    }

    public int getPosition() {
        return pos;
    }

    public ArrayList<RouterObject> getRouterArray() {
        return routerArray;
    }

    public String printRouters() {
        return stringIds;
    }

    public String getFloor() {
        return this.floor;
    }

    public String printID() {
        return this.id;
    }

    public void setCount(int number) {
        this.count = number;
    }

    public int getCount() {
        return this.count;
    }

    public void setTempID() {
        this.id = "temp";
    }

    public RouterObject[] getRouters() {
        return routers;
    }

    public ArrayList<String> getRouterIDs() {
        return routerIDs;
    }

    public String getBuilding() {
        return this.building;
    }

    public String print() {
        //return name+":"+id+": "+ Integer.toString(strength);
        return Integer.toString(pos) + "--" + stringIds;
    }

    public String printFirstRouter() {
        return routers[0].printBSSID();
    }
}
