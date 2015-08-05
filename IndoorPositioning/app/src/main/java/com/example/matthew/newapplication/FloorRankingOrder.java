package com.example.matthew.newapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FloorRankingOrder {
    private Graph map;
    private Map distanceFromStartLogic;
    public ArrayList<String> goalList;
    private int initialDistance;

    public FloorRankingOrder(Graph G, int initDist) {
        this.map = G;
        distanceFromStartLogic = new HashMap();
        goalList = new ArrayList<String>();
        this.initialDistance = initDist;
        goalList = CommonSenseArrayList(map, map.getStart(), map.getGoal(), initialDistance);

    }

    public void markDistanceFromStartLogic(String start, Graph G, int initialDistance) {
        distanceFromStartLogic.put(start, initialDistance);
        String current = start;
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        int distance = initialDistance;
        oldNeighbor.add(start);
        int k = 0;
        while (G.toList(G.vertices()).size() != distanceFromStartLogic.size()) {
            // Like Astar, so we need two arrays, one with checked neighbors that have been assigned a distance
            // and one that has the next layer of neighbors to be assigned
            // We assign the distance value to ones that haven't already been assigned

            for (k = 0; k < oldNeighbor.size(); k++) {
//    			oldNeighbor.remove(k);
                nList = G.adjacentTo(oldNeighbor.get(k));
                distance = Integer.parseInt(distanceFromStartLogic.get(oldNeighbor.get(k)).toString());


                for (String n : nList) {
                    int nDistance = distance + G.getPointsAt(n) - G.getFloorWeight(n);
                    if (!distanceFromStartLogic.containsKey(n)) {
                        distanceFromStartLogic.put(n, nDistance);
                        if (!oldNeighbor.contains(n)) {
                            oldNeighbor.add(n);
                        }
                    } else if (Integer.parseInt(distanceFromStartLogic.get(n).toString()) < nDistance) {
                        distanceFromStartLogic.remove(n);
                        distanceFromStartLogic.put(n, nDistance);

                    }
                }
            }
        }
    }


    public ArrayList<String> CommonSenseArrayList(Graph map, String start, String goal, int initDist) {
        String current = start;
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(hashMap);
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
        int dist = initDist;
        while (!goalList.contains(goal)) {

            markDistanceFromStartLogic(current, map, dist);

            for (String n : map.toList(map.vertices())) {
                if (Integer.parseInt(distanceFromStartLogic.get(n).toString()) < 0) {
                    continue;
                } else if (n.equals(goal)) {
                    goalList.add(goal);
                } else if (map.getPointsAt(n) == 0) {
                    continue;
                } else if (!goalList.contains(n)) {
                    hashMap.put(n, map.getPointsAt(n));
                }
            }
            if (!hashMap.isEmpty()) {
                sortedMap.putAll(hashMap);
                goalList.add(sortedMap.firstKey());
                current = sortedMap.firstKey();
                hashMap.clear();
                sortedMap.clear();
                dist = initDist;
                distanceFromStartLogic.clear();
//    	        System.out.println(goalList);
            } else {
                dist = dist + dist;
//    			System.out.println("This isn't working...");
                distanceFromStartLogic.clear();
            }


        }


        return goalList;

    }

}