package com.example.matthew.newapplication;

import java.util.ArrayList;

public class ArrayToArrayList {
	
	// This is really just a method I didn't want in Main, since it was going to be compiled into an Android app and 
	// unfortunately nothing else has been initialized
	// There are probably several ways I could do this better, but...
	ArrayToArrayList(){
		
	}
	
	static ArrayList<String> getArrayList(String[] oldGoalArray, ArrayList<String> newGoalArray){
		
		for(String newOnes : oldGoalArray){
			newGoalArray.add(newOnes);
		}
		return newGoalArray;
	}

}
