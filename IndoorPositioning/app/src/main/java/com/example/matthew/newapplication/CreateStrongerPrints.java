package com.example.matthew.newapplication;

import java.util.ArrayList;

/**
 * Created by Matthew on 1/28/2015.
 */
public class CreateStrongerPrints {
    private int length;
    private ArrayList<GridData> dataList;
    private ArrayList<GridData> strongerDataList =new ArrayList<>();

    public CreateStrongerPrints(ArrayList<GridData> allData) {
        dataList=allData;
        length=allData.size();

        //re-initialize the count of every object to 1
        for(int i=0;i<length;i++){
            dataList.get(i).setCount(1);
        }

        for(int i=0;i<length;i++) {
            GridData currentObj = dataList.get(i);
            String fullLocation = currentObj.printFullLocation();

            if (strongerDataList.size() != 0) {
                boolean multiple=false;

                for (int n = 0; n < strongerDataList.size(); n++) {
                    GridData compare = strongerDataList.get(n);
                    int newCount = compare.getCount();
                    if (compare.printFullLocation().equals(fullLocation)) {
                        multiple = true;

                        ArrayList<RouterObject> compareRouterArray = compare.getRouterArray();
                        ArrayList<RouterObject> currentRouterArray = currentObj.getRouterArray();
                        int routersMatched = 0;

                        ArrayList<RouterObject> newAverages = new ArrayList<>();

                        for (int m = 0; m < currentRouterArray.size(); m++) {
                            if (!compare.getRouterIDs().contains(currentRouterArray.get(m).printBSSID())) {
                                //same position but different AP measured... not sure how to handle yet
                                //newAverages.add(currentRouterArray.get(m));
                                compareRouterArray.add(currentRouterArray.get(m));
                                //Log.d("check","not a repeat AP");

                            } else {
                                strengthCompare:
                                for (int p = 0; p < compareRouterArray.size(); p++) {
                                    if (compareRouterArray.get(p).printBSSID().equals(currentRouterArray.get(m).printBSSID())) {
                                        routersMatched += 1;
                                        int strengthFromNewList = currentRouterArray.get(m).getStrength();
                                        int strengthFromData = compareRouterArray.get(p).getStrength();

                                        //Log.d("count:",Integer.toString(newCount));

                                        int newAverageStrength = (((strengthFromData * newCount) + strengthFromNewList) / (newCount + 1));
                                        compareRouterArray.remove(p);
                                        compareRouterArray.add(new RouterObject(currentRouterArray.get(m).printBSSID(), newAverageStrength));
                                        //newAverages.add(new RouterObject(currentRouterArray.get(m).printBSSID(), newAverageStrength));
                                        //Log.d("new average strength:",Integer.toString(newAverageStrength));
                                        break strengthCompare;
                                    }
                                }
                            }
                        }

                        //Log.d("new averages size:",Integer.toString(compareRouterArray.size()));

                        RouterObject[] newAveragesList = new RouterObject[compareRouterArray.size()];

                        for (int b = 0; b < compareRouterArray.size(); b++) {
                            newAveragesList[b] = compareRouterArray.get(b);
                        }

                        int previousCount = compare.getCount();
                        strongerDataList.remove(compare);
                        GridData newAverageObj = new GridData(newAveragesList,compare.getPosition(),compare.getBuilding(),compare.getFloor(),compare.printID());
                        newAverageObj.setCount(previousCount+1);
                        strongerDataList.add(newAverageObj);
                        //compare.putNewRouters(newAveragesList);
                        //compare.setCount(compare.getCount() + 1);
                    }
                }
                if (!multiple) {
                    strongerDataList.add(currentObj);
                }

             }else {
                strongerDataList.add(currentObj);
            }

        }

    }

    public ArrayList<GridData> getStrongerDataList(){
        return strongerDataList;
    }


}
