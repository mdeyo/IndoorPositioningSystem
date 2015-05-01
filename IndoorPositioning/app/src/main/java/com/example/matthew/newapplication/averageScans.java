package com.example.matthew.newapplication;

import java.util.ArrayList;

/**
 * Created by Matthew on 4/20/2015.
 */
public class averageScans {

    RouterObject[] scan1 = null;
    RouterObject[] scan2 = null;
    RouterObject[] scan3 = null;
    RouterObject[] result;
    ArrayList<RouterObject> resultArray = new ArrayList<>();

    public averageScans(RouterObject[] scan1, RouterObject[] scan2){
        this.scan1=scan1;
        this.scan2=scan2;
        int total = scan1.length+scan2.length;
        this.result = new RouterObject[total];
        //add all the RouterObjects from the first scan
        for(int i=0;i<this.scan1.length;i++){
            resultArray.add(scan1[i]);
        }
        //integrate in the second scan by checking for repeated IDs
        for(int i=0;i<this.scan2.length;i++){
            boolean repeat=false;
            for(int n=0;n<resultArray.size();n++){
                if (resultArray.get(n).printBSSID()==scan2[i].printBSSID()){
                    repeat=true;
                    int averageStrength = (resultArray.get(n).getStrength()+scan2[i].getStrength())/2;
                    String id = resultArray.get(n).printBSSID();
                    resultArray.remove(n);
                    resultArray.add(new RouterObject(id,averageStrength));
                    break;
                }
            }
            if(!repeat){
                resultArray.add(scan2[i]);
            }


        }

        //change result format into list
        result = resultArray.toArray(new RouterObject[resultArray.size()]);


    }
    public averageScans(RouterObject[] scan1, RouterObject[] scan2, RouterObject[] scan3){
        this.scan1=scan1;
        this.scan2=scan2;
        this.scan3=scan3;

    }

    public RouterObject[] calculate(){
        return result;
    }


}
