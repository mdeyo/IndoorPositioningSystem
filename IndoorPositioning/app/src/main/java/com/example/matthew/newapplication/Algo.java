//package com.example.matthew.newapplication;
//import android.util.Log;
package com.example.matthew.newapplication;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Algo {

    private Graph localG; // Defines the connections between nodes and edges and weightings/points
    private ArrayList<String> frontierList; // Must define node
    private ArrayList<String> cameFromList;
    private String start;
    private String goal;
    public int timeLength;
    public Path shortestPath;
    private Map localDistanceFromGoal;
    private Map deviationsFromPathCheck;
    public int pointsCollected;
    public int qrCollected;
    ArrayList<String> goalList;
    private ArrayList<String> localGoalList;
    private int weightedDist;
    private int stairMod;
    private int floorMod;
    private int shortPathLength;
    private Map<String, Integer> pointLocRemoved;
    private ArrayList<String> pointRemKeySet;
    int breakTime;

    int timeLeft;

    public Algo(Graph g, String start, String goal, int timeLeft, int stairM, int floorM,int breakT, ArrayList<String> goalList) { //, Heuristic heuristic){
    	this.stairMod = stairM;
        this.floorMod = floorM;
        this.localG = new Graph(g, stairMod, floorMod);
        this.shortPathLength = 1000;
        localG.setStartLocation(start);
        localG.setGoalLocation(goal);
        localG.gradientGraph(localG);
//        FloorRankingOrder goalLogic = new FloorRankingOrder(localG, 90, stairMod, floorMod);
        cameFromList = new ArrayList<String>();
        frontierList = new ArrayList<String>();
        this.goalList = new ArrayList<String>(goalList);
        localDistanceFromGoal = new HashMap();
        deviationsFromPathCheck = new HashMap<String, Integer>();
        this.start = start;
        this.goal = goal;
        this.timeLeft = timeLeft;
        timeLength = 0;
        pointsCollected = 0;
        weightedDist = 0;
        this.pointLocRemoved = new HashMap<String, Integer>();
        this.pointRemKeySet = new ArrayList<String>();
        this.breakTime = breakT;

        calcOptPath(start,timeLeft);
    }


    public Path calcOptPath(String currentStart,int timeR) {

        this.localGoalList = new ArrayList<String>(goalList);

        this.timeLeft=timeR;

        Log.d("calcOptPath",String.valueOf(timeLeft));

        int breakT = 180-breakTime;
        this.timeLength = 0;

        if(timeLeft==0){
            timeLeft=180;
        }

        if (currentStart.equals(goal)) {
            return null;
        }

        if(localG.getPointsAt(currentStart) > 0){
            removePoints(currentStart, localG);
            localGoalList.remove(currentStart);
        }

        if(localG.getPointsAt(this.goal) > 0){
            removePoints(this.goal, localG);
        }

        ArrayList<String> neighborCheck = new ArrayList<String>();

        //Check if the goal node is blocked (if it is, it is impossible to find a path there)
        if (localG.isObstacle(goal)) {
            return null;
        }

        localG.setDistanceFromStart(currentStart, 0);
        cameFromList.clear();
        frontierList.clear();
        frontierList.add(currentStart);
        neighborCheck.add(currentStart);
        String current = null;
        String currentGoal = getLocalGoalPointFirst();
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
        while (frontierList.size() != 0) {
            int totalDistance = 99999999;
            for (String mm : neighborCheck) {
                if (localG.getPointsAt(mm) > 0) {
                    totalDistance = getLocalTotalDistance(mm); // Due to the break, this does nothing
                    current = mm;
                    break;
                } else if (getLocalTotalDistance(mm) < totalDistance) {
                    totalDistance = getLocalTotalDistance(mm);
                    current = mm;
                }
            }
//            if (neighborCheck.size() == 0) {
//                // Then I need to go back to the previous node I was just at.
//                String neighborGoBack = cameFromList.get(cameFromList.size() - 2);
//                int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighborGoBack));
//                localG.setDistanceFromStart(neighborGoBack, neighborDistanceFromStart);
//                current = neighborGoBack;
//                System.out.println("Yes I do something");
//            }
            neighborCheck.clear();
            int currentFloor = 0;
            int previousFloor = 0;
            int currentBuilding = 0;
            //move current Node to the closed (already searched) list
            if(current.equals(start)){
                frontierList.remove(current);
                cameFromList.add(current);
                String lineNew = start;
                String[] namesNew = lineNew.split(":");
                currentBuilding= Integer.parseInt(namesNew[0]);

            }else{
                frontierList.remove(current);
                String linePrev = cameFromList.get(cameFromList.size()-1);
                String[] namesPrev = linePrev.split(":");
                previousFloor = Integer.parseInt(namesPrev[1]);

                cameFromList.add(current);
                String lineNew = cameFromList.get(cameFromList.size()-1);
                String[] namesNew = lineNew.split(":");
                currentFloor= Integer.parseInt(namesNew[1]);
                currentBuilding= Integer.parseInt(namesNew[0]);
            }
            if(current.equals(start)){
                timeLength += 0;
                weightedDist += 0;
            }
            //Adds to timeLength for points
            else if(localG.getPointsAt(current)>0){
                timeLength =timeLength +3;
                pointsCollected += localG.getPointsAt(current);// + pointsCollected;
                qrCollected = qrCollected +1;
                weightedDist += getDistanceToNextPoint(current);
                // Set up to look at different maps now
                removePoints(current, localG);
            }
            //Adds to timeLength for stairs
            else if (previousFloor != currentFloor){
                timeLength = timeLength + 10;
                weightedDist += getDistanceToNextPoint(current);
            }
            //Adds to timeLength for normal nodes
            else{
                if(currentBuilding > 33){
                    timeLength = timeLength + 2;
                    weightedDist += getDistanceToNextPoint(current);
                }
                else{
                    timeLength = timeLength + 1;
                    weightedDist += getDistanceToNextPoint(current);
                }
            }
            if(current.equals(goal)) {
                return reconstructPath(cameFromList);
            }
            if(cameFromList.contains(currentGoal)){
                if(timeLength+breakT < timeLeft){
                    removeLocalGoalList(0);
                    currentGoal = getLocalGoalPointFirst();
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                }
//                else{
//                    currentGoal = goal;
//                    localDistanceFromGoal.clear();
//                    markLocalNeighbors(currentGoal,localG);
//                }
            }
            if(timeLength+breakT > timeLeft){
                if(currentGoal != goal){
                    currentGoal = goal;
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                }
            }

            //go through all the current Nodes neighbors and calculate if one should be our next step
            neighborCheck = localG.adjacentTo(current);
            for (String neighbor : localG.adjacentTo(current)) {
                boolean neighborIsBetter;

                //if we have already searched this Node, don't bother and continue to the next one
//                             if (cameFromList.contains(neighbor)){
//                              neighborCheck.remove(neighbor);
//                                     continue;
//                             }
                if (localG.isObstacle(neighbor)) {
                    neighborCheck.remove(neighbor);
                }
                //also just continue if the neighbor is an obstacle
                if (!localG.isObstacle(neighbor)) {

                    // calculate how long the path is if we choose this neighbor as the next step in the path
                    int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighbor));
                    //TODO Include time element and test as an alternative to the time break? Include allowed time in replace of time break
                    //first time it reaches that limit, the goal switches to the main goal
                    //Test in a different project...
                    if (localG.getPointsAt(neighbor) > 0) {
                        neighborIsBetter = true;
//                                       if(!frontierList.contains(neighbor)) {
//                                             frontierList.add(neighbor);}
                    }
                    //add neighbor to the open list if it is not there
                    else if (!frontierList.contains(neighbor)) {
                        frontierList.add(neighbor);
                        neighborIsBetter = true;
                        //if neighbor is closer to end it could also be better
                    } else if (neighborDistanceFromStart < localG.getDistanceFromStart(current)) {
                        neighborIsBetter = true;
                    } else {
                        neighborIsBetter = false;
                    }
                    // set neighbors parameters if it is better
                    if (neighborIsBetter) {
//                                             G.setPreviousNode(neighbor, current);
                        localG.setDistanceFromStart(neighbor, neighborDistanceFromStart);
                        //neighbor.setHeuristicDistanceFromGoal(heuristic.heuristic(neighbor, currentGoalNode));
                    }
                }

            }


        }
        return null;
    }

    public void removePoints(String currentLoc, Graph local){
//      System.out.println(currentLoc + ": " + localG.getPointsAt(currentLoc));
        pointRemKeySet.add(currentLoc);
        pointLocRemoved.put(currentLoc, localG.getPointsAt(currentLoc));
        localG.reverseWeightingsGradient(currentLoc, local);
        localG.removePoints(currentLoc);


    }

    public void returnMissingPoints(ArrayList<String> pointLocCollected){
//        System.out.println(pointRemKeySet);
        for(String pointReturn : pointRemKeySet){
            if(pointLocCollected.contains(pointReturn)){
            		goalList.remove(pointReturn);
            }
            else{
                int pointVal = pointLocRemoved.get(pointReturn);
                localG.assignPoints(pointReturn, pointVal);
                localG.markWeightingsGradient(pointReturn,localG);
            }
        }
        pointLocRemoved.clear();
        pointRemKeySet.clear();
    }

    public void clearValues(){

        timeLength = 0;
        pointsCollected = 0;
        qrCollected =0;
        weightedDist =0;


    }
    
    public boolean distanceFromPath (String curLoc, Graph G){
    	int initialDistance = 5;
    	deviationsFromPathCheck.clear();
    	deviationsFromPathCheck.put(curLoc, initialDistance);
    	String current = curLoc;
    	ArrayList<String> nList = new ArrayList<String>();
    	ArrayList<String> oldNeighbor = new ArrayList<String>();
    	int distance = initialDistance;
    	oldNeighbor.add(curLoc);
    	int k = 0;
    	boolean closeEnoughToPath = false;
    	while(G.toList(G.vertices()).size()!=deviationsFromPathCheck.size()){
    		
    		for(k=0; k<oldNeighbor.size(); k++){
    			nList = G.adjacentTo(oldNeighbor.get(k));
    			distance = Integer.parseInt(deviationsFromPathCheck.get(oldNeighbor.get(k)).toString());
    			

    			for(String n: nList){
    				int nDistance = 0;
    					nDistance = distance - 1;	

    				if(!deviationsFromPathCheck.containsKey(n)){
    					deviationsFromPathCheck.put(n, nDistance);
    						if(!oldNeighbor.contains(n)){
    							oldNeighbor.add(n);
    						}}
    				else if(Integer.parseInt(deviationsFromPathCheck.get(n).toString())<nDistance){
    					deviationsFromPathCheck.remove(n);
    					deviationsFromPathCheck.put(n, nDistance);
    					
    				}
    				}}}
    	for(String checkPath : G.toList(G.vertices())){
    		if(Integer.parseInt(deviationsFromPathCheck.get(checkPath).toString()) > 0){
    			if(shortestPath.contains(checkPath)){
    				closeEnoughToPath = true;
    				return closeEnoughToPath;
    			}
    		}
    		
    	}
    	return closeEnoughToPath;
    	}
    

    public Path reCalcOptPath(String currentStart, int timeLeft, ArrayList<String> pointColl) {

        if(timeLeft==0){
            timeLeft=180;
        }
    	

        ArrayList<String> pointLocCollected = new ArrayList<String>(pointColl);
        returnMissingPoints(pointLocCollected);
//    	System.out.println("Before Re Calc Path Length is: " + shortestPath.getLength());
//    	System.out.println("Before Re Calc Logged Path Length is: " + shortPathLength);
    	
    	if(shortestPath.getLength() - shortPathLength > 4){
    		// Original distanceFrom Path
//    		if(!distanceFromPath(currentStart, localG)){

// 	    	StopWatch process = new StopWatch();
// 	    	process.start();
    		System.out.println("Do you want to re-route?");
    		System.out.println("Like you had a choice... Rerouting...");
    		System.out.println("We apologize for the delay, but you really shouldn't have left the path this far...");
    		FloorRankingOrder goalLogic = new FloorRankingOrder(localG, 90, stairMod, floorMod);
    		this.goalList = new ArrayList<String>(goalLogic.goalList);
//    		
//    		System.out.println("New Goal List from Rerouting: " + goalList);
//   		 	process.stop();
//	        System.out.println("Run time Recalc with new GoalList: " + process.getElapsedTime() +" ms");
    	}



        this.localGoalList = new ArrayList<String>(goalList);
        int breakT = 180-breakTime;
        clearValues();

        this.timeLeft=timeLeft;
        this.timeLength = 180 - timeLeft;

        if (currentStart.equals(goal)) {
            return null;
        }

        if(localG.getPointsAt(currentStart) > 0){
            removePoints(currentStart, localG);
            localGoalList.remove(currentStart);
        }

        if(localG.getPointsAt(this.goal) > 0){
            removePoints(this.goal, localG);
        }

 
        ArrayList<String> neighborCheck = new ArrayList<String>();

        //Check if the goal node is blocked (if it is, it is impossible to find a path there)
        if (localG.isObstacle(goal)) {
            return null;
        }

        localG.setDistanceFromStart(currentStart, 0);
        cameFromList.clear();
        frontierList.clear();
        frontierList.add(currentStart);
        neighborCheck.add(currentStart);

        String current = null;
        String currentGoal = getLocalGoalPointFirst();
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
        while (frontierList.size() != 0) {
            int totalDistance = 99999999;
            for (String mm : neighborCheck) {

                if (localG.getPointsAt(mm) > 0) {
                    totalDistance = getLocalTotalDistance(mm); // Due to the break, this does nothing
                    current = mm;
                    break;

                } else if (getLocalTotalDistance(mm) < totalDistance) {
                    totalDistance = getLocalTotalDistance(mm);
                    current = mm;
                }

            }

            if (neighborCheck.size() == 0) {
                // Then I need to go back to the previous node I was just at.
                String neighborGoBack = cameFromList.get(cameFromList.size() - 2);
                int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighborGoBack));
                localG.setDistanceFromStart(neighborGoBack, neighborDistanceFromStart);

                current = neighborGoBack;
            }
            neighborCheck.clear();
            int currentFloor = 0;
            int previousFloor = 0;
            int currentBuilding = 0;
            //move current Node to the closed (already searched) list
            

            if(current.equals(start) || cameFromList.size()==0){
                frontierList.remove(current);
                cameFromList.add(current);
                String lineNew = start;
                String[] namesNew = lineNew.split(":");
                currentBuilding= Integer.parseInt(namesNew[0]);

            }else{
                frontierList.remove(current);
//                Log.d("list", String.valueOf(cameFromList.size()));
                String linePrev = cameFromList.get(cameFromList.size() - 1);
                String[] namesPrev = linePrev.split(":");
                previousFloor = Integer.parseInt(namesPrev[1]);

                cameFromList.add(current);
                String lineNew = cameFromList.get(cameFromList.size()-1);
                String[] namesNew = lineNew.split(":");
                currentFloor= Integer.parseInt(namesNew[1]);
                currentBuilding= Integer.parseInt(namesNew[0]);
            }
            if(current.equals(start)){
                timeLength += 0;
                weightedDist += 0;
            }
            //Adds to timeLength for points
            else if(localG.getPointsAt(current)>0){
                timeLength =timeLength +3;
                pointsCollected += localG.getPointsAt(current);// + pointsCollected;
                qrCollected = qrCollected +1;
                weightedDist += getDistanceToNextPoint(current);
                // Set up to look at different maps now
                removePoints(current, localG);
            }
            //Adds to timeLength for stairs
            else if (previousFloor != currentFloor){
                timeLength = timeLength + 10;
                weightedDist += getDistanceToNextPoint(current);
            }
            //Adds to timeLength for normal nodes
            else{
                if(currentBuilding > 33){
                    timeLength = timeLength + 2;
                    weightedDist += getDistanceToNextPoint(current);
                }
                else{
                    timeLength = timeLength + 1;
                    weightedDist += getDistanceToNextPoint(current);
                }

            }
            if(current.equals(goal)) {
                return reconstructPath(cameFromList);
            }

            if(cameFromList.contains(currentGoal)){
                if(timeLength+breakT < timeLeft){
                    removeLocalGoalList(0);
                    currentGoal = getLocalGoalPointFirst();
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                }
                else{
                    currentGoal = goal;
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                }

            }

            if(timeLength+breakT > timeLeft){
                if(currentGoal != goal){
                    currentGoal = goal;
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                }
            }

            //go through all the current Nodes neighbors and calculate if one should be our next step
            neighborCheck = localG.adjacentTo(current);
            for (String neighbor : localG.adjacentTo(current)) {
                boolean neighborIsBetter;

                //if we have already searched this Node, don't bother and continue to the next one
//                             if (cameFromList.contains(neighbor)){
//                            	neighborCheck.remove(neighbor);
//                                     continue;
//                             }
                if (localG.isObstacle(neighbor)) {
                    neighborCheck.remove(neighbor);
                }
                //also just continue if the neighbor is an obstacle
                if (!localG.isObstacle(neighbor)) {

                    // calculate how long the path is if we choose this neighbor as the next step in the path
                    int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighbor));

                    if (localG.getPointsAt(neighbor) > 0) {
                        neighborIsBetter = true;
//                                    	 if(!frontierList.contains(neighbor)) {
//                                             frontierList.add(neighbor);}
                    }
                    //add neighbor to the open list if it is not there
                    else if (!frontierList.contains(neighbor)) {
                        frontierList.add(neighbor);
                        neighborIsBetter = true;
                        //if neighbor is closer to end it could also be better
                    } else if (neighborDistanceFromStart < localG.getDistanceFromStart(current)) {
                        neighborIsBetter = true;
                    } else {
                        neighborIsBetter = false;
                    }
                    // set neighbors parameters if it is better
                    if (neighborIsBetter) {
//                                             G.setPreviousNode(neighbor, current);
                        localG.setDistanceFromStart(neighbor, neighborDistanceFromStart);
                        //neighbor.setHeuristicDistanceFromGoal(heuristic.heuristic(neighbor, currentGoalNode));
                    }
                }

            }


        }
        return null;
    }

    public Graph getLocalG() {
        return localG;
    }


    private void markLocalNeighbors(String goal, Graph g){
//    	StopWatch s = new StopWatch();
//    	s.start();  
    	localDistanceFromGoal.put(goal, 0);
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        int distance = 0;
        oldNeighbor.add(goal);
        int k = 0;
        while(g.toList(g.vertices()).size()!=localDistanceFromGoal.size()){

            for(k=0; k<oldNeighbor.size(); k++){
                nList = g.adjacentTo(oldNeighbor.get(k));
                distance = getLocalDistanceFromGoal(oldNeighbor.get(k));

                for(String n: nList){
                    int nDistance = distance - g.getPointsAt(n) + g.getFloorWeight(n);
                    if(!localDistanceFromGoal.containsKey(n)){
                        localDistanceFromGoal.put(n, nDistance);
                        if(!oldNeighbor.contains(n)){
                            oldNeighbor.add(n);
                        }}
                    else if(getLocalDistanceFromGoal(n)>nDistance){
                        localDistanceFromGoal.remove(n);
                        localDistanceFromGoal.put(n, nDistance);

                    }
        
                }}}
//        s.stop();
//        System.out.println("Run time Marking Local Neighbors: " + s.getElapsedTime() +" ms");    
    }
    private void clearLocalNeighbors(){
        localDistanceFromGoal.clear();
    }


    private int getDistanceToNextPoint(String v){
        localG.validateVertex(v);
        int distanceToNextPoint = 10;
        if(localG.isStairs(v)){
            distanceToNextPoint = localG.getFloorWeight(v) - localG.getPointsAt(v);
        }
        else{
            distanceToNextPoint = localG.getFloorWeight(v) - localG.getPointsAt(v);
        }

        return distanceToNextPoint;
    }
    private int getLocalTotalDistance(String current){
        int thisTotalDistance = getLocalDistanceFromGoal(current) + localG.getDistanceFromStart(current);
        return thisTotalDistance;
    }

    private int getLocalDistanceFromGoal(String v){
        localG.validateVertex(v);
        return Integer.parseInt(localDistanceFromGoal.get(v).toString());
    }

    public String getLocalGoalList(int index) {
        return localGoalList.get(index);
    }
    public String removeLocalGoalList(int index) {
        return localGoalList.remove(index);
    }
    public String getLocalGoalPointFirst() {
        return localGoalList.get(0);
    }

    public static void logger(String logged){
        //AlgoFXXSXXTrialX
        File log = new File("AlgoAdapTestFloorandStairMod.txt");
        try{
            if(log.exists()==false){
                System.out.println("We had to make a new file.");
                log.createNewFile();
            }
            PrintWriter out = new PrintWriter(new FileWriter(log, true));
            out.println(logged);
            out.close();
        }catch(IOException e){
            System.out.println("COULD NOT LOG!!");
        }
    }

    public ArrayList<String> printPath(String title, int trialNumber, int breakTime){
        ArrayList<String> path = new ArrayList<String>();
        for(int i =0; i<shortestPath.getLength(); i++){
            path.add(shortestPath.getWayPoint(i));
        }
        System.out.print("Waypoints of algorithm path: ");
        System.out.println(path);
        System.out.print("Length of shortest path: ");
        System.out.println(shortestPath.getLength());
        System.out.println("Length of weighted path: " + getWeightedDist());
        System.out.print("Time-Length of shortest path: ");
        System.out.println(getTimeLength());
        System.out.println("You collected " + getCollectedPoints() +" points!");
        System.out.println("You collected " + getQRCollected() +" QR Codes!");


//       logger(title + " Results");
//       logger("Waypoints of algorithm path: " + path);
//       logger("Length of shortest path: " + "\t" + shortestPath.getLength());
//       logger("Length of weighted path: " + "\t" + getWeightedDist());
//       logger("Time-Length of shortest path: " + "\t" + getTimeLength());
//       logger("You collected " + "\t" + getCollectedPoints() + "\t" +" points!");
//       logger("You collected " + "\t" + getQRCollected() + "\t" +" QR Codes!");

        logger(title + "\t" + trialNumber + "\t" + localG.getBaseFloorWeight() +"\t" + localG.getStairWeight() +"\t" + breakTime +"\t" +shortestPath.getLength()+ "\t" + getWeightedDist()+ "\t" + getTimeLength()+ "\t" +  getCollectedPoints()+ "\t" +  getQRCollected());
        return path;
    }

    ArrayList<String> getPath(){
        ArrayList<String> path = new ArrayList<String>();
        for(int i =0; i<shortestPath.getLength(); i++){
            path.add(shortestPath.getWayPoint(i));
        }
        return path;
    }

//    public ArrayList<String> getPath(){
//        ArrayList<String> path = new ArrayList<String>();
//        for(int i =0; i<shortestPath.getLength(); i++){
//            path.add(shortestPath.getWayPoint(i));
//        }
//        return path;
//    }

    public int getWeightedDist(){
        return weightedDist;
    }

    public int getTimeLength() {
        return timeLength;
    }
    public int getCollectedPoints() {

    	return pointsCollected;

    }
    public int getQRCollected() {
        return qrCollected;
    }

    private Path reconstructPath(ArrayList<String> cameFromList) {
        Path path = new Path();

        for(String uu:cameFromList){
            path.appendWayPoint(uu);

        }
        this.shortestPath = new Path(path);
        if(shortestPath.getLength()< shortPathLength){
        	shortPathLength = shortestPath.getLength();
        }
        return path;
    }

}