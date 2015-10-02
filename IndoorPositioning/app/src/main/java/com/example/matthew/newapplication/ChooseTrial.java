package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class ChooseTrial extends Activity {

    ListView trialList;
    ArrayAdapter<String> adapter;
    HashMap<String,String> trialGoals,trialStarts;
    TextView adaptableChoice, adaptiveChoice;
    boolean hasChosenMode = false;
    String navMode = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trial);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        adaptiveChoice = (TextView) findViewById(R.id.adaptiveButton);
        adaptableChoice = (TextView) findViewById(R.id.adaptableButton);

        adaptiveChoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                hasChosenMode = true;
                Assets.setMode("adaptive");
                navMode="adaptive";
                adaptiveChoice.setBackgroundResource(R.drawable.round_button_highlighted);
                adaptableChoice.setBackgroundResource(R.drawable.round_button_outline);
            }
        });

        adaptableChoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                hasChosenMode = true;
                Assets.setMode("adaptable");
                navMode="adaptable";
                adaptableChoice.setBackgroundResource(R.drawable.round_button_highlighted);
                adaptiveChoice.setBackgroundResource(R.drawable.round_button_outline);
            }
        });

        trialList = (ListView) findViewById(R.id.trialListView);

        // create the arrayAdapter that contains the BTDevices, and set it to the ListView
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        trialList.setAdapter(adapter);


        adapter.add("Alpha");
        adapter.add("Bravo");
        adapter.add("Charlie");
        adapter.add("Delta");
        adapter.add("Echo");
        adapter.add("Foxtrot");
        adapter.add("Golf");
        adapter.add("Hotel");
        adapter.add("India");
        adapter.add("Juliet");
        adapter.add("Kilo");
        adapter.add("Lima");
        adapter.add("Mike");
        adapter.add("November");

        trialGoals = new HashMap<>();
        trialGoals.put("Alpha","33:2:81");
        trialGoals.put("Bravo","33:2:81");
        trialGoals.put("Charlie","35:1:109");
        trialGoals.put("Delta","35:1:109");
        trialGoals.put("Echo","37:4:61");
        trialGoals.put("Foxtrot","37:4:61");
        trialGoals.put("Golf","35:0:115");
        trialGoals.put("Hotel","35:0:115");
        trialGoals.put("India","33:2:81");
        trialGoals.put("Juliet","33:2:81");
        trialGoals.put("Kilo","37:4:54");
        trialGoals.put("Lima","37:4:54");
        trialGoals.put("Mike","37:4:61");
        trialGoals.put("November","33:3:108");


        trialStarts = new HashMap<>();
        trialStarts.put("Alpha","37:2:106");
        trialStarts.put("Bravo","37:2:106");
        trialStarts.put("Charlie","33:3:108");
        trialStarts.put("Delta","33:3:108");
        trialStarts.put("Echo","35:0:102");
        trialStarts.put("Foxtrot","35:0:102");
        trialStarts.put("Golf","37:4:61");
        trialStarts.put("Hotel","37:4:61");
        trialStarts.put("India","37:2:91");
        trialStarts.put("Juliet","37:2:91");
        trialStarts.put("Kilo","35:1:109");
        trialStarts.put("Lima","35:1:109");
        trialStarts.put("Mike","anywhere");
        trialStarts.put("November","37:1:63");


        trialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                 //has the user selected a NavMode, continue if true
                                             }
                                         });

            trialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

            {
                @Override
                public boolean onItemLongClick (AdapterView < ? > adapterView, View view,int i,
                long l){
                //has the user selected a NavMode, continue if true
                if (hasChosenMode) {
                    String goal = trialGoals.get(adapter.getItem(i));
                    Assets.setGoal(goal);
                    String startLocation = trialStarts.get(adapter.getItem(i));

                    if (i == 0) {//chosen alpha
                        Assets.setTrialNumber("2R");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial2R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 1) {//chosen bravo
                        Assets.setTrialNumber("2R");
                        Assets.setUpdate("close35:2");
                        Assets.setGraph("Trial2R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 2) {//chosen charlie
                        Assets.setTrialNumber("5");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial5", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 3) {//chosen delta
                        Assets.setTrialNumber("5");
                        Assets.setUpdate("remove9");
                        Assets.setGraph("Trial5", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 4) {//chosen echo
                        Assets.setTrialNumber("8");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial8", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 5) {//chosen foxtrot
                        Assets.setTrialNumber("8");
                        Assets.setUpdate("remove9");
                        Assets.setGraph("Trial8", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 6) {//chosen golf
                        Assets.setTrialNumber("8R");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial8R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 7) {//chosen hotel
                        Assets.setTrialNumber("8R");
                        Assets.setUpdate("moveGoal,35:4:113"); //TODO runMode move goal to 35:4
                        Assets.setGraph("Trial8R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 8) {//chosen india
                        Assets.setTrialNumber("9R");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial9R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 9) {//chosen juliet
                        Assets.setTrialNumber("9R");
                        Assets.setUpdate("moveGoal,33:4:132"); //TODO runMode move goal to 33:4:132
                        Assets.setGraph("Trial9R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 10) {//chosen kilo
                        Assets.setTrialNumber("11");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial11", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 11) {//chosen lima
                        Assets.setTrialNumber("11");
                        Assets.setUpdate("close35:3"); //TODO need runMode method for this
                        Assets.setGraph("Trial11", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 12) {//chosen mike
                        Assets.setTrialNumber("0");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial1R", getResources().getAssets());
                        Assets.setMode("walk");
                    } else if (i == 13) {//chosen november
                        Assets.setTrialNumber("1R");
                        Assets.setUpdate("remove9");
                        Assets.setGraph("Trial1R", getResources().getAssets());
                        Assets.setMode(navMode);
                    } else if (i == 14) {//chosen oscar
                        Assets.setTrialNumber("1R");
                        Assets.setUpdate("none");
                        Assets.setGraph("Trial1R", getResources().getAssets());
                        Assets.setMode(navMode);

                    }

                    Toast.makeText(getApplicationContext(), "Goal at " + goal + ", start at " + startLocation, Toast.LENGTH_LONG).show();

                    if(Assets.currentRunMode!=null){
                        Assets.currentRunMode.finish();
                    }

                    Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                    startActivity(nextScreen);
                }
                return false;
            }
            }

            );
        }
}
