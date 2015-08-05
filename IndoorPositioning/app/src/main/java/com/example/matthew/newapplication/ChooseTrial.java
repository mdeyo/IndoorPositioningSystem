package com.example.matthew.newapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;


public class ChooseTrial extends Activity {

    ListView trialList;
    ArrayAdapter<String> adapter;
    HashMap<String,String> trialMap;
    TextView adaptableChoice, adaptiveChoice;
    boolean hasChosenMode = false;

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
                adaptiveChoice.setBackgroundResource(R.drawable.round_button_highlighted);
                adaptableChoice.setBackgroundResource(R.drawable.round_button_outline);
            }
        });

        adaptableChoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                hasChosenMode = true;
                Assets.setMode("adaptable");
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

        trialMap = new HashMap<>();
        trialMap.put("Alpha","37:1:75");
        trialMap.put("Bravo","33:3:108");
        trialMap.put("Charlie","37:4:61");
        trialMap.put("Delta","37:2:80");
        trialMap.put("Echo","37:4:61");
        trialMap.put("Foxtrot+","37:4:61");

        trialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //has the user selected a NavMode, continue if true
                if (hasChosenMode) {
                    String goal = trialMap.get(adapter.getItem(i));
                    Assets.setGoal(goal);

                    Toast.makeText(getApplicationContext(), "Goal at " + goal, Toast.LENGTH_SHORT).show();

                    Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                    startActivity(nextScreen);
                }
                return false;
            }
        });
    }

    public void setTrial(View view){
        if(view.getId()==R.id.trial1){
            Assets.setGoal("37:1:75");
            Toast.makeText(getApplicationContext(),"Goal on 37:1",Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.trial2){
            Assets.setGoal("33:3:108");
            Toast.makeText(getApplicationContext(),"Goal on 33:3",Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.trial3){
            Assets.setGoal("37:4:61");
            Toast.makeText(getApplicationContext(),"Goal on 37:4",Toast.LENGTH_SHORT).show();
        }

        Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
        startActivity(nextScreen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_trial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
