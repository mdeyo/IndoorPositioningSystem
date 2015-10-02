package com.example.matthew.newapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class HighPointPriority {
    public ArrayList<String> goalPoint;
    private Graph G;

    public HighPointPriority(Graph Gmap){
        this.G = Gmap;
        goalPoint = new ArrayList<String>();
        goalPoint = HighPointArrayList(G, G.getStart(),G.getGoal());

    }
    public ArrayList<String> HighPointArrayList(Graph Gmap, String start, String goal) {
        ArrayList <String> goals = new ArrayList<String>();

        HashMap<String,Integer> map = new HashMap<String,Integer>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(bvc);
        for(int m=0; m < Gmap.toList(Gmap.vertices()).size(); m++){


            String current = Gmap.toList(Gmap.vertices()).get(m);
            if(Gmap.getPointsAt(current)>0){
                map.put(current, Gmap.getPointsAt(current));

            }


        }
        sortedMap.putAll(map);
        goals.clear();
        goals.addAll(sortedMap.keySet());
        goals.add(goal);

        return goals;
    }


    public String getGoalPoint(int index) {
        return goalPoint.get(index);
    }
    public String removeGoalPoint(int index) {
        return goalPoint.remove(index);
    }
    public String getGoalPointFirst() {
        return goalPoint.get(0);
    }
}
