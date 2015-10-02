package com.example.matthew.newapplication;

import java.util.ArrayList;

public class Path {
        // The waypoints in the path (list of coordiantes making up the path)
        private ArrayList<String> waypoints = new ArrayList<String>();
        
        public Path() {
        }
        
        public Path(Path path) {
        	this.waypoints.clear();
			this.waypoints = new ArrayList(path.getWaypoints());
		}

		public int getLength() {
                return waypoints.size();
        }

        public String getWayPoint(int index) {
                return waypoints.get(index);
        }


        public ArrayList<String> getWaypoints() {
			return waypoints;
		}

		/**
         * Append a waypoint to the path.  
         * 
         * @param x The x coordinate of the waypoint.
         * @param y The y coordinate of the waypoint.
         */
        public void appendWayPoint(String n) {
                waypoints.add(n);
        }

        /**
         * Add a waypoint to the beginning of the path.  
         * 
         * @param x The x coordinate of the waypoint.
         * @param y The y coordinate of the waypoint.
         */
        public void prependWayPoint(String n) {
                waypoints.add(0, n);
        }

        /**
         * Check if this path contains the WayPoint
         * 
         * @param x The x coordinate of the waypoint.
         * @param y The y coordinate of the waypoint.
         * @return True if the path contains the waypoint.
         */
        public boolean contains(String n) {
                for(String node : waypoints) {
                        if (n.equals(node) )
                                return true;
                }
                return false;
        }

}
