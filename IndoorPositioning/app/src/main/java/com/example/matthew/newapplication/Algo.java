//package com.example.matthew.newapplication;
//import android.util.Log;
package com.example.matthew.newapplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map timeMinEstimateFromGoal;
    private Map deviationsFromPathCheck;
    public int pointsCollected;
    public int qrCollected;
    private ArrayList<String> usedGoalList;
    private ArrayList<String> prevUsedGoalList;
    private ArrayList<String> usedGoalsToVisitList;
    private ArrayList<String> prevGoalsToVisitList;
    ArrayList<String> goalList;
    private ArrayList<String> reroutedGoalList;
    private ArrayList<String> localGoalList;
    private int weightedDist;
    private int stairMod;
    private int floorMod;
    private int shortPathLength;
    private Map<String, Integer> pointLocRemoved;
    private ArrayList<String> pointRemKeySet;
    private int breakTime;
    private int allowedTime;
    private int count = 0;
    private int timeLeft;
    private int recount = 0;
    private String breakNode;
    private String prevBreakNode;
    private int timeQRCodeEst;
    private int timeFloorEst;
    private int timeStairEst;
    private int windowSizeReCalc;
    private int alertReason;
    private boolean deviationListChange = false;
    private boolean tooLongRoute = false;
    private boolean updateTime = false;
//TODO There are a ton of changes, so good luck. Hopefully, you can just use this one and make the slight additions you need to run the code.
    //TODO The other TODO markings display the locations were it is giving feedback about the route
    public Algo(Graph g, String start, String goal, int timeLeft, int stairM, int floorM,int breakT, ArrayList<String> goalList, int allowTime, int tStair, int tFloor, int tQR, int window) { //, Heuristic heuristic){
    	this.stairMod = stairM;
        this.floorMod = floorM;
        this.localG = new Graph(g, stairMod, floorMod);
        this.shortPathLength = 1000;
        localG.setStartLocation(start);
        localG.setGoalLocation(goal);
        localG.gradientGraph(localG);
        this.timeQRCodeEst = tQR;
        this.timeFloorEst = tFloor;
        this.timeStairEst = tStair;
        this.windowSizeReCalc = window;
        cameFromList = new ArrayList<String>();
        frontierList = new ArrayList<String>();
        this.goalList = new ArrayList<String>(goalList);
        //TODO Remove the following two lines
//        this.goalList.clear();
//        this.goalList.add("37:2:77");
//        this.goalList.add("37:2:91");
//        this.goalList.add(goal);
        localDistanceFromGoal = new HashMap();
        deviationsFromPathCheck = new HashMap<String, Integer>();
        timeMinEstimateFromGoal = new HashMap<String, Integer>();
        this.start = start;
        this.goal = goal;
        this.timeLeft = timeLeft;
        timeLength = 0;
        pointsCollected = 0;
        weightedDist = 0;
        this.pointLocRemoved = new HashMap<String, Integer>();
        this.pointRemKeySet = new ArrayList<String>();
        this.breakTime = allowTime-breakT;
        timeHeuristic();     
        this.allowedTime = allowTime;
        this.prevUsedGoalList = new ArrayList<String>();
        this.usedGoalList = new ArrayList<String>();
        this.reroutedGoalList = new ArrayList<String>();
        this.prevGoalsToVisitList = new ArrayList<String>();
        this.usedGoalsToVisitList = new ArrayList<String>();

//        System.out.println(this.goalList);
        //For my looping function TODO Remove this when I'm done looping. Both lines below because this is providing the goalList for now.
//      FloorRankingOrder goalLogic = new FloorRankingOrder(localG, windowSizeReCalc, stairMod, floorMod);
//      this.goalList = new ArrayList<String>(goalLogic.goalList); 
//      System.out.println(this.goalList);
    }


    public Path calcOptPath(String currentStart,int timeR) {

        if(timeR==0){
            timeR=180;
        }

        this.localGoalList = new ArrayList<String>(goalList);
        System.out.println("LocalGoalList Used at Beginning of run = " + localGoalList);
        this.timeLeft=timeR;

        int breakT = allowedTime-breakTime;
        this.timeLength = 0;

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
        prevUsedGoalList.add(currentGoal);
        usedGoalList.add(currentGoal);
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
        while (frontierList.size() != 0) {
            int totalDistance = 99999999;
            
            if(neighborCheck.isEmpty()){
            	
            	return reconstructPath(cameFromList);
            }
            for (String mm : neighborCheck) {
            	
                if (localG.getPointsAt(mm) > 0) {
                    totalDistance = getLocalTotalDistance(mm); // Due to the break, this does nothing
                    current = mm;
                    break;
                    
                    }
                else if (getLocalTotalDistance(mm) < totalDistance) {
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
                timeLength =timeLength +timeQRCodeEst;
                pointsCollected += localG.getPointsAt(current);// + pointsCollected;
                prevGoalsToVisitList.add(current);
                usedGoalsToVisitList.add(current);
                qrCollected = qrCollected +1;
                weightedDist += getDistanceToNextPoint(current);
                // Set up to look at different maps now
                removePoints(current, localG);
            }
            //Adds to timeLength for stairs
            else if (previousFloor != currentFloor){
                timeLength = timeLength + timeStairEst;
                weightedDist += getDistanceToNextPoint(current);
            }
            //Adds to timeLength for normal nodes
            else{
                if(currentBuilding > 33){
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
                else{
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
            }
            if(current.equals(goal)) {
            	System.out.println("GoalList = " + goalList);
            	System.out.println("prevUsedGoalList = " + prevUsedGoalList);
            	System.out.println("usedGoalList = " + usedGoalList);
            	prevGoalsToVisitList.add(goal);
            	usedGoalsToVisitList.add(goal);
                return reconstructPath(cameFromList);
            }
            if(cameFromList.contains(currentGoal)){
                if(timeLength+breakT < allowedTime-(180-timeLeft)){
                    removeLocalGoalList(0);
                    currentGoal = getLocalGoalPointFirst();
                    prevUsedGoalList.add(currentGoal);
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                    if(currentGoal.equals(goal)){
                    	prevBreakNode = current;
                    	breakNode = current;
                    }
                }
                else{
                    currentGoal = goal;
                    prevUsedGoalList.add(currentGoal);
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                    prevBreakNode = current;
                    breakNode = current;
                }
            }
            if(timeLength+breakT > allowedTime-(180-timeLeft)){
                if(currentGoal != goal){
                    currentGoal = goal;
                    prevUsedGoalList.add(currentGoal);
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                    prevBreakNode = current;
                    breakNode = current;
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
//                    System.out.println(timeLeft);

                    
                    if (allowedTime < timeLength + 180 - timeLeft + getTimeMinEstimateFromGoal(neighbor)){
                    	neighborIsBetter = false;

                    	neighborCheck.remove(neighbor);

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
            		prevUsedGoalList.remove(pointReturn);
            		prevGoalsToVisitList.remove(pointReturn);
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

    public Boolean goalListCheck(){
    	if(prevUsedGoalList.size() != usedGoalList.size()){
    		return false;
    	}
    	else{
    		for(int i=0; i < usedGoalList.size(); i++){
    			if(prevUsedGoalList.get(i).equals(usedGoalList.get(i))){
    				continue;
    			}
    			else{
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    public Boolean goalsToVisitCheck(){
    	if(prevGoalsToVisitList.size() != usedGoalsToVisitList.size()){
    		return false;
    	}
    	else{
    		for(int i=0; i < usedGoalsToVisitList.size(); i++){
    			if(prevGoalsToVisitList.get(i).equals(usedGoalsToVisitList.get(i))){
    				continue;
    			}
    			else{
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    
    public void goalListAssigning(boolean rerouting){
    	if(updateTime){
    		rerouting = true;
    	}
    	if(rerouting){
    		prevUsedGoalList.clear();
    		for(String goalAdded :usedGoalList){
    			prevUsedGoalList.add(goalAdded);
    		}
    		prevBreakNode = breakNode;
    		prevGoalsToVisitList = new ArrayList<String>(usedGoalsToVisitList);
    		tooLongRoute = false;
    		deviationListChange = false;
    		updateTime = false;
    		
    	}
    	else{
    		prevUsedGoalList = new ArrayList<String>(usedGoalList);
    		prevBreakNode = breakNode;
    		prevGoalsToVisitList = new ArrayList<String>(usedGoalsToVisitList);
    		tooLongRoute = false;
    		deviationListChange = false;
    		updateTime = false;
    	}
    	    	
    }

    public String updateNotification(int reason){
    	String response;
    	switch (reason){
    	
    	default:
    		return response = "No Update";

    	case 1:
    		return response = "Slight Change At End of Route";

    	case 2:
    		return response = "Too Slow: QR Removed From Path";
    		
    	case 3:
	   		return response = "Too Slow: Whole Path Changed";

    	case 4:
    		return response = "Ahead of Schedule: QR Added To Path";
    		
    	case 5:
    		return response = "Rerouting.. Too Much Deviation From Path";
    		
    	case 6:
    		return response = "Alert: New Update";
    	
    	case 7:
    		return response = "Uh-oh... This isn't supposed to happen";
    	case 8:
    		return response = "Too Slow: Path Cut Early to Reach Goal";
    	}}
//TODO Here are the two new methods I built to find the reason for the error and the output string...
    // List of Update Reasons
    // 0 = No update
    // 1 = BreakNode slight change
    // 2 = Too slow (removed QR Code)
    // 3 = Too slow (changed GoalList)
    // 4 = Too fast (added goal)
    // 5 = Deviation significant
    // 6 = Update?!?!
    // 7 = WTF I have no clue what is going on... Update?!?!
    // 8 = Too slow (routed more directly to the goal
    public int updateReason(){
    	int updateReason;
    	boolean completeDiff = false;
    	if(goalListCheck()){
    		//All the goals are the same as before
    		if(goalsToVisitCheck()){
    			// All of the visited goals are the same
    			if(prevBreakNode.equals(breakNode)){
    				// The route isn't getting elongated
    				return updateReason = 0;
    			}
    			else{
    				// There is a slight modification
    				return updateReason = 1;
    			}
    		}
    		else{
    			// There are new nodes that I actually visit
    			if(usedGoalsToVisitList.size() > prevGoalsToVisitList.size() ){
    				// A new goal was added that I can reach
    				return updateReason = 4;
    			}
    			else{
    				// A goal that I used to be able to reach was removed
    				return updateReason = 2;
    			}
    		}
    	}
    	else{
    		// Checking if the visit list has different nodes
    		for(int i=0; i < usedGoalsToVisitList.size(); i++){
    			if(prevGoalsToVisitList.get(i).equals(usedGoalsToVisitList.get(i))){
    				continue;
    			}
    			else{
    				completeDiff = true;
    				break;
    			}
    		}
    		//some goals have changed from before
    		if(goalsToVisitCheck()){
    			// All of the visited goals are the same
    			// I don't think this will happen
    			if(prevBreakNode.equals(breakNode)){
    				// The route isn't getting elongated
    				return updateReason = 0;
    			}
    			else{
    				// There is a slight modification
    				return updateReason = 1;
    			}
    		}
    		else{
    			// some of the visited goals are different
    			if(completeDiff){
    				// the visited goals are completely different
    				if(updateTime){
    					// An actual update happened
    					return updateReason = 6;
    				}
    				else if(deviationListChange){
    					// There were too many deviations from the user's path
    					return updateReason = 5;
    				}
    				else if(tooLongRoute){
    					// There were too many empty sets in a row so the list is all new
    					return updateReason = 3;
    				}
    				else if(usedGoalsToVisitList.size() != prevGoalsToVisitList.size() ){
    					// They were just in a different order
        				if(usedGoalsToVisitList.size() > prevGoalsToVisitList.size()){
        					// I added another goal that I can visit
        					return updateReason = 4;
        				}
        				else{
        					// I removed a goal that I used to visit
            				return updateReason = 2;
            			}
    				}
    				else{
    					
    	    			if(prevBreakNode.equals(breakNode)){
    	    				// This would have to be like pure dumb luck and I don't think it is possible
    	    				return updateReason = 1;
    	    			}
    	    			else{
    	    				// There is a slight modification
    	    				return updateReason = 8;
    	    			}
    					// Something bad is happening
//    					return updateReason = 7;
    				}			
       			}
    			else{
    				// This makes no sense...
    				return updateReason = 7;
    			}	
    		}
       	}   	    	    	
    }
    
//    public int updateReasonTests(){
//    	int updateReason;
//    	goalList.clear();
//    	prevUsedGoalList.clear();
//    	usedGoalList.clear();
//    	prevGoalsToVisitList.clear();
//    	usedGoalsToVisitList.clear();
//    	tooLongRoute = false;
//    	updateTime = false;
//    	deviationListChange = false;
//    	prevBreakNode = "1";
//    	breakNode = "2";
//    	goalList.add("1");
//    	goalList.add("2");
//    	prevUsedGoalList.add("1");
//    	prevUsedGoalList.add("2");
//    	usedGoalList.add("1");
//    	prevGoalsToVisitList.add("7");
//    	usedGoalsToVisitList.add("1");
////    	prevGoalsToVisitList.add("7");
//    	
////    	if(goalListCheck()){
////    	else{
////    		if(goalsToVisitCheck()){
//
////    		else{
////    			if(completeDiff){
////    				if(updateTime){
////    					return updateReason = 6;
////    				}
////    				else if(deviationListChange){
////    					return updateReason = 5;
////    				}
////    				else if(tooLongRoute){
////    					return updateReason = 3;
////    				}
////    				else if(usedGoalsToVisitList.size() != prevGoalsToVisitList.size() ){
////        				if(usedGoalsToVisitList.size() > prevGoalsToVisitList.size()){
////        					return updateReason = 4;
////        				}
////        				else{
////            				return updateReason = 2;
////            			}}
////    				else{
////    					if(prevBreakNode.equals(breakNode)){
////    	    				return updateReason = 1;
////    	    			}
////    	    			else{
////    	    				return updateReason = 1;
////    	    			}}}
////    			else{
////    				return updateReason = 7;
////    			}}}
//    	System.out.println("GoalList = " + goalList);
//    	System.out.println("prevUsedGoalList = " + prevUsedGoalList);
//    	System.out.println("usedGoalList = " + usedGoalList);
//    	System.out.println("prevGoalsToVisitList = " + prevGoalsToVisitList);
//    	System.out.println("usedGoalsToVisitList = " + usedGoalsToVisitList);
//    	System.out.println("prevBreakNode = " + prevBreakNode);
//    	System.out.println("breakNode = " + breakNode);
//    	System.out.println("tooLongRoute = " + tooLongRoute);
//    	System.out.println("updateTime = " + updateTime);
//    	System.out.println("deviationListChange = " + deviationListChange);	
//    	return updateReason();
//    }
    
    private void removeObstacleGoals(){

        List<String> newList = new ArrayList<>(prevUsedGoalList);

    	for(String goalCheck :newList){
    		if(goalCheck.equals(goal)){
    			continue;
    		}
    		else if (localG.isObstacle(goalCheck)) {
                prevUsedGoalList.remove(goalCheck);
            }
    		else{
    			continue;
    		}
    	}
    	FloorRankingOrder goalLogic = new FloorRankingOrder(localG, windowSizeReCalc, stairMod, floorMod);
 		this.reroutedGoalList = new ArrayList<String>(goalLogic.goalList);
    	
    }
    
    
    
    
    public Path reCalcOptPath(String currentStart, int timeL, ArrayList<String> pointColl, int recNum, boolean updateNew) {

        if(timeL==0){
            timeL=180;
        }

    	if(updateNew){
    		removeObstacleGoals();
    		updateTime = true;
    	}else{
            updateTime = false;
        }
    	
        usedGoalList.clear();
        usedGoalsToVisitList.clear();
    	int reCalcAllowedTime = allowedTime;
    	int calcAddedValue = 0;
    	if(allowedTime == 180){
    		calcAddedValue = 0;
    	}
    	else{
    		calcAddedValue = (allowedTime - 180)/3;
    	}

    	
    	if(timeL < 120){
    		reCalcAllowedTime = allowedTime-calcAddedValue;
    	}
    	else if(timeL < 90){
     		reCalcAllowedTime = allowedTime-calcAddedValue;
    	}
    	else if(timeL < 60){
    		reCalcAllowedTime = 180;
    	}
    	
    	
        recount = recNum;
//        System.out.println(recount);
    	count = 0;
    	ArrayList<String> pointLocCollected = new ArrayList<String>(pointColl);
        returnMissingPoints(pointLocCollected);

        //TODO Matt, one of the loops that checks for deviations
    	// Reroute due to deviation from path
        if(reroutedGoalList.isEmpty()){
        	this.localGoalList = new ArrayList<String>(prevUsedGoalList);
        }
        else{
        	this.localGoalList = new ArrayList<String>(reroutedGoalList);
        	reroutedGoalList.clear();
        }
    	
        
        if(shortestPath.getLength() - shortPathLength > 4){
    		// Original distanceFrom Path
//    		if(!distanceFromPath(currentStart, localG)){

// 	    	StopWatch process = new StopWatch();
// 	    	process.start();
    		System.out.println("Do you want to re-route deviation?");
    		System.out.println("Like you had a choice... Rerouting...");
    		System.out.println("We apologize for the delay, but you really shouldn't have left the path this far...");
    		System.out.println("Please, please be this one...");
    		FloorRankingOrder goalLogic = new FloorRankingOrder(localG, windowSizeReCalc, stairMod, floorMod);
    		reroutedGoalList = new ArrayList<String>(goalLogic.goalList);
    		this.localGoalList = new ArrayList<String>(reroutedGoalList);
    		reroutedGoalList.clear();
    		deviationListChange = true;
//    		System.out.println("New Goal List from Rerouting: " + localGoalList);
//   		 	process.stop();
//	        System.out.println("Run time Recalc with new GoalList: " + process.getElapsedTime() +" ms");
    	}
        System.out.println("LocalGoalList Used at Beginning of run = " + localGoalList);
        
//        System.out.println(localGoalList);
        int breakT = reCalcAllowedTime-breakTime;
        clearValues();
        this.timeLeft=timeL;
        this.timeLength = 0;//180 - timeL; Because we are comparing it to current values

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
        usedGoalList.add(currentGoal);
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
               
        while (frontierList.size() != 0) {
//        	System.out.println("currentGoal is: " +currentGoal);
        	if(neighborCheck.isEmpty()){
        		count++;
        	}
            int totalDistance = 99999999;
            for (String mm : neighborCheck) {

                if (localG.getPointsAt(mm) > 0) {
                    totalDistance = getLocalTotalDistance(mm); // Due to the break, this does nothing
                    current = mm;
//                    recount = 0;
                    break;

                } else if (getLocalTotalDistance(mm) < totalDistance) {
                    totalDistance = getLocalTotalDistance(mm);
                    current = mm;
//                    recount = 0;
                }
            }
//            if (neighborCheck.size() == 0) {
//                // Then I need to go back to the previous node I was just at.
//                String neighborGoBack = cameFromList.get(cameFromList.size() - 2);
//                int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighborGoBack));
//                localG.setDistanceFromStart(neighborGoBack, neighborDistanceFromStart);
//
//                current = neighborGoBack;
//            }
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
                timeLength =timeLength +timeQRCodeEst;
                pointsCollected += localG.getPointsAt(current);// + pointsCollected;
                usedGoalsToVisitList.add(current);
                qrCollected = qrCollected +1;
                weightedDist += getDistanceToNextPoint(current);
                // Set up to look at different maps now
                removePoints(current, localG);
            }
            //Adds to timeLength for stairs
            else if (previousFloor != currentFloor){
                timeLength = timeLength + timeStairEst;
                weightedDist += getDistanceToNextPoint(current);
            }
            //Adds to timeLength for normal nodes
            else{
                if(currentBuilding > 33){
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
                else{
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
            }          
            if(current.equals(goal)) {
//            	System.out.println("Tony, you're killing me...");
            	reroutedGoalList.clear();
            	usedGoalsToVisitList.add(goal);
            	System.out.println("GoalList = " + goalList);
            	System.out.println("prevUsedGoalList = " + prevUsedGoalList);
            	System.out.println("usedGoalList = " + usedGoalList);
            	System.out.println("prevGoalsToVisitList = " + prevGoalsToVisitList);
            	System.out.println("usedGoalsToVisitList = " + usedGoalsToVisitList);
            	System.out.println("prevBreakNode = " + prevBreakNode);
            	System.out.println("breakNode = " + breakNode);
            	System.out.println("tooLongRoute = " + tooLongRoute);
            	System.out.println("updateTime = " + updateTime);
            	System.out.println("deviationListChange = " + deviationListChange);
                return reconstructPath(cameFromList);
            }
            if(cameFromList.contains(currentGoal)){
                if(timeLength+breakT < reCalcAllowedTime-(180-timeLeft)){
                    removeLocalGoalList(0);
                    currentGoal = getLocalGoalPointFirst();
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                    if(currentGoal.equals(goal)){
                        breakNode = current;
                    	
                    }
                }
                else{
                    currentGoal = goal;
                    usedGoalList.add(currentGoal);
                    localGoalList.clear();
                    localGoalList.add(goal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                	breakNode = current;
                	
                }
            }
            
            // Create a tracking of the node that it broke this loop on... Might need to ensure that it holds the adaptable path better
            if(timeLength+breakT > reCalcAllowedTime-(180-timeLeft)){
                if(currentGoal != goal){
                    currentGoal = goal;
                    usedGoalList.add(currentGoal);
                    localGoalList.clear();
                    localGoalList.add(goal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);                		
                    breakNode = current;
                	
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
                    		if (reCalcAllowedTime < timeLength + 180 - timeLeft + getTimeMinEstimateFromGoal(neighbor)){
                    			neighborIsBetter = true;
//                               if(currentGoal != goal){
//                             	  System.out.println("Yes I needed to be changed");
//                             	  currentGoal = goal;
//                             	  localDistanceFromGoal.clear();
//                             	  markLocalNeighbors(currentGoal,localG);
//                             	}
                             	if(recount<2){
//                             		if(!currentGoal.equals(goal)){
                             		
                             	    neighborCheck.remove(neighbor);}
//                             	System.out.println("Hey moron, I'm stuck here..");
//                             }
                    		}
                    		// TODO Catches rerouting due to path taking too long
                             // Recalc due to too long a time length path
                             if(count > 3){
                           		System.out.println("Do you want to re-route?");
                         		System.out.println("Like you had a choice... Rerouting...");
                         		System.out.println("We apologize for the delay, but you really shouldn't have left the path this far...");
                         		System.out.println("What are yo!?!?!?!?");
                         		FloorRankingOrder goalLogic = new FloorRankingOrder(localG, windowSizeReCalc, stairMod, floorMod);
//                         		count = 0;
//                         		recount =+ 1;
                         		this.reroutedGoalList = new ArrayList<String>(goalLogic.goalList);
//                         		System.out.println("Local goal list" + localGoalList);
//                         		System.out.println("GoalList" + goalList);
//                          		if(recount>4){
//                         			System.out.println("Was this necessary?");
//                         			return reconstructPath(cameFromList);
//                         		}
                         		tooLongRoute =true;
                         		return reCalcOptPath(currentStart, timeLeft, pointColl, ++recount, false);
                          	  }
                    // set neighbors parameters if it is better
                    if (neighborIsBetter) {
//                                             G.setPreviousNode(neighbor, current);
                        localG.setDistanceFromStart(neighbor, neighborDistanceFromStart);
                        //neighbor.setHeuristicDistanceFromGoal(heuristic.heuristic(neighbor, currentGoalNode));
                    }
//                    else{
//                     	localG.setDistanceFromStart(neighbor, 999999);
                     }}}
//        }
        return null;
    }

    public Path reCalcOldGoalListNoChanges(String currentStart, int timeL, ArrayList<String> pointColl, int recNum) {

        if(timeL==0){
            timeL=180;
        }

        usedGoalList.clear();
        usedGoalsToVisitList.clear();
    	int reCalcAllowedTime = allowedTime;
    	int calcAddedValue = 0;
    	if(allowedTime == 180){
    		calcAddedValue = 0;
    	}
    	else{
    		calcAddedValue = (allowedTime - 180)/3;
    	}

    	
    	if(timeL < 120){
    		reCalcAllowedTime = allowedTime-calcAddedValue;
    	}
    	else if(timeL < 90){
     		reCalcAllowedTime = allowedTime-calcAddedValue;
    	}
    	else if(timeL < 60){
    		reCalcAllowedTime = 180;
    	}
    	
    	

    	ArrayList<String> pointLocCollected = new ArrayList<String>(pointColl);
        returnMissingPoints(pointLocCollected);


        this.localGoalList = new ArrayList<String>(prevUsedGoalList);
        System.out.println("LocalGoalList Used at Beginning of run = " + localGoalList);
        int breakT = reCalcAllowedTime-breakTime;
        clearValues();
        this.timeLeft=timeL;
        this.timeLength = 0;//180 - timeL; Because we are comparing it to current values

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
        usedGoalList.add(currentGoal);
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
               
        while (frontierList.size() != 0) {
        	
        	if(neighborCheck.isEmpty()){
        		recount++;
        	}
            int totalDistance = 99999999;
            for (String mm : neighborCheck) {

                if (localG.getPointsAt(mm) > 0) {
                    totalDistance = getLocalTotalDistance(mm); // Due to the break, this does nothing
                    current = mm;
//                    recount = 0;
                    break;

                } else if (getLocalTotalDistance(mm) < totalDistance) {
                    totalDistance = getLocalTotalDistance(mm);
                    current = mm;
//                    recount = 0;
                }
            }
//            if (neighborCheck.size() == 0) {
//                // Then I need to go back to the previous node I was just at.
//                String neighborGoBack = cameFromList.get(cameFromList.size() - 2);
//                int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighborGoBack));
//                localG.setDistanceFromStart(neighborGoBack, neighborDistanceFromStart);
//
//                current = neighborGoBack;
//            }
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
                timeLength =timeLength +timeQRCodeEst;
                pointsCollected += localG.getPointsAt(current);// + pointsCollected;
                usedGoalsToVisitList.add(current);
                qrCollected = qrCollected +1;
                weightedDist += getDistanceToNextPoint(current);
                // Set up to look at different maps now
                removePoints(current, localG);
            }
            //Adds to timeLength for stairs
            else if (previousFloor != currentFloor){
                timeLength = timeLength + timeStairEst;
                weightedDist += getDistanceToNextPoint(current);
            }
            //Adds to timeLength for normal nodes
            else{
                if(currentBuilding > 33){
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
                else{
                    timeLength = timeLength + timeFloorEst;
                    weightedDist += getDistanceToNextPoint(current);
                }
            }          
            if(current.equals(goal)) {
//            	System.out.println("Tony, you're killing me...");
            	System.out.println("GoalList = " + goalList);
            	System.out.println("prevUsedGoalList = " + prevUsedGoalList);
            	System.out.println("usedGoalList = " + usedGoalList);
            	usedGoalsToVisitList.add(goal);
                return reconstructPath(cameFromList);
            }
            if(cameFromList.contains(currentGoal)){
                if(timeLength+breakT < reCalcAllowedTime-(180-timeLeft)){
                    removeLocalGoalList(0);
                    currentGoal = getLocalGoalPointFirst();
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                    if(currentGoal.equals(goal)){                   		
                    	breakNode = current;
                    	
                    }
                }
                else{
                    currentGoal = goal;
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                	breakNode = current;
                	
                }
            }
            
            // Create a tracking of the node that it broke this loop on... Might need to ensure that it holds the adaptable path better
            if(timeLength+breakT > reCalcAllowedTime-(180-timeLeft)){
                if(currentGoal != goal){
                    currentGoal = goal;
                    usedGoalList.add(currentGoal);
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal,localG);
                	breakNode = current;
                	
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
                    		if (reCalcAllowedTime < timeLength + 180 - timeLeft + getTimeMinEstimateFromGoal(neighbor)){
                    			neighborIsBetter = true;
//                               if(currentGoal != goal){
//                             	  System.out.println("Yes I needed to be changed");
//                             	  currentGoal = goal;
//                             	  localDistanceFromGoal.clear();
//                             	  markLocalNeighbors(currentGoal,localG);
//                             	}
                             	if(recount<2){
//                             		if(!currentGoal.equals(goal)){
                             		
                             	    neighborCheck.remove(neighbor);}
//                             	System.out.println("Hey moron, I'm stuck here..");
//                             }
                    		}


                    // set neighbors parameters if it is better
                    if (neighborIsBetter) {
//                                             G.setPreviousNode(neighbor, current);
                        localG.setDistanceFromStart(neighbor, neighborDistanceFromStart);
                        //neighbor.setHeuristicDistanceFromGoal(heuristic.heuristic(neighbor, currentGoalNode));
                    }
//                    else{
//                     	localG.setDistanceFromStart(neighbor, 999999);
                     }}}
//        }
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

    private void timeHeuristic(){ 
//    	StopWatch s = new StopWatch();
//    	s.start();  
    	timeMinEstimateFromGoal.put(goal, 0);
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        int time = 0;
        oldNeighbor.add(goal);
        int k = 0;
        String prevNode =goal;
        int currentFloor = 0;
        int previousFloor = 0;
        int currentBuilding = 0;
        //move current Node to the closed (already searched) list
        String lineNew = goal;
        String[] namesNew = lineNew.split(":");
        currentBuilding= Integer.parseInt(namesNew[0]);
               
        while(localG.toList(localG.vertices()).size()!=timeMinEstimateFromGoal.size()){

            for(k=0; k<oldNeighbor.size(); k++){
            	prevNode = oldNeighbor.get(k);
                nList = localG.adjacentTo(prevNode);
                time = getTimeMinEstimateFromGoal(prevNode);
                String linePrev = prevNode;
                String[] namesPrev = linePrev.split(":");
                previousFloor = Integer.parseInt(namesPrev[1]);
                for(String n: nList){
                	
                    lineNew = n;
                    namesNew = lineNew.split(":");
                    currentFloor= Integer.parseInt(namesNew[1]);
                    currentBuilding= Integer.parseInt(namesNew[0]);
                	int timeAdded = 0;
                    //Adds to timeLength for points
                    if(localG.getPointsAt(n)>0){
                    	timeAdded = timeQRCodeEst;
                    }
                    //Adds to timeLength for stairs
                    else if (previousFloor != currentFloor){
                    	timeAdded = timeStairEst;
                    }
                    //Adds to timeLength for normal nodes
                    else{
                        if(currentBuilding > 33){
                        	timeAdded = timeFloorEst;
                        }
                        else{
                        	timeAdded = timeFloorEst;
                        }
                    }
                    int nTime = time + timeAdded;
                    if(!timeMinEstimateFromGoal.containsKey(n)){
                    	timeMinEstimateFromGoal.put(n, nTime);
                        if(!oldNeighbor.contains(n)){
                            oldNeighbor.add(n);
                        }}
                    else if(getTimeMinEstimateFromGoal(n)>nTime){
                    	timeMinEstimateFromGoal.remove(n);
                        timeMinEstimateFromGoal.put(n, nTime);
                    }}}}
//        s.stop();
//        System.out.println("Run time Marking Local Neighbors: " + s.getElapsedTime() +" ms");  
//        System.out.println(timeMinEstimateFromGoal.toString()); 
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
    private int getTimeMinEstimateFromGoal(String v){
        localG.validateVertex(v);
        return Integer.parseInt(timeMinEstimateFromGoal.get(v).toString());
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
        File log = new File("SelectedAdapTestsForSelection.txt");
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
        System.out.println("Length of allowed time: " + allowedTime);
        System.out.println("Length of breakTime: " + breakTime);
        System.out.println("Length of floor weight: " + localG.getBaseFloorWeight());
        System.out.println("Length of stair weight: " + localG.getStairWeight());
        System.out.println("You collected " + getCollectedPoints() +" points!");
        System.out.println("You collected " + getQRCollected() +" QR Codes!");
//        System.out.println(goalList);

//       logger(title + " Results");
//       logger("Waypoints of algorithm path: " + path);
//       logger("Length of shortest path: " + "\t" + shortestPath.getLength());
//       logger("Length of weighted path: " + "\t" + getWeightedDist());
//       logger("Time-Length of shortest path: " + "\t" + getTimeLength());
//       logger("You collected " + "\t" + getCollectedPoints() + "\t" +" points!");
//       logger("You collected " + "\t" + getQRCollected() + "\t" +" QR Codes!");

        logger(title + "\t" + trialNumber + "\t" + localG.getBaseFloorWeight() +"\t" + localG.getStairWeight() +"\t" + allowedTime +"\t" + breakTime +"\t" +
        timeQRCodeEst +"\t" + timeFloorEst +"\t" + timeStairEst +"\t" + windowSizeReCalc +"\t" +shortestPath.getLength()+ "\t" + getWeightedDist()+ "\t" +
        		getTimeLength()+ "\t" +  getCollectedPoints()+ "\t" +  getQRCollected()+ "\t" +path.get(path.size()-1)+ "\t" +goal);
        return path;
    }

    public ArrayList<String> getPath(){
        ArrayList<String> path = new ArrayList<String>();
        for(int i =0; i<shortestPath.getLength(); i++){
            path.add(shortestPath.getWayPoint(i));
        }
        return path;
    }

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
//        System.out.println("Reconstructing..." + cameFromList.isEmpty());

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