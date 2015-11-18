package com.example.matthew.newapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunMode extends Activity implements SensorEventListener {

    boolean alarmOn = false;
    boolean trialHasTime = true;
    long rejectTime=0;
    long rejectTimeInterval = 12000; //time interval 12 seconds

    //Scenario updates definitions ********************

    //Remove9-point QR update
    String updateString = Assets.updateString;
    int UPDATE_REMOVE9_TIME = 70*1000;
    boolean isNineGone = false;
    boolean isComplete = false;
    //2 - 70
    //5 - 70
    //8 - 40
    //8R -80 change goal
    //9R - 120 change goal
    //11 - 80
//    int UPDATE_2R_TIME=70*1000;
//    int UPDATE_5_TIME=70*1000;
//    int UPDATE_8_TIME=40*1000;
//    int UPDATE_8R_TIME=80*1000;
//    int UPDATE_9R_TIME=120*1000;
//    int UPDATE_11_TIME=80*1000;

    int UPDATE_1_TIME=60*1000;
    int UPDATE_2_TIME=30*1000;
    int UPDATE_3_TIME=20*1000;
    int UPDATE_4_TIME=60*1000;
    int UPDATE_5_TIME=40*1000;
    int UPDATE_6_TIME=35*1000;
    int UPDATE_7_TIME=25*1000;
    int UPDATE_8_TIME=30*1000;

    ArrayList<String> goalList = new ArrayList<String>();
    ArrayList<String> goalListAdp1 = new ArrayList<String>();
    ArrayList<String> goalListAdp2 = new ArrayList<String>();

    int lengthOfGoalList = 0;

    //Close off floor 35:2 update
    int UPDATE_CLOSED_TIME = 70*1000;
    boolean updateOccured = false;
    ArrayList<String> floor35_2;
    int floor2_35 = R.drawable.floor2_35;
    //Close off floor 35:3 update
    ArrayList<String> floor35_3;
    int floor3_35 = R.drawable.floor3_35;

    ArrayList<String> floor37_2;
    int floor2_37 = R.drawable.floor2_37;

    ArrayList<String> floor37_3;
    int floor3_37 = R.drawable.floor3_37;

    ArrayList<String> floor37_4;
    int floor4_37 = R.drawable.floor4_37;

    ArrayList<String> floor37_5;
    int floor5_37 = R.drawable.floor5_37;

    //move trial goal update
    int UPDATE_MOVEGOAL_TIME = 80*1000;

    //*************************************************

    //Absolute time and mainThread
    boolean firstQRscan = true;
    String timeFromStart;
    long trialStartTime = 0, appStartTime = 0, startDelay = 0, delayedQRTime = 0, delayedUpdateTime = 0;
    boolean adaptableUpdateChosen = false;
    final long TOTAL_TIME = 180 * 1000;
    final long TOTAL_WALKTHROUGH_TIME = 20 * 60 * 1000; //20 minutes
    final long EXCESS_TIME = 60 * 1000;
    final long REPEAT_TIME = 1800;
    final long CLOCK_TIME = 200;
    int timeLeft;

    Handler timerHandler = new Handler();
    Runnable scanTimerRunnable = new Runnable() {
        //        int seconds, minutes;
//        long millis;
        @Override
        public void run() {
            Log.d("heartbeat","scanTimer");

            buildSidePath();

            if (currentRunMode == CurrentRunMode.SCANNING) {
                //Log.d("heartbeat","scanTimer:scanning");
                showQRCodes();

                if (scanOn) {
                    unregisterReceiver(wifiReciever);
                    scanOn = false;
                }
                if (!scanOn) {
                    registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                }
//
                mainWifiObj.reconnect();
                mainWifiObj.startScan();
                startScanTime = System.currentTimeMillis();
                scanOn = true;

                timerHandler = new Handler();
                timerHandler.removeCallbacks(this);
                timerHandler.post(this);

//                timerHandler.postDelayed(this, REPEAT_TIME);
//                timerHandler2.postDelayed(this,REPEAT_TIME);

//                this.run();

                //TODO
//                    if (scanOn) {
//                        unregisterReceiver(wifiReciever);
//                        mainWifiObj.disconnect();
//                        scanOn = false;
//                    }
            }

//            timerHandler = new Handler();
//            timerHandler.removeCallbacks(this);
//            timerHandler.post(this);
        }
    };

    boolean countdownOn = false;
    Handler timerHandler2 = new Handler();
    Runnable timerRunnable = new Runnable() {
        int seconds,minutes;
        long millis;

        @Override
        public void run() {
//            Log.d("heartbeat","timer");
            if (countdownOn) {
//                Log.d("updateString value",updateString);
                millis = System.currentTimeMillis() - trialStartTime - delayedQRTime - delayedUpdateTime;
                if(updateString.equals("update")) {
                    if (trialNumber.equals("1")) {
                        if (millis > UPDATE_1_TIME && !updateOccured) {
                            closeFloor375();
                        }
                    } else if (trialNumber.equals("2")) {
                        if (millis > UPDATE_2_TIME && !updateOccured) {
                            closeFloor353();
                        }
                    } else if (trialNumber.equals("3")) {
                        if (millis > UPDATE_3_TIME && !updateOccured) {
                            closeFloor352();
                        }
                    } else if (trialNumber.equals("4")) {
                        if (millis > UPDATE_4_TIME && !updateOccured) {
                            closeFloor352();
                        }
                    } else if (trialNumber.equals("5")) {
                        if (millis > UPDATE_5_TIME && !updateOccured) {
                            closeFloor374();
                        }
                    } else if (trialNumber.equals("6")) {
                        if (millis > UPDATE_6_TIME && !updateOccured) {
                            closeFloor372();
                        }
                    } else if (trialNumber.equals("7")) {
                        if (millis > UPDATE_7_TIME && !updateOccured) {
                            closeFloor373();
                        }
                    } else if (trialNumber.equals("8")) {
                        if (millis > UPDATE_8_TIME && !updateOccured) {
                            closeFloor373();
                        }
                    }
                }
//                if(updateString.equals("close35:2")) {
//                    if (millis > UPDATE_2R_TIME && !updateOccured) {
//                        closeFloor352();
//                    }
//                }
//                if(updateString.equals("close35:3")) {
//                    if (millis > UPDATE_11_TIME && !updateOccured) {
//                        closeFloor353();
//                    }
//                }
//                if(updateString.equals("remove9")) {
//                    if (trialNumber.equals("5") && millis > UPDATE_5_TIME && !updateOccured) {
//                        remove9();
//                    }
//                    if (trialNumber.equals("8") && millis > UPDATE_8_TIME && !updateOccured) {
//                        remove9();
//                    }
//                }
//                if(updateString.startsWith("moveGoal")){
//                    if (trialNumber.equals("8R") && millis > UPDATE_8R_TIME && !updateOccured) {
//                        moveGoal();
//                    }
//                    if (trialNumber.equals("9R") && millis > UPDATE_9R_TIME && !updateOccured) {
//                        moveGoal();
//                    }
//                }
                if(!navMode.equals("walk")) {
                    if (millis < TOTAL_TIME) {
                        millis = TOTAL_TIME - millis;
                        seconds = (int) (millis / 1000);
                        timeLeft = seconds;
                        minutes = seconds / 60;
                        seconds = seconds % 60;
                        timeFromStart = String.format("%d:%02d", minutes, seconds);
//                    Log.d("heartbeat","timer:less than total "+timeFromStart);
                        timeRemainingText.setText("Time Remaining: " + timeFromStart);
//                    timerHandler2.postDelayed(this, CLOCK_TIME);
                    } else if (millis < (TOTAL_TIME + EXCESS_TIME)) {
                        millis = Math.abs(TOTAL_TIME - millis);
                        seconds = (int) (millis / 1000);
                        minutes = seconds / 60;
                        seconds = seconds % 60;
                        timeFromStart = String.format("%d:%02d", minutes, seconds);
//                    Log.d("heartbeat","timer:out of time "+timeFromStart);
                        timeRemainingText.setTextColor(Color.RED);
                        timeRemainingText.setText("Time Remaining: -" + timeFromStart);
                        alertBarOn = 1;
                        alertBarText.setVisibility(View.VISIBLE);
                        alertBarTextBelow.setVisibility(View.VISIBLE);
                        alertBarText.setText("Time's Up!");
                        alertBarTextBelow.setText("Hurry to the finish location");
                        trialHasTime = false;
//                    timerHandler2.postDelayed(this, CLOCK_TIME);
                    } else {
//                timeRemainingText.setText("Time's Up!");
                        Log.d("heartbeat", "timer:finished");
                        onlyShowLayout(finishedLayout);

//                alertBarText.setText("Time's up!");

                        try {
                            this.finalize();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    if (millis < TOTAL_WALKTHROUGH_TIME) {
                        millis = TOTAL_WALKTHROUGH_TIME - millis;
                        seconds = (int) (millis / 1000);
                        timeLeft = seconds;
                        minutes = seconds / 60;
                        seconds = seconds % 60;
                        timeFromStart = String.format("%d:%02d", minutes, seconds);
//                    Log.d("heartbeat","timer:less than total "+timeFromStart);
                        timeRemainingText.setText("Time Remaining: " + timeFromStart);
//                    timerHandler2.postDelayed(this, CLOCK_TIME);
                    } else if (millis < (TOTAL_WALKTHROUGH_TIME + EXCESS_TIME)) {
                        millis = Math.abs(TOTAL_WALKTHROUGH_TIME - millis);
                        seconds = (int) (millis / 1000);
                        minutes = seconds / 60;
                        seconds = seconds % 60;
                        timeFromStart = String.format("%d:%02d", minutes, seconds);
//                    Log.d("heartbeat","timer:out of time "+timeFromStart);
                        timeRemainingText.setTextColor(Color.RED);
                        timeRemainingText.setText("Time Remaining: -" + timeFromStart);
                        alertBarOn = 2;
                        alertBarText.setVisibility(View.VISIBLE);
                        alertBarTextBelow.setVisibility(View.VISIBLE);
                        alertBarText.setText("Time's Up!");
                        alertBarTextBelow.setText("Hurry to the finish location");
//                    timerHandler2.postDelayed(this, CLOCK_TIME);
                    } else {
//                timeRemainingText.setText("Time's Up!");
                        Log.d("heartbeat", "timer:finished");
                        onlyShowLayout(finishedLayout);

//                alertBarText.setText("Time's up!");

                        try {
                            this.finalize();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
                timerHandler2.postDelayed(this, CLOCK_TIME);

            }
        }
    };

    enum CurrentRunMode {
        INITIAL, CHOOSE, SCANNING
    }

    CurrentRunMode currentRunMode = CurrentRunMode.INITIAL;

    //Fingerprint data from Assets
    ArrayList<GridData> strongerDataList = new ArrayList<GridData>();

    //Top View map
    ImageView map,closedFloor;// compassBeatImage;
    String buildingnumber, floornumber;
    private SensorManager sensorManager;
    private boolean color = false;
    private TextView textAccel, headingText, QRScan;
    private long lastUpdate;
    int[] mapsize = new int[240];
    private DrawingView drawView;
    ArrayList<String> pathHistory = new ArrayList<>();
    List<Point> localPathHistory = new ArrayList<>();
    ArrayList<String> futurePath = new ArrayList<>();
    List<Point> localFuturePath = new ArrayList<>();

    //TODO
    ///////////////////////////////////////////////
//    String goal = "35:3:105";
//    String goal = "37:2:106";
    String goal;
    String navMode;
    ArrayList<String> pointsScanned = new ArrayList<>();
    ///////////////////////////////////////////////

    //WiFi scan management
    private int scanCount = 0;
    boolean scanOn;
    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    List<ScanResult> wifiScanList;
    String[] wifiText;
    TextView scanTitle, localPoints, maxAccel;
    ListView scanlist;
    LinearLayout localDataLayout;
    boolean viewingLocalData;
    boolean localDataVisible;
    ListView datalistView;
    RouterObject wifis[];
    int currentPosition = 0;
    int numberOfRoutersSaved, numberOfRoutersUsed;
    RouterObject[] mostRecentScan = null;
    RouterObject[] olderScan = null;
    RouterObject[] averageOfRecentScans = null;
    long startScanTime, timeForScan, startAlgTime = 0l;
    GridView gridView;
    CustomGridAdapter gridAdapter;
    int[] numbers = new int[240];
    int previousPosition = 0;
    String previousBuilding = "0";
    String previousFloor = "0";

    public enum scanFrequencyMode {
        AVERAGE_TWO, USE_EACH_ONE
    }

    scanFrequencyMode scanMode;

    public enum fingerprintMatchingMode {
        INCLUDE_PREVIOUS, EXCLUDE_PREVIOUS
    }

    fingerprintMatchingMode matchingMode;

    //Progress bar objects
    int TIMER_RUNTIME = 4000; // in ms --> 6seconds
    boolean mbActive;
    ProgressBar mProgressBar;
    RelativeLayout progressLayout, topViewLayout;
    TextView progressBarText, locationText;
    ImageView truckImage, cloudImage, buildImage, wallsViewRight;

    //Accelerometer measumements
    int ACCELEROMETER_WAIT_TIME;
    float recentChangesAccelerometer = 0;
    float previousChangesAccelerometer = 0;
    float changesAccelerometer = 0;
    boolean accelerationZ;

    //sideViewToggle
    private RelativeLayout sideViewLayout, sideViewToggle;
    private ImageView sideViewToggleImage,sideViewImage;
    private TextView sideViewToggleText;
    private boolean sideViewOn = false;
    int currentFloorImage = R.drawable.side_view_clear_0_3;

    //SideViewLayout
    boolean firstSideView = true;
    private DrawingView drawView2;
    List<Point> pathL = new ArrayList<Point>();
    List<Point> pathLA1 = new ArrayList<Point>();
    List<Point> pathLA2 = new ArrayList<Point>();
    Bitmap bitmap;
    private String start, finish, predictedPoints;
    private ArrayList<String> sidePath, sidePath2, sidePath3;
    private TextView points;
    ArrayList<String> connections = Assets.connections;

    public enum sidePathMode {
        STATIC, DYNAMIC
    }

    sidePathMode sidePathUpdates;

    //AlertBar Layout
    private ImageView alertBarToggle;
    private int alertBarOn = 0;
    private TextView alertBarText, alertBarTextBelow;
    private RelativeLayout alertToggleLayout;

    //loaded data from Assets
    private Graph G, G_HP, G_L, G_orig;
    Map<String, QRCodeLocation> QRmap = new HashMap<String, QRCodeLocation>();
    String trialNumber;
    HighPointPriority goalHigh,goalHigh_previous;
    FloorRankingOrder goalLogic, goalLogic_previous;
    Algo pathLogic,pathFinder,pathHigh,pathLogicAdp1,pathLogicAdp2;
    //TODO variations of Logic navigation
    FloorRankingOrder goalLogic10, goalLogic10_previous;
    FloorRankingOrder goalLogic200, goalLogic200_previous;

    //Information Display
    LinearLayout informationDisplay;
    TextView pointsCollectedText,pointsRemainingText, qrCodesRemainingText, timeRemainingText;

    //Logging performance
    FileWriter writer;
    File logFile = null;
    String fullPosition = null;

    //Popup Dialogue
//    RelativeLayout popupLayout;
//    TextView popupText1,popupText2,popupText3;

    //Finished Layout
    RelativeLayout finishedLayout;
    TextView finishedButton,finishedText;

    //Visualizing QR Codes
    ArrayList<String> QRCodeLocations = new ArrayList<>();
    List<QRLocationXY> localQRCodeLocations = new ArrayList<>();
    List<QRLocationXY> sideViewQRCodeLocations = new ArrayList<>();
    //Handling QR Points accumulated
    int pointsCollected = 0;
    int totalPointsOnRoute = 0;
    int QRCodesRemaining = 0;

    //Adaptable choices menu
    RelativeLayout adaptableChoices, adaptableChoicesButtons;
    RelativeLayout adaptableChoice1, adaptableChoice2, adaptableChoice3;
    TextView choice1Points,choice1Time,choice2Points,choice2Time,choice3Points,choice3Time;
    TextView adaptableToggle, adaptableChoose;
    int adaptableSelected = 0;
    boolean firstRouteChoice = true;

    //Adaptive Choice View
    RelativeLayout adaptiveChoiceLayout;
    TextView adaptivePoints,adaptiveTime;

    //custom toast display
    Toast toast1;
    boolean toastActivated = false;
    Toast toast;

//    AppendLog updater;
    GridData newestGrid;

    BroadcastReceiver listenForPower;

    int baseWeight = 10;
    int stairWeight = 15;
    int breakTime = 100;
    int baseWeightAdp1 = 10;
    int stairWeightAdp1 = 15;
    int breakTimeAdp1 = 140;
    int baseWeightAdp2 = 10;
    int stairWeightAdp2 = 20;
    int breakTimeAdp2 = 100;
    int floorMod = baseWeight-10;
    int stairMod = stairWeight-10;
    int floorModAdp1 = baseWeightAdp1-10;
    int stairModAdp1 = stairWeightAdp1-10;
    int floorModAdp2 = baseWeightAdp2-10;
    int stairModAdp2 = stairWeightAdp2-10;
    int allowedTime = 180;
    int allowedTimeAdp1 = 180;
    int allowedTimeAdp2 = 180;

    int tStair = 0;
    int tQR = 0;
    int tFloor = 0;
    int window = 0;
    int tStairAdp1 = 0;
    int tQRAdp1 = 0;
    int tFloorAdp1 = 0;
    int windowAdp1 = 0;
    int tStairAdp2 = 0;
    int tQRAdp2 = 0;
    int tFloorAdp2 = 0;
    int windowAdp2 = 0;

    boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectAll()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());



        //Handling QR Points accumulated
        pointsCollected = 0;
        totalPointsOnRoute = 0;
        QRCodesRemaining = 0;
        firstQRscan = true;

        gridView = (GridView) findViewById(R.id.mapGrid);
        map = (ImageView) findViewById(R.id.wallsView);
        closedFloor = (ImageView) findViewById(R.id.closedFloors);
        //TODO initialize as gone
        closedFloor.setVisibility(View.GONE);
        wallsViewRight = (ImageView) findViewById(R.id.wallsViewRight);
        wallsViewRight.setVisibility(View.GONE);
        drawView = (DrawingView) findViewById(R.id.drawing);

        sidePath = new ArrayList<String>();
        sidePath2 = new ArrayList<String>();
        sidePath3 = new ArrayList<String>();


        scanOn = false;
        viewingLocalData = false;

        String pathColor = "#66CCFF";
        drawView.setColor(pathColor);

        if (savedInstanceState == null) {
            //nothing
        } else {
            goal = (String) savedInstanceState.getSerializable("goal");
            floornumber = (String) savedInstanceState.getSerializable("floor");
            buildingnumber = (String) savedInstanceState.getSerializable("building");
        }

        if(Assets.goal!=null) {
            goal = Assets.goal;
        }

        trialNumber = Assets.trialNumber;

        goal = Assets.goal;
        navMode = Assets.mode;
        Log.d("mode",navMode);
        scanMode = Assets.scanMode;
        matchingMode = Assets.matchingMode;
        sidePathUpdates = Assets.sideMode;

        Assets.setRunModeActivity(this);

        floor35_2 = Assets.floor35_2;
        floor35_3 = Assets.floor35_3;
        floor37_2 = Assets.floor37_2;
        floor37_3 = Assets.floor37_3;
        floor37_4 = Assets.floor37_4;
        floor37_5 = Assets.floor37_5;

        //initialize large data from Assets
        QRmap = Assets.QRMap;
        int baseWeight = 10;
        int stairWeight = 15;
        G = Assets.G;
        QRCodeLocations = Assets.QRLocations;

        if(trialNumber.equals("1")) {
            // Trial 1
            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 20;
            stairWeightAdp1 = 15;
            breakTimeAdp1 = 50;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 4;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 10;
            stairWeightAdp2 = 15;
            breakTimeAdp2 = 20;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 15;
            tQRAdp2 = 3;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList0 = {"37:2:91", "37:3:93", "37:4:61", "37:5:74", "35:2:116", "35:4:113"};
            String[] gList1 = {"37:1:161", "37:2:91", "37:3:93", "37:4:61", "37:5:74", "37:3:50", "35:2:116", "35:1:116", "35:4:113"};
            String[] gList2 = {"37:2:91", "37:3:93", "37:4:61", "37:5:74", "35:2:116", "35:4:113"};
            goalList = ArrayToArrayList.getArrayList(gList0,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList1,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList2,goalListAdp2);
        }

        else if(trialNumber.equals("2")) {
            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 10;
            stairWeightAdp1 = 15;
            breakTimeAdp1 = 20;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 10;
            stairWeightAdp2 = 50;
            breakTimeAdp2 = 20;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 10;
            tQRAdp2 = 4;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList3 = {"37:5:68", "37:3:93", "37:4:65", "37:4:54", "35:3:116", "35:3:105", "37:3:50", "37:5:74", "37:2:106", "37:1:63", "35:0:115", "35:1:109", "35:2:132"};
            String[] gList4 = {"37:5:68", "37:3:93", "37:4:65", "37:4:54", "35:3:116", "35:3:105", "37:3:50", "37:5:74", "37:2:106", "37:1:63", "35:0:115", "35:1:109", "35:2:132"};
            String[] gList5 = {"37:5:68", "37:3:93", "37:4:65", "37:4:54", "37:5:74", "37:3:50", "35:3:116", "35:3:105", "35:2:132"};             baseWeight = 10;
            goalList = ArrayToArrayList.getArrayList(gList3,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList4,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList5,goalListAdp2);
        }

        else if(trialNumber.equals("3")) {
            //New definition of trial 3
            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3; //average
            window = 120;
            baseWeightAdp1 = 20;
            stairWeightAdp1 = 50;
            breakTimeAdp1 = 50;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2; //fassstt
            windowAdp1 = 120;
            baseWeightAdp2 = 10;
            stairWeightAdp2 = 15;
            breakTimeAdp2 = 20;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 10;
            tQRAdp2 = 3;
            tFloorAdp2 =2; //faassst
            windowAdp2 = 120;
//          String goal = "33:4:132";
            String[] gList9 =  {"35:2:98", "35:3:105", "35:3:132", "37:2:77", "37:4:54"};
            String[] gList10 = {"35:0:115", "37:2:77", "35:2:116", "35:4:113", "37:4:54"};
            String[] gList11 = {"35:2:98", "35:3:105", "35:3:132", "37:2:77", "37:4:54"};
            goalList = ArrayToArrayList.getArrayList(gList9,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList10,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList11,goalListAdp2);
        }
        else if(trialNumber.equals("4")) {
            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 20;
            stairWeightAdp1 = 30;
            breakTimeAdp1 = 20;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 20;
            stairWeightAdp2 = 50;
            breakTimeAdp2 = 50;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 15;
            tQRAdp2 = 3;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList6 = {"37:2:91", "37:2:77", "35:3:116", "37:5:74", "35:2:116", "35:0:115", "35:0:133"};
            String[] gList7 = {"37:2:91", "37:2:77", "35:3:116", "35:2:116", "35:0:115", "35:0:133"};
            String[] gList8 = {"35:3:116", "35:2:116", "37:2:77", "37:5:74", "35:4:113", "35:0:115", "35:0:133"};
            goalList = ArrayToArrayList.getArrayList(gList6,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList7,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList8,goalListAdp2);
        }
//        else if(trialNumber.equals("8R")) {
//            // Trial 8 Maybe too long
//            breakTime = 80;
//            allowedTime = 180;
//            baseWeightAdp1 = 20;
//            stairWeightAdp1 = 15;
//            breakTimeAdp1 = 50;
//            allowedTimeAdp1 = 180;
//            baseWeightAdp2 = 20;
//            stairWeightAdp2 = 40;
//            breakTimeAdp2 = 80;
//            allowedTimeAdp2 = 210;
//            String[] gList3 = {"35:2:98", "35:3:132", "35:4:181", "33:4:132", "33:3:123", "33:2:99", "33:2:165", "33:3:140", "37:3:50", "35:2:116", "37:2:106", "37:3:93", "37:4:65", "37:4:61"};
//            String[] gList4 = {"35:2:98", "35:3:132", "33:3:140", "35:4:181", "33:2:165", "35:2:116", "37:3:50", "37:2:106", "37:4:54", "37:4:61"};
//            String[] gList5 = {"35:2:98", "33:2:165", "33:3:140", "35:3:132", "35:4:181", "33:4:132", "33:3:123", "33:2:99", "35:2:116", "37:3:50", "37:2:106", "37:3:93", "37:5:68", "37:4:65", "37:4:61"};
//            goalList = ArrayToArrayList.getArrayList(gList3,goalList);
//            goalListAdp1 = ArrayToArrayList.getArrayList(gList4,goalListAdp1);
//            goalListAdp2 = ArrayToArrayList.getArrayList(gList5,goalListAdp2);
//        }
        else if(trialNumber.equals("5")) {
            // Trial 5
            start = "35:3:105";
            goal = "37:2:106";

            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 10;
            stairWeightAdp1 = 15;
            breakTimeAdp1 = 20;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 10;
            stairWeightAdp2 = 30;
            breakTimeAdp2 = 50;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 15;
            tQRAdp2 = 3;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList12 = {"35:3:116", "37:4:54", "37:4:65", "37:5:68", "37:3:61", "37:2:77", "37:2:106"};
            String[] gList13 = {"35:3:116", "37:4:54", "37:4:65", "37:5:68", "37:3:61", "37:2:77", "37:2:106"};
            String[] gList14 = {"35:3:116", "37:4:54", "37:4:65", "37:5:68", "37:3:61", "37:2:77", "37:2:106"};
            goalList = ArrayToArrayList.getArrayList(gList12,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList13,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList14,goalListAdp2);
        }
        else if(trialNumber.equals("6")) {
            // Trial 6
            start = "35:1:109";
            goal = "37:4:61";

            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 10;
            stairWeightAdp1 = 15;
            breakTimeAdp1 = 20;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 20;
            stairWeightAdp2 = 30;
            breakTimeAdp2 = 20;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 10;
            tQRAdp2 = 4;
            tFloorAdp2 = 3;
            windowAdp2 = 120;
            String[] gList15 = {"35:2:116", "37:2:77", "35:0:115", "37:2:106", "35:4:113", "37:4:54", "37:4:61"};
            String[] gList16 = {"35:2:116", "37:2:77", "35:0:115", "37:2:106", "35:4:113", "37:4:54", "37:4:61"};
            String[] gList17 = {"35:2:116", "37:2:77", "37:2:106", "35:0:115", "35:0:102", "35:2:98", "35:3:105", "35:4:113", "37:4:54", "37:4:61"};
            goalList = ArrayToArrayList.getArrayList(gList15,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList16,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList17,goalListAdp2);
        }
        else if(trialNumber.equals("7")) {
            // Trial 7
            start = "37:5:59";
            goal = "37:2:106";

            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 10;
            stairWeightAdp1 = 30;
            breakTimeAdp1 = 50;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 10;
            tQRAdp1 = 3;
            tFloorAdp1 = 2;
            windowAdp1 = 150;
            baseWeightAdp2 = 10;
            stairWeightAdp2 = 15;
            breakTimeAdp2 = 20;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 10;
            tQRAdp2 = 4;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList18 = {"37:3:93", "37:3:61", "35:3:116", "35:2:132", "35:1:116", "37:2:77", "37:2:106"};
            String[] gList19 = {"37:4:65", "37:3:93", "37:3:61", "35:3:116", "35:2:132", "37:2:77", "37:2:106"};
            String[] gList20 = {"37:3:93", "37:3:61", "35:3:116", "35:2:132", "35:1:116", "37:2:77", "37:2:106"};
            goalList = ArrayToArrayList.getArrayList(gList18,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList19,goalListAdp1);
            goalListAdp2 =ArrayToArrayList.getArrayList(gList20,goalListAdp2);
            }
        else if(trialNumber.equals("8")) {
            // Trial 8
            goal = "35:2:116";
            start = "37:1:63";

            baseWeight = 10;
            stairWeight = 15;
            breakTime = 20;
            allowedTime = 180;
            tStair = 10;
            tQR = 4;
            tFloor = 3;
            window = 120;
            baseWeightAdp1 = 10;
            stairWeightAdp1 = 15;
            breakTimeAdp1 = 20;
            allowedTimeAdp1 = 180;
            tStairAdp1 = 15;
            tQRAdp1 = 4;
            tFloorAdp1 = 2;
            windowAdp1 = 120;
            baseWeightAdp2 = 20;
            stairWeightAdp2 = 30;
            breakTimeAdp2 = 50;
            allowedTimeAdp2 = 180;
            tStairAdp2 = 10;
            tQRAdp2 = 3;
            tFloorAdp2 = 2;
            windowAdp2 = 120;
            String[] gList21 = {"37:2:91", "37:3:93", "37:5:68", "37:4:54", "37:2:77", "35:2:116"};
            String[] gList22 = {"37:2:91", "37:3:93", "37:5:68", "37:4:54", "37:2:77", "35:2:116"};
            String[] gList23 = {"37:1:75", "37:2:91", "37:3:93", "37:5:68", "37:4:54", "37:3:50", "37:2:77", "35:2:116"};
            goalList = ArrayToArrayList.getArrayList(gList21,goalList);
            goalListAdp1 = ArrayToArrayList.getArrayList(gList22,goalListAdp1);
            goalListAdp2 = ArrayToArrayList.getArrayList(gList23,goalListAdp2);
        }

        floorMod = baseWeight-10;
        stairMod = stairWeight-10;
        floorModAdp1 = baseWeightAdp1 - 10;
        stairModAdp1 = stairWeightAdp1 - 10;
        floorModAdp2 = baseWeightAdp2 - 10;
        stairModAdp2 = stairWeightAdp2 - 10;

//        predictedPoints = Assets.predictedPoints;
        SideViewPreview.setup();

        sideViewToggleImage = (ImageView) findViewById(R.id.sideViewToggleImage);
        sideViewImage = (ImageView) findViewById(R.id.sideView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.side_view_clear_0_3);
        sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, 100));
        sideViewToggle = (RelativeLayout) findViewById(R.id.sideViewToggle);
        sideViewLayout = (RelativeLayout) findViewById(R.id.sideView_layout);
        alertToggleLayout = (RelativeLayout) findViewById(R.id.alertBarToggleLayout);
        topViewLayout = (RelativeLayout) findViewById(R.id.topView_layout);
        finishedLayout = (RelativeLayout) findViewById(R.id.finishedLayout);
        finishedButton = (TextView) findViewById(R.id.finishedButton);
        finishedText = (TextView) findViewById(R.id.finishedText);

        finishedLayout.setVisibility(View.GONE);
        alertToggleLayout.setVisibility(View.GONE);

        sideViewLayout.setVisibility(View.GONE);
        sideViewToggleText = (TextView) findViewById(R.id.sideViewToggleText);
        sideViewToggle.setVisibility(View.GONE);

        adaptiveChoiceLayout = (RelativeLayout) findViewById(R.id.adaptiveChoiceLayout);
        adaptivePoints = (TextView) findViewById(R.id.adaptivePoints);
        adaptiveTime = (TextView) findViewById(R.id.adaptiveTime);

        adaptableChoices = (RelativeLayout) findViewById(R.id.adaptableChoicesLayout);
        adaptableChoicesButtons = (RelativeLayout) findViewById(R.id.adaptableChoicesButtons);
        adaptableChoices.setVisibility(View.GONE);
        adaptableChoicesButtons.setVisibility(View.GONE);
        adaptableToggle = (TextView) findViewById(R.id.adaptableToggle);
        adaptableChoice1 = (RelativeLayout) findViewById(R.id.button_1);
        adaptableChoice2 = (RelativeLayout) findViewById(R.id.button_2);
        adaptableChoice3 = (RelativeLayout) findViewById(R.id.button_3);
        adaptableChoose = (TextView) findViewById(R.id.adaptableChoose);
        choice1Points = (TextView) findViewById(R.id.button1points);
        choice1Time = (TextView) findViewById(R.id.button1time);
        choice2Points = (TextView) findViewById(R.id.button2points);
        choice2Time = (TextView) findViewById(R.id.button2time);
        choice3Points = (TextView) findViewById(R.id.button3points);
        choice3Time = (TextView) findViewById(R.id.button3time);

        adaptableChoices.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
//                buildSidePath();
            }
        });

        adaptableToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                adaptableChoicesToggle();
            }
        });

        adaptableChoice1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resetAdaptableButtons();
                appendLog("NavMode", "mode1", true);
                if (adaptableSelected == 1) {
                    adaptableSelected = 0;
                    adaptableChoice1.setBackgroundResource(R.drawable.round_button_green);
                } else {
                    adaptableSelected = 1;
                    adaptableChoice1.setBackgroundResource(R.drawable.round_button_green_outlined);
                }

            }
        });

        adaptableChoice2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resetAdaptableButtons();
                appendLog("NavMode", "mode2", true);
                if (adaptableSelected == 2) {
                    adaptableSelected = 0;
                    adaptableChoice2.setBackgroundResource(R.drawable.round_button_purple);
                } else {
                    adaptableSelected = 2;
                    adaptableChoice2.setBackgroundResource(R.drawable.round_button_purple_outlined);
                }
            }
        });

        adaptableChoice3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resetAdaptableButtons();
                appendLog("NavMode", "mode3", true);
                if (adaptableSelected == 3) {
                    adaptableSelected = 0;
                    adaptableChoice3.setBackgroundResource(R.drawable.round_button_blue);
                } else {
                    adaptableSelected = 3;
                    adaptableChoice3.setBackgroundResource(R.drawable.round_button_blue_outlined);
                }
            }
        });

        adaptableChoose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(adaptableSelected!=0) {
                    alarmOn =false;
                    adaptableChoiceView(false);
                }
            }
        });

        ////////////  Alert Bar
        alertBarToggle = (ImageView) findViewById(R.id.alertBarToggle);
        alertBarText = (TextView) findViewById(R.id.alertBarToggleText);
        alertBarTextBelow = (TextView) findViewById(R.id.alertBarTextBelow);
        alertBarText.setText("Alert Bar");
        if(navMode.equals("adaptive")) {
            alertBarText.setText("Computer-selected Mode");
            alertBarTextBelow.setText("No current alerts");
        }else if(navMode.equals("adaptable")) {
            alertBarText.setText("User-Choice Mode");
            alertBarTextBelow.setText("Choose route");
        }else if(navMode.equals("walk")) {
            alertBarText.setText("Walkthrough Mode");
            alertBarTextBelow.setText("No current alerts");
        }
        alertBarText.setVisibility(View.GONE);
        alertBarTextBelow.setVisibility(View.GONE);
        alertBarTextBelow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(navMode.equals("adaptable")) {
                    adaptableChoiceView(true);
                }
            }
        });

        informationDisplay = (LinearLayout) findViewById(R.id.informationDisplay);
        informationDisplay.setVisibility(View.GONE);
        timeRemainingText = (TextView) findViewById(R.id.timeRemainingText);
        pointsCollectedText = (TextView) findViewById(R.id.pointsCollectedText);
//        pointsRemainingText = (TextView) findViewById(R.id.pointsRemainingText);
        qrCodesRemainingText = (TextView) findViewById(R.id.qrCodesRemainingText);

//        localDataLayout = (LinearLayout) findViewById(R.id.local_data_layout);
//        localDataLayout.setVisibility(View.GONE);
//        localDataVisible = true;

        ACCELEROMETER_WAIT_TIME = 1000;
        textAccel = (TextView) findViewById(R.id.textView);
        textAccel.setBackgroundColor(Color.GREEN);
        textAccel.setVisibility(View.GONE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        locationText = (TextView) findViewById(R.id.location_text);
        scanlist = (ListView) findViewById(R.id.listView1);
        scanTitle = (TextView) findViewById(R.id.scanTextView);
        datalistView = (ListView) findViewById(R.id.data_listView);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mainWifiObj.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY,"Tag");
        wifiReciever = new WifiScanReceiver();

//        listenForPower = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.d("intent","pressed");
//
//                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
//                    Log.d("intent","screen off pressed");
////                    sendBroadcast(new Intent(Intent.ACTION_SCREEN_ON));
//                    startService(new Intent(Intent.ACTION_SCREEN_ON));
//
//                }
//            }
//        };
//        IntentFilter filter1 = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter1.addAction(Intent.ACTION_SCREEN_OFF);
//        this.registerReceiver(listenForPower, filter1);

        //progress bar class creation
        progressLayout = (RelativeLayout) findViewById(R.id.progressBar_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarText = (TextView) findViewById(R.id.progressBar_text);
        truckImage = (ImageView) findViewById(R.id.truck_image);
        buildImage = (ImageView) findViewById(R.id.build_image);
        cloudImage = (ImageView) findViewById(R.id.parse_image);

        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        progressBarText.setTypeface(font);
        sideViewToggleText.setTypeface(font);

        drawView2 = (DrawingView) findViewById(R.id.drawingView1);
        drawView2.setColor(pathColor);
        points = (TextView) findViewById(R.id.points);
        points.setVisibility(View.GONE);
//        button= (Button) findViewById(R.id.button);

        numberOfRoutersSaved = 9;
        numberOfRoutersUsed = 9;

        QRScan = (TextView) findViewById(R.id.qr_scan_button);
        QRScan.setTypeface(font);

        strongerDataList = Assets.strongerDataList;

        //initialize floornumber and buildingnumber
        buildingnumber = "0";
        floornumber = "0";

        lastUpdate = System.currentTimeMillis();

        QRScan.setOnClickListener(new View.OnClickListener() {


            public void onClick(View arg0) {
                AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
                adb.setCancelable(true);
                adb.setTitle("QR code scanner");
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                adb.setPositiveButton("Scan!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!firstQRscan) {
//                                setupLog();}
                            appendLog("QR", "pressed", true);
                        }
//                            appendLog("QR","pressed",true);
                        IntentIntegrator integrator = new IntentIntegrator(RunMode.this);
                        startDelay = System.currentTimeMillis();
                        integrator.initiateScan();
                    }
                });
                adb.show();
            }
        });

        finishedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), Questionnaire.class);
                startActivity(nextScreen);
            }
        });

        sideViewToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                toggleView();
            }
        });

        alertBarToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                updateAlertBar();
            }
        });

//        if (!mainWifiObj.isWifiEnabled()) {
//            //prompt user to turn wifi on - app won't work
//        }
//            final GestureDetector gestureDetector;
//            gestureDetector = new GestureDetector(new MyGestureDetector());

//            CoverButton.setOnTouchListener(new View.OnTouchListener() {
//
//                public boolean onTouch(View v, MotionEvent event) {
//                    hand.setVisibility(View.VISIBLE);
//                    if (gestureDetector.onTouchEvent(event)) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//            });
//            SmallCoverButton.setOnTouchListener(new View.OnTouchListener() {
//
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (gestureDetector.onTouchEvent(event)) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//            });
//            viewDataButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View arg0) {
//                    viewLocalData();
//                    if (localDataVisible) {
//                        unregisterReceiver(wifiReciever);
//                        scanOn = false;
//                        localDataLayout.setVisibility(View.VISIBLE);
//                        localDataVisible = false;
//                    } else if (!localDataVisible) {
//                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                        mainWifiObj.startScan();
//                        scanOn = true;
//                        localDataLayout.setVisibility(View.GONE);
//                        localDataVisible = true;
//                        hideLocalData();
//                    }
//
//                }
//            });
//        datalistView.setOnItemLongClickListener(
//                new AdapterView.OnItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(AdapterView<?> a, View v, final int position, long id) {
//
//                        final int selectedPosition = position;
//                        AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
//                        adb.setCancelable(false);
//
//                        final GridData obj = (GridData) datalistView.getItemAtPosition(position);
//
//                        String title = obj.printPosition();
//                        String routers = obj.printRouters();
//                        final String ID = obj.printID();
//
//                        adb.setTitle("Box:" + title);
//                        adb.setMessage(routers);
//
//                        adb.setPositiveButton("Cancel", null);
//                        adb.setNegativeButton("Remove this data from server", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                if (ID.equals("0")) {
//                                    Toast.makeText(getApplicationContext(), "This piece of data isn't on the server yet", Toast.LENGTH_SHORT).show();
//                                    return;
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Deleting from online server now...", Toast.LENGTH_SHORT).show();
//                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");
//                                    query.getInBackground(obj.printID(), new GetCallback<ParseObject>() {
//                                        public void done(ParseObject object, ParseException e) {
//                                            if (e == null) {
//                                                object.deleteInBackground(new DeleteCallback() {
//                                                    public void done(ParseException e) {
//                                                        if (e == null) {
//                                                            Toast.makeText(getApplicationContext(), "Removed from server!", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            Toast.makeText(getApplicationContext(), "Failed to remove :/", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//                                                });
//                                            } else {
//                                                Toast.makeText(getApplicationContext(), "Couldn't find it on the server", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                }
//
//                                dataList.remove(obj);
//
//                            }
//                        });
//
//                        adb.show();
//                        return true;
//
//                    }
//                });

        //            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                public void onItemClick(AdapterView<?> parent, View v,
//                                        final int position, long id) {
//                    String full = buildingnumber+":"+floornumber+":"+String.valueOf(position);
//                    appendLog(full,true);
//                    locationText.setText("added: "+String.valueOf(full));

//                }
//            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
//                SensorManager.SENSOR_DELAY_NORMAL);

        // for the system's orientation sensor registered listeners
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_GAME);

//        if(!scanOn) {
//            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//            mainWifiObj.startScan();
//            scanOn=true;
//            showCustomAlert("Scanning On");
//        }else if(scanOn){
//            mainWifiObj.reconnect();
//            mainWifiObj.startScan();
//        }

        if (!mainWifiObj.isWifiEnabled()) {
            showCustomAlert("Ah! you need the wifis!");
//            Toast.makeText(getApplicationContext(),"Ah! you need the wifis!",Toast.LENGTH_LONG).show();

        }
        //mainWifiObj.startScan();


    }
//
//    @Override
//    protected void onStart() {
//        if(initialized){
//            initialized=false;
//        }else{
//            recreate();
//        }
//    }

    @Override
    protected void onPause() {

        timerHandler.removeCallbacks(scanTimerRunnable);
        timerHandler2.removeCallbacks(timerRunnable);
        // unregister listener
        //unregisterReceiver(wifiReciever);
        super.onPause();
        if (toastActivated) {
            toast.cancel();
        }

        sensorManager.unregisterListener(this);
//        unregisterReceiver(listenForPower);

//        unregisterReceiver(wifiReciever);
        if (scanOn) {
            unregisterReceiver(wifiReciever);
            //mainWifiObj.reconnect();
//            unregisterReceiver(wifiReciever);
            scanOn = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (currentRunMode == CurrentRunMode.SCANNING) {
                getAccelerometer(event);
            }
        }

        //lost capabilities without gyroscope on the cell phone
//        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//            getAccelerometer(event);
//        }
//        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
//            updateGravityVector(event);
//        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // accelerations measured
        float x = values[0];
        float y = values[1];
        float z = values[2];

//        appendAccelerationsLog(x, y, z);

        float accelerations = (float) Math.sqrt(x * x + y * y + z * z);

//        Log.d("accelerations",String.valueOf(accelerations));

//        float xInGravity = x * gravityVector[0];
//        float yInGravity = y * gravityVector[1];
//        float zInGravity = z * gravityVector[2];
//        float accelerationInGravity = (float) Math.sqrt(xInGravity * xInGravity + yInGravity * yInGravity + zInGravity + zInGravity);

//        textAccel.setText("movement: "+String.valueOf(accelerations));
        float calibrationAmount = 9.913f;
        textAccel.setText(String.valueOf(accelerations-calibrationAmount));


        if (Math.abs(accelerations) > 16) {

            //exclude previous location on next fingerprint match
            matchingMode=fingerprintMatchingMode.EXCLUDE_PREVIOUS;

            if (color) {
                textAccel.setBackgroundColor(Color.GREEN);
            } else {
                textAccel.setBackgroundColor(Color.RED);
            }
            color = !color;

            //TODO Sept 11th - work on frozen scanning
//            timerHandler.removeCallbacks(scanTimerRunnable);
//            scanTimerRunnable.run();
//            timerHandler.postDelayed(scanTimerRunnable, 0);
//            timerHandler2.postDelayed(scanTimerRunnable,0);
//            timerHandler2.postDelayed(timerRunnable,0);

        }

//        float linearAccelerationSquared = (x * x + y * y);
        //long actualTime = event.timestamp;
        long actualTime = System.currentTimeMillis();

        if (accelerations > changesAccelerometer) {
            changesAccelerometer = accelerations;
        }

//        if ((z * z) > 7) {
//            accelerationZ = true;
//        }

        if (changesAccelerometer >= 12) {
            if (actualTime - lastUpdate > ACCELEROMETER_WAIT_TIME) {

                lastUpdate = System.currentTimeMillis();
                recentChangesAccelerometer = changesAccelerometer;
                //Log.d("linearA",String.valueOf(recentChangesAccelerometer));
//                localPoints.setText("local: 9");
//                textAccel.setText(String.valueOf(recentChangesAccelerometer));
                //recentChangesAccelerometer=0;

                if (changesAccelerometer > 16) {
//                    localPoints.setText("local: 24");
                }
                //Toast.makeText(getApplicationContext(), "Guessing position...", Toast.LENGTH_SHORT).show();
//                startScanTime = System.currentTimeMillis();
                //Log.d("wifi listener","register! ***********************************************************************");

                //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                mainWifiObj.startScan();
                //Log.d("wifi listener","scanning...");

            }
            changesAccelerometer = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Method for handling result of QR Code scan
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {

            Log.d("onResult","start");

            //if not first QR code scan, add delayed time for trial timer to account for it
            if(!firstQRscan){
                delayedQRTime += (System.currentTimeMillis()-startDelay);
            }

            // handle scan result, contents = "33:4:100" text format
            String id = scanResult.getContents();

            //if the scanned QR code text matches a known QR code
            if (QRmap.containsKey(id)) {

                //user scans the goal QR code!!
                if(id.equals(goal)){
                    isComplete = true;
                    onlyShowLayout(finishedLayout);
                    finishedText.setText("Trial Complete!");
                }

                //re-initialize image mapping to all be empty
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = -1;
                }

                lengthOfGoalList --;

                //work with saved QRCodeLocation Object from txt file
                QRCodeLocation obj = QRmap.get(id);
                int QRPosition = obj.getPosition();
                numbers[QRPosition] = 0;
                updateGrid(numbers);
                buildingnumber = obj.getBuilding();
                floornumber = obj.getFloor();
                currentPosition = QRPosition;
                fullPosition = obj.getFullPosition();

                Log.d("onResult","contains key");

                if (firstQRscan) {
                    pointsScanned.clear();
                    setupLog();
                    firstQRscan = false;
                    appendLog("Location", scanResult.getContents(), true);
                    start = scanResult.getContents();

                    Log.d("onResult",navMode);

                    runNavigationAlg();

                    if (navMode.equals("adaptive")) {
                        currentRunMode = CurrentRunMode.CHOOSE;
                        adaptableChoiceView(true);
                    }
                    else if (navMode.equals("adaptable")) {
                        currentRunMode = CurrentRunMode.CHOOSE;
                        adaptableChoiceView(true);
                    }
                    else if (navMode.equals("walk")) {
                        Log.d("onResult","walk - here");
                        adaptableChoiceView(false);
                        currentRunMode = CurrentRunMode.SCANNING;
                        updateMapView();
                        showLocalQRCodes();
                    }


                    //app time starts regardless of mode - used to measure time spent choosing route
                    appStartTime = System.currentTimeMillis();
                    timerHandler.postDelayed(scanTimerRunnable, 0);
                    timerHandler2.postDelayed(timerRunnable, 0);

                } else {
                    appendLog("QR", "result", true);
                    timerHandler.postDelayed(scanTimerRunnable, 0);
                    timerHandler2.postDelayed(timerRunnable, 0);
                    QRCodesRemaining--;
                }

                //add points to number user has collected
                addPointsToCollected(obj.getPoints());
                pointsScanned.add(fullPosition);
                removePoints(fullPosition);
                newLocation(fullPosition);
            }
        }

//        timerHandler.postDelayed(scanTimerRunnable, );

    }

    //Adaptable mode choices
    public void adaptableChoicesToggle() {

        if(navMode.equals("walk")){
            //do nothing
        }

        else if(navMode.equals("adaptive")){
            if(adaptiveChoiceLayout.isShown()){
                adaptiveChoiceLayout.setVisibility(View.GONE);
                adaptableToggle.setText("Begin");
                buildSidePath();
            }else {
                adaptableChoiceView(false);
            }
        }

        else {

            //TODO
            buildSidePath();

            if (adaptableChoicesButtons.isShown()) {
                adaptableChoicesButtons.setVisibility(View.GONE);
                adaptableToggle.setText("Route Options");
            } else {
                adaptableChoicesButtons.setVisibility(View.VISIBLE);
                adaptableToggle.setText("Preview Routes");
            }
        }
    }

    public void resetAdaptableButtons() {
        adaptableChoice1.setBackgroundResource(R.drawable.round_button_green);
        adaptableChoice2.setBackgroundResource(R.drawable.round_button_purple);
        adaptableChoice3.setBackgroundResource(R.drawable.round_button_blue);
    }

    public void adaptableChoiceView(boolean b) {
        alertBarOn = 1;
        updateAlertBar();
        if (b) {

            currentRunMode = CurrentRunMode.CHOOSE;

            adaptableChoices.setVisibility(View.VISIBLE);
            adaptableChoicesButtons.setVisibility(View.VISIBLE);
            adaptiveChoiceLayout.setVisibility(View.GONE);

            //TODO testing new updateThreeAlgos method
//            runNavigationAlg();
            updateThreeAlgos();

            if(firstRouteChoice){
//                timerHandler.postDelayed(scanTimerRunnable, 0);
                resetAdaptableButtons();
            }


            if(navMode.equals("adaptive")){
                adaptableChoicesButtons.setVisibility(View.GONE);
                adaptiveChoiceLayout.setVisibility(View.VISIBLE);
                adaptableToggle.setText("View Route");
            }

            if (adaptableSelected == 1) {
                adaptableChoice1.setBackgroundResource(R.drawable.round_button_green_outlined);
            } else if (adaptableSelected == 2) {
                adaptableChoice2.setBackgroundResource(R.drawable.round_button_purple_outlined);
            } else if (adaptableSelected == 3) {
                adaptableChoice3.setBackgroundResource(R.drawable.round_button_blue_outlined);
            }


//            alertToggleLayout.setVisibility(View.GONE);
            topViewLayout.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.VISIBLE);
//                    bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
//                    sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewToggle.setVisibility(View.GONE);
            sideViewOn = true;
            QRScan.setClickable(false);
            //TODO commented out 8/19
//            buildSidePath();
        }
        else {

            if (firstRouteChoice) {
                alertToggleLayout.setVisibility(View.GONE);
                firstRouteChoice = false;
                trialStartTime = System.currentTimeMillis();
                appendLog("Event","Start",true);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.side_view_clear_0_3);
                sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, 100));
            }

            if(updateOccured && !adaptableUpdateChosen){
                adaptableUpdateChosen=true;
                delayedUpdateTime = System.currentTimeMillis() - startDelay;
            }


            if(adaptableSelected==1 || navMode.equals("adaptive")) {
                showOptimalPath(sidePath);
                totalPointsOnRoute = pathLogic.getCollectedPoints();
                QRCodesRemaining = pathLogic.getQRCollected();
            }else if(adaptableSelected==2){
                showOptimalPath(sidePath2);
                totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp1.getQRCollected();
            }else if(adaptableSelected==3){
                showOptimalPath(sidePath3);
                totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp2.getQRCollected();
            }

            rejectTime = System.currentTimeMillis();

            // needs to be turned on after first route choice and forced updated route choice
            countdownOn = true;

            currentRunMode = CurrentRunMode.SCANNING;

            //TODO
//            buildSidePath();

            informationDisplay.setVisibility(View.VISIBLE);
            sideViewToggle.setVisibility(View.VISIBLE);
            alertToggleLayout.setVisibility(View.VISIBLE);
            adaptableChoices.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.GONE);
            sideViewOn = false;
            appendLog("Layout", "topView", true);

            topViewLayout.setVisibility(View.VISIBLE);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
//            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewToggle.setVisibility(View.VISIBLE);
            QRScan.setClickable(true);


            //TODO - Sept 11th - need these lines to start trial
            timerHandler.postDelayed(scanTimerRunnable, 0);
            timerHandler2.postDelayed(timerRunnable, CLOCK_TIME);

        }

    }

    //Called each time the device completes WiFi scan
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            wifiScanList = mainWifiObj.getScanResults();

            Log.d("wifi","onrecieved");

//            Toast.makeText(getApplicationContext(),"wifi",Toast.LENGTH_SHORT).show();

            timeForScan = System.currentTimeMillis() - startScanTime;
//            Log.d("Scan Time", String.valueOf(timeForScan) + " milliseconds *********************8");
//            Log.d("Scan", String.valueOf(wifiScanList.size()) + " routers found");

//            startScanTime = System.currentTimeMillis();
//            mainWifiObj.startScan();

            startAlgTime = System.currentTimeMillis();
//            Log.d("number of routers", String.valueOf(wifiScanList.size()));

            numberOfRoutersSaved = wifiScanList.size();
            wifis = new RouterObject[numberOfRoutersSaved];
            String id;
            int level, strength;
            for (int i = 0; i < numberOfRoutersSaved; i++) {
                level = wifiScanList.get(i).level;
                //String name = wifiScanList.get(i).SSID;
                id = wifiScanList.get(i).BSSID;
                //int strength = WifiManager.calculateSignalLevel(level, 100);
                strength = level;
                RouterObject router = new RouterObject(id, strength);
                wifis[i] = router;
            }

            Arrays.sort(wifis);
            mostRecentScan = wifis;

            wifiText = new String[numberOfRoutersSaved];
            for (int i = 0; i < numberOfRoutersSaved; i++) {
                wifiText[i] = wifis[i].print();
            }

            if (wifis != null || wifis.length != 0) {
                //dataList.add(new GridData(wifis, currentPosition, buildingnumber, floornumber, "0"));
            } else {
                showCustomAlert("Routers not found :(");
//                Toast.makeText(getApplicationContext(), "Routers not found", Toast.LENGTH_SHORT).show();
            }

//            for (int i = 0; i < dataList.size(); i++) {
//                dataStringList.add(dataList.get(i).print());
//            }

            scanlist.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.router_list_item, wifiText));
            scanTitle.setText("Current data");

            matchingAlgorithm();
            //unregisterReceiver(wifiReciever);
            //Log.d("wifi listener","unregister");
        }
    }

    //Fingerprint matching algorithm - called each time after WiFi scan processed
    public void matchingAlgorithm() {
        if (mostRecentScan != null) {
            if (scanMode == scanFrequencyMode.AVERAGE_TWO) {
                //takes two scans from receiver and averages them before guessing
                scanCount++;
                if (scanCount < 2) {
                    olderScan = mostRecentScan;
                } else if (scanCount == 2) {
//                    Log.d("scan mode", "double");
                    scanCount = 0;
                    averageScans average = new averageScans(olderScan, mostRecentScan);
                    averageOfRecentScans = average.calculate();
                    FingerprintMatchingAlg Alg = new FingerprintMatchingAlg(currentPosition, G, recentChangesAccelerometer, previousChangesAccelerometer, floornumber, buildingnumber, strongerDataList, averageOfRecentScans, matchingMode);

                    //reset matchingMode to include previous location next time
                    matchingMode=fingerprintMatchingMode.INCLUDE_PREVIOUS;

                    //Accelerations over time need to be updated
//                    accelerationZ = false;
//                    previousChangesAccelerometer = recentChangesAccelerometer;
//                    recentChangesAccelerometer = 0;
//                textAccel.setText(String.valueOf(recentChangesAccelerometer));

                    GridData algResult = Alg.getResult();

                    for (int i = 0; i < numbers.length; i++) {
                        numbers[i] = -1;
                    }

                    if (algResult != null) {
                        newestGrid = algResult;
//                        Thread updater = updateLocation();
//                        updater.run();


                        //re-initialize image mapping to all be empty
                        for (int i = 0; i < numbers.length; i++) {
                            numbers[i] = -1;
                        }

                        numbers[algResult.getPosition()] = 0;
                        updateGrid(numbers);
                        buildingnumber = algResult.getBuilding();
                        floornumber = algResult.getFloor();
                        currentPosition = algResult.getPosition();
                        newLocation(algResult.printFullLocation());

                        if (buildingnumber.equals("35") && previousBuilding.equals("33")) {
//                        animate35over33();
                            updateMapView();

                            previousPosition = currentPosition;
                            previousBuilding = buildingnumber;
                            previousFloor = floornumber;
                        } else {
                            updateMapView();
                            previousPosition = currentPosition;
                            previousBuilding = buildingnumber;
                            previousFloor = floornumber;
                        }

                    } else {
                        locationText.setText("Missing Graph Data");
//                    Log.d("check", "alg didn't give result");
                        if (buildingnumber.equals("0")) {
//                        showCustomAlert("We can't find your position :(");
                            //Toast.makeText(getApplicationContext(),"We can't find your position :(",Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                toast1.getView().isShown();
                                toast1.setText("No result");
                            } catch (Exception e) {
//                            toast1 = Toast.makeText(getApplicationContext(), "No result", Toast.LENGTH_SHORT);
                            }
//                        toast1.show();
                        }

                    }
                }
            } else if (scanMode == scanFrequencyMode.USE_EACH_ONE) {
//                Log.d("scan mode", "single");

                FingerprintMatchingAlg Alg = new FingerprintMatchingAlg(currentPosition, G, recentChangesAccelerometer, previousChangesAccelerometer, floornumber, buildingnumber, strongerDataList, mostRecentScan, matchingMode);

                //reset matchingMode to include previous location next time
                matchingMode=fingerprintMatchingMode.INCLUDE_PREVIOUS;

                //Accelerations over time need to be updated
                accelerationZ = false;
                previousChangesAccelerometer = recentChangesAccelerometer;
                recentChangesAccelerometer = 0;
//                textAccel.setText(String.valueOf(recentChangesAccelerometer));

                GridData algResult = Alg.getResult();

                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = -1;
                }

                if (algResult != null) {

                    //re-initialize image mapping to all be empty
                    for (int i = 0; i < numbers.length; i++) {
                        numbers[i] = -1;
                    }

                    numbers[algResult.getPosition()] = 0;
                    updateGrid(numbers);
                    buildingnumber = algResult.getBuilding();
                    floornumber = algResult.getFloor();
                    currentPosition = algResult.getPosition();
                    newLocation(algResult.printFullLocation());

//                    previousPosition = currentPosition;
//                    previousBuilding = buildingnumber;
//                    previousFloor = floornumber;
//                    if (buildingnumber.equals("35") && previousBuilding.equals("33")) {
////                        animate35over33();
//                        updateMapView();
//                        previousPosition = currentPosition;
//                        previousBuilding = buildingnumber;
//                        previousFloor = floornumber;
//                    } else {
//                        updateMapView();
//                        previousPosition = currentPosition;
//                        previousBuilding = buildingnumber;
//                        previousFloor = floornumber;
//                    }
                } else {
                    locationText.setText("Missing Graph Data");
//                    if (buildingnumber.equals("0")) {
//                        //Toast.makeText(getApplicationContext(),"We can't find your position :(",Toast.LENGTH_LONG).show();
//                    } else {
//
//                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Issue with scanMode assignment", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Need to scan location first", Toast.LENGTH_SHORT).show();

        }
    }

    public void newLocation(String s) {
        final String full = s;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//        if(s.equals(goal)){
            //goal reached, not sure what to do now -> don't run nav alg
//            fullPosition = full;
            //update the path history by adding this position if not same as previous
//            addToPathHistory(fullPosition);
//            showPathHistory();
//            updateMapView();
            //TODO update app to know that the trial is complete...
//            onlyShowLayout(finishedLayout);
//        }
//        else {
            fullPosition = full;
            //update the path history by adding this position if not same as previous
            addToPathHistory(fullPosition);
//        }
        if(navMode.equals("walk")){
            updateMapView();
            showPathHistory();
            buildSidePath();
            showLocalQRCodes();
        }

//        timerHandler.removeCallbacks(scanTimerRunnable);
//        timerHandler.postDelayed(scanTimerRunnable,REPEAT_TIME);
    }

    public void updateOneAlgo(boolean update){
//        long startTime = System.currentTimeMillis();
//        Log.d("nav start updateNav",String.valueOf(startTime));

        if(navMode.equals("walk")){
            //do nothing...
        }

        if(!navMode.equals("walk")) {

            boolean timeCheck = (System.currentTimeMillis()-rejectTime)>rejectTimeInterval;

            if(navMode.equals("adaptive")){
                    pathLogic.reCalcOptPath(fullPosition, timeLeft, pointsScanned, 0, update);
                    int number = pathLogic.updateReason();

                    if(number == 0 || number == 1) {
                        sidePath = pathLogic.getPath();
                        totalPointsOnRoute = pathLogic.getCollectedPoints();
                        QRCodesRemaining = pathLogic.getQRCollected();
                        showOptimalPath(sidePath);
                        Log.d("lengthOfGoalList", String.valueOf(lengthOfGoalList));
                        Log.d("goalList", pathLogic.goalList.toString());
                    }


                    if (number != 0 && number!=1 && timeCheck) {
                        sidePath = pathLogic.getPath();
                        totalPointsOnRoute = pathLogic.getCollectedPoints();
                        QRCodesRemaining = pathLogic.getQRCollected();
                        showOptimalPath(sidePath);
                        Log.d("lengthOfGoalList", String.valueOf(lengthOfGoalList));
                        Log.d("goalList", pathLogic.goalList.toString());
                        adaptivePathUpdate(number);
//                        lengthOfGoalList = pathLogic.goalList.size();
                    }

            }

            else if(adaptableSelected==1){
                pathLogic.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,update);
                int number = pathLogic.updateReason();
                if(number!=0 && number!=1){
                    if(timeCheck) {
                        adaptablePathUpdate(number);
                    }
                    else{
                        rejectAdaptable();
                    }
                }
                else{
                    sidePath = pathLogic.getPath();
                    showOptimalPath(sidePath);
                    totalPointsOnRoute = pathLogic.getCollectedPoints();
                    QRCodesRemaining = pathLogic.getQRCollected();}
            }

            else if(adaptableSelected==2){
                pathLogicAdp1.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,update);
                int number = pathLogicAdp1.updateReason();
                if(number!=0 && number!=1){
                    if(timeCheck) {
                        adaptablePathUpdate(number);
                    }
                    else{
                        rejectAdaptable();
                    }                }
                else{
                    sidePath2 = pathLogicAdp1.getPath();
                    showOptimalPath(sidePath2);
                    totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                    QRCodesRemaining = pathLogicAdp1.getQRCollected();

                }
            }
            else if(adaptableSelected==3){
                pathLogicAdp2.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,update);
                int number = pathLogicAdp2.updateReason();
                if(number!=0 && number!=1){
                    if(timeCheck) {
                        adaptablePathUpdate(number);
                    }
                    else{
                        rejectAdaptable();
                    }
                }
                else{
                    sidePath3 = pathLogicAdp2.getPath();
                    showOptimalPath(sidePath3);
                    totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                    QRCodesRemaining = pathLogicAdp2.getQRCollected();
                }
            }

            if(QRCodesRemaining<0){
                QRCodesRemaining=0;
            }

            pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
            qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining) + " Points: " + String.valueOf(totalPointsOnRoute));

            if(currentRunMode.equals(CurrentRunMode.CHOOSE)) {
                adaptivePoints.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
                adaptiveTime.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
                choice1Points.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
                choice1Time.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
                choice2Points.setText("Points: " + String.valueOf(pathLogicAdp1.getCollectedPoints()));
                choice2Time.setText("Time: " + String.valueOf(pathLogicAdp1.getTimeLength()) + " sec");
                choice3Points.setText("Points: " + String.valueOf(pathLogicAdp2.getCollectedPoints()));
                choice3Time.setText("Time: " + String.valueOf(pathLogicAdp2.getTimeLength()) + " sec");
                adaptiveChoiceLayout.invalidate();
            }


            showOptimalPath();
            buildSidePath();
            showPathHistory();
            updateMapView();
        }

//        long finishTime = System.currentTimeMillis();
//        Log.d("nav finish updateNav",String.valueOf(finishTime-startTime));

    }

    void confirmAdaptable(){
        if(adaptableSelected==1){
            pathLogic.goalListAssigning(true);
            pathLogic.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
            sidePath = pathLogic.getPath();
            showOptimalPath(sidePath);
            totalPointsOnRoute = pathLogic.getCollectedPoints();
            QRCodesRemaining = pathLogic.getQRCollected();
        }

        else if(adaptableSelected==2){
            pathLogicAdp1.goalListAssigning(true);
            pathLogicAdp1.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
                sidePath2 = pathLogicAdp1.getPath();
                showOptimalPath(sidePath2);
                totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp1.getQRCollected();


        }
        else if(adaptableSelected==3){
            pathLogicAdp2.goalListAssigning(true);
            pathLogicAdp2.reCalcOptPath(fullPosition, timeLeft, pointsScanned, 0,false);
                sidePath3 = pathLogicAdp2.getPath();
                showOptimalPath(sidePath3);
                totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp2.getQRCollected();

        }
    }

    void rejectAdaptable(){
        if(adaptableSelected==1){
            pathLogic.reCalcOldGoalListNoChanges(fullPosition, timeLeft, pointsScanned, 0);
                sidePath = pathLogic.getPath();
                showOptimalPath(sidePath);
                totalPointsOnRoute = pathLogic.getCollectedPoints();
                QRCodesRemaining = pathLogic.getQRCollected();
        }

        else if(adaptableSelected==2){
            pathLogicAdp1.reCalcOldGoalListNoChanges(fullPosition, timeLeft, pointsScanned, 0);
                sidePath2 = pathLogicAdp1.getPath();
                showOptimalPath(sidePath2);
                totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp1.getQRCollected();
        }

        else if(adaptableSelected==3){
            pathLogicAdp2.reCalcOldGoalListNoChanges(fullPosition,timeLeft,pointsScanned,0);
                sidePath3 = pathLogicAdp2.getPath();
                showOptimalPath(sidePath3);
                totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp2.getQRCollected();

        }
    }

    public void updateThreeAlgos(){

        if(navMode.equals("walk")){
            //do nothing...
        }

        if(!navMode.equals("walk")) {

            if(navMode.equals("adaptive")){
                pathLogic.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
                sidePath = pathLogic.getPath();
                showOptimalPath(sidePath);
                totalPointsOnRoute = pathLogic.getCollectedPoints();
                QRCodesRemaining = pathLogic.getQRCollected();
            }

            else{

                pathLogic.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
                pathLogicAdp1.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
                pathLogicAdp2.reCalcOptPath(fullPosition,timeLeft,pointsScanned,0,false);
                sidePath = pathLogic.getPath();
                sidePath2 = pathLogicAdp1.getPath();
                sidePath3 = pathLogicAdp2.getPath();

                if(adaptableSelected==1 ){
                    showOptimalPath(sidePath);
                    totalPointsOnRoute = pathLogic.getCollectedPoints();
                    QRCodesRemaining = pathLogic.getQRCollected();
                }
                else if(adaptableSelected==2){
                    showOptimalPath(sidePath2);
                    totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                    QRCodesRemaining = pathLogicAdp1.getQRCollected();
                }
                else if(adaptableSelected==3){
                    showOptimalPath(sidePath3);
                    totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                    QRCodesRemaining = pathLogicAdp2.getQRCollected();
                }
            }

            if(QRCodesRemaining<0){
                QRCodesRemaining=0;
            }

            pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
            qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining) + " Points: " + String.valueOf(totalPointsOnRoute));

            if(currentRunMode.equals(CurrentRunMode.CHOOSE)) {
                adaptivePoints.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
                adaptiveTime.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
                choice1Points.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
                choice1Time.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
                choice2Points.setText("Points: " + String.valueOf(pathLogicAdp1.getCollectedPoints()));
                choice2Time.setText("Time: " + String.valueOf(pathLogicAdp1.getTimeLength()) + " sec");
                choice3Points.setText("Points: " + String.valueOf(pathLogicAdp2.getCollectedPoints()));
                choice3Time.setText("Time: " + String.valueOf(pathLogicAdp2.getTimeLength()) + " sec");
                adaptiveChoiceLayout.invalidate();
            }

            buildSidePath();
            showPathHistory();
            updateMapView();
        }

//        long finishTime = System.currentTimeMillis();
//        Log.d("nav finish updateNav",String.valueOf(finishTime-startTime));

    }

    public void runNavigationAlg() {
//        AsyncTask nav = new NavigationThread().execute();

        long startTime = System.currentTimeMillis();
        Log.d("nav start runNav",String.valueOf(startTime));

        if(navMode.equals("walk")){
            //do nothing...
        }

//        else if(navMode.equals("adaptable")){
//            if(goalLogic_previous==null) {
//                //previous list of goals not set yet - save current ones as previous
//
////                goalLogic = new FloorRankingOrder(G_L, 90);
////                goalHigh = new HighPointPriority(G_HP);
//            }
//            if(adaptableSelected==1) {
//                if(goalLogic_previous==null) {
//                    //previous list of goals not set yet - save current ones as previous
////
//// goalLogic = new FloorRankingOrder(G_L, 90);
//
//                }else{
//                    if(goalLogic.goalList.equals(goalLogic_previous.goalList)){
//                        //good - same list of goals
//                    }else{
//                        Assets.playUpdateSound();
//                        adaptablePathUpdate();
//                        goalLogic_previous=goalLogic;
//                    }
//                }
//            }else if(adaptableSelected==2){
//                if(goalHigh_previous==null) {
//                    goalHigh = new HighPointPriority(G_HP);
//                }else{
//                    if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
//                        //good
//                    }else{
//                        Assets.playUpdateSound();
//                        adaptablePathUpdate();
//                        goalHigh_previous=goalHigh;
//                    }
//                }
//            }else if(adaptableSelected==3){
//                //TODO handle third nav option update
//            }
//        }
//        //adaptive mode
//        else if(navMode.equals("adaptive")) {
//            if(goalHigh_previous==null) {
//                goalHigh = new HighPointPriority(G_HP);
//            }else{
//                if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
//                    //good
//                }else{
//                    Assets.playUpdateSound();
//                    goalHigh_previous=goalHigh;
//                }
//            }
//            if (goalLogic_previous == null) {
//
////                goalLogic = new FloorRankingOrder(G_L, 90);
//            } else {
//                if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
//                    //good
//                } else {
//                    Assets.playUpdateSound();
//                    goalLogic_previous = goalLogic;
//                }
//            }
//        }

        if(!navMode.equals("walk")) {

//            String[] gList15 = {"37:2:91", "37:4:65", "37:4:54", "37:2:77", "35:3:116", "35:3:105", "33:3:140", "35:4:181", "33:4:132", "33:3:123", "33:2:81"};
//            String[] gList16 = {"37:2:91", "37:4:65", "37:4:54", "37:2:77", "35:3:116", "35:3:105", "33:3:140", "35:4:181", "33:4:132", "33:3:123", "33:2:81"};
//            String[] gList17 = {"37:2:91", "37:2:77", "35:3:116", "35:3:105", "33:3:140", "35:4:181", "33:4:132", "33:3:123", "33:2:81"};
//            goalList = ArrayToArrayList.getArrayList(gList15,goalList);
//            goalListAdp1 = ArrayToArrayList.getArrayList(gList16,goalListAdp1);
//            goalListAdp2 = ArrayToArrayList.getArrayList(gList17,goalListAdp2);

//            pathLogic = new Algo(g,start, goal, timeLeft, stairMod, floorMod, breakTime, goalList, allowedTime,tStair, tFloor, tQR, window);
//            pathLogicAdp1 = new Algo(g,start, goal, timeLeft, stairModAdp1, floorModAdp1, breakTimeAdp1, goalListAdp1, allowedTimeAdp1,tStairAdp1, tFloorAdp1, tQRAdp1, windowAdp1);
//            pathLogicAdp2 = new Algo(g,start, goal, timeLeft, stairModAdp2, floorModAdp2, breakTimeAdp2, goalListAdp2, allowedTimeAdp2,tStairAdp2, tFloorAdp2, tQRAdp2, windowAdp2);

            pathLogic = new Algo(G,fullPosition, goal, timeLeft, stairMod, floorMod, breakTime, goalList,allowedTime,tStair, tFloor, tQR, window);
            pathLogicAdp1 = new Algo(G,fullPosition, goal, timeLeft, stairModAdp1, floorModAdp1, breakTimeAdp1, goalListAdp1,allowedTimeAdp1,tStairAdp1, tFloorAdp1, tQRAdp1, windowAdp1);
            pathLogicAdp2 = new Algo(G,fullPosition, goal, timeLeft, stairModAdp2, floorModAdp2, breakTimeAdp2, goalListAdp2,allowedTimeAdp2,tStairAdp2, tFloorAdp2, tQRAdp2, windowAdp2);

            Log.d("goalList",goalList.toString());
            Log.d("goalList1",goalListAdp1.toString());
            Log.d("goalList2",goalListAdp2.toString());


            pathLogic.calcOptPath(fullPosition,timeLeft);
            pathLogicAdp1.calcOptPath(fullPosition,timeLeft);
            pathLogicAdp2.calcOptPath(fullPosition,timeLeft);

            sidePath = pathLogic.getPath();
            sidePath2 = pathLogicAdp1.getPath();
            sidePath3 = pathLogicAdp2.getPath();

            Log.d("Results for Logic:",sidePath.toString());
            Log.d("Results for Logic:",pathLogic.goalList.toString());

            Log.d("Results for LogicA1:",sidePath2.toString());
            Log.d("Results for LogicA1:",pathLogicAdp1.goalList.toString());

            if(QRCodesRemaining<0){
                QRCodesRemaining=0;
            }

            pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
            qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining) + " Points: " + String.valueOf(totalPointsOnRoute));


            if(adaptableSelected==1 || navMode.equals("adaptive")) {
                lengthOfGoalList = pathLogic.goalList.size();
                showOptimalPath(sidePath);
                totalPointsOnRoute = pathLogic.getCollectedPoints();
                QRCodesRemaining = pathLogic.getQRCollected();
            }else if(adaptableSelected==2){
                lengthOfGoalList = pathLogicAdp1.goalList.size();
                showOptimalPath(sidePath2);
                totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp1.getQRCollected();
            }else if(adaptableSelected==3){
                lengthOfGoalList = pathLogicAdp2.goalList.size();
                showOptimalPath(sidePath3);
                totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
                QRCodesRemaining = pathLogicAdp2.getQRCollected();
            }

        if(currentRunMode.equals(CurrentRunMode.CHOOSE)) {
            adaptivePoints.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
            adaptiveTime.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
            choice1Points.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
            choice1Time.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
            choice2Points.setText("Points: " + String.valueOf(pathLogicAdp1.getCollectedPoints()));
            choice2Time.setText("Time: " + String.valueOf(pathLogicAdp1.getTimeLength()) + " sec");
            choice3Points.setText("Points: " + String.valueOf(pathLogicAdp2.getCollectedPoints()));
            choice3Time.setText("Time: " + String.valueOf(pathLogicAdp2.getTimeLength()) + " sec");
            adaptiveChoiceLayout.invalidate();
        }

            buildSidePath();
            showPathHistory();
            updateMapView();



//            long timeLength = System.currentTimeMillis() - timeStart;
//            Log.d("nav alg done", String.valueOf(timeLength));

            long finishTime = System.currentTimeMillis();
            Log.d("nav finish runNav",String.valueOf(finishTime-startTime));
        }
//        */
    }

    public void showOptimalPath() {

        ArrayList<String> p = new ArrayList<>();

        if(adaptableSelected==1 || navMode.equals("adaptive")) {
            p = pathLogic.getPath();
        }else if(adaptableSelected==2){
            p = pathLogicAdp1.getPath();
        }else if(adaptableSelected==3){
            p = pathLogicAdp2.getPath();
        }

        localFuturePath.clear();
        String transition = null;
        boolean before = true;
        boolean foundPath = false;
        boolean breakInPath = false;

        if (p.size() > 0) {
            Point point;
            int[] pos;

            for (int i = 0; i < p.size(); i++) {
                //if still before the local path in the list or iterating through the local path:
                if ((before || foundPath) && !breakInPath) {
                    String s = p.get(i);
                    String[] pieces = s.split(":");
                    if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                        before = false;
                        foundPath = true;
//                        Log.d("transition here", "found on this floor");
                        pos = ConvertGrid(Integer.parseInt(pieces[2]));
                        point = new Point((25 + 49 * (pos[0] - 1)), (42 + 52 * (pos[1] - 1)));
                        localFuturePath.add(point);
                        //if the point isn't the last in the list
                        if (p.size() != i + 1) {
                            //if the point is a connection and the next point is on another floor
//                            if (connections.contains(s) && !pieces[1].equals(p.get(i + 1).split(":")[1])) {
                            if (!pieces[1].equals(p.get(i + 1).split(":")[1])) {
                                breakInPath = true;
                                int nextFloor = Integer.parseInt(p.get(i + 1).split(":")[1]);
                                if (Integer.parseInt(pieces[1]) < nextFloor) {
                                    transition = "up";
//                                    Log.d("transition", "up at " + s);
                                } else if (Integer.parseInt(pieces[1]) > nextFloor) {
                                    transition = "down";
//                                    Log.d("transition", "down at " + s);
                                }
                            } else if (connections.contains(s) && !pieces[0].equals(p.get(i + 1).split(":")[0])) {
                                breakInPath = true;
                                String nextBuilding = p.get(i + 1).split(":")[0];
                                if (pieces[0].equals("33") && nextBuilding.equals("35")) {
                                    transition = "right";
//                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("37")) {
                                    transition = "right";
//                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("37") && nextBuilding.equals("35")) {
                                    transition = "left";
//                                    Log.d("transition", "left");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("33")) {
                                    transition = "down";
//                                    Log.d("transition", "down");
                                }
                            }
                        } else {
                            breakInPath = true;
                            transition = "end of path";
                        }
                    } else {
                        transition = "not on this floor";
//                        Log.d("transition here", "not on this floor");

                    }
                }
            }
        } else {
            transition = "empty list";
        }
//        Log.d("transition here", transition);
        final String pass = transition;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
                    drawView.updateFuturePath(localFuturePath,pass);
//                    pointsCollectedText.setText("Points Collected on Route: " + String.valueOf(pointsCollected) + "/" + String.valueOf(totalPointsOnRoute));
//                    qrCodesRemainingText.setText("QR Codes Remaining: " + String.valueOf(QRCodesRemaining));
////                }
////            });
    }

    public void showOptimalPath(ArrayList<String> p) {

        localFuturePath.clear();
        String transition = null;
        boolean before = true;
        boolean foundPath = false;
        boolean breakInPath = false;

        if (p.size() > 0) {
            Point point;
            int[] pos;

            for (int i = 0; i < p.size(); i++) {
                //if still before the local path in the list or iterating through the local path:
                if ((before || foundPath) && !breakInPath) {
                    String s = p.get(i);
                    String[] pieces = s.split(":");
                    if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                        before = false;
                        foundPath = true;
//                        Log.d("transition here", "found on this floor");
                        pos = ConvertGrid(Integer.parseInt(pieces[2]));
                        point = new Point((25 + 49 * (pos[0] - 1)), (42 + 52 * (pos[1] - 1)));
                        localFuturePath.add(point);
                        //if the point isn't the last in the list
                        if (p.size() != i + 1) {
                            //if the point is a connection and the next point is on another floor
//                            if (connections.contains(s) && !pieces[1].equals(p.get(i + 1).split(":")[1])) {
                            if (!pieces[1].equals(p.get(i + 1).split(":")[1])) {
                                breakInPath = true;
                                int nextFloor = Integer.parseInt(p.get(i + 1).split(":")[1]);
                                if (Integer.parseInt(pieces[1]) < nextFloor) {
                                    transition = "up";
//                                    Log.d("transition", "up at " + s);
                                } else if (Integer.parseInt(pieces[1]) > nextFloor) {
                                    transition = "down";
//                                    Log.d("transition", "down at " + s);
                                }
                            } else if (connections.contains(s) && !pieces[0].equals(p.get(i + 1).split(":")[0])) {
                                breakInPath = true;
                                String nextBuilding = p.get(i + 1).split(":")[0];
                                if (pieces[0].equals("33") && nextBuilding.equals("35")) {
                                    transition = "right";
//                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("37")) {
                                    transition = "right";
//                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("37") && nextBuilding.equals("35")) {
                                    transition = "left";
//                                    Log.d("transition", "left");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("33")) {
                                    transition = "down";
//                                    Log.d("transition", "down");
                                }
                            }
                        } else {
                            breakInPath = true;
                            transition = "end of path";
                        }
                    } else {
                        transition = "not on this floor";
//                        Log.d("transition here", "not on this floor");

                    }
                }
            }
        } else {
            transition = "empty list";
        }
//        Log.d("transition here", transition);
        final String pass = transition;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
        drawView.updateFuturePath(localFuturePath,pass);
//        buildSidePath();
//                    pointsCollectedText.setText("Points Collected on Route: " + String.valueOf(pointsCollected) + "/" + String.valueOf(totalPointsOnRoute));
//                    qrCodesRemainingText.setText("QR Codes Remaining: " + String.valueOf(QRCodesRemaining));
////                }
////            });
    }

    public void calibratePath() {
        int x0 = 0;
        int y0 = 16;
        int dx = 49;
        int dy = 52;
        drawView.paintRectangle(3 * dx, y0, dx, dy);
        drawView.paintRectangle(dx * 23, y0, dx, dy);
        drawView.paintRectangle(dx * 23, 3 * dy + y0, dx, dy);
        drawView.paintRectangle(3 * dx, 3 * dy + y0, dx, dy);
        drawView.paintRectangle(dx * 23, 7 * dy + y0, dx, dy);
        drawView.paintRectangle(3 * dx, 7 * dy + y0, dx, dy);
    }

    public void showLocalQRCodes() {

        localQRCodeLocations.clear();

        if (QRCodeLocations.size() > 0) {
            QRLocationXY QRPoint;
            int[] pos;

//            Log.d("rawr","qrCodeLocations longer than 0");

            int x0 = 0;
            int y0 = 16;
            int dx = 49;
            int dy = 52;
            String full;

            if(navMode.equals("walk")){
                for (String s : QRCodeLocations) {
                    String[] pieces = s.split(":");
                    if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                        pos = ConvertGrid(Integer.parseInt(pieces[2]));
                        full = pieces[0] + ":" + pieces[1] + ":" + pieces[2];
                        QRPoint = new QRLocationXY(full, x0 + dx * (pos[0] - 1), (y0 + dy * (pos[1] - 1)), 1);
                        localQRCodeLocations.add(QRPoint);
//                    Log.d("rawr","added "+s+" to the local QR codes");
                    }
                }
            }
            else {
                for (String s : QRCodeLocations) {
                    String[] pieces = s.split(":");
                    if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber) && Integer.parseInt(pieces[3]) > 0) {
                        pos = ConvertGrid(Integer.parseInt(pieces[2]));
                        full = pieces[0] + ":" + pieces[1] + ":" + pieces[2];
                        QRPoint = new QRLocationXY(full, x0 + dx * (pos[0] - 1), (y0 + dy * (pos[1] - 1)), Integer.parseInt(pieces[3]));
                        localQRCodeLocations.add(QRPoint);
//                    Log.d("rawr","added "+s+" to the local QR codes");
                    }
                }

                String[] pieces = goal.split(":");
                if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                    pos = ConvertGrid(Integer.parseInt(pieces[2]));
                    full = pieces[0] + ":" + pieces[1] + ":" + pieces[2];
                    QRPoint = new QRLocationXY(full, x0 + dx * (pos[0] - 1), (y0 + dy * (pos[1] - 1)),10);
                    localQRCodeLocations.add(QRPoint);
//                    Log.d("rawr","added "+s+" to the local QR codes");
                }
            }
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                drawView.updateQRCodeLocations(localQRCodeLocations);
//            }
//        });
    }

    public void removePoints(String full) {

        if(QRmap.containsKey(full)) {
            QRmap.get(full).removePoints();
//            pointsScanned.add(full);
//            pointsCollected += QRmap.get(full).getPoints();

//        pointsCollectedText.setText("Points Collected on Route: "+String.valueOf(pointsCollected)+"/total");
//        pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
//        pointsRemainingText.setText("Points Remaining on Route: " + String.valueOf(totalPointsOnRoute));
            //remove QR points from the global Graph that all nav algs clone off of
            G.reverseWeightingsGradient(full, G);
            G.removePoints(full);

            //remove QR points from list of QRCodeLocations by creating a new list without it
            ArrayList<String> newList = new ArrayList<>();
            for (String s : QRCodeLocations) {
                String[] pieces = s.split(":");
                if ((pieces[0] + ":" + pieces[1] + ":" + pieces[2]).equals(full)) {
                    newList.add(pieces[0] + ":" + pieces[1] + ":" + pieces[2] + ":0");
                } else {
                    newList.add(s);
                }
            }
            QRCodeLocations = newList;
        }

//        updateOneAlgos();
        showQRCodes();
        showLocalQRCodes();

    }

    //Update the visual display for the user
    public Thread makeProgressThread() {
        Thread timerThread = new Thread() {
            @Override
            public void run() {
                mbActive = true;
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressLayout.setVisibility(View.VISIBLE);
//                            Animation animateTruck = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.truck_animation);
//                            truckImage.startAnimation(animateTruck);
//                            Animation animateBuild = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.build_animation);
//                            buildImage.startAnimation(animateBuild);
//                            Animation animateLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.layout_animation);
//                            progressLayout.startAnimation(animateLayout);
                        }
                    });
                    int waited = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarText.setText("Downloading data from server...");
                        }
                    });
                    while (mbActive && (waited < TIMER_RUNTIME)) {
                        sleep(100);
                        if (mbActive) {
                            waited += 100;
                            updateProgress(waited);
//                            Log.d("progress tag", String.valueOf(waited));
                        }
//                        if (waited == 1000) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startScanTime = System.currentTimeMillis();
//                                    Log.d("wifi listener", "register! ***********************************************************************");
                        //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        //mainWifiObj.disconnect();
//                                    if (!scanOn) {
//                                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                                        mainWifiObj.startScan();
//                                        scanOn = true;
                        //showCustomAlert("Scanning On");
//                                    }
//                                    Log.d("wifi listener", "scanning...");

//                                }
//                            });
//                        }
                        if (waited == 2000) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarText.setText("Creating Grid objects...");
                                }
                            });
                        }
                        if (waited == 3700) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarText.setText("Almost ready...");
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    onContinue();
                }
            }

            public void updateProgress(final int timePassed) {
//                int progress;
                if (null != mProgressBar) {
                    // Ignore rounding error here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int progress = mProgressBar.getMax() * timePassed / TIMER_RUNTIME;
                            mProgressBar.setProgress(progress);
                        }
                    });
                }
            }

            public void onContinue() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressLayout.setVisibility(View.GONE);
                    }
                });
            }
        };
        return timerThread;
    }

//    Handler navHandler = new Handler();
//    Runnable navRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            long timeStart = System.currentTimeMillis();
//            Log.d("nav alg start", String.valueOf(timeStart));
//
//            //clone the master Graph into one for each nav method
//            G_HP = new Graph(G);
//            G_L = new Graph(G);
//            G_orig = new Graph(G);
//
//            int breakTime = 100;
//
//            //Original Algorithm
//            G_orig.setStartLocation(start);  //set goal location first, then mark neighbors
//            G_orig.setGoalLocation(goal);
//            G_orig.markNeighbors(goal, G_orig);
//            ArrayList<String> originalGoal = new ArrayList<String>();
//            originalGoal.add(goal);
//            pathFinder = new Algo(G_orig, fullPosition, goal, originalGoal, timeLeft);
//            pathFinder.calcOptPath(fullPosition, timeLeft, 100);
//
//            G_HP.setStartLocation(fullPosition);
//            G_HP.setGoalLocation(goal);
//            G_HP.markNeighbors(goal, G_HP);
//
//            G_L.setStartLocation(fullPosition);
//            G_L.setGoalLocation(goal);
//            G_L.markNeighbors(goal, G_L);
//
//            if (navMode.equals("adaptable")) {
//                if (goalLogic_previous == null) {
//                    //previous list of goals not set yet - save current ones as previous
//                    goalLogic = new FloorRankingOrder(G_L, 90);
//                    goalHigh = new HighPointPriority(G_HP);
//                }
//                if (adaptableSelected == 1) {
//                    if (goalLogic_previous == null) {
//                        //previous list of goals not set yet - save current ones as previous
//                        goalLogic = new FloorRankingOrder(G_L, 90);
//
//                    } else {
//                        if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
//                            //good - same list of goals
//                        } else {
//                            Assets.playUpdateSound();
//                            adaptablePathUpdate();
//                            goalLogic_previous = goalLogic;
//                        }
//                    }
//                } else if (adaptableSelected == 2) {
//                    if (goalHigh_previous == null) {
//                        goalHigh = new HighPointPriority(G_HP);
//                    } else {
//                        if (goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)) {
//                            //good
//                        } else {
//                            Assets.playUpdateSound();
//                            adaptablePathUpdate();
//                            goalHigh_previous = goalHigh;
//                        }
//                    }
//                } else if (adaptableSelected == 3) {
//                    //TODO handle third nav option update
//                }
//            } else {  //adaptive mode
//                if (goalHigh_previous == null) {
//                    goalHigh = new HighPointPriority(G_HP);
//                } else {
//                    if (goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)) {
//                        //good
//                    } else {
//                        Assets.playUpdateSound();
//                        goalHigh_previous = goalHigh;
//                    }
//                }
//                if (goalLogic_previous == null) {
//                    goalLogic = new FloorRankingOrder(G_L, 90);
//                } else {
//                    if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
//                        //good
//                    } else {
//                        Assets.playUpdateSound();
//                        goalLogic_previous = goalLogic;
//                    }
//                }
//            }
//
//            pathHigh = new Algo(G_HP, fullPosition, goal, goalHigh.goalPoint, timeLeft);
//            pathHigh.calcOptPath(fullPosition, timeLeft, breakTime);
//
//            pathLogic = new Algo(G_L, fullPosition, goal, goalLogic.goalList, timeLeft);
//            pathLogic.calcOptPath(fullPosition, timeLeft, breakTime);
//
//            sidePath = pathLogic.getPath();
//            sidePath2 = pathHigh.getPath();
//            sidePath3 = pathFinder.getPath();
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    navigationUI();
//                }
//            });
//
//            long timeLength = System.currentTimeMillis() - timeStart;
//            Log.d("nav alg done", String.valueOf(timeLength));
//
//            timerHandler.postDelayed(this, 0);
//        }
//    };

//    public Thread tonyNavigation() {
//        Thread timerThread = new Thread() {
//            @Override
//            public void run() {
//                long timeStart = System.currentTimeMillis();
//                Log.d("nav alg start",String.valueOf(timeStart));
//
//                //clone the master Graph into one for each nav method
//                G_HP = new Graph(G);
//                G_L = new Graph(G);
//                G_orig = new Graph(G);
//
//                int breakTime = 100;
//
//                //Original Algorithm
//                G_orig.setStartLocation(start);  //set goal location first, then mark neighbors
//                G_orig.setGoalLocation(goal);
//                G_orig.markNeighbors(goal, G_orig);
//                ArrayList<String> originalGoal = new ArrayList<String>();
//                originalGoal.add(goal);
//                pathFinder = new Algo(G_orig,fullPosition, goal, originalGoal,timeLeft);
//                pathFinder.calcOptPath(fullPosition,timeLeft,100);
//
//                G_HP.setStartLocation(fullPosition);
//                G_HP.setGoalLocation(goal);
//                G_HP.markNeighbors(goal, G_HP);
//
//                G_L.setStartLocation(fullPosition);
//                G_L.setGoalLocation(goal);
//                G_L.markNeighbors(goal, G_L);
//
//                if(navMode.equals("adaptable")){
//                    if(goalLogic_previous==null) {
//                        //previous list of goals not set yet - save current ones as previous
//                        goalLogic = new FloorRankingOrder(G_L, 90);
//                        goalHigh = new HighPointPriority(G_HP);
//                    }
//                    if(adaptableSelected==1) {
//                        if(goalLogic_previous==null) {
//                            //previous list of goals not set yet - save current ones as previous
//                            goalLogic = new FloorRankingOrder(G_L, 90);
//
//                        }else{
//                            if(goalLogic.goalList.equals(goalLogic_previous.goalList)){
//                                //good - same list of goals
//                            }else{
//                                Assets.playUpdateSound();
//                                adaptablePathUpdate();
//                                goalLogic_previous=goalLogic;
//                            }
//                        }
//                    }else if(adaptableSelected==2){
//                        if(goalHigh_previous==null) {
//                            goalHigh = new HighPointPriority(G_HP);
//                        }else{
//                            if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
//                                //good
//                            }else{
//                                Assets.playUpdateSound();
//                                adaptablePathUpdate();
//                                goalHigh_previous=goalHigh;
//                            }
//                        }
//                    }else if(adaptableSelected==3){
//                        //TODO handle third nav option update
//                    }
//                }else {  //adaptive mode
//                    if(goalHigh_previous==null) {
//                        goalHigh = new HighPointPriority(G_HP);
//                    }else{
//                        if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
//                            //good
//                        }else{
//                            Assets.playUpdateSound();
//                            goalHigh_previous=goalHigh;
//                        }
//                    }
//                    if (goalLogic_previous == null) {
//                        goalLogic = new FloorRankingOrder(G_L, 90);
//                    } else {
//                        if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
//                            //good
//                        } else {
//                            Assets.playUpdateSound();
//                            goalLogic_previous = goalLogic;
//                        }
//                    }
//                }
//
//                pathHigh = new Algo(G_HP, fullPosition, goal, goalHigh.goalPoint,timeLeft);
//                pathHigh.calcOptPath(fullPosition,timeLeft,breakTime);
//
//                pathLogic = new Algo(G_L, fullPosition, goal, goalLogic.goalList,timeLeft);
//                pathLogic.calcOptPath(fullPosition,timeLeft,breakTime);
//
//                sidePath = pathLogic.getPath();
//                sidePath2 = pathHigh.getPath();
//                sidePath3 = pathFinder.getPath();
//
//                runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                    navigationUI();
//                        }
//                    });
//
//                long timeLength = System.currentTimeMillis()-timeStart;
//                Log.d("nav alg done",String.valueOf(timeLength));
//
//                try {
//                    this.join();
//                }catch (InterruptedException e){
//
//                }
//            }
//
//            public void updateProgress() {
////                int progress;
////                updateMapView();
//            }
//
//            public void onContinue() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressLayout.setVisibility(View.GONE);
//                    }
//                });
//            }
//        };
//        return timerThread;
//    }

    public void updateGrid(int[] data) {
        gridAdapter = new com.example.matthew.newapplication.CustomGridAdapter(getApplicationContext(), data);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
    }

    public int[] updateMapView() {
//        appendLog(fullPosition,true);

        mapsize = new int[240];
        map.setPadding(0, 0, 0, 0);

//        Animation animateCompassBeat = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.beat_animation);
//        compassBeatImage.startAnimation(animateCompassBeat);


        if (buildingnumber.equals("0")) {
            //initialize image mapping to all blank
            for (int i = 0; i < mapsize.length; i++) {
                mapsize[i] = -1;
            }
            map.setImageResource(R.drawable.navigation);
//            map.setPadding(200,200,200,200);
            //Toast.makeText(getApplicationContext(),"We can't find your position :(",Toast.LENGTH_LONG).show();
            //Log.d("length", Integer.toString(mapsize.length));

            //return mapsize;
        } else if (buildingnumber.equals("33")) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapGridLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);

            ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
            RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();

            if (floornumber.equals("0")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -43, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -26, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor0_33;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("1")) {
                 margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -81, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor1_33;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("2")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -79, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor2_33;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("3")) {
               margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -21, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor3_33;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("4")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -43, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -18, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor4_33;
                map.setImageResource(currentFloorImage);
            }

//            if (!localDataVisible) {
                //initialize image mapping to all blank
                for (int i = 0; i < mapsize.length; i++) {
                    mapsize[i] = -1;
                }
//            }

            //Log.d("length", Integer.toString(mapsize.length));
            //showCustomAlert("Building " + buildingnumber + ", Floor " + floornumber);
            //Toast.makeText(getApplicationContext(), "Building " + buildingnumber + ", Floor " + floornumber, Toast.LENGTH_SHORT).show();

            locationText.setText("Building " + buildingnumber + ", Floor " + floornumber);
            numbers = mapsize;
            return mapsize;

        } else if (buildingnumber.equals("35")) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapGridLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);

            ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
            RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();


            if (floornumber.equals("0")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -63, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -16, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor0_35;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("1")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -99, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -16, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor1_35;
                map.setImageResource(currentFloorImage);
            }


            if (floornumber.equals("2")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -61, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -16, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = floor2_35;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("3")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -59, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -16, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor3_35;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("4")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -61, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -16, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor4_35;
                map.setImageResource(currentFloorImage);
            }

//            if (floornumber.equals("5")) {
//                map.setImageResource(R.drawable.floor5_35);
//            }

            //initialize image mapping to all blank
            for (int i = 0; i < mapsize.length; i++) {
                mapsize[i] = -1;
            }

            //Log.d("length", Integer.toString(mapsize.length));
            //showCustomAlert("Building " + buildingnumber + ", Floor " + floornumber);
//            Toast.makeText(getApplicationContext(), "Building " + buildingnumber + ", Floor " + floornumber, Toast.LENGTH_SHORT).show();

            locationText.setText("Building " + buildingnumber + ", Floor " + floornumber);
            numbers = mapsize;
            return mapsize;
        } else if (buildingnumber.equals("37")) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapGridLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);

            ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
            RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();

//            Log.d("grid dims width",String.valueOf(layout.getWidth()));
//            Log.d("grid dims height",String.valueOf(layout.getHeight()));
//            Log.d("grid conversion 49px",String.valueOf(convertToPx(49)));

            if (floornumber.equals("1")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -93, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor1_37;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("2")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -89, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -6, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor2_37;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("3")) {
//                ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
//                RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -94, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -10, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor3_37;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("4")) {
//                ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
//                RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -94, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -10, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor4_37;
                map.setImageResource(currentFloorImage);
            }

            if (floornumber.equals("5")) {
//                ImageView wallsLayout = (ImageView) findViewById(R.id.wallsView);
//                RelativeLayout.LayoutParams wallsParams = (RelativeLayout.LayoutParams) wallsLayout.getLayoutParams();
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -94, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -10, getResources().getDisplayMetrics()));
                wallsParams.setMargins(margin_left, margin_top, margin_right, 0);
                wallsLayout.setLayoutParams(wallsParams);
                currentFloorImage = R.drawable.floor5_37;
                map.setImageResource(currentFloorImage);
            }

//            final ImageView finalWalls = wallsLayout;
//            final RelativeLayout.LayoutParams fwallsParams = wallsParams;




            //initialize image mapping to all zeros
            for (int i = 0; i < mapsize.length; i++) {
                mapsize[i] = -1;
            }

            //Log.d("length", Integer.toString(mapsize.length));

            //showCustomAlert("Building " + buildingnumber + ", Floor " + floornumber);
//            Toast.makeText(getApplicationContext(), "Building " + buildingnumber + ", Floor " + floornumber, Toast.LENGTH_SHORT).show();

            locationText.setText("Building " + buildingnumber + ", Floor " + floornumber);
            numbers = mapsize;
            return mapsize;
        }
//        checkForExtremes();
        return mapsize;

    }

    public void addToPathHistory(String full) {
        if (pathHistory.size() == 0) {
            pathHistory.add(full);
            runNavigationAlg();

        } else {
            String previous = pathHistory.get(pathHistory.size() - 1);

            if(full.equals(goal)){
                pathHistory.add(full);
                appendLog("Location", full, true);
                //but don't run algo from goal location
            }
            else if (!full.equals(previous)) {
                pathHistory.add(full);
                appendLog("Location", full, true);
                //TODO experimenting with updateAlgos - save runNav for first scan and updates and QR removal
                updateOneAlgo(false);
            }
            else{
//                updateAlgos();
            }
        }
    }

    public void showPathHistory() {

        localPathHistory.clear();

        if (pathHistory.size() > 0) {
            Point point;
            int[] pos;

            for (String s : pathHistory) {
                String[] pieces = s.split(":");
                if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                    pos = ConvertGrid(Integer.parseInt(pieces[2]));
                    point = new Point((25 + 49 * (pos[0] - 1)), (42 + 52 * (pos[1] - 1)));
                    localPathHistory.add(point);
//                    Log.d("rawr","added "+s+" to the local path history");
                }
            }
        }
        drawView.updatePathHistory(localPathHistory);
        showLocalQRCodes();
    }

    public void showCustomAlert(String inputString) {
        toastActivated = true;

        Context context = getApplicationContext();
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = getLayoutInflater();

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.toast, null);
        toast = new Toast(context);

        TextView toastwords = (TextView) toastRoot.findViewById(R.id.toast_text);
        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        toastwords.setText(inputString);
        toastwords.setTypeface(font);
        locationText.setTypeface(font);

        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, -640);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

    public void addPointsToCollected(int p) {
        pointsCollected += p;
        pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
//        pointsRemainingText.setText("Points Remaining on Route: " + String.valueOf(totalPointsOnRoute));
        if(QRCodesRemaining<0){
            QRCodesRemaining=0;
        }
        qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining)+" Points: "+String.valueOf(totalPointsOnRoute));
    }

    public void updateAlertBar() {
        if (alertBarOn == 2) {
            appendLog("AlertBar", "on", true);
            //you're stuck looking at the alert bar
        } else if (alertBarOn == 0) {
            //alertBarLayout.setVisibility(View.VISIBLE);
            appendLog("AlertBar", "on", true);
            alertBarOn = 1;
            alertBarText.setVisibility(View.VISIBLE);
            alertBarTextBelow.setVisibility(View.VISIBLE);

        } else if (alertBarOn == 1) {
            //alertBarLayout.setVisibility(View.GONE);
            alertBarOn = 0;
            appendLog("AlertBar", "off", true);
            alertBarText.setVisibility(View.GONE);
            alertBarTextBelow.setVisibility(View.GONE);
        }
    }

    //Visuals for SideView
    public void toggleView() {

        drawView.invalidate();
        drawView2.invalidate();
        buildSidePath();

        if (sideViewOn == false) {
            appendLog("Layout", "sideView", true);
            topViewLayout.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.VISIBLE);
            bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewOn = true;
            QRScan.setClickable(false);
            sideViewToggleText.setText("Top View");
        } else if (sideViewOn) {
            appendLog("Layout", "topView", true);
            topViewLayout.setVisibility(View.VISIBLE);
            sideViewLayout.setVisibility(View.GONE);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.side_view_clear_0_3);
            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, 100));
            sideViewOn = false;
            QRScan.setClickable(true);
            sideViewToggleText.setText("Side View");
        }
    }

    public void buildSidePath() {
//        if(sidePath!=null) {

        sidePath = pathLogic.getPath();
        sidePath2 = pathLogicAdp1.getPath();
        sidePath3 = pathLogic.getPath();


        Log.d("side view", "start");
            if (navMode.equals("walk")) {
                int[] pos, lastPos;
                Point firstPoint, currentPoint;
                firstPoint = new Point(0, 0);

                //start with path History if exists
                if (pathHistory.size() > 0) {
                    //save route starting point as firstPoint
                    pos = SideViewPreview.convertBuildFloor(pathHistory.get(0));
                    updatePathSideView(1, pos[0], pos[1]);
                    firstPoint = new Point(pos[0], pos[1]);

                    for (int i = 1; i < pathHistory.size(); i++) {
                        pos = SideViewPreview.convertBuildFloor(pathHistory.get(i));
                        updatePathSideView(1, pos[0], pos[1]);
                    }

                    lastPos = SideViewPreview.convertBuildFloor(pathHistory.get(pathHistory.size() - 1));
                    currentPoint = new Point(lastPos[0], lastPos[1]);


//                String last = sidePath.get(sidePath.size() - 1);
//                int[] lastCoords = SideViewPreview.convertBuildFloor(last);
//                updatePathSideView(1, lastCoords[0], lastCoords[1]);

                    drawView2.updateSidePath(firstPoint, currentPoint, pathLA1);
                    drawView2.invalidate();
                    showQRCodes();

                } else {
                    points.setText("Error in loading path");
                }

            }
            //currently using High Point for the adaptive case - final decision from Tony
            //not testing side path static vs dynamic in adaptive mode
            if (navMode.equals("adaptive")) {
                pathL.clear();
                if (sidePath.size() != 0) {
                    int[] pos;
                    Point firstPoint, currentPoint;
                    firstPoint = new Point(0, 0);

                    //start with path History if exists
                    if (pathHistory.size() > 0) {
                        //save route starting point as firstPoint
                        pos = SideViewPreview.convertBuildFloor(pathHistory.get(0));
                        updatePathSideView(1, pos[0], pos[1]);
                        firstPoint = new Point(pos[0], pos[1]);

                        for (int i = 1; i < pathHistory.size(); i++) {
                            pos = SideViewPreview.convertBuildFloor(pathHistory.get(i));
                            updatePathSideView(1, pos[0], pos[1]);
                        }
                    }

                    //////////////////// sideaPath1
                    String first = sidePath.get(0);
                    pos = SideViewPreview.convertBuildFloor(first);
                    updatePathSideView(1, pos[0], pos[1]);
                    currentPoint = new Point(pos[0], pos[1]);

                    for (int i = 1; i < sidePath.size() - 1; i++) {
                        pos = SideViewPreview.convertBuildFloor(sidePath.get(i));
                        updatePathSideView(1, pos[0], pos[1]);
                    }

                    //if just starting trial and pathHistory is empty
                    if (pathHistory.size() == 0) {
                        //use currentPoint as start
                        firstPoint = currentPoint;
                    }

                    String last = sidePath.get(sidePath.size() - 1);
                    int[] lastCoords = SideViewPreview.convertBuildFloor(last);
                    updatePathSideView(1, lastCoords[0], lastCoords[1]);

                    drawView2.invalidate();
                    drawView2.updateSidePath(firstPoint, currentPoint, pathL);
                    drawView2.invalidate();
                    Log.d("side view",sidePath.toString());
                    Log.d("side view",pathL.toString());
                    showQRCodes();
                } else {
                    points.setText("Error in loading path");
                }
            }

            //using adaptive mode will use all 3 side paths
            else if (navMode.equals("adaptable")) {
                //TODO manual assign
                sidePathUpdates = sidePathMode.DYNAMIC;

                pathL.clear();
                pathLA1.clear();
                pathLA2.clear();

                int[] pos;
                Point firstPoint, currentPoint;
                firstPoint = new Point(0, 0);

                //start with path History if exists
                if (pathHistory.size() > 0) {
                    //save route starting point as firstPoint
                    pos = SideViewPreview.convertBuildFloor(pathHistory.get(0));
                    updatePathSideView(1, pos[0], pos[1]);
                    updatePathSideView(2, pos[0], pos[1]);
                    updatePathSideView(3, pos[0], pos[1]);
                    firstPoint = new Point(pos[0], pos[1]);

                    for (int i = 1; i < pathHistory.size(); i++) {
                        pos = SideViewPreview.convertBuildFloor(pathHistory.get(i));
                        updatePathSideView(1, pos[0], pos[1]);
                        updatePathSideView(2, pos[0], pos[1]);
                        updatePathSideView(3, pos[0], pos[1]);
                    }
                }

                if (sidePath.size() != 0 && sidePath2.size() != 0) {

//                    Log.d("buildsidepath","got here 1");

                    //////////////////// Logic Path

                    String first = sidePath.get(0);
                    pos = SideViewPreview.convertBuildFloor(first);
                    updatePathSideView(1, pos[0], pos[1]);
                    currentPoint = new Point(pos[0], pos[1]);

                    if (pathHistory.size() == 0) {
                        firstPoint = currentPoint;
                    }

                    for (int i = 1; i < sidePath.size() - 1; i++) {
                        pos = SideViewPreview.convertBuildFloor(sidePath.get(i));
                        updatePathSideView(1, pos[0], pos[1]);
                    }

                    String last = sidePath.get(sidePath.size() - 1);
                    int[] lastCoords = SideViewPreview.convertBuildFloor(last);
                    updatePathSideView(1, lastCoords[0], lastCoords[1]);

                    /////////////////// High Point path

                    first = sidePath2.get(0);
                    pos = SideViewPreview.convertBuildFloor(first);
                    updatePathSideView(2, pos[0], pos[1]);

                    for (int i = 1; i < sidePath2.size() - 1; i++) {
                        pos = SideViewPreview.convertBuildFloor(sidePath2.get(i));
                        updatePathSideView(2, pos[0], pos[1]);
                    }

                    last = sidePath2.get(sidePath2.size() - 1);
                    lastCoords = SideViewPreview.convertBuildFloor(last);
                    updatePathSideView(2, lastCoords[0], lastCoords[1]);

                    //////////////////// Original Alg Path

                    first = sidePath3.get(0);
                    pos = SideViewPreview.convertBuildFloor(first);
                    updatePathSideView(3, pos[0], pos[1]);

                    for (int i = 1; i < sidePath3.size() - 1; i++) {
                        pos = SideViewPreview.convertBuildFloor(sidePath3.get(i));
                        updatePathSideView(3, pos[0], pos[1]);
                    }

                    last = sidePath3.get(sidePath3.size() - 1);
                    lastCoords = SideViewPreview.convertBuildFloor(last);
                    updatePathSideView(3, lastCoords[0], lastCoords[1]);

                    points.setText("Possible Points: " + predictedPoints);

                    if (currentRunMode == CurrentRunMode.CHOOSE) {
//                        Log.d("buildsidepath","got here 2");
                        drawView2.updateSidePath(pathL, pathLA1, pathLA2);
                        drawView2.invalidate();
                        showQRCodes();
                    } else if (currentRunMode == CurrentRunMode.SCANNING) {
                        //normal run mode = only show currently selected route
                        if (adaptableSelected == 1) {
//                            Log.d("buildsidepath","hp 3");
                            drawView2.updateSidePath(firstPoint, currentPoint, pathL);
                            drawView2.invalidate();
                            showQRCodes();
                        } else if (adaptableSelected == 2) {
                            drawView2.updateSidePath(firstPoint, currentPoint, pathLA1);
                            drawView2.invalidate();
                            showQRCodes();
                        } else if (adaptableSelected == 3) {
                            drawView2.updateSidePath(firstPoint, currentPoint, pathLA2);
                            drawView2.invalidate();
                            showQRCodes();
                        } else {
                            drawView2.updateSidePath(pathL, pathLA1, pathLA2);
                            drawView2.invalidate();
                            showQRCodes();
                        }
                    }
                } else {
                    points.setText("Error in loading path");
                }
//            }
            }
            Log.d("side view", "done");

    }

    public void updatePathSideView(int n, int x, int y) {
        Point point = new Point(x, y);
        if (n == 1) {
            pathL.add(point);
        } else if (n == 2) {
            pathLA1.add(point);
        } else if (n==3) {
            pathLA2.add(point);
        }
    }

    public void onlyShowLayout(RelativeLayout show) {
        alertToggleLayout.setVisibility(View.GONE);
        finishedLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        finishedLayout.setVisibility(View.GONE);
        sideViewLayout.setVisibility(View.INVISIBLE);
        sideViewToggle.setVisibility(View.GONE);

        show.setVisibility(View.VISIBLE);
    }

    public void showQRCodes() {
        sideViewQRCodeLocations.clear();

        if(navMode.equals("walk")){
            if (QRCodeLocations.size() > 0) {
                QRLocationXY QRPoint;
                for (String s : QRCodeLocations) {
                    String[] pieces = s.split(":");
                    int[] pos = SideViewPreview.convertBuildFloor(pieces[0] + ":" + pieces[1] + ":" + pieces[2]);
                    QRPoint = new QRLocationXY(pieces[0] + ":" + pieces[1] + ":" + pieces[2], pos[0], pos[1], 1);
//                    Log.d("sideView",QRPoint.toString());
                    sideViewQRCodeLocations.add(QRPoint);
                }
            }
        }
        else {
            if (QRCodeLocations.size() > 0) {
                QRLocationXY QRPoint;
                for (String s : QRCodeLocations) {
                    String[] pieces = s.split(":");
                    int[] pos = SideViewPreview.convertBuildFloor(pieces[0] + ":" + pieces[1] + ":" + pieces[2]);
                    QRPoint = new QRLocationXY(pieces[0] + ":" + pieces[1] + ":" + pieces[2], pos[0], pos[1], Integer.parseInt(pieces[3]));
                    sideViewQRCodeLocations.add(QRPoint);
                }
            }
        }

//        Log.d("SideView QR",sideViewQRCodeLocations.toString());

        drawView2.updateSideViewQR(sideViewQRCodeLocations);
        drawView2.invalidate();
    }

    //Conversion methods
    public int[] ConvertGrid(int position) {
        int xpos = (position % 24) + 1;
        int ypos = position / 24 + 1;
        return new int[]{xpos, ypos};
    }

    int convertToPx(int dp) {
        int answer = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return answer;
    }

    //Logging the positions measured during the trial
    public void setupLog() {
        String time = formatTimeNice();
        String fileName = "sdcard/" + time + " - User Test.txt";//+userName+" - "+surveyName+".txt";
        logFile = new File(fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        appendLog("Header", "test - " + time, false);
//        appendLog("Start","calibrate time",true);


//        appendLog(userName+ "- "+surveyName+"\n"+time);
    }

    public void appendAccelerationsLog(float x, float y, float z) {

        if (logFile.exists()) {
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append("Accelerations," + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z) + "," + String.valueOf(System.currentTimeMillis() - appStartTime));
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendLog(String tag, String t, boolean includeTime) {

        if (logFile.exists()) {
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                if (includeTime) {
                    buf.append(tag + "," + t + "," + String.valueOf((System.currentTimeMillis() - appStartTime)));
                } else {
                    buf.append(tag + "," + t);
                }
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String formatTimeNice() {
        Time time = new Time();
        time.setToNow();
        String full = time.toString();
        String year = full.substring(0, 4);
        String month = getMonthForNumber(full.substring(4, 6));
        String day = full.substring(6, 8);
        String t = full.substring(9, 11) + ":" + full.substring(11, 13);
        return day + " " + month + " " + year + " - " + t;
    }

    String getMonthForNumber(String m) {
        String month = "invalid";
        Integer mInt = Integer.parseInt(m) - 1;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (mInt >= 0 && mInt <= 11) {
            month = months[mInt];
        }
        return month;
    }

    void remove9(){
        drawView2.isNineGone=true;
        drawView.isNineGone=true;

        Assets.playUpdateSound();

        if(QRmap.get("35:4:113").getPoints()==9){
            removePoints("35:4:113");
        }else if(QRmap.get("35:3:132").getPoints()==9){
            removePoints("35:3:132");
        }else if(QRmap.get("35:3:116").getPoints()==9){
            removePoints("35:3:116");
        }

        updateOccured=true;
        showLocalQRCodes();
        showQRCodes();

        updateOneAlgo(true);

        alertBarOn=0;
        alertBarText.setText("Update! QR code removed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void closeFloor352(){
        //audio notification
        Assets.playUpdateSound();

        for(String s:floor35_2){
            //TODO trying setObstacle method
            if(QRmap.containsKey(s)){
                removePoints(s);
            }
//            if(G.hasVertex(s)){
//                G.removeVertex(s);
//            }
            G.setObstacle(s);
        }

//        G = G.updateConnections();

        System.out.println(G.toString());

        updateOneAlgo(true);
        showLocalQRCodes();
        showQRCodes();
        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor2_35_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 35:2 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void closeFloor353(){
        //audio notification
        Assets.playUpdateSound();

        for(String s:floor35_3){
            if(QRmap.containsKey(s)){
                removePoints(s);
            }
//            if(G.hasVertex(s)){
//                G.removeVertex(s);
//            }
            G.setObstacle(s);

        }

//        G = G.updateConnections();

        updateOneAlgo(true);

        showLocalQRCodes();
        showQRCodes();
        floor3_35=R.drawable.floor3_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor3_35_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 35:3 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    //TODO finish update methods
    void closeFloor372(){
        //audio notification
        Assets.playUpdateSound();

        for(String s:floor37_2){
            G.setObstacle(s);

            if(QRmap.containsKey(s)){
                removePoints(s);
            }
//            if(G.hasVertex(s)){
//                G.removeVertex(s);
//            }
        }

//        G = G.updateConnections();

        updateOneAlgo(true);
        showLocalQRCodes();
        showQRCodes();
        //TODO top down view of closed floor
//        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor2_37_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 37:2 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void closeFloor373(){
        //audio notification
        Assets.playUpdateSound();

        for(String s:floor37_3){
            G.setObstacle(s);

            if(QRmap.containsKey(s)){
                removePoints(s);
            }
//            if(G.hasVertex(s)){
//                G.removeVertex(s);
//            }
        }

//        G = G.updateConnections();

        updateOneAlgo(true);
        showLocalQRCodes();
        showQRCodes();
        //TODO top down view of closed floor
//        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor3_37_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 37:3 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void closeFloor374(){
        //audio notification
        Assets.playUpdateSound();

        for(String s:floor37_4){
            G.setObstacle(s);
            if(QRmap.containsKey(s)){
                removePoints(s);
            }
//            if(G.hasVertex(s)){
//                G.removeVertex(s);
//            }
        }

//        G = G.updateConnections();

        updateOneAlgo(true);
        showLocalQRCodes();
        showQRCodes();
        //TODO top down view of closed floor
//        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor4_37_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 37:4 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void closeFloor375(){
        Log.d("rawr","floor375");
        updateOccured = true;

        //audio notification
        Assets.playUpdateSound();

        for(String s:floor37_5){
            G.setObstacle(s);

            if(QRmap.containsKey(s)){
                removePoints(s);
            }

        }

//        G = G.updateConnections();

        updateOneAlgo(true);
        showLocalQRCodes();
        showQRCodes();
        //TODO top down view of closed floor
//        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.floor4_37_closed);
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Floor 37:4 closed!");
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void moveGoal(){
        Assets.playUpdateSound();

        String newgoal = updateString.split(",")[1];
        this.goal = newgoal;

        updateOneAlgo(true);

        showLocalQRCodes();
        showQRCodes();
        updateMapView();
        updateOccured=true;

        alertBarOn=0;
        alertBarText.setText("Update! Goal now at "+newgoal);
        updateAlertBar();

        if(navMode.equals("adaptive")){
            alertBarTextBelow.setText("see Side View for changes");
        }

        if(navMode.equals("adaptable")){
            adaptableChoiceView(true);
            startDelay = System.currentTimeMillis();
            countdownOn=false;
        }
    }

    void adaptablePathUpdate(int number){
        if(trialHasTime && !isComplete){
        if(!alarmOn) {
            alarmOn = true;
            if(number==4){
                Assets.playGameboySound();
            }
            else {
                Assets.playAlertSound();
            }
                AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
                adb.setCancelable(true);
                adb.setTitle("Route Update");
                adb.setMessage(pathLogic.updateNotification(number));
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appendLog("Update", "Adaptable Rejected", true);
                        //TODO keep current list of goals - override algo method
                        alarmOn =false;
                        rejectTime = System.currentTimeMillis();
                        rejectAdaptable();
                        dialog.cancel();
                    }
                });
                adb.setPositiveButton("New Route", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        confirmAdaptable();
                        alarmOn =false;
                        appendLog("Update", "Adaptable Choice", true);
                        adaptableChoiceView(true);
                    }
                });
                adb.show();
            }
        }
    }

    void adaptivePathUpdate(int number){
        if(trialHasTime && !isComplete){

            if(!alarmOn) {
            alarmOn = true;
            appendLog("Update", "Adaptive", true);
                if(number==4){
                    Assets.playGameboySound();
                }
                else {
                    Assets.playAlertSound();
                }                AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
                adb.setCancelable(true);
                adb.setTitle("Route Update");
                adb.setMessage(pathLogic.updateNotification(number));
                adb.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alarmOn =false;
                        rejectTime = System.currentTimeMillis();
                        dialog.cancel();
                    }
                });
                adb.show();
            }
        }
    }

    //    public void UpdateFromServer() {
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");
//
//        Thread newTimer = makeProgressThread();
//        newTimer.start();
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> newDataList, ParseException e) {
//                ArrayList<GridData> result = new ArrayList<GridData>();
//                RouterObject[] routerResults = new RouterObject[numberOfRoutersUsed];
//                if (e == null) {
//                    int numberOfPoints = newDataList.size();
//                    if (!newDataList.isEmpty()) {
//                        //timerThread.run();
//
//                        //Toast.makeText(getApplicationContext(), "Waiting for server...", Toast.LENGTH_LONG).show();
//                        for (int i = 0; i < numberOfPoints; i++) {
//                            ParseObject obj = newDataList.get(i);
//                            int pos = obj.getInt("position");
//                            String routers = obj.getString("routers");
//                            String build = obj.getString("buildingNumber");
//                            String flo = obj.getString("floorNumber");
//                            String id = obj.getObjectId();
//
//                            Pattern pattern1 = Pattern.compile(Pattern.quote("/"));
//                            String[] splitRaw = pattern1.split(routers);
//
//                            for (int n = 0; n < numberOfRoutersUsed; n++) {
//                                Pattern pattern2 = Pattern.compile(Pattern.quote("="));
//                                String[] eachRouter = pattern2.split(splitRaw[n]);
//                                routerResults[n] = (new RouterObject(eachRouter[0], Integer.parseInt(eachRouter[1])));
//                                //System.out.println("id: "+routerResults[n].printBSSID());
//                            }
//
//                            GridData item = new GridData(routerResults, pos, build, flo, id);
//                            result.add(item);
//
//                        }
//                        dataList = result;
//                        //print statement used to see if loading bar time is sufficient
//                        Log.d("update", "all done");
//                        viewLocalData();
//                    }
//                } else {
//                }
//            }
//        });
//    }
//    public void viewLocalData() {
//        int length = dataList.size();
//        GridData[] displayDataList = new GridData[length];
//        int n = 0;
//
//        for (int i = 0; i < length; i++) {
//            GridData obj = dataList.get(i);
//            if (obj.getFloor().equals(floornumber)) {
//                if (obj.getBuilding().equals(buildingnumber)) {
//                    displayDataList[n] = obj;
//                    n += 1;
//                }
//            }
//        }
//        if (n == 0) {
//            showCustomAlert("No available data for this floor :(");
//            //Toast.makeText(getApplicationContext(), "no data for this floor :(", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            GridData[] sortDisplay = new GridData[n];
//            System.arraycopy(displayDataList, 0, sortDisplay, 0, sortDisplay.length);
//            Arrays.sort(sortDisplay);
//
//            ArrayList<GridData> finalDisplay = new ArrayList<GridData>();
//
//            if (result != null) {
//                lastPrediction = result.getPosition();
//            }
//
//            //re-initialize image mapping to all zeros
//            for (int i = 0; i < numbers.length; i++) {
//                numbers[i] = 0;
//            }
//
//            for (int i = 0; i < sortDisplay.length; i++) {
//                finalDisplay.add(i, sortDisplay[i]);
//                numbers[sortDisplay[i].getPosition()] += 1;
//                updateGrid();
//            }
//
//            datalistView.setAdapter(new ViewDataAdapter(finalDisplay, getApplicationContext()));
//            //Toast.makeText(getApplicationContext(), "data!", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    public void hideLocalData() {
//        //re-initialize image mapping to all zeros
//        for (int i = 0; i < numbers.length; i++) {
//            numbers[i] = -1;
//        }
//        numbers[lastPrediction] = 0;
//        updateGrid();
//    }

//    public void animate35over33() {
//
//        if (floornumber.equals("1")) {
//            newBuildingInt = R.drawable.floor1_35;
//        } else if (floornumber.equals("2")) {
//            newBuildingInt = R.drawable.floor2_35;
//        }
//
//        Animation animateIncomingRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.incoming_walls_right);
//        wallsViewRight.startAnimation(animateIncomingRight);
//
//        Animation animateChangeWalls = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.walls_change);
//        animateChangeWalls.setAnimationListener(AnimationListenerForRightWall);
//        map.startAnimation(animateChangeWalls);
//
//        map.setImageResource(newBuildingInt);
//    }

//    Animation.AnimationListener AnimationListenerForRightWall = new Animation.AnimationListener() {
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            wallsViewRight.setVisibility(View.INVISIBLE);
//            //map.setImageResource(newBuildingInt);
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//            // oTODO Auto-generated method stub
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//            // oTODO Auto-generated method stub
//        }
//    };

//    public void checkForExtremes() {
//        wallsViewRight.setVisibility(View.INVISIBLE);
//
//        if (buildingnumber.equals("33")) {
//            if (floornumber.equals("1")) {
//                Log.d("got here", "check2");
//
//                int[] myArray = new int[]{67, 90, 91, 113, 114, 115};
//                ArrayList<Integer> edgePositions = initArrayList(myArray);
//
//                if (edgePositions.contains(currentPosition)) {
//                    Log.d("got here", "check3");
//
//                    wallsViewRight.setVisibility(View.VISIBLE);
//                } else {
//                    wallsViewRight.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//
//    }

//    public ArrayList<Integer> initArrayList(int[] a) {
//        ArrayList<Integer> list = new ArrayList<Integer>();
//        for (int i : a) {
//            list.add(i);
//        }
//        return list;
//
    //    private void updateGravityVector(SensorEvent event) {
//        float[] values = event.values;
//        float x = values[0];
//        float y = values[1];
//        float z = values[2];
//
//        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
//
//        gravityVector[0] = x / magnitude;
//        gravityVector[1] = y / magnitude;
//        gravityVector[2] = z / magnitude;
//    }

    //    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
//
//        private static final int SWIPE_MIN_DISTANCE = 200;
//        //private static final int SWIPE_MAX_OFF_PATH = 250;
//        private static final int SWIPE_THRESHOLD_VELOCITY = 280;
//
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                               float velocityY) {
//
//            System.out.println(" in onFling() :: ");
//            //if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//            //return false;
//            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Animation animateCoverLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cover_left_animation);
//                animateCoverLeft.setAnimationListener(AnimationLeftListener);
//                SmallCoverButton.startAnimation(animateCoverLeft);
//
//
//            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//
//                hand.setVisibility(View.VISIBLE);
//                Animation animateCoverRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cover_right_animation);
//                animateCoverRight.setAnimationListener(AnimationRightListener);
//                CoverButton.startAnimation(animateCoverRight);
//            }
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//    }
//
//    Animation.AnimationListener AnimationRightListener = new Animation.AnimationListener() {
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            CoverButton.setVisibility(View.GONE);
//            SmallCoverButton.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//    };
//
//    Animation.AnimationListener AnimationLeftListener = new Animation.AnimationListener() {
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            SmallCoverButton.setVisibility(View.GONE);
//            CoverButton.setVisibility(View.VISIBLE);
//            hand.setVisibility(View.GONE);
//
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//            // oTODO Auto-generated method stub
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//            // ToODO Auto-generated method stub
//        }
//    };

//    void navigationUI(){
//        if(adaptableSelected==1 || navMode.equals("adaptive")) {
//            showOptimalPath(sidePath);
//            totalPointsOnRoute = pathLogic.getCollectedPoints();
//            QRCodesRemaining = pathLogic.getQRCollected();
//        }else if(adaptableSelected==2){
//            showOptimalPath(sidePath2);
//            totalPointsOnRoute = pathHigh.getCollectedPoints();
//            QRCodesRemaining = pathHigh.getQRCollected();
//        }else if(adaptableSelected==3){
//            showOptimalPath(sidePath3);
//            totalPointsOnRoute = pathFinder.getCollectedPoints();
//            QRCodesRemaining = pathFinder.getQRCollected();
//        }
//
//        if(QRCodesRemaining<0){
//            QRCodesRemaining=0;
//        }
//
//        pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
//        qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining)+" Points: "+String.valueOf(totalPointsOnRoute));
//
//        choice1Points.setText("Points: "+String.valueOf(pathLogic.getCollectedPoints()));
//        choice1Time.setText("Time: "+String.valueOf(pathLogic.getTimeLength())+" sec");
//        choice2Points.setText("Points: "+String.valueOf(pathHigh.getCollectedPoints()));
//        choice2Time.setText("Time: "+String.valueOf(pathHigh.getTimeLength())+" sec");
//        choice3Points.setText("Points: "+String.valueOf(pathFinder.getCollectedPoints()));
//        choice3Time.setText("Time: "+String.valueOf(pathFinder.getTimeLength())+" sec");
//    }
//
//    class NavigationThread extends AsyncTask<Void,Void,Void> {
//        // Do the long-running work in here
//        protected Void doInBackground(Void... urls) {
//            long startTime = System.currentTimeMillis();
//            Log.d("nav start runNav",String.valueOf(startTime));
//
//            int baseWeight = 10;
//            int stairWeight = 15;
//            int breakTime = 100;
//            int baseWeightAdp1 = 10;
//            int stairWeightAdp1 = 15;
//            int breakTimeAdp1 = 140;
//            int baseWeightAdp2 = 10;
//            int stairWeightAdp2 = 20;
//            int breakTimeAdp2 = 100;
//            int floorMod = baseWeight-10;
//            int stairMod = stairWeight-10;
//            int floorModAdp1 = baseWeightAdp1-10;
//            int stairModAdp1 = stairWeightAdp1-10;
//            int floorModAdp2 = baseWeightAdp2-10;
//            int stairModAdp2 = stairWeightAdp2-10;
//
//            if(trialNumber.equals("11")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 15;
//                breakTimeAdp1 = 140;
//                baseWeightAdp2 = 10;
//                stairWeightAdp2 = 20;
//                breakTimeAdp2 = 100;
//            }
//            else if(trialNumber.equals("2R")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 15;
//                breakTimeAdp1 = 130;
//                baseWeightAdp2 = 20;
//                stairWeightAdp2 = 40;
//                breakTimeAdp2 = 150;
//            }
//            else if(trialNumber.equals("9R")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 20;
//                breakTimeAdp1 = 100;
//                baseWeightAdp2 = 10;
//                stairWeightAdp2 = 50;
//                breakTimeAdp2 = 140;
//            }
//            else if(trialNumber.equals("8R")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 15;
//                breakTimeAdp1 = 140;
//                baseWeightAdp2 = 10;
//                stairWeightAdp2 = 20;
//                breakTimeAdp2 = 140;
//            }
//            else if(trialNumber.equals("5")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 15;
//                breakTimeAdp1 = 140;
//                baseWeightAdp2 = 10;
//                stairWeightAdp2 = 20;
//                breakTimeAdp2 = 140;
//            }
//            else if(trialNumber.equals("8")) {
//                baseWeightAdp1 = 10;
//                stairWeightAdp1 = 15;
//                breakTimeAdp1 = 120;
//                baseWeightAdp2 = 20;
//                stairWeightAdp2 = 40;
//                breakTimeAdp2 = 100;
//            }
//
//            floorModAdp1 = baseWeightAdp1 - 10;
//            stairModAdp1 = stairWeightAdp1 - 10;
//            floorModAdp2 = baseWeightAdp2 - 10;
//            stairModAdp2 = stairWeightAdp2 - 10;
//
//            if(navMode.equals("walk")){
//                //do nothing...
//            }
//
////        else if(navMode.equals("adaptable")){
////            if(goalLogic_previous==null) {
////                //previous list of goals not set yet - save current ones as previous
////
//////                goalLogic = new FloorRankingOrder(G_L, 90);
//////                goalHigh = new HighPointPriority(G_HP);
////            }
////            if(adaptableSelected==1) {
////                if(goalLogic_previous==null) {
////                    //previous list of goals not set yet - save current ones as previous
//////
////// goalLogic = new FloorRankingOrder(G_L, 90);
////
////                }else{
////                    if(goalLogic.goalList.equals(goalLogic_previous.goalList)){
////                        //good - same list of goals
////                    }else{
////                        Assets.playUpdateSound();
////                        adaptablePathUpdate();
////                        goalLogic_previous=goalLogic;
////                    }
////                }
////            }else if(adaptableSelected==2){
////                if(goalHigh_previous==null) {
////                    goalHigh = new HighPointPriority(G_HP);
////                }else{
////                    if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
////                        //good
////                    }else{
////                        Assets.playUpdateSound();
////                        adaptablePathUpdate();
////                        goalHigh_previous=goalHigh;
////                    }
////                }
////            }else if(adaptableSelected==3){
////                //TODO handle third nav option update
////            }
////        }
////        //adaptive mode
////        else if(navMode.equals("adaptive")) {
////            if(goalHigh_previous==null) {
////                goalHigh = new HighPointPriority(G_HP);
////            }else{
////                if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
////                    //good
////                }else{
////                    Assets.playUpdateSound();
////                    goalHigh_previous=goalHigh;
////                }
////            }
////            if (goalLogic_previous == null) {
////
//////                goalLogic = new FloorRankingOrder(G_L, 90);
////            } else {
////                if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
////                    //good
////                } else {
////                    Assets.playUpdateSound();
////                    goalLogic_previous = goalLogic;
////                }
////            }
////        }
//
//            if(!navMode.equals("walk")) {
//
////                pathLogic = new Algo(G,fullPosition,goal,timeLeft,stairMod,floorMod,breakTime);
////                pathLogicAdp1 = new Algo(G,fullPosition, goal, timeLeft, stairModAdp1, floorModAdp1,breakTimeAdp1);
////                pathLogicAdp2 = new Algo(G,fullPosition, goal, timeLeft, stairModAdp2, floorModAdp2,breakTimeAdp2);
//
//                pathLogic.calcOptPath(fullPosition,timeLeft);
//                pathLogicAdp1.calcOptPath(fullPosition,timeLeft);
//                pathLogicAdp2.calcOptPath(fullPosition,timeLeft);
//
//                sidePath = pathLogic.getPath();
//                sidePath2 = pathLogicAdp1.getPath();
//                sidePath3 = pathLogicAdp2.getPath();
//
//                Log.d("Results for Logic:",sidePath.toString());
//                Log.d("Results for Logic:",pathLogic.goalList.toString());
//
//                Log.d("Results for LogicA1:",sidePath2.toString());
//                Log.d("Results for LogicA1:",pathLogicAdp1.goalList.toString());
//
//
//                long finishTime = System.currentTimeMillis();
//                Log.d("nav finish runNav",String.valueOf(finishTime-startTime));
//            }
//            return null;
//        }
//
//        // This is called each time you call publishProgress()
//        protected void onProgressUpdate(Integer... progress) {
//            navigationUI();
//        }
//
//        // This is called when doInBackground() is finished
//        protected void onPostExecute(Long result) {
////            RunMode.this.runOnUiThread(new Runnable() {
////                public void run() {
//                    pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
//                    qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining) + " Points: " + String.valueOf(totalPointsOnRoute));
//
//                    if(currentRunMode.equals(CurrentRunMode.CHOOSE)) {
//                        adaptivePoints.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
//                        adaptiveTime.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
//                        choice1Points.setText("Points: " + String.valueOf(pathLogic.getCollectedPoints()));
//                        choice1Time.setText("Time: " + String.valueOf(pathLogic.getTimeLength()) + " sec");
//                        choice2Points.setText("Points: " + String.valueOf(pathLogicAdp1.getCollectedPoints()));
//                        choice2Time.setText("Time: " + String.valueOf(pathLogicAdp1.getTimeLength()) + " sec");
//                        choice3Points.setText("Points: " + String.valueOf(pathLogicAdp2.getCollectedPoints()));
//                        choice3Time.setText("Time: " + String.valueOf(pathLogicAdp2.getTimeLength()) + " sec");
//                        adaptiveChoiceLayout.invalidate();
//                    }
//
//                    buildSidePath();
//                    showPathHistory();
//                    updateMapView();
//
//
//                    if(adaptableSelected==1 || navMode.equals("adaptive")) {
//                        showOptimalPath(sidePath);
//                        totalPointsOnRoute = pathLogic.getCollectedPoints();
//                        QRCodesRemaining = pathLogic.getQRCollected();
//                    }else if(adaptableSelected==2){
//                        showOptimalPath(sidePath2);
//                        totalPointsOnRoute = pathLogicAdp1.getCollectedPoints();
//                        QRCodesRemaining = pathLogicAdp1.getQRCollected();
//                    }else if(adaptableSelected==3){
//                        showOptimalPath(sidePath3);
//                        totalPointsOnRoute = pathLogicAdp2.getCollectedPoints();
//                        QRCodesRemaining = pathLogicAdp2.getQRCollected();
//                    }                }
////            });
//
////        }
//    }
}
