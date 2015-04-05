import java.util.HashMap;
import java.util.Map;

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
 *  The <tt>Graph</tt> class represents an undirected graph of vertices
 *  with string names.
 *  It supports the following operations: add an edge, add a vertex,
 *  get all of the vertices, iterate over all of the neighbors adjacent
 *  to a vertex, is there a vertex, is there an edge between two vertices.
 *  Self-loops are permitted; parallel edges are discarded.
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/45graph">Section 4.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Graph {

    // symbol table: key = string vertex, value = set of neighboring vertices
    private ST<String, SET<String>> st;
    
    private Map points;

    // number of edges
    private int E;

   /**
     * Create an empty graph with no vertices or edges.
     */
    public Graph() {
        st = new ST<String, SET<String>>();
    }

   /**
     * Create an graph from given input stream using given delimiter.
     */
    public Graph(In in, String delimiter) {
        st = new ST<String, SET<String>>();
        points = new HashMap();
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] names = line.split(delimiter);
            if(!points.containsKey(names[0])){
            	points.put(names[0], names[1]);
            }
            for (int i = 2; i < names.length; i++) {
                addEdge(names[0], names[i]);
            }
        }
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
    private void validateVertex(String v) {
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
     * Return the set of neighbors of vertex v as in Iterable.
     */
    public Iterable<String> adjacentTo(String v) {
        validateVertex(v);
        return st.get(v);
    }

   /**
     * Is v a vertex in this graph?
     */
    public boolean hasVertex(String v) {
        return st.contains(v);
    }
    
    /**
     * Is v a vertex in this graph?
     */
    public String getFirstValue(String v) {
    	if (!hasVertex(v)) throw new IllegalArgumentException(v + " is not a vertex");
        return st.get(v).toString();
    }

   /**
     * Is v-w an edge in this graph?
     */
    public boolean hasEdge(String v, String w) {
        validateVertex(v);
        validateVertex(w);
        return st.get(v).contains(w);
    }

   /**
     * Return a string representation of the graph.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String v : st) {
            s.append(v + " :");
            s.append(points.get(v).toString()+": ");
            for (String w : st.get(v)) {
                s.append(w + "|");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        In in = new In("test");
        Graph G = new Graph(in,",");

        // print out graph
        System.out.println(G.toString());
//        StdOut.println(G);

        // print out graph again by iterating over vertices and edges
//        for (String v : G.vertices()) {
//            StdOut.print(v + " : ");
//            for (String w : G.adjacentTo(v)) {
//                StdOut.print(w + "|");
//            }
//            StdOut.println();
//        }

    }

}
