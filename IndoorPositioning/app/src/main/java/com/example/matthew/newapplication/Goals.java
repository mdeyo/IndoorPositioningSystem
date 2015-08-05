package com.example.matthew.newapplication;

public class Goals implements Comparable<Goals> {

    //	int goalX;
//	int goalY;
    String goal;
    int point;

    public Goals(String goal, int point) {
//		this.goalX = goalX;
//		this.goalY = goalY;		
        this.goal = goal;
        this.point = point;
    }


    //	public int getGoalX() {
//		return goalX;
//	}
//
//
//	public int getGoalY() {
//		return goalY;
//	}
    public String getGoal() {
        return goal;
    }

    @Override
    public int compareTo(Goals otherGoal) {
        int point = this.point;
        int otherPoint = otherGoal.getPoint();

        if (point < otherPoint) {
            return -1;
        } else if (point > otherPoint) {
            return 1;
        } else {
            return 0;
        }

    }


    private int getPoint() {

        return point;
    }

}
