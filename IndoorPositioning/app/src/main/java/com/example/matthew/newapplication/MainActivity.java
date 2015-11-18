package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends Activity {
    private Button buildButton, runButton, questionButton, wifiButton, qButton, sideButton;
    ImageView mapWithPin;
    TextView title, loadingText;
    private DrawingView drawView;
    Paint paint;
    List<Point> points = new ArrayList<Point>();
    WifiManager mainWifiObj;
    ArrayList<GridData> dataList = new ArrayList<GridData>();
    ArrayList<GridData> strongerDataList = new ArrayList<GridData>();
    RelativeLayout menuLayout, loadingLayout;

    int loadingNumber = 1;
    String loadingString = "Loading";
    int waitTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (Assets.strongerDataList.size() > 0) {
                //Done saving server data in Assets
                loadingLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.VISIBLE);
                try {
                    this.finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (waitTime > 20) {
                //Error saving server data in Assets
                loadingLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Error loading data from Parse server", Toast.LENGTH_LONG).show();
                try {
                    this.finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                //still waiting for server data in Assets
                timerHandler.postDelayed(this, 500);
                waitTime++;
                if (loadingNumber == 1) {
                    loadingString = "Loading.   ";
                    loadingNumber = 2;
                } else if (loadingNumber == 2) {
                    loadingString = "Loading..  ";
                    loadingNumber = 3;
                } else if (loadingNumber == 3) {
                    loadingString = "Loading... ";
                    loadingNumber = 4;
                } else if (loadingNumber == 4) {
                    loadingString = "Loading....";
                    loadingNumber = 5;
                } else if (loadingNumber == 5) {
                    loadingNumber = 1;
                    loadingString = "Loading    ";
                }
                loadingText.setText(loadingString);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menuLayout = (RelativeLayout) findViewById(R.id.mainMenu);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        Assets assets = new Assets(getResources().getAssets(),this);

        buildButton = (Button) findViewById(R.id.button_build_mode);
        runButton = (Button) findViewById(R.id.button_run_mode);
        qButton = (Button) findViewById(R.id.qButton);
        sideButton = (Button) findViewById(R.id.sideButton);
        mapWithPin = (ImageView) findViewById(R.id.imageViewMain);
        title = (TextView) findViewById(R.id.main_title);
        loadingText = (TextView) findViewById(R.id.loadingText);
        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        title.setTypeface(font);
        runButton.setTypeface(font);
        buildButton.setTypeface(font);
        final int clickedColor = Color.parseColor("#5CBDFF");


        drawView = (DrawingView) findViewById(R.id.drawing);

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (mainWifiObj.isWifiEnabled()) {
            Parse.initialize(this, "9IFIo0LdgpyPESCd8eNaCrdiFjAM61Jz3B9EvbYo", "BGwRcE1fJtEwvpmuEn7n4WsL3P0HIqK242MdpEIu");
            PullFromServer();
        } else {
            Toast.makeText(this, "Need WiFi connection to work", Toast.LENGTH_SHORT).show();
        }

        timerHandler.postDelayed(timerRunnable, 0);

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
//                        Assets.playUpdateSound();
//                        Assets.playAlertSound();
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
                //TODO removed setup activity for faster testing
//                Intent nextScreen = new Intent(getApplicationContext(), ChooseNavModeAndGoal.class);
                Intent nextScreen = new Intent(getApplicationContext(), ChooseTrial.class);
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        qButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), Questionnaire.class);
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        sideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //runButton.setBackgroundResource(R.drawable.round_rect_grey_highlighted);
                Intent nextScreen = new Intent(getApplicationContext(), ChoosePath.class);
                startActivity(nextScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

//        qButton.setVisibility(View.GONE);
        sideButton.setVisibility(View.GONE);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d("request code:" + String.valueOf(requestCode), "result code:" + String.valueOf(resultCode));
            Log.d("scan result: ", scanResult.getContents());
            // handle scan result

        }
        // else continue with any other code you need in the method

    }

    //Called once at beginning to pull scan data from Parse
    public void PullFromServer() {
        menuLayout.setVisibility(View.GONE);
//        Thread timer = makeProgressThread();
//        timer.start();
//        progressLayout.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");
        query.setLimit(1000);
//        startServerTime = System.currentTimeMillis();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> newDataList, ParseException e) {
                ArrayList<GridData> result = new ArrayList<GridData>();
                if (e == null) {
                    int numberOfPoints = newDataList.size();

                    if (!newDataList.isEmpty()) {
                        //Toast.makeText(getApplicationContext(),"Waiting for server...",Toast.LENGTH_LONG);
                        for (int i = 0; i < numberOfPoints; i++) {

                            ParseObject obj = newDataList.get(i);
                            int pos = obj.getInt("position");
                            String routers = obj.getString("routers");
                            String build = obj.getString("buildingNumber");
                            String flo = obj.getString("floorNumber");
                            String full = build + ":" + flo + ":" + String.valueOf(pos);
//                            Log.d("from Parse",full);
                            String id = obj.getObjectId();

                            Pattern pattern1 = Pattern.compile(Pattern.quote("/"));
                            String[] splitRaw = pattern1.split(routers);

                            RouterObject[] routerResults = new RouterObject[splitRaw.length];

                            //Log.d("object", id);
                            for (int n = 0; n < splitRaw.length; n++) {
                                Pattern pattern2 = Pattern.compile(Pattern.quote("="));
                                String[] eachRouter = pattern2.split(splitRaw[n]);
                                int strength = Integer.parseInt(eachRouter[1]);
                                routerResults[n] = (new RouterObject(eachRouter[0], strength));
                            }

                            GridData item = new GridData(routerResults, pos, build, flo, id);
                            result.add(item);

//                            updateProgress(((double)i)/((double)(numberOfPoints-1)));
                        }

                        Log.d("Parse", "got " + String.valueOf(numberOfPoints) + " points");

                        //print statement used to see if loading bar time is sufficient
//                        Log.d("Server Time", String.valueOf(System.currentTimeMillis() - startServerTime) + " milliseconds");
//                        Log.d("Parse Server", "pulled " + numberOfPoints + " objects from server");
                        dataList = result;
//                        Log.d("building", dataList.get(0).printFullLocation().substring(0, 2));
//                        for(int i=0;i<dataList.size();i++){
//                            Log.d(dataList.get(i).printFullLocation(),dataList.get(i).printRouters());
//                        }
                        CreateStrongerPrints upgrade = new CreateStrongerPrints(dataList);
                        strongerDataList = upgrade.getStrongerDataList();
                        dataList.clear();

                        Assets.saveData(strongerDataList);
//                        updateProgress(1);

//                        Log.d("Stronger fingerprints", "compiled into " + Integer.toString(strongerDataList.size()) + " prints");
//                        for(int i=0;i<strongerDataList.size();i++){
//                            Log.d(strongerDataList.get(i).printFullLocation(),strongerDataList.get(i).printRouters());
//                        }
//                        progressLayout.setVisibility(View.GONE);
                    }
                } else {
//                    showCustomAlert("Error connecting to server :(");
                    Toast.makeText(getApplicationContext(), "Error connecting to server :(", Toast.LENGTH_SHORT);
                }
            }
        });
//        Thread timer = makeProgressThread();
//        timer.start();
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
