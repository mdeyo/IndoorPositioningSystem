package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ChoosePathColor extends Activity {

    private Button Button1, Button2, Button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_path_color);

        Button1 = (Button) findViewById(R.id.button_1);
        Button2 = (Button) findViewById(R.id.button_3);
        Button3 = (Button) findViewById(R.id.button_2);

//        runButton = (Button) findViewById(R.id.button_run_mode);
//        mapWithPin = (ImageView) findViewById(R.id.imageViewMain);
//        title = (TextView) findViewById(R.id.main_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
//        title.setTypeface(font);
        Button1.setTypeface(font);

        final int clickedColor = Color.parseColor("#5CBDFF");

        Button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                nextScreen.putExtra("path", "#66CCFF");
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                nextScreen.putExtra("path", "#FF00FF");
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        Button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                nextScreen.putExtra("path", "#660066");
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_path, menu);
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
