package com.example.matthew.newapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.SaveCallback;

import android.widget.ProgressBar;



public class FloorPlans extends Activity implements SensorEventListener {
    private ImageView map;
    private  String buildingnumber, floornumber;
    private Button sendDataButton, updateButton, viewDataButton, testAlg;
    private SensorManager sensorManager;
    private boolean color = false;
    private TextView view;
    private long lastUpdate;
    private Context ctx;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    TextView scanTitle;
    ListView scanlist;
    ListView datalistView;
    RouterObject wifis[];
    int[] coords = new int[]{0,0};
    private int lastPosition=999;
    int numberOfRoutersSaved, numberOfRoutersUsed;
    RouterObject[] mostRecentScan =null;

    GridView gridView;
    CustomGridAdapter gridAdapter;
    int[] numbers = new int[320];


    ArrayList<GridData> dataList = new ArrayList<GridData>();

    ParseObject gridDataOnline;
    ArrayList<String> dataStringList = new ArrayList<String>();

    int TIMER_RUNTIME = 6000; // in ms --> 6seconds
    boolean mbActive;
    ProgressBar mProgressBar;
    RelativeLayout progressLayout;
    TextView progressBarText;
    ImageView truckImage, cloudImage, buildImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorplans35);
        gridView = (GridView) findViewById(R.id.mapGrid);
        map = (ImageView) findViewById(R.id.wallsView);

        ctx = this;
        sendDataButton = (Button) findViewById(R.id.button_data);
        viewDataButton = (Button) findViewById(R.id.button_viewdata);
        updateButton = (Button) findViewById(R.id.button_update);
        //testAlg = (Button) findViewById(R.id.button_test);

        //sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        scanlist = (ListView)findViewById(R.id.listView1);
        scanTitle = (TextView)findViewById(R.id.scanTextView);
        datalistView = (ListView) findViewById(R.id.data_listView);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();

        //progress bar class creation
        progressLayout = (RelativeLayout)findViewById(R.id.progressBar_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBarText = (TextView)findViewById(R.id.progressBar_text);
        truckImage = (ImageView) findViewById(R.id.truck_image);
        buildImage = (ImageView) findViewById(R.id.build_image);
        cloudImage = (ImageView) findViewById(R.id.parse_image);

        numberOfRoutersSaved = 9;
        numberOfRoutersUsed = 9;

        Parse.initialize(this, "9IFIo0LdgpyPESCd8eNaCrdiFjAM61Jz3B9EvbYo", "BGwRcE1fJtEwvpmuEn7n4WsL3P0HIqK242MdpEIu");
        PullFromServer();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                floornumber = null;
                buildingnumber = null;
            } else {
                floornumber = extras.getString("floor");
                buildingnumber = extras.getString("building");

            }
        } else {
            floornumber = (String) savedInstanceState.getSerializable("floor");
            buildingnumber = (String) savedInstanceState.getSerializable("building");
        }

        if(buildingnumber.equals(null)){
            Log.d("null", "error in FloorPlans");
        }

        else if(buildingnumber.equals("33")){

//            RelativeLayout layout = (RelativeLayout) findViewById(R.id.wallsView_layout);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
//            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
//            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics()));
//            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
//            params.setMargins(margin_left, margin_top, margin_right, 0);
//            layout.setLayoutParams(params);

           // gridView.setColumnWidth(90);


            if (floornumber.equals("0")) {map.setImageResource(R.drawable.floor0_33);}

            if (floornumber.equals("1")) {map.setImageResource(R.drawable.floor1_33);}

            if (floornumber.equals("2")) {map.setImageResource(R.drawable.floor2_33);}

            if (floornumber.equals("3")) {map.setImageResource(R.drawable.floor3_35);}

            if (floornumber.equals("4")) {map.setImageResource(R.drawable.floor4_35);}

            if (floornumber.equals("5")) {map.setImageResource(R.drawable.floor5_35);}
        }

        else if(buildingnumber.equals("35")){

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.wallsView_layout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();

//            int margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 134, getResources().getDisplayMetrics()));
//            int margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106, getResources().getDisplayMetrics()));
//            int margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 116, getResources().getDisplayMetrics()));
//            params.setMargins(margin_left, margin_top, margin_right, 0);
            //layout.setLayoutParams(params);

           // gridView.setColumnWidth(90);

            if (floornumber.equals("0")) {map.setImageResource(R.drawable.floor0_35);}

            if (floornumber.equals("1")) {
//                margin_top = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
//                margin_left = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106, getResources().getDisplayMetrics()));
//                margin_right = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 116, getResources().getDisplayMetrics()));
//                params.setMargins(margin_left, margin_top, margin_right, 0);
                //layout.setLayoutParams(params);
                map.setImageResource(R.drawable.floor1_35);}

            if (floornumber.equals("2")) {map.setImageResource(R.drawable.floor2_35);}

            if (floornumber.equals("3")) {map.setImageResource(R.drawable.floor3_35);}

            if (floornumber.equals("4")) {map.setImageResource(R.drawable.floor4_35);}

            if (floornumber.equals("5")) { map.setImageResource(R.drawable.floor5_35);}
        }

        //initialize image mapping to all zeros
        for(int i=0;i<numbers.length;i++){numbers[i]=0;}

        Log.d("length",Integer.toString(numbers.length));

        updateGrid();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                final int originalValue = numbers[position];


                //numbers[position-24]=9;
                updateGrid();

                AlertDialog.Builder adb = new AlertDialog.Builder(FloorPlans.this);
                adb.setCancelable(false);

                adb.setTitle("Box #: " + Integer.toString(position));
                adb.setMessage("Save your current wifi data as this place on the map?");
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked...nothing happens
                        numbers[position]=originalValue;

                        numbers[110]=10;
                        numbers[133]=11;
                        numbers[132]=9;

                        updateGrid();
                        dialog.cancel();
                    }
                });

                adb.setPositiveButton("Scan here", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, do stuff
                        registerReceiver(wifiReciever, new IntentFilter(
                                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        lastPosition = position;
                        //if (buildingnumber.equals("35")) {coords = ScanGrid35(position);}
                        //if (buildingnumber.equals("33")) {coords = ScanGrid33(position);}
                        Toast.makeText(getApplicationContext(),"Scanning nearby routers", Toast.LENGTH_SHORT).show();
                        mainWifiObj.startScan();

                        numbers[position]=originalValue+1;
                        updateGrid(); }
                        });
                adb.show();
            }
        });

        lastUpdate = System.currentTimeMillis();

        view = (TextView) findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);

        sendDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                int length = dataList.size();
                Toast.makeText(getApplicationContext(),"Sending data...", Toast.LENGTH_SHORT).show();

                gridDataOnline = new ParseObject("gridDataOnline");

                GridData obj = dataList.get(length - 1);
                String positionString = obj.printPosition();
                //System.out.println(obj.printPosition());

                    int[] coordinatesFinal = new int[2];

                    if (buildingnumber.equals("35")) {
                        coordinatesFinal = ScanGrid35(Integer.parseInt(positionString));
                    } else if (buildingnumber.equals("33")) {
                        coordinatesFinal = ScanGrid33(Integer.parseInt(positionString));
                    }

                    gridDataOnline.put("buildingNumber", obj.getBuilding());
                    gridDataOnline.put("floorNumber", obj.getFloor());
                    gridDataOnline.put("position", Integer.parseInt(positionString));
                    gridDataOnline.put("yCoord", coordinatesFinal[0]);
                    gridDataOnline.put("xCoord", coordinatesFinal[1]);
                    gridDataOnline.put("routers", obj.printRouters());
                    gridDataOnline.saveInBackground();

                gridDataOnline.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(),"Saved successfully!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Failed to send... :(",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                obj.setTempID();

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                UpdateFromServer();
                }
            });

        viewDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                viewLocalData();
            }
        });

//        testAlg.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                if(mostRecentScan!=null) {
//                    FingerprintingAlg Alg = new FingerprintingAlg(lastPosition, buildingnumber, dataList, mostRecentScan);
//                    GridData result;
//                    Log.d("rawr","got here");
//                    if (Alg.getResult() != null) {
//                        //re-initialize image mapping to all be empty
//                        for(int i=0;i<numbers.length;i++){numbers[i]=-1;}
//
//                        result = Alg.getResult();
//                        Log.d("result",result.printFirstRouter());
//                        numbers[result.getPosition()]=0;
//                        updateGrid();
//
//                        Toast.makeText(getApplicationContext(),result.printFirstRouter(),Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Log.d("rawr", "alg didn't give result");
//                    }
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Need to scan location first",Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        });

        datalistView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> a, View v, final int position, long id) {

                        final int selectedPosition = position;
                        AlertDialog.Builder adb = new AlertDialog.Builder(FloorPlans.this);
                        adb.setCancelable(false);

                        final GridData obj = (GridData) datalistView.getItemAtPosition(position);

                        String title = obj.printPosition();
                        String routers = obj.printRouters();
                        final String ID = obj.printID();

                        adb.setTitle("Box:"+title);
                        adb.setMessage(routers);

                        adb.setPositiveButton("Cancel", null);
                        adb.setNegativeButton("Remove this data from server",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if(ID.equals("0")){

                                    Toast.makeText(getApplicationContext(),"This piece of data isn't on the server yet", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Deleting from online server now...", Toast.LENGTH_SHORT).show();
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");
                                    query.getInBackground(obj.printID(), new GetCallback<ParseObject>() {
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null) {
                                                object.deleteInBackground(new DeleteCallback() {
                                                    public void done(ParseException e) {
                                                        if(e==null){
                                                            Toast.makeText(getApplicationContext(),"Removed from server!",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Toast.makeText(getApplicationContext(),"Failed to remove :/",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),"Couldn't find it on the server",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }

                                dataList.remove(obj);

                            }
                        });

                        adb.show();
                        return true;

                    }});

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        /*
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
*/
       // registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        super.onResume();

    }

    @Override
    protected void onPause() {
        // unregister listener
        //unregisterReceiver(wifiReciever);
        super.onPause();

       // sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Toast.makeText(this, "sensor changed", Toast.LENGTH_SHORT).show();

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2]-0.33f;
        DecimalFormat df = new DecimalFormat("#.###");
/*
        x_accel.setText("x:"+df.format(x));
        y_accel.setText("y:"+df.format(y));
        z_accel.setText("z:"+df.format(z));
*/
        float accelerationSquareRoot = (x * x + y * y );
        //float accelationSquareRoot = (z * z);//(SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        //long actualTime = event.timestamp;
        long actualTime = System.currentTimeMillis();

        if (accelerationSquareRoot >= 4)
        {
            if(actualTime - lastUpdate > 2000) {
                lastUpdate = System.currentTimeMillis();

                if (color) {
                    view.setBackgroundColor(Color.GREEN);
                } else {
                    view.setBackgroundColor(Color.RED);
                }
                color = !color;
                view.setText("movement");
            }
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
//        if (id == R.id.action_floor2) {
//            map.setImageResource(R.drawable.floor2);
//            return true;
//        }
//        if (id == R.id.action_floor3) {
//            map.setImageResource(R.drawable.outline35);
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
                            progressLayout.setVisibility(view.VISIBLE);
                            Animation animateTruck = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.truck_animation);
                            truckImage.startAnimation(animateTruck);
                            Animation animateBuild = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.build_animation);
                            buildImage.startAnimation(animateBuild);
                            //Animation animateCloud = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.layout_animation);
                            //cloudImage.startAnimation(animateCloud);
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
                        if (waited == 3000) {
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
                        progressLayout.setVisibility(view.GONE);
                    }
                });
            }
        };
        return timerThread;
    }

    public void PullFromServer(){

        Thread timer = makeProgressThread();
        timer.start();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> newDataList, ParseException e) {
                ArrayList<GridData> result = new ArrayList<GridData>();
                if (e == null) {
                    int numberOfPoints = newDataList.size();
                    if(!newDataList.isEmpty()){
                        //Toast.makeText(getApplicationContext(),"Waiting for server...",Toast.LENGTH_LONG);
                        for (int i=0; i<numberOfPoints; i++) {
                            ParseObject obj = newDataList.get(i);
                            int pos = obj.getInt("position");
                            String routers = obj.getString("routers");
                            String build = obj.getString("buildingNumber");
                            String flo = obj.getString("floorNumber");
                            String id = obj.getObjectId();

                            //String raw = dataList.get(i).print();
                            //String PLAYER = "1||1||Abdul-Jabbar||Karim||1996||1974";
                            Pattern pattern1 = Pattern.compile(Pattern.quote("/"));
                            String[] splitRaw = pattern1.split(routers);

                            RouterObject[] routerResults = new RouterObject[splitRaw.length];

                            //Log.d("object",id);
                            for(int n=0; n<splitRaw.length; n++) {
                                Pattern pattern2 = Pattern.compile(Pattern.quote("="));
                                String[] eachRouter = pattern2.split(splitRaw[n]);
                                int strength = Integer.parseInt(eachRouter[1]);
                                routerResults[n] = (new RouterObject(eachRouter[0],strength));
                            }

                            GridData item = new GridData(routerResults,pos,build,flo,id);
                            result.add(item);

                        }
                        dataList=result;
                        //print statement used to see if loading bar time is sufficient
                        Log.d("update","all done");
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error connecting to server",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void UpdateFromServer(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("gridDataOnline");

        Thread newTimer = makeProgressThread();
        newTimer.start();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> newDataList, ParseException e) {
                ArrayList<GridData> result = new ArrayList<GridData>();
                RouterObject[] routerResults = new RouterObject[numberOfRoutersUsed];
                if (e == null) {
                    int numberOfPoints = newDataList.size();
                    if(!newDataList.isEmpty()){
                        //timerThread.run();
                        Toast.makeText(getApplicationContext(),"Waiting for server...",Toast.LENGTH_LONG).show();
                        for (int i=0; i<numberOfPoints; i++) {
                            ParseObject obj = newDataList.get(i);
                            int pos = obj.getInt("position");
                            String routers = obj.getString("routers");
                            String build = obj.getString("buildingNumber");
                            String flo = obj.getString("floorNumber");
                            String id = obj.getObjectId();

                            Pattern pattern1 = Pattern.compile(Pattern.quote("/"));
                            String[] splitRaw = pattern1.split(routers);

                            //System.out.println("position: "+Integer.toString(pos));

                            for(int n=0; n<numberOfRoutersUsed; n++) {
                                Pattern pattern2 = Pattern.compile(Pattern.quote("="));
                                String[] eachRouter = pattern2.split(splitRaw[n]);
                                routerResults[n] = (new RouterObject(eachRouter[0],Integer.parseInt(eachRouter[1])));
                                //System.out.println("id: "+routerResults[n].printBSSID());
                            }

                            GridData item = new GridData(routerResults,pos,build,flo,id);
                            result.add(item);

                        }
                        dataList=result;
                        //print statement used to see if loading bar time is sufficient
                        Log.d("update","all done");
                        viewLocalData();
                    }
                }
                else {
                }
            }
        });
    }

    public void viewLocalData(){
        int length = dataList.size();
        GridData[] displayDataList = new GridData[length];
        int n=0;

        for(int i=0;i<length;i++){
            GridData obj = dataList.get(i);
            if(obj.getFloor().equals(floornumber)){
                if(obj.getBuilding().equals(buildingnumber)){
                    displayDataList[n]=obj;
                    n+=1;
                }
            }
        }
        if(n==0){
            Toast.makeText(getApplicationContext(), "no data for this floor :(", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            GridData[] sortDisplay = new GridData[n];
            System.arraycopy(displayDataList, 0, sortDisplay, 0, sortDisplay.length);
            Arrays.sort(sortDisplay);

            ArrayList<GridData> finalDisplay = new ArrayList<GridData>();

            //re-initialize image mapping to all zeros
            for(int i=0;i<numbers.length;i++){numbers[i]=0;}

            for (int i = 0; i < sortDisplay.length; i++) {
                finalDisplay.add(i, sortDisplay[i]);
                numbers[sortDisplay[i].getPosition()]+=1;
                updateGrid();
            }

            datalistView.setAdapter(new ViewDataAdapter(finalDisplay, getApplicationContext()));
            //Toast.makeText(getApplicationContext(), "data!", Toast.LENGTH_SHORT).show();
        }

    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {

            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

            wifis = new RouterObject[numberOfRoutersSaved];
            for(int i = 0; i < numberOfRoutersSaved; i++){
                int level = wifiScanList.get(i).level;
                //String name = wifiScanList.get(i).SSID;
                String id = wifiScanList.get(i).BSSID;
                //int strength = WifiManager.calculateSignalLevel(level,100);
                int strength = level;
                RouterObject router = new RouterObject(id,strength);
                wifis[i] = router;
            }

            Arrays.sort(wifis);

            mostRecentScan = wifis;

            String[] wifiText = new String[numberOfRoutersSaved];
            for(int i = 0; i < numberOfRoutersSaved; i++){
                wifiText[i] = wifis[i].print();
            }

            if(wifis!=null || wifis.length!=0) {
                dataList.add(new GridData(wifis, lastPosition, buildingnumber, floornumber, "0"));
            }
            else{
                Log.d("app","routers not found");
            }

            for(int i = 0; i < dataList.size(); i++){
                dataStringList.add(dataList.get(i).print());
            }

            //Toast.makeText(getApplicationContext(),dataList.get(0).print(), Toast.LENGTH_SHORT).show();

            scanlist.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.router_list_item, wifiText));
            scanTitle.setText("Current data\nBox:"+lastPosition);

            Toast.makeText(getApplicationContext(),"Add to the server with the 'Send Data' button", Toast.LENGTH_SHORT).show();
            unregisterReceiver(wifiReciever);

        }
    }

    public int[] ScanGrid35(int position){
        int xpos = (position%22)+1;
        int ypos = position/22+1;
        return new int[]{ypos,xpos};
    }

    public int[] ScanGrid33(int position){
        int xpos = (position%14)+1;
        int ypos = position/14+1;
        return new int[]{ypos,xpos};
    }

    public void updateGrid(){
        gridAdapter = new com.example.matthew.newapplication.CustomGridAdapter(getApplicationContext(), numbers );
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
    }
}
