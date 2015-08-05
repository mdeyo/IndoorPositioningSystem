package com.example.matthew.newapplication;

//import android.content.res.AssetManager; Used by Matt
//import android.util.Log;   Used by Matt

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//import map.Node;


/*************************************************************************
 *  Compilation:  javac Graph.java
 *  Dependencies: ST.java SET.java In.java
 *
 *  Undirected graph data type implemented using a symbol table
 *  whose keys are vertices (String) and whose values are sets
 *  of neighbors (SET of Strings).
 *
 *  Remarks
 *  -------
 *   - Parallel edges are not allowed
 *   - Self-loop are allowed
 *   - Adjacency lists store many different copies of the same
 *     String. You can use less memory by interning the strings.
 *
 *************************************************************************/

/**
 * The <tt>Graph</tt> class represents an undirected graph of vertices
 * with string names.
 * It supports the following operations: add an edge, add a vertex,
 * get all of the vertices, iterate over all of the neighbors adjacent
 * to a vertex, is there a vertex, is there an edge between two vertices.
 * Self-loops are permitted; parallel edges are discarded.
 * <p/>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/45graph">Section 4.5</a> of
 * <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Graph implements Cloneable {

    // symbol table: key = string vertex, value = set of neighboring vertices
    private ST<String, SET<String>> st;
    public Map points;
    private Map distanceFromGoal;
    private Map distanceFromStart;
    private String start;
    private String goal;
    private Map previousNode;
    private int thisTotalDistance;
    private Map floorWeight;
    public ArrayList<String> obstacle;
    public ArrayList<String> stairs;
    private int baseFloorWeight;
    private int stairWeight;
    //	private static JFrame frame;
// 	number of edges
    private int E;

    /**
     * Create an empty graph with no vertices or edges.
     */
//Will NOT Work because I'd have to deep clone/copy all of the objects within Graph and this is a pain the @$$ so I'm hard coding stuff instead
    //Maybe it can work...
    public Graph(Graph g) {
//    	this.st = new ST<String, SET<String>>(g.st);

        this.st = new ST<String, SET<String>>();
        for (String current : g.st.keys()) {
            SET<String> setCurrent = g.st.get(current);
            SET<String> second = setCurrent.clone();
            st.put(current, second);

        }
        this.points = new HashMap(g.points);
        this.distanceFromGoal = new HashMap(g.distanceFromGoal);
        this.distanceFromStart = new HashMap(g.distanceFromStart);
        this.previousNode = new HashMap(g.previousNode);
        this.floorWeight = new HashMap(g.floorWeight);
        this.start = g.start;
        this.goal = g.goal;
        this.obstacle = new ArrayList(g.obstacle);
        this.thisTotalDistance = g.thisTotalDistance;
        this.stairs = new ArrayList(g.stairs);
        this.baseFloorWeight = g.baseFloorWeight;
        this.stairWeight = g.stairWeight;


    }

    public Graph() {
        st = new ST<String, SET<String>>();
    }

    /**
     * Create an graph from given input stream using given delimiter.
     */
    public Graph(String delimiter, InputStream is, int baseWeight, int stairW) {

        st = new ST<String, SET<String>>();
        points = new HashMap();
        distanceFromGoal = new HashMap();
        distanceFromStart = new HashMap();
        previousNode = new HashMap();
        floorWeight = new HashMap();
        start = null;
        goal = null;
        obstacle = new ArrayList<String>();
        thisTotalDistance = 0;
        stairs = new ArrayList<String>();
        baseFloorWeight = baseWeight;
        stairWeight = stairW;
        String[] stairList = {"33:0:99", "33:0:118", "33:0:129", "33:1:26", "33:1:69", "33:1:74", "33:1:94", "33:1:107",
                "33:2:45", "33:2:50", "33:2:70", "33:2:74", "33:3:74", "33:3:94", "33:4:73", "33:4:94",
                "35:0:120", "35:0:142", "35:1:96", "35:1:120", "35:1:142", "35:2:120", "35:2:142", "35:3:120",
                "35:3:142", "35:4:120", "35:4:142", "37:1:109", "37:2:99", "37:2:109", "37:2:116", "37:3:99",
                "37:3:116", "37:4:99", "37:4:116", "37:5:99", "37:5:116"};
        for (int m = 0; m < stairList.length; m++) {
            stairs.add(stairList[m]);
        }

        try {
//            AssetManager assetManager = getResources().getAssets();
//            InputStream is = assman.open("NodeMap");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            if (is != null) {

                while ((line = r.readLine()) != null) {
                    String[] names = line.split(delimiter);
                    if (!points.containsKey(names[0])) {
                        points.put(names[0], names[1]);
                    }
                    if (!floorWeight.containsKey(names[0])) {
                        floorWeight.put(names[0], names[2]);
//                		if(Integer.parseInt(names[2])>10){
//                		stairs.add(names[0]);
//                	}
                    }
                    for (int i = 3; i < names.length; i++) {
                        addEdge(names[0], names[i]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        st = new ST<String, SET<String>>();
//        points = new HashMap();
//        while (in.hasNextLine()) {
//            String line = in.readLine();
//            String[] names = line.split(delimiter);
//            if (!points.containsKey(names[0])) {
//                points.put(names[0], names[1]);
//            }
//            if (!floorWeight.containsKey(names[0])) {
//                floorWeight.put(names[0], names[2]);
////                if(Integer.parseInt(names[2])>baseFloorWeight){
////                	stairs.add(names[0]);
////                }
//            }
//            for (int i = 3; i < names.length; i++) {
//                addEdge(names[0], names[i]);
//            }
//        }

        for (String n : stairs) {
            floorWeight.remove(n);
            floorWeight.put(n, stairWeight);
        }
    }

    public Graph(In in, String delimiter, int baseWeight, int stairW) {

        st = new ST<String, SET<String>>();
        points = new HashMap();
        floorWeight = new HashMap();
        distanceFromGoal = new HashMap();
        distanceFromStart = new HashMap();
        previousNode = new HashMap();
        start = null;
        goal = null;
        thisTotalDistance = 0;
        obstacle = new ArrayList<String>();
        stairs = new ArrayList<String>();
        baseFloorWeight = baseWeight;
        stairWeight = stairW;
        String[] stairList = {"33:0:99", "33:0:118", "33:0:129", "33:1:26", "33:1:69", "33:1:74", "33:1:94", "33:1:107",
                "33:2:45", "33:2:50", "33:2:70", "33:2:74", "33:3:74", "33:3:94", "33:4:73", "33:4:94",
                "35:0:120", "35:0:142", "35:1:96", "35:1:120", "35:1:142", "35:2:120", "35:2:142", "35:3:120",
                "35:3:142", "35:4:120", "35:4:142", "37:1:109", "37:2:99", "37:2:109", "37:2:116", "37:3:99",
                "37:3:116", "37:4:99", "37:4:116", "37:5:99", "37:5:116"};
        for (int m = 0; m < stairList.length; m++) {
            stairs.add(stairList[m]);
        }
        while (in.hasNextLine()) {
            String line = in.readLine();
            /////////////////
            String[] names = line.split(delimiter);
            if (!points.containsKey(names[0])) {
                points.put(names[0], names[1]);
            }
            /////////////////////
            if (!floorWeight.containsKey(names[0])) {
                floorWeight.put(names[0], names[2]);
//                if(Integer.parseInt(names[2])>baseFloorWeight){
//                	stairs.add(names[0]);
//                }
            }
            for (int i = 3; i < names.length; i++) {
                addEdge(names[0], names[i]);
            }
        }


        for (String n : stairs) {
            floorWeight.remove(n);
            floorWeight.put(n, stairWeight);
        }
    }

//    @Override 
//    protected Graph clone() { 
//    	Graph clone = null; 
//    	try{ clone = (Graph) super.clone(); 
//    	}
//    	catch(CloneNotSupportedException e){
//    		throw new RuntimeException(e); // won't happen 
//    		}
//    		return clone; 
//    		}


    public int compareTo(String current, String otherNode) {
        int thisTotalDistanceFromGoal = getDistanceFromGoal(current) + getDistanceFromStart(current);
        int otherTotalDistanceFromGoal = getDistanceFromGoal(otherNode) + getDistanceFromStart(otherNode);

        if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
            return -1;
        } else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getBaseFloorWeight() {
        return baseFloorWeight;
    }

    public int getTotalDistance(String current) {
        int thisTotalDistance = getDistanceFromGoal(current) + getDistanceFromStart(current);
        return thisTotalDistance;
    }

    public void markNeighbors(String goal, Graph G) {
        distanceFromGoal.put(goal, 0);
        String current = goal;
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        int distance = 0;
        oldNeighbor.add(goal);
        int k = 0;
        while (st.size() != distanceFromGoal.size()) {
            // Like Astar, so we need two arrays, one with checked neighbors that have been assigned a distance
            // and one that has the next layer of neighbors to be assigned
            // We assign the distance value to ones that haven't already been assigned

            for (k = 0; k < oldNeighbor.size(); k++) {
//    			oldNeighbor.remove(k);
                nList = G.adjacentTo(oldNeighbor.get(k));
                distance = G.getDistanceFromGoal(oldNeighbor.get(k));


                for (String n : nList) {
                    int nDistance = distance - G.getPointsAt(n) + G.getFloorWeight(n);
                    if (!distanceFromGoal.containsKey(n)) {
                        distanceFromGoal.put(n, nDistance);
                        if (!oldNeighbor.contains(n)) {
                            oldNeighbor.add(n);
                        }
                    } else if (G.getDistanceFromGoal(n) > nDistance) {
                        distanceFromGoal.remove(n);
                        distanceFromGoal.put(n, nDistance);

                    }
                }
            }
        }
    }

    public void setDistanceFromStart(String node, int num) {


        distanceFromStart.put(node, num);

    }

    public void setPreviousNode(String node, String oldOne) {
        previousNode.put(node, oldOne);
    }

    public String getPreviousNode(String node) {
        validateVertex(node);
        return previousNode.get(node).toString();
    }

    public int getDistanceFromStart(String v) {
        validateVertex(v);
        return Integer.parseInt(distanceFromStart.get(v).toString());

    }

    /**
     * Number of vertices.
     */
    public int V() {
        return st.size();
    }

    /**
     * Number of edges.
     */
    public int E() {
        return E;
    }

    // throw an exception if v is not a vertex
    public void validateVertex(String v) {
        if (!hasVertex(v)) throw new IllegalArgumentException(v + " is not a vertex");
    }

    /**
     * Degree of this vertex.
     */
    public int degree(String v) {
        validateVertex(v);
        return st.get(v).size();
    }

    /**
     * Add edge v-w to this graph (if it is not already an edge)
     */
    public void addEdge(String v, String w) {
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        if (!hasEdge(v, w)) E++;
        st.get(v).add(w);
        st.get(w).add(v);
    }

    /**
     * Add vertex v to this graph (if it is not already a vertex)
     */
    public void addVertex(String v) {
        if (!hasVertex(v)) st.put(v, new SET<String>());
    }

    /**
     * Return the set of vertices as an Iterable.
     */
    public Iterable<String> vertices() {
        return st;
    }

    /**
     * Return the set of neighbors of vertex v as in ArrayList.
     */
    public ArrayList<String> adjacentTo(String v) {
        validateVertex(v);
        return toList(st.get(v));
//        return st.get(v);
    }

    /**
     * Is v a vertex in this graph?
     */
    public boolean hasVertex(String v) {
        return st.contains(v);
    }

    public boolean isObstacle(String v) {
        return obstacle.contains(v);
    }

    public boolean isStairs(String v) {
        return stairs.contains(v);
    }

    public void setObstacle(String v) {
        if (!this.obstacle.contains(v)) {
            this.obstacle.add(v);
            distanceFromGoal.put(v, 9999999);
        }
    }
    /**
     * Is v a vertex in this graph?
     */
//    public String getFirstValue(String v) {
//        if (!hasVertex(v)) {
//            Log.d("Graph", v + " is not a vertex");
////            Log.d("Graph string:",toString());
//            return "none";
//        }
//
//        else {
//            Log.d("Graph", v + " is a vertex");
//            return v;
////            return st.get(v).toString();
//        }
//    }

    /**
     * Is v-w an edge in this graph?
     */
    public boolean hasEdge(String v, String w) {
        validateVertex(v);
        validateVertex(w);
        return st.get(v).contains(w);
    }

    public int getPointsAt(String v) {
        validateVertex(v);
        return Integer.parseInt(points.get(v).toString());
    }

    public void removePoints(String v) {
        this.points.remove(v);
        this.points.put(v, 0);
    }

    public void removeVertex(String v){
        validateVertex(v);
        st.delete(v);
    }

    Graph updateConnections(){

       ST<String, SET<String>> st2 = new ST<>();

        for(String s:st){
            SET<String> edges = st.get(s);
            SET<String> newEdges = new SET<>();
            for(String neighbor:edges){
                if(st.contains(neighbor)){
                    newEdges.add(neighbor);
                }
            }
            st2.put(s,newEdges);
        }

        Log.d("remove st",st.get("35:2:142").toString());
        Log.d("remove st2",st2.get("35:2:142").toString());

        st.clear();
        st = st2;

        Log.d("removed st",st.get("35:2:142").toString());

        return this;
    }

    public void doublePoints(String v) {
        int newPoints = getPointsAt(v) * 2;
        removePoints(v);
        points.put(v, newPoints);

    }

    public int getFloorWeight(String v) {
        validateVertex(v);
        return Integer.parseInt(floorWeight.get(v).toString());

    }

    public void gradientGraph(Graph Gmap) {

        ArrayList<String> goals = new ArrayList<String>();

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (int m = 0; m < Gmap.toList(Gmap.vertices()).size(); m++) {
            String current = Gmap.toList(Gmap.vertices()).get(m);
            if (Gmap.getPointsAt(current) > 0) {
//    		Goals now = new Goals(current, G.getPointsAt(current));
                map.put(current, Gmap.getPointsAt(current));
            }
        }
        goals.addAll(map.keySet());
//		System.out.println(goals);


        for (String r : goals) {
            Gmap.markWeightingsGradient(r, Gmap);
        }

    }

    public void markWeightingsGradient(String goalcheck, Graph Gmap) {
//      	String current = goalcheck;
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        Map rateChange = new HashMap();
        rateChange.clear();
        oldNeighbor.clear();
        nList.clear();
        int qrPoint = 0;
        oldNeighbor.add(goalcheck);
        qrPoint = Gmap.getPointsAt(goalcheck);
//    	System.out.println("The points are" + qrPoint);
        int weightChange = -qrPoint;
//    	System.out.println(weightChange);
        rateChange.put(goalcheck, weightChange);
//    	while(weightChange < 0 ){

        for (int k = 0; k < oldNeighbor.size(); k++) {
//    			oldNeighbor.remove(k);
            if (isStairs(oldNeighbor.get(k))) {

                continue;
            }
            nList = Gmap.adjacentTo(oldNeighbor.get(k));
//    			int weighting = Gmap.getFloorWeight(oldNeighbor.get(k));
//    			weightChange = -(Gmap.getPointsAt(oldNeighbor.get(k))-1) -(10 - weighting);
            int increment = 1;
            int windowSize = 4;
            if (Gmap.baseFloorWeight > 10) {
                windowSize = 6;
            }
            weightChange = Integer.parseInt(rateChange.get(oldNeighbor.get(k)).toString()) + increment;
//    			System.out.println(weightChange);
            if (weightChange > 0) {
//    				System.out.println("Say What???!?!?!");
                break;
            }
            if (qrPoint + weightChange > windowSize * increment) {
//    				System.out.println("The difference is " + qrPoint+weightChange);
                break;
            }
            for (String n : nList) {

                int nWeight = weightChange + Gmap.getFloorWeight(n);

                if (!rateChange.containsKey(n)) {
                    rateChange.put(n, weightChange);
                }


                if (weightChange > 0) {
                    if (!oldNeighbor.contains(n)) {
                        oldNeighbor.add(n);
                    }
                    continue;
                } else if (oldNeighbor.contains(n)) {
                    continue;
                }
//    				else if(Gmap.getPointsAt(n)>0){
//    					if(!oldNeighbor.contains(n)){
//							oldNeighbor.add(n);
//						}
//    					continue;
//    				}
//    				else if(Gmap.getFloorWeight(n)>100){
//    					if(!oldNeighbor.contains(n)){
//							oldNeighbor.add(n);
//						}
//    					continue;
//    				}
                else {
                    if (nWeight < 0) {
                        nWeight = 0;
                    }
                    floorWeight.remove(n);
                    floorWeight.put(n, nWeight);
                    if (!oldNeighbor.contains(n)) {
                        oldNeighbor.add(n);
                    }

                }
            }
        }
//    		System.out.println(rateChange);
    }

//    				}

    public void reverseWeightingsGradient(String goalcheck, Graph Gmap) {
//  	String current = goalcheck;
        ArrayList<String> nList = new ArrayList<String>();
        ArrayList<String> oldNeighbor = new ArrayList<String>();
        Map rateReverse = new HashMap();
        rateReverse.clear();
        oldNeighbor.clear();
        nList.clear();
        int qrPoint = 0;
        oldNeighbor.add(goalcheck);
        qrPoint = Gmap.getPointsAt(goalcheck);
//	System.out.println("The points are" + qrPoint);
        int weightChange = -qrPoint;
//	System.out.println(weightChange);
        rateReverse.put(goalcheck, weightChange);
//	while(weightChange < 0 ){

        for (int k = 0; k < oldNeighbor.size(); k++) {
//			oldNeighbor.remove(k);
            if (isStairs(oldNeighbor.get(k))) {

                continue;
            }
            nList = Gmap.adjacentTo(oldNeighbor.get(k));
//			int weighting = Gmap.getFloorWeight(oldNeighbor.get(k));
//			weightChange = -(Gmap.getPointsAt(oldNeighbor.get(k))-1) -(10 - weighting);
            int increment = 1;
            int windowSize = 4;
            weightChange = Integer.parseInt(rateReverse.get(oldNeighbor.get(k)).toString()) + increment;
//			System.out.println(weightChange);
            if (weightChange > 0) {
//				System.out.println("Say What???!?!?!");
                break;
            }
            if (qrPoint + weightChange > windowSize * increment) {
//				System.out.println("The difference is " + qrPoint+weightChange);
                break;
            }
            for (String n : nList) {

                int nWeight = -weightChange + Gmap.getFloorWeight(n);

                if (!rateReverse.containsKey(n)) {
                    rateReverse.put(n, weightChange);
                }


                if (weightChange > 0) {
                    if (!oldNeighbor.contains(n)) {
                        oldNeighbor.add(n);
                    }
                    continue;
                } else if (oldNeighbor.contains(n)) {
                    continue;
                }
//				else if(Gmap.getPointsAt(n)>0){
//					if(!oldNeighbor.contains(n)){
//						oldNeighbor.add(n);
//					}
//					continue;
//				}
//				else if(Gmap.getFloorWeight(n)>100){
//					if(!oldNeighbor.contains(n)){
//						oldNeighbor.add(n);
//					}
//					continue;
//				}
                else {
                    if (nWeight < 0) {
                        nWeight = 0;
                    }
                    floorWeight.remove(n);
                    floorWeight.put(n, nWeight);
                    if (!oldNeighbor.contains(n)) {
                        oldNeighbor.add(n);
                    }

                }
            }
        }
//		System.out.println(rateChange);
    }


    public int getDistanceFromGoal(String v) {
        validateVertex(v);
        return Integer.parseInt(distanceFromGoal.get(v).toString());

    }

    public void setGoalLocation(String v) {
        this.goal = v;
    }

    public String getStart() {
        return start;
    }

    public String getGoal() {
        return goal;
    }

    public void setStartLocation(String v) {
        this.start = v;
    }

    /**
     * Return a string representation of the graph.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String v : st) {
            s.append(v + "  - (");
            s.append(points.get(v).toString() + " points)  - ");
            s.append(" (" + floorWeight.get(v).toString() + " floor weight)  - ");
            for (String w : st.get(v)) {
                s.append(w + "|");
            }
            s.append("\n");
        }
        return s.toString();
    }


    public static <E> ArrayList<E> toList(Iterable<E> iterable) {
        if (iterable instanceof ArrayList) {
            return (ArrayList<E>) iterable;
        }
        ArrayList<E> list = new ArrayList<E>();
        if (iterable != null) {
            for (E e : iterable) {
                list.add(e);
            }
        }
        return list;
    }

    // Converts the locations to x,y integer locations, but not useful for more than one floor
    public int[] ConvertGrid(int position) {
        int xpos = (position % 24) + 1;
        int ypos = position / 24 + 1;
        return new int[]{xpos, ypos};
    }


}






