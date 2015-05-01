package com.example.matthew.newapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
//import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//import android.graphics.Path;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class RunMode extends Activity implements SensorEventListener {
    private ImageView map, compassBeatImage;
    private String buildingnumber, floornumber;
    private Button scanAgainButton, updateButton, viewDataButton, testAlg, QRScan;
    private RelativeLayout CoverButton, SmallCoverButton, hand;
    private SensorManager sensorManager;
    private boolean color = false;
    private TextView textAccel, headingText;
    private long lastUpdate;
    private Context ctx;
    int lastPrediction;
    GridData result;
    int[] mapsize = new int[240];

    private int scanCount = 0;
    private DrawingView drawView;
    Paint paint;

    List<Point> points33_0 = new ArrayList<Point>();
    List<Point> points33_1 = new ArrayList<Point>();
    List<Point> points33_2 = new ArrayList<Point>();
    List<Point> points33_3 = new ArrayList<Point>();
    List<Point> points33_4 = new ArrayList<Point>();
    List<Point> points33_5 = new ArrayList<Point>();
    List<Point> points35_0 = new ArrayList<Point>();
    List<Point> points35_1 = new ArrayList<Point>();
    List<Point> points35_2 = new ArrayList<Point>();
    List<Point> points35_3 = new ArrayList<Point>();
    List<Point> points35_4 = new ArrayList<Point>();
    List<Point> points35_5 = new ArrayList<Point>();

    boolean toastActivated = false;
    boolean scanOn;
    boolean viewingLocalData;
    Toast toast;


    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    TextView scanTitle, localPoints, maxAccel;
    ListView scanlist;
    LinearLayout localDataLayout;
    boolean localDataVisible;
    ListView datalistView;
    RouterObject wifis[];
    int[] coords = new int[]{0, 0};
    private int currentPosition = 0;
    int numberOfRoutersSaved, numberOfRoutersUsed;
    RouterObject[] mostRecentScan = null;
    RouterObject[] olderScan = null;
    RouterObject[] averageOfRecentScans = null;

    long startScanTime, timeForScan, startAlgTime, timeForAlg, startServerTime, timeForServer = 0l;

    GridView gridView;
    CustomGridAdapter gridAdapter;
    int[] numbers = new int[240];

    ArrayList<GridData> dataList = new ArrayList<GridData>();
    ArrayList<GridData> strongerDataList = new ArrayList<GridData>();


    ParseObject gridDataOnline;
    ArrayList<String> dataStringList = new ArrayList<String>();

    int TIMER_RUNTIME = 6400; // in ms --> 6seconds
    boolean mbActive;
    ProgressBar mProgressBar;
    RelativeLayout progressLayout;
    TextView progressBarText, locationText;
    ImageView truckImage, cloudImage, buildImage, wallsViewRight;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    //Animation.AnimationListener AnimationRightListener;
    int newBuildingInt;
    int previousPosition = 0;
    String previousBuilding = "0";
    String previousFloor = "0";
    String currentNode = "";

    int ACCELEROMETER_WAIT_TIME;
    float recentChangesAccelerometer = 0;
    float previousChangesAccelerometer = 0;
    float changesAccelerometer = 0;
    boolean accelerationZ;
    float[] gravityVector = new float[3];
    long lastSensorUpdate = 0l;

    private In in;
    private Graph G;


    //QRCodeLocation firstQR = new QRCodeLocation("001","33","1",129);
    //QRCodeLocation secondQR = new QRCodeLocation("002","33","1",110);

    Map<String, QRCodeLocation> QRmap = new HashMap<String, QRCodeLocation>();
    //ArrayList<QRCodeLocation> QRCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_mode);
        gridView = (GridView) findViewById(R.id.mapGrid);
        map = (ImageView) findViewById(R.id.wallsView);
        wallsViewRight = (ImageView) findViewById(R.id.wallsViewRight);
        wallsViewRight.setVisibility(View.INVISIBLE);
        compassBeatImage = (ImageView) findViewById(R.id.compass_beating);
        drawView = (DrawingView) findViewById(R.id.drawing);


        scanOn = false;
        viewingLocalData = false;
//
//        In in = new In("NodeMap");
//        Graph G = new Graph(in, ",",getResources().getAssets());

        //QRmap.put("001",firstQR);
        //QRmap.put("002",secondQR);

        try {
            in = new In("NodeMap");
            G = new Graph(in, ",",getResources().getAssets().open("NodeMap"));

            AssetManager assetManager = getResources().getAssets();
            InputStream is = assetManager.open("QRCodeList");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    //likes_food_list.add(line);
                    Pattern pattern1 = Pattern.compile(Pattern.quote("/"));
                    String[] splitRaw = pattern1.split(line);

                    int lengthOfTextLine = splitRaw.length;
                    if (line.equals("ID/building/floor/position")) {
                        //nothing
                    } else if (lengthOfTextLine != 4) {
                        Log.d("error", "mistake in txt file");
                    } else {
                        String id = splitRaw[0];
                        String building = splitRaw[1];
                        String floor = splitRaw[2];
                        int positionFromQR = Integer.parseInt(splitRaw[3]);
                        QRmap.put(id, new QRCodeLocation(id, building, floor, positionFromQR));

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("graph",G.toString());

        ctx = this;
        QRScan = (Button) findViewById(R.id.qr_scan_button);
        scanAgainButton = (Button) findViewById(R.id.button_next_floor);
        viewDataButton = (Button) findViewById(R.id.button_viewdata);
        CoverButton = (RelativeLayout) findViewById(R.id.cover_up);
        SmallCoverButton = (RelativeLayout) findViewById(R.id.small_cover_up);
        hand = (RelativeLayout) findViewById(R.id.right_hand);
        hand.setVisibility(View.GONE);

        //headingText = (TextView) findViewById(R.id.heading_text);
        localPoints = (TextView) findViewById(R.id.textLocal);

        localDataLayout = (LinearLayout) findViewById(R.id.local_data_layout);

        localDataLayout.setVisibility(View.GONE);
        localDataVisible = true;

        ACCELEROMETER_WAIT_TIME = 1000;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        locationText = (TextView) findViewById(R.id.location_text);
        scanlist = (ListView) findViewById(R.id.listView1);
        scanTitle = (TextView) findViewById(R.id.scanTextView);
        datalistView = (ListView) findViewById(R.id.data_listView);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "rawr");

        //progress bar class creation
        progressLayout = (RelativeLayout) findViewById(R.id.progressBar_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarText = (TextView) findViewById(R.id.progressBar_text);
        truckImage = (ImageView) findViewById(R.id.truck_image);
        buildImage = (ImageView) findViewById(R.id.build_image);
        cloudImage = (ImageView) findViewById(R.id.parse_image);

        Typeface font = Typeface.createFromAsset(getAssets(), "myriad_pro.ttf");
        progressBarText.setTypeface(font);
        QRScan.setTypeface(font);

        numberOfRoutersSaved = 9;
        numberOfRoutersUsed = 9;

        if (mainWifiObj.isWifiEnabled()) {

            Parse.initialize(this, "9IFIo0LdgpyPESCd8eNaCrdiFjAM61Jz3B9EvbYo", "BGwRcE1fJtEwvpmuEn7n4WsL3P0HIqK242MdpEIu");
            PullFromServer();

            //initialize floornumber and buildingnumber
            buildingnumber = "0";
            floornumber = "0";
            numbers = updateMapView();
            updateGrid();

            gridView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
                    adb.setCancelable(true);
                    adb.setTitle("QR code scanner");
                    //adb.setMessage("Save your current wifi data as this place on the map?");
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked...nothing happens
                            //numbers[position] = originalValue;
                            //updateGrid();
                            dialog.cancel();
                        }
                    });

                    adb.setPositiveButton("Scan!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            IntentIntegrator integrator = new IntentIntegrator(RunMode.this);
                            integrator.initiateScan();
                        }
                    });

                    adb.show();
                    return true;
                }
            });


//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            public boolean onItemLongClick(AdapterView<?> parent, View v,
//                                    final int position, long id) {
//                AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
//                adb.setCancelable(true);
//                adb.setTitle("QR code scanner");
//                //adb.setMessage("Save your current wifi data as this place on the map?");
//                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // if this button is clicked...nothing happens
//                        //numbers[position] = originalValue;
//                        //updateGrid();
//                        dialog.cancel();
//                    }
//                });
//
//
//                adb.setPositiveButton("Scan here", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        IntentIntegrator integrator = new IntentIntegrator(RunMode.this);
//                        integrator.initiateScan();
//                    }
//                });
//
//                adb.show();
//                return true;
//            }
//        });

            lastUpdate = System.currentTimeMillis();

            textAccel = (TextView) findViewById(R.id.textView);
            textAccel.setBackgroundColor(Color.GREEN);

            scanAgainButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    if (scanOn) {
                        unregisterReceiver(wifiReciever);
                        scanOn = false;
                        showCustomAlert("Scanning Off");
                    } else if (!scanOn) {
                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        mainWifiObj.startScan();
                        scanOn = true;
                        showCustomAlert("Scanning On");
                    }
//                //toast.show();
//
//                int buildingInt = Integer.parseInt(buildingnumber);
//                int floorInt = Integer.parseInt(floornumber);
//                if (floorInt < 5) {
//                    floorInt += 1;
//                } else if (floorInt == 5) {
//                    floorInt = 0;
//                    if (buildingInt == 35) {
//                        buildingInt = 33;
//                    } else if (buildingInt == 33) {
//                        buildingInt = 35;
//                    }
//                }
//                floornumber = String.valueOf(floorInt);
//                buildingnumber = String.valueOf(buildingInt);
//                numbers = updateMapView();
//
                }
            });

            final GestureDetector gestureDetector;
            gestureDetector = new GestureDetector(new MyGestureDetector());

            CoverButton.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hand.setVisibility(View.VISIBLE);
                    if (gestureDetector.onTouchEvent(event)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            SmallCoverButton.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });

            viewDataButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    viewLocalData();
                    if (localDataVisible) {
                        unregisterReceiver(wifiReciever);
                        scanOn = false;
                        localDataLayout.setVisibility(View.VISIBLE);
                        localDataVisible = false;
                    } else if (!localDataVisible) {
                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        mainWifiObj.startScan();
                        scanOn = true;
                        localDataLayout.setVisibility(View.GONE);
                        localDataVisible = true;
                        hideLocalData();
                    }

                }
            });
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
            QRScan.setOnClickListener(new View.OnClickListener() {


                public void onClick(View arg0) {

                    updatePathView(50);
                    updatePathView(40);

                    AlertDialog.Builder adb = new AlertDialog.Builder(RunMode.this);
                    adb.setCancelable(true);
                    adb.setTitle("QR code scanner");
                    //adb.setMessage("Save your current wifi data as this place on the map?");
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    adb.setPositiveButton("Scan!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            IntentIntegrator integrator = new IntentIntegrator(RunMode.this);
                            integrator.initiateScan();
                        }
                    });

                    adb.show();
//                    return true;
                }
//                    IntentIntegrator integrator = new IntentIntegrator(RunMode.this);
//                    integrator.initiateScan();
//
////                    animate35over33();
//
//                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d("request code:" + String.valueOf(requestCode), "result code:" + String.valueOf(resultCode));
            Log.d("scan result: ", scanResult.getContents());
            showCustomAlert("You scanned: " + scanResult.getContents());
            // handle scan result
            String id = scanResult.getContents();
            if (QRmap.containsKey(id)) {
                Log.d("QRcode", String.valueOf(Integer.parseInt(scanResult.getContents())));

                //re-initialize image mapping to all be empty
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = -1;
                }

                QRCodeLocation obj = QRmap.get(id);
                int QRPosition = obj.getPosition();
                numbers[QRPosition] = 0;
                updateGrid();
                buildingnumber = obj.getBuilding();
                floornumber = obj.getFloor();
                currentPosition = QRPosition;
                updateMapView();
            }

            //continue normal operations after the QR Code Scan
            if (scanOn) {
                mainWifiObj.startScan();
            } else {
                registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifiObj.startScan();
                scanOn = true;
            }
        }
        // else continue with any other code you need in the method

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);

        // for the system's orientation sensor registered listeners
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                SensorManager.SENSOR_DELAY_GAME);

//        if(!scanOn) {
//            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//            mainWifiObj.startScan();
//            scanOn=true;
//            showCustomAlert("Scanning On");
//        }

        if (!mainWifiObj.isWifiEnabled()) {
            showCustomAlert("Ah! you need the wifis!");
//            Toast.makeText(getApplicationContext(),"Ah! you need the wifis!",Toast.LENGTH_LONG).show();

        }
        //mainWifiObj.startScan();
    }

    @Override
    protected void onPause() {
        // unregister listener
        //unregisterReceiver(wifiReciever);
        super.onPause();
        if (toastActivated) {
            toast.cancel();
        }

        sensorManager.unregisterListener(this);

        unregisterReceiver(wifiReciever);
        if (scanOn) {
            //mainWifiObj.reconnect();
//            unregisterReceiver(wifiReciever);
            scanOn = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Toast.makeText(this, "sensor changed", Toast.LENGTH_SHORT).show();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            getAccelerometer(event);
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            updateGravityVector(event);
        }

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // get the angle around the z-axis rotated
            float degree = Math.round(event.values[0]);
            //headingText.setText("Heading: " + Float.toString(degree) + " degrees");
        }

    }

    private void updateGravityVector(SensorEvent event) {
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        gravityVector[0] = x / magnitude;
        gravityVector[1] = y / magnitude;
        gravityVector[2] = z / magnitude;


    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0] - 0.01f;
        float y = values[1];
        float z = values[2] - 0.33f;
        //float z =values[2];

        float xInGravity = x * gravityVector[0];
        float yInGravity = y * gravityVector[1];
        float zInGravity = z * gravityVector[2];

        float accelerationInGravity = (float) Math.sqrt(xInGravity * xInGravity + yInGravity * yInGravity + zInGravity + zInGravity);
        textAccel.setText(String.valueOf(accelerationInGravity));

        if (Math.abs(accelerationInGravity) > 4) {

            if (color) {
                textAccel.setBackgroundColor(Color.GREEN);
            } else {
                textAccel.setBackgroundColor(Color.RED);
            }
            color = !color;
        }


        float linearAccelerationSquared = (x * x + y * y);
        //long actualTime = event.timestamp;
        long actualTime = System.currentTimeMillis();


        if (linearAccelerationSquared > changesAccelerometer) {
            changesAccelerometer = linearAccelerationSquared;
        }

        if ((z * z) > 7) {
            accelerationZ = true;
        }


        if (linearAccelerationSquared >= 4) {
            if (actualTime - lastUpdate > ACCELEROMETER_WAIT_TIME) {

                lastUpdate = System.currentTimeMillis();
                recentChangesAccelerometer = linearAccelerationSquared;
                //Log.d("linearA",String.valueOf(recentChangesAccelerometer));
                localPoints.setText("local: 9");
                //textAccel.setText(String.valueOf(recentChangesAccelerometer));
                //recentChangesAccelerometer=0;

                if (linearAccelerationSquared > 20) {
                    localPoints.setText("local: 24");
                }


                //Toast.makeText(getApplicationContext(), "Guessing position...", Toast.LENGTH_SHORT).show();
                startScanTime = System.currentTimeMillis();
                //Log.d("wifi listener","register! ***********************************************************************");

                //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifiObj.startScan();
                //Log.d("wifi listener","scanning...");

            }
            changesAccelerometer = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
//        if (id == R.id.action_floor0) {
//            map.setImageResource(R.drawable.floor0);
//            return true;
//        }
//        if (id == R.id.action_floor1) {
//            map.setImageResource(R.drawable.floor1);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public Thread makeProgressThread() {
        Thread timerThread = new Thread() {
            @Override
            public void run() {
                mbActive = true;
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressLayout.setVisibility(textAccel.VISIBLE);
                            Animation animateTruck = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.truck_animation);
                            truckImage.startAnimation(animateTruck);
                            Animation animateBuild = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.build_animation);
                            buildImage.startAnimation(animateBuild);
                            Animation animateLayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.layout_animation);
                            progressLayout.startAnimation(animateLayout);
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
                        }
                        if (waited == 2800) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startScanTime = System.currentTimeMillis();
                                    Log.d("wifi listener", "register! ***********************************************************************");
                                    //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                                    //mainWifiObj.disconnect();
                                    if (!scanOn) {
                                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                                        mainWifiObj.startScan();
                                        scanOn = true;
                                        //showCustomAlert("Scanning On");
                                    }
                                    Log.d("wifi listener", "scanning...");

                                }
                            });
                        }
                        if (waited == 2700) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarText.setText("Creating Grid objects...");
                                }
                            });
                        }
                        if (waited == 5800) {
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
                if (null != mProgressBar) {
                    // Ignore rounding error here
                    final int progress = mProgressBar.getMax() * timePassed / TIMER_RUNTIME;
                    mProgressBar.setProgress(progress);
                }
            }

            public void onContinue() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressLayout.setVisibility(textAccel.GONE);
                    }
                });
            }
        };
        return timerThread;
    }

    public void PullFromServer() {
        Thread timer = makeProgressThread();
        timer.start();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> newDataList, ParseException e) {
                startServerTime = System.currentTimeMillis();
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
                        }

                        //print statement used to see if loading bar time is sufficient
                        Log.d("Server Time", String.valueOf(System.currentTimeMillis() - startServerTime) + " milliseconds");
                        Log.d("Parse Server", "pulled " + numberOfPoints + " objects from server");
                        dataList = result;

                        Log.d("building", dataList.get(0).printFullLocation().substring(0, 2));
//                        for(int i=0;i<dataList.size();i++){
//                            Log.d(dataList.get(i).printFullLocation(),dataList.get(i).printRouters());
//                        }
                        CreateStrongerPrints upgrade = new CreateStrongerPrints(dataList);
                        strongerDataList = upgrade.getStrongerDataList();
                        Log.d("Stronger fingerprints", "compiled into " + Integer.toString(strongerDataList.size()) + " prints");
//                        for(int i=0;i<strongerDataList.size();i++){
//                            Log.d(strongerDataList.get(i).printFullLocation(),strongerDataList.get(i).printRouters());
//                        }

                    }
                } else {
                    showCustomAlert("Error connecting to server :(");
                    //Toast.makeText(getApplicationContext(), "Error connecting to server :(", Toast.LENGTH_SHORT);
                }
            }
        });
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

    public void viewLocalData() {
        int length = dataList.size();
        GridData[] displayDataList = new GridData[length];
        int n = 0;

        for (int i = 0; i < length; i++) {
            GridData obj = dataList.get(i);
            if (obj.getFloor().equals(floornumber)) {
                if (obj.getBuilding().equals(buildingnumber)) {
                    displayDataList[n] = obj;
                    n += 1;
                }
            }
        }
        if (n == 0) {
            showCustomAlert("No available data for this floor :(");
            //Toast.makeText(getApplicationContext(), "no data for this floor :(", Toast.LENGTH_SHORT).show();
            return;
        } else {
            GridData[] sortDisplay = new GridData[n];
            System.arraycopy(displayDataList, 0, sortDisplay, 0, sortDisplay.length);
            Arrays.sort(sortDisplay);

            ArrayList<GridData> finalDisplay = new ArrayList<GridData>();

            if (result != null) {
                lastPrediction = result.getPosition();
            }

            //re-initialize image mapping to all zeros
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = 0;
            }

            for (int i = 0; i < sortDisplay.length; i++) {
                finalDisplay.add(i, sortDisplay[i]);
                numbers[sortDisplay[i].getPosition()] += 1;
                updateGrid();
            }

            datalistView.setAdapter(new ViewDataAdapter(finalDisplay, getApplicationContext()));
            //Toast.makeText(getApplicationContext(), "data!", Toast.LENGTH_SHORT).show();
        }

    }

    public void hideLocalData() {
        //re-initialize image mapping to all zeros
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = -1;
        }
        numbers[lastPrediction] = 0;
        updateGrid();
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

            timeForScan = System.currentTimeMillis() - startScanTime;
            //Log.d("Scan Time", String.valueOf(timeForScan)+" milliseconds *********************8");
            //Log.d("Scan", String.valueOf(wifiScanList.size())+" routers found");

            startScanTime = System.currentTimeMillis();
            mainWifiObj.startScan();


            startAlgTime = System.currentTimeMillis();
            Log.d("number of routers", String.valueOf(wifiScanList.size()));

            numberOfRoutersSaved = wifiScanList.size();
            wifis = new RouterObject[numberOfRoutersSaved];
            for (int i = 0; i < numberOfRoutersSaved; i++) {
                int level = wifiScanList.get(i).level;
                //String name = wifiScanList.get(i).SSID;
                String id = wifiScanList.get(i).BSSID;
                //int strength = WifiManager.calculateSignalLevel(level, 100);
                int strength = level;
                RouterObject router = new RouterObject(id, strength);
                wifis[i] = router;
            }

            Arrays.sort(wifis);
            mostRecentScan = wifis;

            String[] wifiText = new String[numberOfRoutersSaved];
            for (int i = 0; i < numberOfRoutersSaved; i++) {
                wifiText[i] = wifis[i].print();
            }

            if (wifis != null || wifis.length != 0) {
                //dataList.add(new GridData(wifis, currentPosition, buildingnumber, floornumber, "0"));
            } else {
                showCustomAlert("Routers not found :(");
//                Toast.makeText(getApplicationContext(), "Routers not found", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < dataList.size(); i++) {
                dataStringList.add(dataList.get(i).print());
            }

            scanlist.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.router_list_item, wifiText));
            scanTitle.setText("Current data");

            testAlgorithm();
            //unregisterReceiver(wifiReciever);
            //Log.d("wifi listener","unregister");


        }
    }

    public int[] ConvertGrid(int position) {
        int xpos = (position % 24) + 1;
        int ypos = position / 24 + 1;
        return new int[]{xpos, ypos};
    }

    public void updateGrid() {
        gridAdapter = new com.example.matthew.newapplication.CustomGridAdapter(getApplicationContext(), numbers);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
    }

    public int[] updateMapView() {

        mapsize = new int[288];
        map.setPadding(0, 0, 0, 0);

        Animation animateCompassBeat = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.beat_animation);
        compassBeatImage.startAnimation(animateCompassBeat);


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
            int margin_top = convertToPx(8);
            int margin_left = convertToPx(0);
            int margin_right = convertToPx(0);
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);

//            gridView.setColumnWidth(90);

            if (floornumber.equals("0")) {
                map.setImageResource(R.drawable.floor0_33);
            }

            if (floornumber.equals("1")) {
                map.setImageResource(R.drawable.floor1_33);
            }

            if (floornumber.equals("2")) {
                map.setImageResource(R.drawable.floor2_33);
            }

            if (floornumber.equals("3")) {
                map.setImageResource(R.drawable.floor3_35);
            }

            if (floornumber.equals("4")) {
                map.setImageResource(R.drawable.floor4_35);
            }

            if (floornumber.equals("5")) {
                map.setImageResource(R.drawable.floor5_35);
            }

            if (!localDataVisible) {
                //initialize image mapping to all blank
                for (int i = 0; i < mapsize.length; i++) {
                    mapsize[i] = -1;
                }
            }

            //Log.d("length", Integer.toString(mapsize.length));
            //showCustomAlert("Building " + buildingnumber + ", Floor " + floornumber);
            locationText.setText("Building " + buildingnumber + ", Floor " + floornumber);

//            Toast.makeText(getApplicationContext(), "Building " + buildingnumber + ", Floor " + floornumber, Toast.LENGTH_SHORT).show();

            numbers = mapsize;
            return mapsize;

        } else if (buildingnumber.equals("35")) {

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.wallsView_layout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();

            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 134, getResources().getDisplayMetrics()));
            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106, getResources().getDisplayMetrics()));
            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 116, getResources().getDisplayMetrics()));
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);

//            gridView.setColumnWidth(90);

            if (floornumber.equals("0")) {
                map.setImageResource(R.drawable.floor0_35);
            }

            if (floornumber.equals("1")) {
                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106, getResources().getDisplayMetrics()));
                margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 116, getResources().getDisplayMetrics()));
                params.setMargins(margin_left, margin_top, margin_right, 0);
                layout.setLayoutParams(params);
                map.setImageResource(R.drawable.floor1_35);
            }

            if (floornumber.equals("2")) {
                map.setImageResource(R.drawable.floor2_35);
            }

            if (floornumber.equals("3")) {
                map.setImageResource(R.drawable.floor3_35);
            }

            if (floornumber.equals("4")) {
                map.setImageResource(R.drawable.floor4_35);
            }

            if (floornumber.equals("5")) {
                map.setImageResource(R.drawable.floor5_35);
            }

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
        } else {

            //mapsize = new int[132];
            map.setPadding(0, 0, 0, 0);

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapGridLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            int margin_top = convertToPx(108);
            int margin_left = convertToPx(27);
            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
            params.setMargins(margin_left, margin_top, margin_right, 0);
            layout.setLayoutParams(params);
//            gridView.setColumnWidth(99);

            if (floornumber.equals("0")) {
                map.setImageResource(R.drawable.floor0_35);
            }

            if (floornumber.equals("1")) {
                map.setImageResource(R.drawable.floor1_35);
            }

            if (floornumber.equals("2")) {
                map.setImageResource(R.drawable.floor2_35);
            }

            if (floornumber.equals("3")) {
                map.setImageResource(R.drawable.floor3_35);
            }

            if (floornumber.equals("4")) {
                map.setImageResource(R.drawable.floor4_35);
            }

            if (floornumber.equals("5")) {
                map.setImageResource(R.drawable.floor5_35);
            }

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
        checkForExtremes();
        return mapsize;

    }

    public void testAlgorithm() {
        if (mostRecentScan != null) {
            scanCount++;
            Log.d("scan count", String.valueOf(scanCount));
            Log.d("most recent scan", String.valueOf(mostRecentScan.length));

            //takes two scans from receiver and averages them before guessing
            if (scanCount < 2) {
                olderScan = mostRecentScan;
            } else if (scanCount == 2) {
                scanCount = 0;
                averageScans average = new averageScans(olderScan, mostRecentScan);
                averageOfRecentScans = average.calculate();
                FingerprintingAlg Alg = new FingerprintingAlg(currentPosition, accelerationZ, recentChangesAccelerometer, previousChangesAccelerometer, floornumber, buildingnumber, strongerDataList, averageOfRecentScans);

                //Accelerations over time need to be updated
                accelerationZ = false;
                previousChangesAccelerometer = recentChangesAccelerometer;
                recentChangesAccelerometer = 0;
//                textAccel.setText(String.valueOf(recentChangesAccelerometer));

                if (Alg.getResult() != null) {
                    //re-initialize image mapping to all be empty
                    for (int i = 0; i < numbers.length; i++) {
                        numbers[i] = -1;
                    }

                    GridData algResult = Alg.getResult();

                    //Graph interface here
                    ///////////////////////////////////////////////////////////////////////////////
                    String guessedNode=algResult.printNodeString();
                    Log.d("*****************",guessedNode);
                    if(guessedNode!=null) {
                        this.currentNode = G.getFirstValue(guessedNode);
                        if (currentNode!="none") {
                            //this method will return an Iterable<String> object with all the neighbor Node strings
                            Iterable<String> neighbors = G.adjacentTo(currentNode);
                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////////

                    //Log.d("Algorithm Time", String.valueOf(System.currentTimeMillis() - startAlgTime)+" milliseconds");
                    numbers[algResult.getPosition()] = 0;
                    updateGrid();
                    //Toast.makeText(getApplicationContext(), result.printID(), Toast.LENGTH_SHORT).show();
                    buildingnumber = algResult.getBuilding();
                    floornumber = algResult.getFloor();
                    currentPosition = algResult.getPosition();
                    if (buildingnumber.equals("35") && previousBuilding.equals("33")) {
                        animate35over33();
                        previousPosition = currentPosition;
                        previousBuilding = buildingnumber;
                        previousFloor = floornumber;
                    } else {
                        updateMapView();
                        previousPosition = currentPosition;
                        previousBuilding = buildingnumber;
                        previousFloor = floornumber;
                    }

                    Log.d("curerentPosition:", String.valueOf(currentPosition));
                    updatePathView(currentPosition);


                } else {
                    Log.d("check", "alg didn't give result");
                    if (buildingnumber.equals("0")) {
                        showCustomAlert("We can't find your position :(");
                        //Toast.makeText(getApplicationContext(),"We can't find your position :(",Toast.LENGTH_LONG).show();
                    } else {
                        showCustomAlert("No result:(");
//                    Toast.makeText(getApplicationContext(), "No result :(", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Need to scan location first", Toast.LENGTH_SHORT).show();

        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 200;
        //private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 280;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            System.out.println(" in onFling() :: ");
            //if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            //return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Animation animateCoverLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cover_left_animation);
                animateCoverLeft.setAnimationListener(AnimationLeftListener);
                SmallCoverButton.startAnimation(animateCoverLeft);


            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                hand.setVisibility(View.VISIBLE);
                Animation animateCoverRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cover_right_animation);
                animateCoverRight.setAnimationListener(AnimationRightListener);
                CoverButton.startAnimation(animateCoverRight);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    Animation.AnimationListener AnimationRightListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            CoverButton.setVisibility(View.GONE);
            SmallCoverButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

    Animation.AnimationListener AnimationLeftListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            SmallCoverButton.setVisibility(View.GONE);
            CoverButton.setVisibility(View.VISIBLE);
            hand.setVisibility(View.GONE);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

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

    int convertToPx(int dp) {
        int answer = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return answer;
    }

    public void updatePathView(int position) {
        Point point = new Point();
        int[] pos = ConvertGrid(position);
        Log.d("position", String.valueOf(pos[0]) + "," + String.valueOf(pos[1]));
        point.x = (float) (25 + 49 * (pos[0] - 1));
        point.y = (float) (45 + 52 * (pos[1] - 1));
        Log.d("coords", point.toString());
        if (buildingnumber.equals("33")) {
            if (floornumber.equals("1")) {
                points33_1.add(point);
                drawView.updatePath(points33_1);
            } else if (floornumber.equals("2")) {
                points33_2.add(point);
                drawView.updatePath(points33_2);
            } else if (floornumber.equals("3")) {
                points33_3.add(point);
                drawView.updatePath(points33_3);
            } else if (floornumber.equals("4")) {
                points33_4.add(point);
                drawView.updatePath(points33_4);
            } else if (floornumber.equals("5")) {
                points33_5.add(point);
                drawView.updatePath(points33_5);
            }
        } else if (buildingnumber.equals("35")) {
            if (floornumber.equals("1")) {
                points35_1.add(point);
                drawView.updatePath(points35_1);
            } else if (floornumber.equals("2")) {
                points35_2.add(point);
                drawView.updatePath(points35_2);
            } else if (floornumber.equals("3")) {
                points35_3.add(point);
                drawView.updatePath(points35_3);
            } else if (floornumber.equals("4")) {
                points35_4.add(point);
                drawView.updatePath(points35_4);
            } else if (floornumber.equals("5")) {
                points35_5.add(point);
                drawView.updatePath(points35_5);
            }
        }
    }

    public void animate35over33() {

        if (floornumber.equals("1")) {
            newBuildingInt = R.drawable.floor1_35;
        } else if (floornumber.equals("2")) {
            newBuildingInt = R.drawable.floor2_35;
        }

        Animation animateIncomingRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.incoming_walls_right);
        wallsViewRight.startAnimation(animateIncomingRight);

        Animation animateChangeWalls = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.walls_change);
        animateChangeWalls.setAnimationListener(AnimationListenerForRightWall);
        map.startAnimation(animateChangeWalls);

        map.setImageResource(newBuildingInt);
    }

    Animation.AnimationListener AnimationListenerForRightWall = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            wallsViewRight.setVisibility(View.INVISIBLE);
            //map.setImageResource(newBuildingInt);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

    public void checkForExtremes() {
        wallsViewRight.setVisibility(View.INVISIBLE);

        if (buildingnumber.equals("33")) {
            if (floornumber.equals("1")) {
                Log.d("got here", "check2");

                int[] myArray = new int[]{67, 90, 91, 113, 114, 115};
                ArrayList<Integer> edgePositions = initArrayList(myArray);

                if (edgePositions.contains(currentPosition)) {
                    Log.d("got here", "check3");

                    wallsViewRight.setVisibility(View.VISIBLE);
                } else {
                    wallsViewRight.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    public ArrayList<Integer> initArrayList(int[] a) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : a) {
            list.add(i);
        }
        return list;
    }

}
