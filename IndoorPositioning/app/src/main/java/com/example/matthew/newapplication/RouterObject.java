package com.example.matthew.newapplication;

/**
 * Created by Matthew on 1/6/2015.
 */
public class RouterObject implements Comparable<RouterObject> {

    private int strength;
    private String id;

    public RouterObject(String i,int s) {
        strength=s;
        id=i;

    }

    @Override
    public int compareTo (RouterObject another){
        return (another.strength - strength);
    }

    public String print(){
        return id+": "+ Integer.toString(strength);
    }

    public String printBSSID(){return id;
    }
    public int getStrength(){return strength;
    }

    public boolean checkMatch(String bssid){
        if(this.id.equals(bssid)){
            return true;
        }
        else{return false;}
    }


}
