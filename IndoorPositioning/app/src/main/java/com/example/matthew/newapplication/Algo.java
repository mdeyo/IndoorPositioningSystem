package com.example.matthew.newapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import app.Graph;


public class Algo {

	/*
	 * Pseudo Code before I talk with Matt and have to actually write the code.
	 * Notes Here and Java below notes
	 * 
	 * We begin with a start location and an end location. 
	 * Our starting location becomes the first point in the open set and the first to be moved to the closed set. 
	 * We then expand and look at the neighbors of the starting point and add them to the open set and 
	 * arrange them in shortest distance to the goal. And then I move the one I liked to the closed set and it
	 * becomes the new current point and I look at its neighbors. 
	 * Here is the basic Python for Breadth First Search: 
	 * frontier = Queue()
	 * frontier.put(start) 
	 * visited = {} 
	 * visited[start] = True
	 * 
	 * while not frontier.empty(): 
	 * current = frontier.get() 
	 * for next in graph.neighbors(current): 
	 * if next not in visited: 
	 * frontier.put(next)
	 * visited[next] = True
	 * 
	 * Modified to account for path finding 
	 * frontier = Queue()
	 * frontier.put(start) 
	 * came_from = {} 
	 * came_from[start] = None
	 * 
	 * while not frontier.empty(): 
	 * current = frontier.get() 
	 * for next in graph.neighbors(current): 
	 * if next not in came_from: 
	 * frontier.put(next)
	 * came_from[next] = current
	 * 
	 * Reconstructing Paths: 
	 * current = goal 
	 * path = [current] 
	 * while current != start: 
	 * current = came_from[current] 
	 * path.append(current)
	 * 
	 * Early Exit once the goal is reached: 
	 * frontier = Queue()
	 * frontier.put(start) 
	 * came_from = {} 
	 * came_from[start] = None
	 * 
	 * while not frontier.empty(): 
	 * current = frontier.get()
	 * 
	 * if current == goal: 
	 * break
	 * 
	 * for next in graph.neighbors(current): 
	 * if next not in came_from:
	 * frontier.put(next) 
	 * came_from[next] = current
	 * 
	 * To add movement costs/points/weightings we need Dijkstra�s Algorithm The
	 * code becomes: 
	 * frontier = PriorityQueue() 
	 * frontier.put(start, 0) 
	 * came_from = {} 
	 * cost_so_far = {} 
	 * came_from[start] = None 
	 * cost_so_far[start] = 0
	 * 
	 * while not frontier.empty(): 
	 * current = frontier.get()
	 * 
	 * if current == goal: 
	 * break
	 * 
	 * for next in graph.neighbors(current): 
	 * new_cost = cost_so_far[current] + graph.cost(current, next) 
	 * if next not in cost_so_far or new_cost < cost_so_far[next]: 
	 * cost_so_far[next] = new_cost 
	 * priority = new_cost
	 * frontier.put(next, priority) 
	 * came_from[next] = current
	 * 
	 * The two algorithms above search every location even non-viable locations.
	 * A* and Greedy Best First Search though guess with a heuristic. I�m not
	 * going to detail Greedy because it is not what I�m looking for to do. A*
	 * is really a combination of the two. 
	 * Heuristic included to Dijkstra:
	 * 
	 * frontier = PriorityQueue() 
	 * frontier.put(start, 0) 
	 * came_from = {}
	 * cost_so_far = {} 
	 * came_from[start] = None 
	 * cost_so_far[start] = 0
	 * 
	 * while not frontier.empty(): 
	 * current = frontier.get()
	 * 
	 * if current == goal: 
	 * break
	 * 
	 * for next in graph.neighbors(current): 
	 * new_cost = cost_so_far[current] + graph.cost(current, next) 
	 * if next not in cost_so_far or new_cost < cost_so_far[next]: 
	 * cost_so_far[next] = new_cost 
	 * priority = new_cost + heuristic(goal, next) 
	 * frontier.put(next, priority) 
	 * came_from[next] = current
	 * 
	 * Here it is written in words: 
	 * OPEN = priority queue containing START
	 * CLOSED = empty set 
	 * while lowest rank in OPEN is not the GOAL: 
	 * current = remove lowest rank item from OPEN 
	 * add current to CLOSED
	 * for neighbors of current: 
	 * cost = g(current) + movementcost(current, neighbor) 
	 * if neighbor in OPEN and cost less than g(neighbor): 
	 * remove neighbor from OPEN, because new path is better 
	 * if neighbor in CLOSED and cost less than g(neighbor): ** 
	 * remove neighbor from CLOSED 
	 * if neighbor not in OPEN and neighbor not in CLOSED: 
	 * set g(neighbor) to cost 
	 * add neighbor to OPEN 
	 * set priority queue rank to g(neighbor) + h(neighbor) 
	 * set neighbor's parent to current
	 * 
	 * reconstruct reverse path from goal to start by following parent pointers
	 * 
	 * Heuristic for grid map, I�ll want to use Diagonal Distance since I�ll
	 * allow all directional movement. The Chebyshev distance uses the
	 * assumption that diagonal cost is the same as cardinal direction movement
	 * which may be fair and easier. If you want actual distance, then we want
	 * something like this: 
	 * function heuristic(node) = 
	 * dx = abs(node.x - goal.x)
	 * dy = abs(node.y - goal.y) 
	 * return D * (dx + dy) + (D2 - 2 * D) * min(dx, dy)
	 * 
	 * Tiebreaking could be a big problem and is shown in my dealings with the
	 * other code, since it won�t let me grab points directly next to the goal.
	 * We could make points the tiebreaker. All weighting be damned. Remember A*
	 * sorts by f value, so I need to change the g and h values to make them
	 * work better. I should go through the implementation notes to correctly do
	 * this.
	 */

    // Work in progress code for the A* Algorithm
    // Incompletes are noted with comments.

    private Graph localG; // Defines the connections between nodes and edges and weightings/points
    private ArrayList<String> frontierList; // Must define node
    //	private SortedGoalsList goalList;
    private ArrayList<String> cameFromList;
    //	private int startX;
    private String start;
    private String goal;
    public int timeLength;
    //	private int goalY;
    public Path shortestPath;
    //Logger log = new Logger(); // Not sure if I need this, but we'll include it for now.
//    private Heuristic heuristic;  // heuristic estimate of cost
    private double distanceTravelled; // physical distance covered
    private Map localDistanceFromGoal;
    public int pointsCollected;
    public int qrCollected;
    private ArrayList<String> goalList;


    public Algo(Graph g, String start, String goal, ArrayList<String> goals) { //, Heuristic heuristic){
        this.localG = new Graph();
        this.localG = g;
        cameFromList = new ArrayList<String>();
        frontierList = new ArrayList<String>();
        this.goalList = goals;
        localDistanceFromGoal = new HashMap();
        this.start = start;
        this.goal = goal;
        timeLength = 0;
        pointsCollected = 0;
//        goalList = new SortedGoalsList();
//        this.startX = map.getStartLocationX();
//        this.startY = map.getStartLocationY();
//        this.goalX = map.getGoalLocationX();
//        this.goalY = map.getGoalLocationY();
//        this.heuristic = heuristic;
    }
	
	/*
	// Currently the app only recognizes cardinal direction movements for simplicity of the code
    // The Manhattan distance works best for cardinal directions
    // If we change it to 8 directional freedom, then we will use diagonal distance
    // Both will be displaced below
    // The heuristic should NOT account for the weightings or the points. 
    // Those will be accounted for in the cost of the 
//	private double heuristic(Node node, Node goal){
//		
//		// Manhattan Distance
//		// There may be a problem here with the TreeMap, but I need to talk to Matt
//		// This code is using a clearly defined grid with x and y
//		// The map grid uses floors and a way to large grid of points compared to the actual values
//		// Just something to keep in mind
//		
//		double dx =  java.lang.Math.abs(node.getX() - goal.getX());
//		double dy =  java.lang.Math.abs(node.getY() - goal.getY());
//		
//		double cost = 1; // Cost to move to one space. Generic 1 for now
//		double heuristic = cost*(dx+dy);
//		
//		
//		
//		// Diagonal Distance
//		
////		double dx =  java.lang.Math.abs(node.getX() - goal.getX());
////		double dy =  java.lang.Math.abs(node.getY() - goal.getY());
////		
////		double cost = 1; // Cost to move to one space. Generic 1 for now
////		double dCost = cost*java.lang.Math.sqrt(2);
////		double heuristic = cost*(dx+dy) + (dCost - 2*cost)*java.lang.Math.min(dx,dy);
//		
//		// If we change it to multiple goals, then it would become something like this:
//		// The biggest issue is that I can't actually determine which index was the minimum
//		// from this application, so the goal achieved would have to be removed from the goal list if visited
//		
////		public double heuristic(Node node, SortedNodeList goal){
////			double heuristic;
////			double[] closestGoal = new double[goal.size()];
////			for(int i = 0; i< goal.size(); i++){
////				double dx =  java.lang.Math.abs(node.getX() - goal.get(i).getX());
////				double dy =  java.lang.Math.abs(node.getY() - goal.get(i).getY());
////				double cost = 1; // Cost to move to one space. Generic 1 for now
////				closestGoal[i] = cost*(dx+dy);
////				
////			}
////			Arrays.sort(closestGoal);
////		 	heuristic = closestGoal[0];
////		}
//			
//		
//
//		
//		return heuristic;
//	}
	
	     
  
     /**
      * cameFromList The list of Nodes not searched yet, sorted by their distance to the goal as guessed by our heuristic.
      */


    //Must define the end result, which is that I want a path. So define path

    // I may end up switching this to a list of goal nodes that define the point locations
    // List of stuff I need to come into this: current location/start location, goal/goals, time and the map
    public Path calcOptPath(String currentStart) {//start, String goal, ArrayList<String> goals) {
//             this.startX = startX;
//             this.start = start;
//             this.goal = goal;
//             this.goalList = goals;
//             this.goalY = goalY;
        this.timeLength = 0;
        if (currentStart.equals(goal)) {
            return null;
        }

        if(localG.getPointsAt(currentStart) > 0){
            localG.reverseWeightingsGradient(currentStart, localG);
            localG.removePoints(currentStart);
            goalList.remove(currentStart);
        }
        if(localG.getPointsAt(this.goal) > 0){
            localG.reverseWeightingsGradient(this.goal, localG);
            localG.removePoints(this.goal);

        }
        //mark start and goal node
//             G.setStartLocation(start);
//             G.setGoalLocation(goal);
        ArrayList<String> neighborCheck = new ArrayList<String>();
        // This loop defines which locations have points and creates a goal list to search that way
        // I think if I just ensure that there are no negative numbers and it is scaled properly, then this won't be necessary
//             for(int x = 0 ; x<map.getMapWidth(); x++){
//            	 for(int y = 0 ; y<map.getMapHeight(); y++){
//            		 Node pointGoal = map.getNode(x, y);
//            		 if(pointGoal.getPointValue() < 0){
//            			 Goals newGoal = new Goals(x,y,pointGoal.getPointValue());
//            			 goalList.add(newGoal);
//            		 }
//            	 }
//            	 
//             }

//             Goals finalGoal = new Goals(goalX,goalY, 0);
//             goalList.add(finalGoal);
        //Check if the goal node is blocked (if it is, it is impossible to find a path there)
        if (localG.isObstacle(goal)) {
            return null;
        }

        localG.setDistanceFromStart(currentStart, 0);
        cameFromList.clear();
        frontierList.clear();
        frontierList.add(currentStart);
        neighborCheck.add(currentStart);

        Log.d("algo", "1");
//             Node current = frontierList.getFirst(); I'm dumb, this can't be outside the loop or else it never gets tested and reassigned...
        //while we haven't reached the goal yet
        String current = null;
        String currentGoal = getGoalPointFirst();
        localDistanceFromGoal.clear();
        markLocalNeighbors(currentGoal, localG);
        while (frontierList.size() != 0) {

            //get the first Node from non-searched Node list, sorted by lowest distance from our goal as guessed by our heuristic


            /**
             * I need to make this useful
             * Create a loop that orders the terms in neighborCheck and actually selects the lowest one or the one with points
             * Currently totalDistance isn't doing anything
             * Consider removing the Total distance
             * Actually, it does just enough to be useful...
             */

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
            Log.d("algo", "2");


            if (neighborCheck.size() == 0) {
                Log.d("algo", "3");
                // Then I need to go back to the previous node I was just at.
                String neighborGoBack = cameFromList.get(cameFromList.size() - 2);
                int neighborDistanceFromStart = (localG.getDistanceFromStart(current) + getDistanceToNextPoint(neighborGoBack));
//              	 frontierList.add(neighborGoBack);
                localG.setDistanceFromStart(neighborGoBack, neighborDistanceFromStart);
//                   current = neighborGoBack;
//              	 frontierList.remove(current);
//                   cameFromList.add(neighborGoBack);
                current = neighborGoBack;
//                   if(neighborGoBack.equals(start)){
//                  	 timeLength= 0;
//                   }
//                   //Adds to timeLength for points
//                   else if(G.getPointsAt(neighborGoBack)>0){
//                  	 timeLength =timeLength +4;
//                  	 G.removePoints(current);
//                   }
//                   //Adds to timeLength for stairs
//                   else if(G.getFloorWeight(neighborGoBack)>10){
//                  	 timeLength = timeLength + 12;
//                   }
//                   //Adds to timeLength for normal nodes
//                   else{
//                  	 timeLength = timeLength + 2;
//                   }


            }
            neighborCheck.clear();
//            	 String current = frontierList.getFirst();

            // check if our current Node location is the goal Node. If it is, we are done.
//                     if(current.equals(goal)) {
//                             return reconstructPath(cameFromList);
//                     }

            //move current Node to the closed (already searched) list
            frontierList.remove(current);
            cameFromList.add(current);
            if (current.equals(start)) {
                timeLength = 0;
            }
            //Adds to timeLength for points
            else if (localG.getPointsAt(current) > 0) {
                timeLength = timeLength + 3;
                pointsCollected = localG.getPointsAt(current) + pointsCollected;
                qrCollected = qrCollected + 1;
//                    	 System.out.println(current);
                // Set up to look at different maps now
                localG.reverseWeightingsGradient(current, localG);
                localG.removePoints(current);
                Log.d("algo", "removed points at "+current);

            }
            //Adds to timeLength for stairs
//                     else if(localG.getFloorWeight(current)>localG.getBaseFloorWeight()){
            else if (localG.isStairs(current)) {
                timeLength = timeLength + 10;
            }
            //Adds to timeLength for normal nodes
            else {
                timeLength = timeLength + 2;
            }
            Log.d("algo", "4");

            if (current.equals(goal)) {
                Log.d("algo", "99");
                return reconstructPath(cameFromList);
            }
            //Determine current goal for exploration

//                     Goals currentGoal = goalList.getFirst();
//                     Node currentGoalNode = map.getNode(currentGoal.getGoalX(), currentGoal.getGoalY());
            if (cameFromList.contains(currentGoal)) {
                if (timeLength < 100) {
                    removeGoalList(0);
                    currentGoal = getGoalPointFirst();
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal, localG);
                } else {
                    currentGoal = goal;
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal, localG);
                }

            }

            Log.d("algo", "5");


            if (timeLength > 100) {
                if (currentGoal != goal) {
                    currentGoal = goal;
                    localDistanceFromGoal.clear();
                    markLocalNeighbors(currentGoal, localG);
                }
            }

//                     if(cameFromList.contains(currentGoalNode)){
//                    	 goalList.remove(currentGoal);
//                    	 currentGoal = goalList.getFirst();
//                    	 currentGoalNode = map.getNode(currentGoal.getGoalX(), currentGoal.getGoalY());
//                     }

            //go through all the current Nodes neighbors and calculate if one should be our next step
            neighborCheck = localG.adjacentTo(current);

//                     for(String neighborDB : G.adjacentTo(current)){
//                         if (cameFromList.contains(neighborDB)){
//                        	neighborCheck.remove(neighborDB);
//                        	} 
//                    	 
//                     }
//                     
//                     if(neighborCheck.size() == 0){
//                    	 // Then I need to go back to the previous node I was just at.
//                    	 String neighbor = cameFromList.get(cameFromList.size() - 1) ;
//                    	 int neighborDistanceFromStart = (G.getDistanceFromStart(current) + getDistanceToNextPoint(neighbor));
//                    	 frontierList.add(neighbor);
//                         G.setPreviousNode(neighbor, current);
//                         G.setDistanceFromStart(neighbor, neighborDistanceFromStart);
//                         current = neighbor;
//                    	 frontierList.remove(current);
//                         cameFromList.add(current);
//                         if(current.equals(start)){
//                        	 timeLength= 0;
//                         }
//                         //Adds to timeLength for points
//                         else if(G.getPointsAt(current)>0){
//                        	 timeLength =timeLength +4;
//                        	 
//                         }
//                         //Adds to timeLength for stairs
//                         else if(G.getFloorWeight(current)>10){
//                        	 timeLength = timeLength + 12;
//                         }
//                         //Adds to timeLength for normal nodes
//                         else{
//                        	 timeLength = timeLength + 2;
//                         }
//                         
//                         neighborCheck= G.adjacentTo(current);
//                    	 
//                     }
            Log.d("algo", "6");

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

    private void markLocalNeighbors(String goal, Graph g) {
        localDistanceFromGoal.put(goal, 0);
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        int distance = 0;
        oldNeighbor.add(goal);
        int k = 0;
        while (g.toList(g.vertices()).size() != localDistanceFromGoal.size()) {
            // Like Astar, so we need two arrays, one with checked neighbors that have been assigned a distance
            // and one that has the next layer of neighbors to be assigned
            // We assign the distance value to ones that haven't already been assigned

            for (k = 0; k < oldNeighbor.size(); k++) {
//     			oldNeighbor.remove(k);
                nList = g.adjacentTo(oldNeighbor.get(k));
                distance = getLocalDistanceFromGoal(oldNeighbor.get(k));


                for (String n : nList) {
                    int nDistance = distance - g.getPointsAt(n) + g.getFloorWeight(n);
                    if (!localDistanceFromGoal.containsKey(n)) {
                        localDistanceFromGoal.put(n, nDistance);
                        if (!oldNeighbor.contains(n)) {
                            oldNeighbor.add(n);
                        }
                    } else if (getLocalDistanceFromGoal(n) > nDistance) {
                        localDistanceFromGoal.remove(n);
                        localDistanceFromGoal.put(n, nDistance);

                    }
                }
            }
        }
    }

    private void clearLocalNeighbors() {
        localDistanceFromGoal.clear();
    }


    private int getDistanceToNextPoint(String v) {
        localG.validateVertex(v);
        int distanceToNextPoint;
        distanceToNextPoint = localG.getFloorWeight(v) - localG.getPointsAt(v);
        return distanceToNextPoint;
    }

    private int getLocalTotalDistance(String current) {
        int thisTotalDistance = getLocalDistanceFromGoal(current) + localG.getDistanceFromStart(current);
        return thisTotalDistance;
    }

    private int getLocalDistanceFromGoal(String v) {
        localG.validateVertex(v);
        return Integer.parseInt(localDistanceFromGoal.get(v).toString());

    }

    public String getGoalList(int index) {
        return goalList.get(index);
    }

    public String removeGoalList(int index) {
        return goalList.remove(index);
    }

    public String getGoalPointFirst() {
        return goalList.get(0);
    }

    public ArrayList<String> printPath() {
        ArrayList<String> path = new ArrayList<String>();
        for (int i = 0; i < shortestPath.getLength(); i++) {


            //System.out.println(shortestPath.getWayPoint(i));

            path.add(shortestPath.getWayPoint(i));

        }
//        System.out.print("Waypoints of algorithm path: ");
//        System.out.println(path);
//        System.out.print("Length of shortest path: ");
//        System.out.println(shortestPath.getLength());
//        System.out.print("Time-Length of shortest path: ");
//        System.out.println(getTimeLength());
        if (localG.getBaseFloorWeight() == 10) {
            System.out.println("You collected " + getCollectedPoints() + " points!");
        } else {
            System.out.println("You collected " + getCollectedPoints() / 10 + " points!");
        }
        System.out.println("You collected " + getQRCollected() + " QR Codes!");
//    	 System.out.println(Algo.G.toString());
//    	 System.out.println(path.get(path.size() - 2));
//    	 System.out.println(path.get(1));
//    	 System.out.println(shortestPath);
        return path;
    }
     
    /* 
     
//     public void printPath() {
//             Node node;
//             for(int x=0; x<map.getMapWidth(); x++) {
//
//                     if (x==0) {
//                             for (int i=0; i<=map.getMapWidth(); i++)
//                                     System.out.print("-");
//                             System.out.println();   
//                     }
//                     System.out.print("|");
//
//                     for(int y=0; y<map.getMapHeight(); y++) {
//                             node = map.getNode(x, y);
//                             if (node.isObstacle) {
//                                     System.out.print("X");
//                             } else if (node.isStart) {
//                                     System.out.print("s");
//                             } else if (node.isGoal) {
//                                     System.out.print("g");
//                             } else if (shortestPath.contains(node.getX(), node.getY())) {
//                                     System.out.print("�");
//                             } else {
//                                     System.out.print(" ");
//                             }
//                             if (y==map.getMapHeight())
//                                     System.out.print("_");
//                     }
//
//                     System.out.print("|");
//                     System.out.println();
//             }
//             for (int i=0; i<=map.getMapWidth(); i++)
//                     System.out.print("-");
//     }
//     
//     public void printMapWeightingPoint() {
//         Node node;
//         for(int x=0; x<map.getMapWidth(); x++) {
//
//                 if (x==0) {
//                         for (int i=0; i<=map.getMapWidth(); i++)
//                                 System.out.print("-");
//                         System.out.println();   
//                 }
//                 System.out.print("|");
//
//                 for(int y=0; y<map.getMapHeight(); y++) {
//                         node = map.getNode(x, y);
//                         if (node.isObstacle) {
//                                 System.out.print("X");
//                         } else if (node.isStart) {
//                                 System.out.print("s");
//                         } else if (node.isGoal) {
//                                 System.out.print("g");
//                         } else if (node.pointValue < 0 && node.weighting > 0){
//                         	System.out.print(node.weighting + node.pointValue);
//                         } else if (node.pointValue > 0 && node.weighting > 0){
//                             System.out.print(node.weighting + node.pointValue);
//                         } else if (node.pointValue < 0) {
//                         	System.out.print(node.pointValue);
//                 		} else if (node.weighting > 0) {
//                         	System.out.print(node.weighting);
//                 		} else {
//                                 System.out.print(" ");
//                         }
//                         if (y==map.getMapHeight())
//                                 System.out.print("_");
//                 }
//
//                 System.out.print("|");
//                 System.out.println();
//         }
//         for (int i=0; i<=map.getMapWidth(); i++)
//                 System.out.print("-");
// }
//     
//     public void printRouteValue() {
//         Node node;
//         for(int x=0; x<map.getMapWidth(); x++) {
//
//                 if (x==0) {
//                         for (int i=0; i<=map.getMapWidth(); i++)
//                                 System.out.print("-");
//                         System.out.println();   
//                 }
//                 System.out.print("|");
//
//                 for(int y=0; y<map.getMapHeight(); y++) {
//                         node = map.getNode(x, y);
//                         if (node.isObstacle) {
//                                 System.out.print("X");
//                         } else if (node.isStart) {
//                                 System.out.print("s");
//                         } else if (node.isGoal) {
//                                 System.out.print("g");
//                         } else if (shortestPath.contains(node.getX(), node.getY())) {
//                             System.out.print(node.weighting + node.pointValue);
//                         } else {
//                                 System.out.print(" ");
//                         }
//                         if (y==map.getMapHeight())
//                                 System.out.print("_");
//                 }
//
//                 System.out.print("|");
//                 System.out.println();
//         }
//         for (int i=0; i<=map.getMapWidth(); i++)
//                 System.out.print("-");
// }
//
//     public void printMapAllValues() {
//         Node node;
//         for(int x=0; x<map.getMapWidth(); x++) {
//
//                 if (x==0) {
//                         for (int i=0; i<=map.getMapWidth(); i++)
//                                 System.out.print("-");
//                         System.out.println();   
//                 }
//                 System.out.print("|");
//
//                 for(int y=0; y<map.getMapHeight(); y++) {
//                         node = map.getNode(x, y);
//                         if (node.isObstacle) {
//                                 System.out.print("X");
//                         } else if (node.isStart) {
//                                 System.out.print("s");
//                         } else if (node.isGoal) {
//                                 System.out.print("g");
//                         } else {
//                         	System.out.print(node.weighting + node.pointValue);
//                         } 
//                         if (y==map.getMapHeight())
//                                 System.out.print("_");
//                 }
//
//                 System.out.print("|");
//                 System.out.println();
//         }
//         for (int i=0; i<=map.getMapWidth(); i++)
//                 System.out.print("-");
// }*/

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

        for (String uu : cameFromList) {
            path.appendWayPoint(uu);

        }
        this.shortestPath = path;
        return path;
    }
  
     /*
//     private class SortedNodeList {
//
//             private ArrayList<String> list = new ArrayList<String>();
//             private int distanceFromStart;
//
//             public String getFirst() {
//                     return list.get(0);
//             }
//
//             public String get(int i) {
//
//				return list.get(i);
//			}
//
//			public void clear() {
//                     list.clear();
//             }
//
//             public void add(String node, int distanceFromStart) {
//                     list.add(node , distanceFromStart);
//                     Collections.sort(list);
//             }
//
//             public void remove(String n) {
//                     list.remove(n);
//             }
//
//             public int size() {
//                     return list.size();
//             }
//
//             public boolean contains(String n) {
//                     return list.contains(n);
//             }
//     }
//     public class SortedGoalsList {
//
//	        private ArrayList<Goals> list = new ArrayList<Goals>();
//
//	        public Goals getFirst() {
//	                return list.get(0);
//	        }
//
//	        public Goals get(int i) {
//
//				return list.get(i);
//			}
//
//			public void clear() {
//	                list.clear();
//	        }
//
//	        public void add(Goals goals) {
//	                list.add(goals);
//	                Collections.sort(list);
//	        }
//
//	        public void remove(Goals n) {
//	                list.remove(n);
//	        }
//
//	        public int size() {
//	                return list.size();
//	        }
//
//	        public boolean contains(Goals n) {
//	                return list.contains(n);
//	        }
//	}*/
}
