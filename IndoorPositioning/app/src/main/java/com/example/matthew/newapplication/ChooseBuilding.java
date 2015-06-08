package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ChooseBuilding extends Activity {

    private Button Button33;
    private Button Button35;
    private Button Button37, wifiButton, newProjectButton;
    ListView projects;
    TextView BuildingTitle;
    String projectKey = "com.example.matthew.indoornav.projectNames";
    SharedPreferences prefs;
    ArrayList<String> projectNamesList = new ArrayList<String>();
    CustomProjectListAdapter adpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosebuilding);

        Button33 = (Button) findViewById(R.id.button33);
        Button35 = (Button) findViewById(R.id.button35);
        Button37 = (Button) findViewById(R.id.button37);
        wifiButton = (Button) findViewById(R.id.button_wifi_scanning);
        newProjectButton = (Button) findViewById(R.id.new_button);
        TextView BuildingTitle = (TextView) findViewById(R.id.building_title);
        projects = (ListView) findViewById(R.id.list_projects);
        String[] rawr = new String[]{"one", "two"};


        prefs = this.getBaseContext().getSharedPreferences("com.example.matthew.indoornav", Context.MODE_PRIVATE);
        projectNamesList.clear();

        projects.setVisibility(View.INVISIBLE);

        //prefs.edit().putString(projectKey,"rawr rawr").commit();
        updateFromPrefs();

        // Decorations //
        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        Button33.setTypeface(font);
        Button35.setTypeface(font);
        Button37.setTypeface(font);
        newProjectButton.setTypeface(font);
        BuildingTitle.setTypeface(font);
        wifiButton.setTypeface(font);
        wifiButton = (Button) findViewById(R.id.button_wifi_scanning);

        wifiButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });


        wifiButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), WifiScanning.class);
                startActivity(nextScreen);
                return true;
            }
        });

        newProjectButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), UploadImage.class);
                startActivity(nextScreen);
                return true;
            }
        });

        Button33.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        Button35.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        /*
        Button37.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(clickedColor, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
        */

        Button33.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), SideView33.class);
                startActivity(nextScreen);
            }
        });
        Button35.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), SideView35.class);
                startActivity(nextScreen);
            }
        });

        Button37.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), SideView37.class);
                startActivity(nextScreen);
            }
        });

    }

    @Override
    protected void onResume() {
        updateFromPrefs();

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void updateFromPrefs() {
        /////////////// projects //////////////////////
        projectNamesList.clear();
        String projectString = prefs.getString(projectKey, null);

        if (projectString != null) {
            String[] projectArray = projectString.split(" ");
            int length = projectArray.length;

            for (int i = 0; i < length; i++) {
                String name = projectArray[i];
                name = name.replaceAll("_", " ");
                if (!name.equals(" ") && !name.equals("") && !name.equals(null)) {
                    projectNamesList.add(name);
                }
            }
        }
        adpt = new CustomProjectListAdapter(this, R.layout.project_list_item, projectNamesList);
        projects.setAdapter(adpt);
        ////////////////////////////////////////////////////
    }
}
