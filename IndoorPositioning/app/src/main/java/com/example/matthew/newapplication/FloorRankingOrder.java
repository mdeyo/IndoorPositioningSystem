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
//    private int floorMod;
//    private int stairMod;
    public FloorRankingOrder (Graph G, int initDist, int floorM, int stairM) {
        this.map = G;
//        this.stairMod = stairM;
//        this.floorMod = floorM;
        distanceFromStartLogic = new HashMap();
        goalList = new ArrayList<String>();
        this.initialDistance = initDist;
//    	StopWatch floorCommArray = new StopWatch();
//    	floorCommArray.start();
        goalList = CommonSenseArrayList(map,map.getStart(),map.getGoal(), initialDistance);
//        floorCommArray.stop();
//        System.out.println("Run time floorCommArray: " + "\t" +  floorCommArray.getElapsedTime() + "\t" + " ms");

    }
    
    public void markDistanceFromStartLogic (String start, Graph G, int initialDistance){
    	distanceFromStartLogic.put(start, initialDistance);
    	String current = start;
    	ArrayList<String> nList = new ArrayList<String>();
    	ArrayList<String> oldNeighbor = new ArrayList<String>();
    	int distance = initialDistance;
    	oldNeighbor.add(start);
    	int k = 0;
    	while(G.toList(G.vertices()).size()!=distanceFromStartLogic.size()){
    		
    		for(k=0; k<oldNeighbor.size(); k++){
    			nList = G.adjacentTo(oldNeighbor.get(k));
    			distance = Integer.parseInt(distanceFromStartLogic.get(oldNeighbor.get(k)).toString());
    			

    			for(String n: nList){
    				int nDistance = 0;
    				if(map.isStairs(n)){
    					nDistance = distance + G.getPointsAt(n) - G.getFloorWeight(n);	
    				}
    				else{
    					nDistance = distance + G.getPointsAt(n) - G.getFloorWeight(n);
    				}
    				if(!distanceFromStartLogic.containsKey(n)){
    					distanceFromStartLogic.put(n, nDistance);
    						if(!oldNeighbor.contains(n)){
    							oldNeighbor.add(n);
    						}}
    				else if(Integer.parseInt(distanceFromStartLogic.get(n).toString())<nDistance){
    					distanceFromStartLogic.remove(n);
    					distanceFromStartLogic.put(n, nDistance);
    					
    				}
    				}}}}


    public ArrayList<String> CommonSenseArrayList(Graph map, String start, String goal, int initDist) {
    	String current = start;
		HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
        ValueComparator bvc =  new ValueComparator(hashMap);
        TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(bvc);
        int dist = initDist;
    	while(!goalList.contains(goal)){
//        	StopWatch floorMarkDistStart = new StopWatch();
//        	floorMarkDistStart.start();
    		markDistanceFromStartLogic (current, map, dist);
//    		floorMarkDistStart.stop();
//            System.out.println("Run time floorMarkDistStart: " + "\t" + floorMarkDistStart.getElapsedTime()  + "\t" + " ms");

    		for(String n: map.toList(map.vertices())){
    			if(Integer.parseInt(distanceFromStartLogic.get(n).toString()) < 0){
    				continue;
    			}
    			else if(n.equals(goal)){
    				if(goalList.size()<4){
    					continue;
    				}
    				else{
    				goalList.add(goal);
    				return goalList;
    				}
    			}
    			else if(map.getPointsAt(n) == 0){
    				continue;
    			}
    			else if(!goalList.contains(n)){    		        
    		        hashMap.put(n, map.getPointsAt(n));  	  		  		        
    			}  			
    		}
    		if(!hashMap.isEmpty()){
    	        sortedMap.putAll(hashMap);
    	        goalList.add(sortedMap.firstKey());
    	        current = sortedMap.firstKey();
    	        hashMap.clear();
    	        sortedMap.clear();
    	        dist = initDist;
    	        distanceFromStartLogic.clear();
    			}
    		else {
    			dist = dist + 20;
    			distanceFromStartLogic.clear();
    		}

    		
    	}
    	
    	
    	
//    	System.out.println(goalList);
    	return goalList;
    	
    }
	
}