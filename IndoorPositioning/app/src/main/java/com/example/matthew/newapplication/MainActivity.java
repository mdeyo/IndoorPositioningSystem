package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private Button buildButton,runButton,wifiButton;
    ImageView mapWithPin;
    TextView title;
    private DrawingView drawView;
    Paint paint;
    List<Point> points = new ArrayList<Point>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);

        buildButton = (Button) findViewById(R.id.button_build_mode);
        runButton = (Button) findViewById(R.id.button_run_mode);
        mapWithPin = (ImageView) findViewById(R.id.imageViewMain);
        title = (TextView) findViewById(R.id.main_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        title.setTypeface(font);
        runButton.setTypeface(font);
        buildButton.setTypeface(font);
        final int clickedColor = Color.parseColor("#5CBDFF");


        drawView = (DrawingView)findViewById(R.id.drawing);




        buildButton.setOnTouchListener(new View.OnTouchListener() {
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

        runButton.setOnTouchListener(new View.OnTouchListener() {
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

        buildButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), ChooseBuilding.class);
                startActivity(nextScreen);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
        });

        mapWithPin.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
        });



        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d("request code:"+String.valueOf(requestCode),"result code:"+ String.valueOf(resultCode));
            Log.d("scan result: ",scanResult.getContents());
            // handle scan result

        }
        // else continue with any other code you need in the method

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

//    public void onDraw(Canvas canvas) {
//        Path path = new Path();
//        boolean first = true;
//        for(int i = 0; i < points.size(); i += 2){
//            Point point = points.get(i);
//            if(first){
//                first = false;
//                path.moveTo(point.x, point.y);
//            }
//
//            else if(i < points.size() - 1){
//                Point next = points.get(i + 1);
//                path.quadTo(point.x, point.y, next.x, next.y);
//            }
//            else{
//                path.lineTo(point.x, point.y);
//            }
//        }
//
//        canvas.drawPath(path, paint);
//    }



    class Point {
        float x, y;
        float dx, dy;

        @Override
        public String toString() {
            return x + ", " + y;
        }
    }

}
