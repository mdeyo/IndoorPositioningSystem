package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Matthew on 1/1/2015.
 */

public class SideView35 extends Activity {

        private ImageView map;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sideview35);

            Button Button0 = (Button) findViewById(R.id.button_floor0);
            Button Button1 = (Button) findViewById(R.id.button_floor1);
            Button Button2 = (Button) findViewById(R.id.button_floor2);
            Button Button3 = (Button) findViewById(R.id.button_floor3);
            Button Button4 = (Button) findViewById(R.id.button_floor4);
            Button Button5 = (Button) findViewById(R.id.button_floor5);

            Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
            Button0.setTypeface(font);
            Button1.setTypeface(font);
            Button2.setTypeface(font);
            Button3.setTypeface(font);
            Button4.setTypeface(font);
            Button5.setTypeface(font);

            Button0.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "0");
                    startActivity(nextScreen);
                }
            });
            Button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "1");
                    startActivity(nextScreen);
                }
            });
            Button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "2");
                    startActivity(nextScreen);
                }
            });
            Button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "3");
                    startActivity(nextScreen);
                }
            });
            Button4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "4");
                    startActivity(nextScreen);
                }
            });
            Button5.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent nextScreen = new Intent(getApplicationContext(), FloorPlans.class);
                    nextScreen.putExtra("building","35");
                    nextScreen.putExtra("floor", "5");
                    startActivity(nextScreen);
                }
            });

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_floors, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
//            if (id == R.id.action_floor0) {
//                map.setImageResource(R.drawable.floor0);
//                return true;
//            }
//            if (id == R.id.action_floor1) {
//                map.setImageResource(R.drawable.floor1);
//                return true;
//            }
//            if (id == R.id.action_floor2) {
//                map.setImageResource(R.drawable.floor2);
//                return true;
//            }

            return super.onOptionsItemSelected(item);
        }

}
