package com.example.matthew.newapplication;

import java.util.ArrayList;

/**
 * Created by Matthew on 1/30/2015.
 */
public class ListHistory {

    private ArrayList<String> history;

    public void ListHistory(){
        history = new ArrayList<>();
    }

    public ArrayList<String> updateHistory(GridData chosen){
        String location = chosen.printFullLocation();
        String building = chosen.getBuilding();
        String flo = chosen.getFloor();
        int position = chosen.getPosition();

        if(history.contains(location)){
            //nothing
        }
        else{
            String previousLocation = history.get(history.size()-1);
            ArrayList<String> nearLastPoint;
            String previousBuilding = previousLocation.substring(4,6);


        }
        return  history;
    }
}

