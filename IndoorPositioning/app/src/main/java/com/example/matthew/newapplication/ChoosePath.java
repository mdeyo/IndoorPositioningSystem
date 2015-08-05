package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChoosePath extends Activity {

    //    private Button option1,option2,option3;
    RelativeLayout button1, button2, button3;
//    private CustomButton b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_path);
//        b1 = (CustomButton) findViewById(R.id.custom1);
//        option1 = (Button) findViewById(R.id.b1);
//        option2 = (Button) findViewById(R.id.b2);
//        option3 = (Button) findViewById(R.id.b3);

        button1 = (RelativeLayout) findViewById(R.id.button_1);
        button2 = (RelativeLayout) findViewById(R.id.button_2);
        button3 = (RelativeLayout) findViewById(R.id.button_3);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.path_button_layout, null);
        TextView text = (TextView) v.findViewById(R.id.optionNumber);
        text.setText("Option number 2");

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), SideViewPreview.class);
                nextScreen.putExtra("path", "1");
                startActivityForResult(nextScreen, 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        button1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_blue_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_blue_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), SideViewPreview.class);
                nextScreen.putExtra("path", "2");
                startActivityForResult(nextScreen, 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        button2.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_blue_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_blue_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), SideViewPreview.class);
                nextScreen.putExtra("path", "3");
                startActivityForResult(nextScreen, 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        button3.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundResource(R.drawable.round_button_blue_highlighted);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setBackgroundResource(R.drawable.round_button_blue_outline);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

    }


    public void selfDestruct(View view) {
        view.setVisibility(View.GONE);
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
