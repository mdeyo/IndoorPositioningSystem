package com.example.matthew.newapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Matthew on 1/11/2015.
 */
public class FingerprintingAlg {


    private int pos;
    private ArrayList<GridData> dataPoints;
    private RouterObject[] scan;
    private GridData result;
    private String currentBuilding, currentFloor;
    private float accelerations;

    private String prevFloor, prevBuilding = "";
    private int prevPosition = 0;
    private float previousAccelerationData;

    private boolean edgeEffect;
    private Graph G;

    //TODO design new floor switching technique
    boolean accelerationZ = false;

    public FingerprintingAlg(int currentPosition,Graph graph, float accelerationData, float previousAccelerationData, String currentFloor, String currentBuilding, ArrayList<GridData> data, RouterObject[] scanned) {
        this.G=graph;
        pos = currentPosition;
        dataPoints = data;
        scan = scanned;
        accelerations = accelerationData;
        //this.prevPosition=previousPosition;
        this.previousAccelerationData = previousAccelerationData;
        this.currentBuilding = currentBuilding;
        this.currentFloor = currentFloor;
        edgeEffect = false;

        String graphVertex = currentBuilding+":"+currentFloor+":"+String.valueOf(currentPosition);

        if(G.hasVertex(graphVertex)){
            Log.d("check location value","found in Graph!");
        //TODO new algorithm using Graph points

        }



        ArrayList<Integer> localPositions = new ArrayList<Integer>();
        localPositions.clear();

        Log.d("currentA", String.valueOf(accelerations));
        Log.d("previousA", String.valueOf(previousAccelerationData));

        if (accelerationData > 16 && previousAccelerationData > 14) {
            Log.d("alg", "accelerations!");
            //faster linear acceleration -> don't include previous location
            //create acceptable range of positions
            // 5x5 grid around previous location makes 25 possible positions to use
            int numberOfColumns = 24;
            for (int i = -2; i < 3; i++) {
                localPositions.add(pos + numberOfColumns * i);
                localPositions.add(pos + numberOfColumns * i - 1);
                localPositions.add(pos + numberOfColumns * i - 2);
                localPositions.add(pos + numberOfColumns * i + 1);
                localPositions.add(pos + numberOfColumns * i + 2);
            }
            localPositions.remove(10); //remove current position -> forces movement on display

        } else {
            //create a smaller acceptable range of positions
            // 3x3 grid around previous location makes 25 possible positions to use
            int numberOfColumns = 24;
            for (int i = -1; i < 2; i++) {
                localPositions.add(pos + numberOfColumns * i);
                localPositions.add(pos + numberOfColumns * i - 1);
                localPositions.add(pos + numberOfColumns * i + 1);
            }
        }

        //if previous position was near edge of the map -> possibility of changing buildings needs to be added
        ArrayList<Integer> edgePositions = new ArrayList<Integer>();
        if (currentBuilding.equals("33") && currentFloor.equals("1") || currentFloor.equals("2")) {
            edgePositions.add(114);
            edgePositions.add(115);
            edgePositions.add(91);
            edgePositions.add(67);
            edgePositions.add(90);
            if (edgePositions.contains(currentPosition)) {
                edgeEffect = true;
                Log.d("alg", "edge effects added");
            }
        }

        if (currentBuilding.equals("35") && currentFloor.equals("1")) {
            edgePositions.add(109);
            edgePositions.add(133);
            edgePositions.add(157);
            if (edgePositions.contains(currentPosition)) {
                edgeEffect = true;
                Log.d("alg", "edge effects added");
            }
        }

        // Prepping the fingerprinting algorithm by only including locations within the localPositions defined above
        ArrayList<GridData> edit0 = new ArrayList<GridData>();

        for (int i = 0; i < dataPoints.size(); i++) {
            GridData obj = dataPoints.get(i);
            int position = obj.getPosition();
            if (edgeEffect) {
                String upFloor = String.valueOf(Integer.parseInt(this.currentFloor) + 1);
                String downFloor = String.valueOf(Integer.parseInt(this.currentFloor) - 1);
                if (accelerationZ) {
                    if (localPositions.contains(position) || obj.getFloor().equals(downFloor) || obj.getFloor().equals(upFloor) || !obj.getBuilding().equals(currentBuilding)) {
                        edit0.add(obj);
                    }
                } else if (obj.getFloor().equals(currentFloor) && localPositions.contains(position) || !obj.getBuilding().equals(currentBuilding)) {
                    edit0.add(obj);
                }
            } else if (localPositions.contains(position) && obj.getFloor().equals(currentFloor)) {
                edit0.add(obj);
            }
        }
        Log.d("alg", "check0 had " + Integer.toString(edit0.size()) + " results");


        if (edit0.size() == 0) {
            edit0 = dataPoints;
        }

        // First iteration only keeps GridData objects that contain the first three routers that the scan found
        ArrayList<GridData> edit1 = new ArrayList<GridData>();

        for (int i = 0; i < edit0.size(); i++) {
            GridData obj = edit0.get(i);
            //Log.d("alg", "first check: " + Integer.toString(i));

            firstIteration:
            //matches up 3 routers
            for (int m = 0; m < scan.length; m++) {
                if (obj.getRouterIDs().contains(scan[m].printBSSID())) {
                    for (int n = 0; n < scan.length; n++) {
                        if (n != m && obj.getRouterIDs().contains(scan[n].printBSSID())) {
                            for (int o = 0; o < scan.length; o++) {
                                if (o != n && o != m && obj.getRouterIDs().contains(scan[o].printBSSID())) {
                                    edit1.add(obj);
                                    break firstIteration;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (edit1.size() == 0) {
            Log.d("alg", "check1 had no matches");
            result = null;
        } else if (edit1.size() == 1) {
            Log.d("alg", "check1 only had 1");
            result = edit1.get(0);
        } else if (edit1.size() > 1) {
            ArrayList<GridData> edit2 = new ArrayList<GridData>();
            Log.d("alg", "check1 had " + Integer.toString(edit1.size()) + " results");

            for (int i = 0; i < edit1.size(); i++) {
                GridData obj = edit1.get(i);
                RouterObject[] check2 = obj.getRouters();
                //Log.d("alg", "second check: " + Integer.toString(i) + " id: " + obj.printID());

                secondIteration:
                //matches up 5 routers
                for (int m = 0; m < scan.length; m++) {
                    if (obj.getRouterIDs().contains(scan[m].printBSSID())) {
                        for (int n = 0; n < scan.length; n++) {
                            if (n != m && obj.getRouterIDs().contains(scan[n].printBSSID())) {
                                for (int o = 0; o < scan.length; o++) {
                                    if (o != n && o != m && obj.getRouterIDs().contains(scan[o].printBSSID())) {
                                        for (int p = 0; p < scan.length; p++) {
                                            if (p != o && p != n && p != m && obj.getRouterIDs().contains(scan[p].printBSSID())) {
                                                for (int q = 0; q < scan.length; q++) {
                                                    if (q != p && q != o && q != n && q != m && obj.getRouterIDs().contains(scan[q].printBSSID())) {
                                                        edit2.add(obj);
                                                        break secondIteration;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (edit2.size() == 0) {
                Log.d("alg", "check2 had no matches, put check1 into secondAlg");
                result = secondAlgorithm(edit1, scan);
            } else if (edit2.size() == 1) {
                Log.d("alg", "check2 only had 1");
                result = edit2.get(0);
            } else {

                ArrayList<GridData> edit3 = new ArrayList<GridData>();
                Log.d("alg", "check2 had " + Integer.toString(edit2.size()) + " results");
                result = secondAlgorithm(edit2, scan);
            }
//                /*
//
//                //third iteration! using two routers signal strengths
//                for (int i = 0; i < edit2.size(); i++) {
//                    GridData obj = edit2.get(i);
//                    RouterObject[] check3 = obj.getRouters();
//                    //Log.d("alg", "third check: " + Integer.toString(i) + " id: " + obj.printID());
//
//                    thirdIteration:
//                    for (int m = 0; m < scan.length; m++) {
//                        if (obj.getRouterIDs().contains(scan[m].printBSSID())) {
//                            for (int n = 0; n < check3.length; n++) {
//                                if (scan[m].printBSSID().equals(check3[n].printBSSID())) {
//                                    int strengthOfData = check3[n].getStrength();
//                                    int strengthOfScan = scan[m].getStrength();
//                                    //Log.d("alg", scan[m].printBSSID() + "= strengths " + Integer.toString(strengthOfData) + "/" + Integer.toString(strengthOfScan));
//
//                                    //if strengths of first router strengths are close enough --> continue to check second router
//                                    if (Math.abs(strengthOfScan - strengthOfData) < 4) {
//
//                                        for (int o = 0; o < scan.length; o++) {
//                                            if (o != m && obj.getRouterIDs().contains(scan[o].printBSSID())) {
//                                                for (int p = 0; p < check3.length; p++) {
//                                                    if (scan[o].printBSSID().equals(check3[p].printBSSID())) {
//                                                        int strengthOfData2 = check3[p].getStrength();
//                                                        int strengthOfScan2 = scan[o].getStrength();
//                                                        //Log.d("alg", scan[o].printBSSID() + "= strengths " + Integer.toString(strengthOfData2) + "/" + Integer.toString(strengthOfScan2));
//
//                                                        //if strengths of second router are close enough --> add object
//                                                        if (Math.abs(strengthOfScan2 - strengthOfData2) < 4) {
//                                                            edit3.add(obj);
//                                                            break thirdIteration;
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (edit3.size() == 0) {
//                    Log.d("alg", "check3 had no matches, chose first from check2");
//                    result = edit2.get(0);
//                } else if (edit3.size() == 1) {
//                    Log.d("alg", "check3 only had 1");
//                    result = edit3.get(0);
//                } else {
//
//                    ArrayList<GridData> edit4 = new ArrayList<GridData>();
//                    Log.d("alg", "check3 had " + Integer.toString(edit3.size()) + " results");
//                    result=secondAlgorithm(edit3,scan);
//*/
//
//                    /*
//                    //fourth iteration! using three routers signal strengths
//                    for (int i = 0; i < edit3.size(); i++) {
//                        GridData obj = edit3.get(i);
//                        RouterObject[] check4 = obj.getRouters();
//                        //Log.d("alg", "fourth check: " + Integer.toString(i) + " id: " + obj.printID());
//
//                        fourthIteration:
//                        for (int m = 0; m < scan.length; m++) {
//                            if (obj.getRouterIDs().contains(scan[m].printBSSID())) {
//                                for (int n = 0; n < check4.length; n++) {
//                                    if (scan[m].printBSSID().equals(check4[n].printBSSID())) {
//                                        int strengthOfData = check4[n].getStrength();
//                                        int strengthOfScan = scan[m].getStrength();
//                                        //Log.d("alg", scan[m].printBSSID() + "= strengths " + Integer.toString(strengthOfData) + "/" + Integer.toString(strengthOfScan));
//
//                                        //if strengths of first router strengths are close enough --> continue to check second router
//                                        if (Math.abs(strengthOfScan - strengthOfData) < 4) {
//                                            for (int o = 0; o < scan.length; o++) {
//                                                if (o != m && obj.getRouterIDs().contains(scan[o].printBSSID())) {
//                                                    for (int p = 0; p < check4.length; p++) {
//                                                        if (scan[o].printBSSID().equals(check4[p].printBSSID())) {
//                                                            int strengthOfData2 = check4[p].getStrength();
//                                                            int strengthOfScan2 = scan[o].getStrength();
//                                                            //Log.d("alg", scan[o].printBSSID() + "= strengths " + Integer.toString(strengthOfData2) + "/" + Integer.toString(strengthOfScan2));
//
//                                                            //if strengths of second router are close enough --> continue to check third router
//                                                            if (Math.abs(strengthOfScan2 - strengthOfData2) < 4) {
//                                                                for (int q = 0; q < scan.length; q++) {
//                                                                    if (q!=0 && q!=m && obj.getRouterIDs().contains(scan[q].printBSSID())) {
//                                                                        for (int r = 0; r < check4.length; r++) {
//                                                                            if (scan[o].printBSSID().equals(check4[r].printBSSID())) {
//                                                                                int strengthOfData3 = check4[r].getStrength();
//                                                                                int strengthOfScan3 = scan[q].getStrength();
//                                                                                //Log.d("alg", scan[q].printBSSID() + "= strengths " + Integer.toString(strengthOfData3) + "/" + Integer.toString(strengthOfScan3));
//
//                                                                                //if strengths of third router are close enough --> add object
//                                                                                if (Math.abs(strengthOfScan3 - strengthOfData3) < 4) {
//                                                                                    edit4.add(obj);
//                                                                                    break fourthIteration;
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                    */
//
//                    /*
//                    if (edit4.size() == 0) {
//                        Log.d("alg", "check4 had no matches");
//                        result = edit3.get(0);
//                    } else if (edit4.size() == 1) {
//                        Log.d("alg", "check4 only had 1");
//                        result = edit4.get(0);
//                    } else {
//                        Log.d("alg", "check4 had " + Integer.toString(edit4.size()) + " results");
//                        ArrayList<GridData> edit5 = new ArrayList<GridData>();
//                        result=secondAlgorithm(edit4,scan);
//                    }
//
//                }
//            }
//    */
        }

        //prevBuilding=this.currentBuilding;
        //prevFloor=this.currentFloor;
        //prevPosition=pos;
        //previousAccelerationData=this.accelerations;


    }


    public GridData getResult() {
        return result;
    }

    private GridData secondAlgorithm(ArrayList<GridData> data, RouterObject[] scan) {

        //Scoring//
        int missingRouter = 20;
        int weightForDifferenceInStrength = 1;
        //////////

        GridData finalResult;
        int numberOfRouters = scan.length;
        int numberOfGridObjects = data.size();
        ArrayList<RouterObject> scanArray = new ArrayList<RouterObject>();
        int[] scores = new int[numberOfGridObjects];

        for (int i = 0; i < numberOfRouters; i++) {
            scanArray.add(scan[i]);
        }


        //Log.d("2nd alg",String.valueOf(scanArray.size())+" routers");

//////////////////////////////////////////////////////////
        for (int i = 0; i < numberOfGridObjects; i++) {
            int pointsForGridObject = 0;
            GridData obj = data.get(i);
            RouterObject[] rawr = obj.getRouters();
            ArrayList<RouterObject> routerArray = obj.getRouterArray();

            int routersMatched = 0;

            for (int m = 0; m < scanArray.size(); m++) {
                if (!obj.getRouterIDs().contains(scanArray.get(m).printBSSID())) {
                    pointsForGridObject += missingRouter;
                } else {
                    strengthCompare:
                    for (int n = 0; n < routerArray.size(); n++) {
                        if (scanArray.get(m).printBSSID().equals(routerArray.get(n).printBSSID())) {
                            routersMatched += 1;
                            int strengthFromScan = scanArray.get(m).getStrength();
                            int strengthFromData = routerArray.get(n).getStrength();
                            pointsForGridObject += weightForDifferenceInStrength * Math.abs(strengthFromData - strengthFromScan);
                            break strengthCompare;
                        }
                    }
                }
            }
            //important print statement for 2nd alg performance
            Log.d("2nd alg", obj.printID() + " : " + String.valueOf(routersMatched) + " routers matched : " + pointsForGridObject + " points");

            scores[i] = pointsForGridObject;
        }
//////////////////////////////////////////////////////////

        int smallest = scores[0];
        int finalIndex = 0;

        for (int i = 1; i < numberOfGridObjects; i++) {
            if (scores[i] < smallest) {
                smallest = scores[i];
                finalIndex = i;
            }
        }

        finalResult = data.get(finalIndex);
        return finalResult;
    }
}

