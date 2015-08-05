package com.example.matthew.newapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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


    //Close off floor 35:2 update
    int UPDATE_CLOSED_TIME = 20*1000;
    boolean updateOccured = false;
    ArrayList<String> floor35_2;
    int floor2_35 = R.drawable.floor2_35;

    //Absolute time and mainThread
    boolean firstQRscan = true;
    String timeFromStart;
    long trialStartTime = 0, appStartTime = 0, startDelay = 0, delayedQRTime = 0, delayedUpdateTime = 0;
    boolean adaptableUpdateChosen = false;
    final long TOTAL_TIME = 180 * 1000;
    final long EXCESS_TIME = 60 * 1000;
    final long REPEAT_TIME = 1200;
    final long CLOCK_TIME = 200;

    Handler timerHandler = new Handler();
    Runnable scanTimerRunnable = new Runnable() {
        //        int seconds, minutes;
//        long millis;
        @Override
        public void run() {
            Log.d("heartbeat","scanTimer");
            if (currentRunMode == CurrentRunMode.SCANNING) {
                Log.d("heartbeat","scanTimer:scanning");
                showQRCodes();
                registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifiObj.reconnect();
                mainWifiObj.startScan();
                startScanTime = System.currentTimeMillis();
                scanOn = true;
//                buildSidePath();
                timerHandler.postDelayed(this, REPEAT_TIME);
                //TODO
//                    if (scanOn) {
//                        unregisterReceiver(wifiReciever);
//                        mainWifiObj.disconnect();
//                        scanOn = false;
//                    }
            }
        }
    };

    boolean countdownOn = false;
    Handler timerHandler2 = new Handler();
    Runnable timerRunnable = new Runnable() {
        int seconds,minutes;
        long millis;

        @Override
        public void run() {
            Log.d("heartbeat","timer");
            if (countdownOn) {
                Log.d("heartbeat","timer:countdownOn");
                millis = System.currentTimeMillis() - trialStartTime - delayedQRTime - delayedUpdateTime;
                if(millis>UPDATE_CLOSED_TIME && !updateOccured){
                    closeFloor();
                }
                if (millis < TOTAL_TIME) {
                    millis = TOTAL_TIME - millis;
                    seconds = (int) (millis / 1000);
                    minutes = seconds / 60;
                    seconds = seconds % 60;
                    timeFromStart = String.format("%d:%02d", minutes, seconds);
                    Log.d("heartbeat","timer:less than total "+timeFromStart);
                    timeRemainingText.setText("Time Remaining: " + timeFromStart);
//                    timerHandler2.postDelayed(this, CLOCK_TIME);
                } else if (millis < (TOTAL_TIME + EXCESS_TIME)) {
                    millis = Math.abs(TOTAL_TIME - millis);
                    seconds = (int) (millis / 1000);
                    minutes = seconds / 60;
                    seconds = seconds % 60;
                    timeFromStart = String.format("%d:%02d", minutes, seconds);
                    Log.d("heartbeat","timer:out of time "+timeFromStart);
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
                    Log.d("heartbeat","timer:finished");
                    onlyShowLayout(finishedLayout);
                    //TODO
//                    if (scanOn) {
//                        unregisterReceiver(wifiReciever);
//                        mainWifiObj.disconnect();
//                        //mainWifiObj.reconnect();
////            unregisterReceiver(wifiReciever);
//                        scanOn = false;
//                    }
//                alertBarText.setText("Time's up!");

                    try {
                        this.finalize();
                    } catch (Throwable e) {
                        e.printStackTrace();
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
    int currentFloorImage = 0;

    //SideViewLayout
    boolean firstSideView = true;
    private DrawingView drawView2;
    List<Point> pathHP = new ArrayList<Point>();
    List<Point> pathL = new ArrayList<Point>();
    List<Point> pathOrig = new ArrayList<Point>();
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

    HighPointPriority goalHigh,goalHigh_previous;
    FloorRankingOrder goalLogic, goalLogic_previous;
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
    TextView finishedButton;

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

    //custom toast display
    Toast toast1;
    boolean toastActivated = false;
    Toast toast;

//    AppendLog updater;
    GridData newestGrid;

    BroadcastReceiver listenForPower;

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

        gridView = (GridView) findViewById(R.id.mapGrid);
        map = (ImageView) findViewById(R.id.wallsView);
        closedFloor = (ImageView) findViewById(R.id.closedFloors);
        //TODO initialize as gone
        closedFloor.setVisibility(View.GONE);
        wallsViewRight = (ImageView) findViewById(R.id.wallsViewRight);
        wallsViewRight.setVisibility(View.GONE);
        drawView = (DrawingView) findViewById(R.id.drawing);

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

        G = Assets.G;
        if(Assets.goal!=null) {
            goal = Assets.goal;
        }
        goal = Assets.goal;
        navMode = Assets.mode;
        scanMode = Assets.scanMode;
        matchingMode = Assets.matchingMode;
        sidePathUpdates = Assets.sideMode;

        floor35_2 = Assets.floor35_2;

        //initialize large data from Assets
        QRmap = Assets.QRMap;
        G = new Graph(Assets.G);
        QRCodeLocations = Assets.QRLocations;
//        predictedPoints = Assets.predictedPoints;
        SideViewPreview.setup();

        sideViewToggleImage = (ImageView) findViewById(R.id.sideViewToggleImage);
        sideViewImage = (ImageView) findViewById(R.id.sideView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.side_view_clear_0_2);
        sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, 100));
        sideViewToggle = (RelativeLayout) findViewById(R.id.sideViewToggle);
        sideViewLayout = (RelativeLayout) findViewById(R.id.sideView_layout);
        alertToggleLayout = (RelativeLayout) findViewById(R.id.alertBarToggleLayout);
        topViewLayout = (RelativeLayout) findViewById(R.id.topView_layout);
        finishedLayout = (RelativeLayout) findViewById(R.id.finishedLayout);
        finishedButton = (TextView) findViewById(R.id.finishedButton);
        finishedLayout.setVisibility(View.GONE);
        alertToggleLayout.setVisibility(View.GONE);

        sideViewLayout.setVisibility(View.GONE);
        sideViewToggleText = (TextView) findViewById(R.id.sideViewToggleText);
        sideViewToggle.setVisibility(View.GONE);

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
                    adaptableChoiceView(false);
                }
            }
        });

        ////////////  Alert Bar
        alertBarToggle = (ImageView) findViewById(R.id.alertBarToggle);
        alertBarText = (TextView) findViewById(R.id.alertBarToggleText);
        alertBarTextBelow = (TextView) findViewById(R.id.alertBarTextBelow);
        alertBarText.setText("Alert Bar");
        alertBarTextBelow.setText("No current alerts");
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

            //if not first QR code scan, add delayed time for trial timer to account for
            if(!firstQRscan){
                delayedQRTime +=(System.currentTimeMillis()-startDelay+500);
            }

            // handle scan result, contents = "33:4:100" text format
            String id = scanResult.getContents();

            //if the scanned QR code text matches a known QR code
            if (QRmap.containsKey(id)) {
                //re-initialize image mapping to all be empty
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = -1;
                }
                //work with saved QRCodeLocation Object from txt file
                QRCodeLocation obj = QRmap.get(id);
                int QRPosition = obj.getPosition();
                numbers[QRPosition] = 0;
                updateGrid(numbers);
                buildingnumber = obj.getBuilding();
                floornumber = obj.getFloor();
                currentPosition = QRPosition;
                fullPosition = obj.getFullPosition();

                //TODO need to remove
//                fullPosition = "35:2:142";
//                buildingnumber="35";
//                floornumber="2";
//                currentPosition=142;


                //add points to number user has collected
                addPointsToCollected(obj.getPoints());
                removePoints(fullPosition);
                newLocation(fullPosition);

                if (firstQRscan) {
                    setupLog();
                    firstQRscan = false;
                    appendLog("Location", scanResult.getContents(), true);
                    start = scanResult.getContents();

                    if (navMode.equals("adaptive")) {
                        currentRunMode = CurrentRunMode.SCANNING;
                        informationDisplay.setVisibility(View.VISIBLE);
                        //make sideView toggle visible after first scan
                        sideViewToggle.setVisibility(View.VISIBLE);
                        alertToggleLayout.setVisibility(View.VISIBLE);

                        //trial starts when first scan complete in adaptive mode!
                        trialStartTime = System.currentTimeMillis();

                        //actually starts trial countdown
                        countdownOn = true;
                    }
                    else if (navMode.equals("adaptable")) {
                        currentRunMode = CurrentRunMode.CHOOSE;
                        adaptableChoiceView(true);
                        //won't set trialStartTime or countDownOn=true until first route choice
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
            }
        }

    }

    //Adaptable mode choices
    public void adaptableChoicesToggle() {
        testNavigationAlg();
        buildSidePath();
        if (adaptableChoicesButtons.isShown()) {
            adaptableChoicesButtons.setVisibility(View.GONE);
            adaptableToggle.setText("Route Options");
        } else {
            adaptableChoicesButtons.setVisibility(View.VISIBLE);
            adaptableToggle.setText("Preview Routes");
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

            resetAdaptableButtons();

            if (adaptableSelected == 1) {
                adaptableChoice1.setBackgroundResource(R.drawable.round_button_green_outlined);
            } else if (adaptableSelected == 2) {
                adaptableChoice2.setBackgroundResource(R.drawable.round_button_purple_outlined);
            } else if (adaptableSelected == 3) {
                adaptableChoice3.setBackgroundResource(R.drawable.round_button_blue_outlined);
            }

//                    timerHandler.postDelayed(scanTimerRunnable, 0);
            adaptableChoices.setVisibility(View.VISIBLE);
            adaptableChoicesButtons.setVisibility(View.VISIBLE);

//            Thread navAlg = tonyNavigation();
//            navAlg.run();

//            TODO handled by tonyNavigation
            testNavigationAlg();
            buildSidePath();

//            alertToggleLayout.setVisibility(View.GONE);
            topViewLayout.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.VISIBLE);
//                    bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
//                    sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewToggle.setVisibility(View.GONE);
            sideViewOn = true;
            QRScan.setClickable(false);
        }
        else {
            if (firstRouteChoice) {
                alertToggleLayout.setVisibility(View.GONE);
                firstRouteChoice = false;
                trialStartTime = System.currentTimeMillis();
            }
            if(updateOccured && !adaptableUpdateChosen){
                adaptableUpdateChosen=true;
                delayedUpdateTime = System.currentTimeMillis() - startDelay;

            }

            // needs to be turned on after first route choice and forced updated route choice
            countdownOn = true;

            currentRunMode = CurrentRunMode.SCANNING;

            buildSidePath();

            informationDisplay.setVisibility(View.VISIBLE);
            sideViewToggle.setVisibility(View.VISIBLE);
            alertToggleLayout.setVisibility(View.VISIBLE);
            adaptableChoices.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.GONE);
            sideViewOn = false;
            appendLog("Layout", "topView", true);

            topViewLayout.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewToggle.setVisibility(View.VISIBLE);
            QRScan.setClickable(true);


            //TODO
            timerHandler.postDelayed(scanTimerRunnable, REPEAT_TIME);
            timerHandler2.postDelayed(timerRunnable, CLOCK_TIME);

        }

    }

    //Called each time the device completes WiFi scan
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            wifiScanList = mainWifiObj.getScanResults();

            timeForScan = System.currentTimeMillis() - startScanTime;
            Log.d("Scan Time", String.valueOf(timeForScan) + " milliseconds *********************8");
            Log.d("Scan", String.valueOf(wifiScanList.size()) + " routers found");

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
                    Log.d("scan mode", "double");
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
                Log.d("scan mode", "single");

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
        if(s.equals(goal)){
            //goal reached, not sure what to do now -> don't run nav alg
            fullPosition = full;
            //update the path history by adding this position if not same as previous
            addToPathHistory(fullPosition);
            showPathHistory();
            updateMapView();
            //TODO update app to know that the trial is complete...
            onlyShowLayout(finishedLayout);
        }
        else {
            fullPosition = full;
            //update the path history by adding this position if not same as previous
            addToPathHistory(fullPosition);
            testNavigationAlg();
            showPathHistory();
            updateMapView();
        }
    }

    public void testNavigationAlg() {

        //clone the master Graph into one for each nav method
        G_HP = new Graph(G);
        G_L = new Graph(G);
        G_orig = new Graph(G);

//        TODO not permanent
//        goal = "37:2:106";
//        fullPosition = "35:2:142";
        start = fullPosition;

        //set goal location first, then mark neighbors
        G_orig.setStartLocation(start);
        G_orig.setGoalLocation(goal);
        G_orig.markNeighbors(goal, G_orig);

        ArrayList<String> originalGoal = new ArrayList<String>();
        originalGoal.add(goal);
        Algo pathFinder = new Algo(G_orig,start, goal, originalGoal);
        pathFinder.calcOptPath(start);
//        Log.d("Results for Original:",pathFinder.printPath().toString());

//        Log.d("Nav set start", fullPosition);
//        Log.d("Nav set goal", goal);
//        Log.d("debug",G.adjacentTo(fullPosition).toString());

        G_HP.setStartLocation(fullPosition);
        G_HP.setGoalLocation(goal);
        G_HP.markNeighbors(goal, G_HP);

        G_L.setStartLocation(fullPosition);
        G_L.setGoalLocation(goal);
        G_L.markNeighbors(goal, G_L);


        if(navMode.equals("adaptable")){
            if(goalLogic_previous==null) {
                //previous list of goals not set yet - save current ones as previous
                goalLogic = new FloorRankingOrder(G_L, 2000);
                goalHigh = new HighPointPriority(G_HP);
            }
            if(adaptableSelected==1) {
                if(goalLogic_previous==null) {
                    //previous list of goals not set yet - save current ones as previous
                    goalLogic = new FloorRankingOrder(G_L, 2000);

                }else{
                    if(goalLogic.goalList.equals(goalLogic_previous.goalList)){
                        //good - same list of goals
                    }else{
                        Assets.playUpdateSound();
                        adaptablePathUpdate();
                        goalLogic_previous=goalLogic;
                    }
                }
            }else if(adaptableSelected==2){
                if(goalHigh_previous==null) {
                    goalHigh = new HighPointPriority(G_HP);
                }else{
                    if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
                        //good
                    }else{
                        Assets.playUpdateSound();
                        adaptablePathUpdate();
                        goalHigh_previous=goalHigh;
                    }
                }
            }else if(adaptableSelected==3){
                //TODO handle third nav option update
            }
        }
        //adaptive mode
        else {
            if(goalHigh_previous==null) {
                goalHigh = new HighPointPriority(G_HP);
            }else{
                if(goalHigh.goalPoint.equals(goalHigh_previous.goalPoint)){
                    //good
                }else{
                    Assets.playUpdateSound();
                    goalHigh_previous=goalHigh;
                }
            }
            if (goalLogic_previous == null) {
                goalLogic = new FloorRankingOrder(G_L, 2000);
            } else {
                if (goalLogic.goalList.equals(goalLogic_previous.goalList)) {
                    //good
                } else {
                    Assets.playUpdateSound();
                    goalLogic_previous = goalLogic;
                }
            }
        }

        Algo pathHigh = new Algo(G_HP, fullPosition, goal, goalHigh.goalPoint);
        pathHigh.calcOptPath(fullPosition);
        //        Log.d("nav HighPoint:", pathHigh.printPath().toString());

        Algo pathLogic = new Algo(G_L, fullPosition, goal, goalLogic.goalList);
        pathLogic.calcOptPath(fullPosition);
//        Log.d("nav Logic:", pathLogic.printPath().toString());


        //TODO print path
//        predictedPoints = String.valueOf(pathLogic.getCollectedPoints());
//        ArrayList<String> path3 = pathLogic.printPath();

        sidePath = pathLogic.printPath();
        sidePath2 = pathHigh.printPath();
        sidePath3 = pathFinder.printPath();

        if(adaptableSelected==1 || navMode.equals("adaptive")) {
            showOptimalPath(sidePath);
            totalPointsOnRoute = pathLogic.getCollectedPoints();
            QRCodesRemaining = pathLogic.getQRCollected();
        }else if(adaptableSelected==2){
            showOptimalPath(sidePath2);
            totalPointsOnRoute = pathHigh.getCollectedPoints();
            QRCodesRemaining = pathHigh.getQRCollected();
        }else if(adaptableSelected==3){
            showOptimalPath(sidePath3);
            totalPointsOnRoute = pathFinder.getCollectedPoints();
            QRCodesRemaining = pathFinder.getQRCollected();
        }

        pointsCollectedText.setText("Points Collected: " + String.valueOf(pointsCollected));
        qrCodesRemainingText.setText("Remaining QR Codes: " + String.valueOf(QRCodesRemaining)+" Points: "+String.valueOf(totalPointsOnRoute));

        choice1Points.setText("Points: "+String.valueOf(pathLogic.getCollectedPoints()));
        choice1Time.setText("Time: "+String.valueOf(pathLogic.getTimeLength())+" sec");
        choice2Points.setText("Points: "+String.valueOf(pathHigh.getCollectedPoints()));
        choice2Time.setText("Time: "+String.valueOf(pathHigh.getTimeLength())+" sec");
        choice3Points.setText("Points: "+String.valueOf(pathFinder.getCollectedPoints()));
        choice3Time.setText("Time: "+String.valueOf(pathFinder.getTimeLength())+" sec");

        buildSidePath();
    }

    public void showOptimalPath(ArrayList<String> p) {

        localFuturePath.clear();
        String transition = null;
        boolean before = true;
        boolean foundPath = false;
        boolean breakInPath = false;

        if (p.size() > 0) {
            Log.d("transition here", "p larger than 0");

            Point point;
            int[] pos;

            for (int i = 0; i < p.size(); i++) {
                //if still before the local path in the list or iterating through the local path:
                if (before || foundPath && !breakInPath) {
                    String s = p.get(i);
                    String[] pieces = s.split(":");
                    if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber)) {
                        before = false;
                        foundPath = true;
                        Log.d("transition here", "found on this floor");
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
                                    Log.d("transition", "up at " + s);
                                } else if (Integer.parseInt(pieces[1]) > nextFloor) {
                                    transition = "down";
                                    Log.d("transition", "down at " + s);
                                }
                            } else if (connections.contains(s) && !pieces[0].equals(p.get(i + 1).split(":")[0])) {
                                breakInPath = true;
                                String nextBuilding = p.get(i + 1).split(":")[0];
                                if (pieces[0].equals("33") && nextBuilding.equals("35")) {
                                    transition = "right";
                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("37")) {
                                    transition = "right";
                                    Log.d("transition", "right");
                                } else if (pieces[0].equals("37") && nextBuilding.equals("35")) {
                                    transition = "left";
                                    Log.d("transition", "left");
                                } else if (pieces[0].equals("35") && nextBuilding.equals("33")) {
                                    transition = "down";
                                    Log.d("transition", "down");
                                }
                            }
                        } else {
                            breakInPath = true;
                            transition = "end of path";
                        }
                    } else {
                        transition = "not on this floor";
                        Log.d("transition here", "not on this floor");

                    }
                }
            }
        } else {
            transition = "empty list";
        }
        Log.d("transition here", transition);
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

            for (String s : QRCodeLocations) {
                String[] pieces = s.split(":");
                if (pieces[0].equals(buildingnumber) && pieces[1].equals(floornumber) && Integer.parseInt(pieces[3]) > 0) {
                    pos = ConvertGrid(Integer.parseInt(pieces[2]));
                    full = pieces[0]+":"+pieces[1]+":"+pieces[2];
                    QRPoint = new QRLocationXY(full,x0 + dx * (pos[0] - 1), (y0 + dy * (pos[1] - 1)), Integer.parseInt(pieces[3]));
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

            pointsCollected += QRmap.get(full).getPoints();
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
                            Log.d("progress tag", String.valueOf(waited));
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

//    public Thread tonyNavigation() {
//        Thread timerThread = new Thread() {
//            @Override
//            public void run() {
//                testNavigationAlg();
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            buildSidePath();
//                        }
//                    });
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
        } else {
            String previous = pathHistory.get(pathHistory.size() - 1);
            if (!full.equals(previous)) {
                pathHistory.add(full);
                appendLog("Location", full, true);
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

        if (sideViewOn == false) {
            appendLog("Layout", "sideView", true);
            topViewLayout.setVisibility(View.GONE);
            sideViewLayout.setVisibility(View.VISIBLE);
            testNavigationAlg();
            buildSidePath();
            bitmap = BitmapFactory.decodeResource(getResources(), currentFloorImage);
            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, convertToPx(10)));
            sideViewOn = true;
            QRScan.setClickable(false);
            sideViewToggleText.setText("Top View");
        } else if (sideViewOn) {
            appendLog("Layout", "topView", true);
            topViewLayout.setVisibility(View.VISIBLE);
            sideViewLayout.setVisibility(View.GONE);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.side_view_clear_0_2);
            sideViewToggleImage.setImageBitmap(Assets.getRoundedCornerBitmap(bitmap, 100));
            sideViewOn = false;
            QRScan.setClickable(true);
            sideViewToggleText.setText("Side View");
        }
//        buildSidePath();

    }

    public void buildSidePath() {
        //currently using High Point for the adaptive case - final decision from Tony
        //not testing side path static vs dynamic in adaptive mode
        if (navMode.equals("adaptive")) {
            pathL.clear();
            if (sidePath.size() != 0) {

                int[] pos;
                Point firstPoint,currentPoint;
                firstPoint=new Point(0,0);

                //start with path History if exists
                if(pathHistory.size()>0) {
                    //save route starting point as firstPoint
                    pos = SideViewPreview.convertBuildFloor(pathHistory.get(0));
                    updatePathSideView(1, pos[0], pos[1]);
                    firstPoint=new Point(pos[0],pos[1]);

                    for (int i = 1; i < pathHistory.size(); i++) {
                        pos = SideViewPreview.convertBuildFloor(pathHistory.get(i));
                        updatePathSideView(1, pos[0], pos[1]);
                    }
                }

                //////////////////// sideaPath1
                String first = sidePath.get(0);
                pos = SideViewPreview.convertBuildFloor(first);
                updatePathSideView(1, pos[0], pos[1]);
                currentPoint=new Point(pos[0],pos[1]);

                for (int i = 1; i < sidePath.size() - 1; i++) {
                    pos = SideViewPreview.convertBuildFloor(sidePath.get(i));
                    updatePathSideView(1, pos[0], pos[1]);
                }

                //if just starting trial and pathHistory is empty
                if(pathHistory.size()==0){
                    //use currentPoint as start
                    firstPoint=currentPoint;
                }

                String last = sidePath.get(sidePath.size() - 1);
                int[] lastCoords = SideViewPreview.convertBuildFloor(last);
                updatePathSideView(1, lastCoords[0], lastCoords[1]);

                drawView2.updateSidePath(firstPoint,currentPoint,pathL);
                drawView2.invalidate();
                showQRCodes();
            } else {
                points.setText("Error in loading path");
            }
        }

        //using adaptive mode will use all 3 side paths
        else if (navMode.equals("adaptable")) {
            //TODO manual assign
            sidePathUpdates = sidePathMode.DYNAMIC;

            //only update sidePath once at beginning - not dynamic
//            if (sidePathUpdates == sidePathMode.STATIC) {
//                if (firstSideView) {
//                    firstSideView = false;
//                    pathHP.clear();
//                    pathL.clear();
//                    pathOrig.clear();
//                    if (sidePath.size() != 0 && sidePath2.size() != 0) {
//
//                        //////////////////// High Point Path
//
//                        String first = sidePath.get(0);
//                        int[] pos = SideViewPreview.convertBuildFloor(first);
//                        updatePathSideView(1, pos[0], pos[1]);
//
//                        for (int i = 1; i < sidePath.size() - 1; i++) {
//                            pos = SideViewPreview.convertBuildFloor(sidePath.get(i));
//                            updatePathSideView(1, pos[0] + 2, pos[1] + 2);
//                        }
//
//                        String last = sidePath.get(sidePath.size() - 1);
//                        int[] lastCoords = SideViewPreview.convertBuildFloor(last);
//                        updatePathSideView(1, lastCoords[0], lastCoords[1]);
//
//                        /////////////////// Logic path
//
//                        first = sidePath2.get(0);
//                        pos = SideViewPreview.convertBuildFloor(first);
//                        updatePathSideView(2, pos[0], pos[1]);
//
//                        for (int i = 1; i < sidePath2.size() - 1; i++) {
//                            pos = SideViewPreview.convertBuildFloor(sidePath2.get(i));
//                            updatePathSideView(2, pos[0] - 2, pos[1] - 2);
//                        }
//
//                        last = sidePath2.get(sidePath2.size() - 1);
//                        lastCoords = SideViewPreview.convertBuildFloor(last);
//                        updatePathSideView(2, lastCoords[0], lastCoords[1]);
//
//                        points.setText("Possible Points: " + predictedPoints);
////            numberQRs.setText("Number of QRs: " + predictedQR);
//
//                        drawView2.updateSidePath(pathHP, pathL,pathOrig);
//                        drawView2.invalidate();
//                        showQRCodes();
//                    } else {
//                        points.setText("Error in loading path");
//                    }
//                } else if (currentRunMode == CurrentRunMode.SCANNING) {
//                    //normal run mode = only show currently selected route
//                    if (adaptableSelected == 1) {
//                        drawView2.updateSidePath(pathHP);
//                        drawView2.invalidate();
//                        showQRCodes();
//                    } else if (adaptableSelected == 2) {
//                        drawView2.updateSidePath(pathL);
//                        drawView2.invalidate();
//                        showQRCodes();
//                    } else if (adaptableSelected == 3) {
//                        drawView2.updateSidePath(pathOrig);
//                        drawView2.invalidate();
//                        showQRCodes();
//                    }
//
//                } else {
//                    drawView2.updateSidePath(pathHP, pathL,pathOrig);
//                    drawView2.invalidate();
//                    showQRCodes();
//                }
//            }

            //update sidePath each time as user moves - dynamic
//            else if (sidePathUpdates == sidePathMode.DYNAMIC) {
                pathHP.clear();
                pathL.clear();
                pathOrig.clear();

            int[] pos;
            Point firstPoint,currentPoint;
            firstPoint=new Point(0,0);

            //start with path History if exists
            if(pathHistory.size()>0) {
                //save route starting point as firstPoint
                pos = SideViewPreview.convertBuildFloor(pathHistory.get(0));
                updatePathSideView(1, pos[0], pos[1]);
                updatePathSideView(2, pos[0], pos[1]);
                updatePathSideView(3, pos[0], pos[1]);
                firstPoint=new Point(pos[0],pos[1]);

                for (int i = 1; i < pathHistory.size(); i++) {
                    pos = SideViewPreview.convertBuildFloor(pathHistory.get(i));
                    updatePathSideView(1, pos[0], pos[1]);
                    updatePathSideView(2, pos[0], pos[1]);
                    updatePathSideView(3, pos[0], pos[1]);
                }
            }

                if (sidePath.size() != 0 && sidePath2.size() != 0) {

                    Log.d("buildsidepath","got here 1");

                    //////////////////// Logic Path

                    String first = sidePath.get(0);
                    pos = SideViewPreview.convertBuildFloor(first);
                    updatePathSideView(1, pos[0], pos[1]);
                    currentPoint=new Point(pos[0],pos[1]);

                    if(pathHistory.size()==0){
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
                        Log.d("buildsidepath","got here 2");
                        drawView2.updateSidePath(pathL, pathHP,pathOrig);
                        drawView2.invalidate();
                        showQRCodes();
                    } else if (currentRunMode == CurrentRunMode.SCANNING) {
                        //normal run mode = only show currently selected route
                        if (adaptableSelected == 1) {
                            Log.d("buildsidepath","hp 3");
                            drawView2.updateSidePath(firstPoint,currentPoint,pathHP);
                            drawView2.invalidate();
                            showQRCodes();
                        } else if (adaptableSelected == 2) {
                            drawView2.updateSidePath(firstPoint,currentPoint,pathL);
                            drawView2.invalidate();
                            showQRCodes();
                        } else if (adaptableSelected == 3) {
                            drawView2.updateSidePath(firstPoint,currentPoint,pathOrig);
                            drawView2.invalidate();
                            showQRCodes();
                        } else {
                            drawView2.updateSidePath(pathL, pathHP,pathOrig);
                            drawView2.invalidate();
                            showQRCodes();
                        }
                    }
                } else {
                    points.setText("Error in loading path");
                }
//            }
        }
    }

    public void updatePathSideView(int n, int x, int y) {
        Point point = new Point(x, y);
        if (n == 1) {
            pathL.add(point);
        } else if (n == 2) {
            pathHP.add(point);
        } else if (n==3) {
            pathOrig.add(point);
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

        if (QRCodeLocations.size() > 0) {
            QRLocationXY QRPoint;
            for (String s : QRCodeLocations) {
                String[] pieces = s.split(":");
                int[] pos = SideViewPreview.convertBuildFloor(pieces[0] + ":" + pieces[1] + ":" + pieces[2]);
                QRPoint = new QRLocationXY(pieces[0] + ":" + pieces[1] + ":" + pieces[2],pos[0], pos[1], Integer.parseInt(pieces[3]));
                sideViewQRCodeLocations.add(QRPoint);
            }
        }

        Log.d("SideView QR",sideViewQRCodeLocations.toString());

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

    void closeFloor(){

        //audio notification
//        Assets.playUpdateSound();
        Assets.playAlertSound();

        for(String s:floor35_2){
            if(QRmap.containsKey(s)){
                removePoints(s);
                Log.d("23 remove points",s);
                Log.d("23 new points",String.valueOf(QRmap.get(s).getPoints()));
                for(String s2:QRCodeLocations){
                    String[] split = s2.split(":");
                    String match = (split[0]+":"+split[1]+":"+split[2]);
                    Log.d("23 new new points",match);
                    if(match.equals(s)){
                        Log.d("23 match points",split[3]);
                    }
                }
            }
            if(G.hasVertex(s)){
                G.removeVertex(s);
            }
        }

        G = G.updateConnections();

//        tonyNavigation().start();
        testNavigationAlg();
        showLocalQRCodes();
        showQRCodes();
        floor2_35=R.drawable.floor2_35_grayed_out;
        sideViewImage.setImageResource(R.drawable.side_view_clear_floor35_2);
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

    void adaptablePathUpdate(){
        AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
        adb.setCancelable(true);
        adb.setTitle("Route Update");
        adb.setMessage("You left the given route, do you wish to choose a new shorter one?");
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        adb.setPositiveButton("New Route", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {adaptableChoiceView(true);}
        });
        adb.show();
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
}