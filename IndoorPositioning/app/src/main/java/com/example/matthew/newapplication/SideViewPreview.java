package com.example.matthew.newapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SideViewPreview extends Activity {

    private DrawingView drawView;
    List<Point> path = new ArrayList<Point>();
    private String start, finish, predictedPoints, predictedQR;
    private Button button;
    private ArrayList<String> nodeList = new ArrayList<>();
    private TextView points, numberQRs;
    private boolean first = true;
    static Graph G;

    static ArrayList<ArrayList<String>> regions = new ArrayList<>();
    static ArrayList<int[]> regionCoords = new ArrayList<>();

    static ArrayList<String> QRCodeLocations = new ArrayList<>();
    ArrayList<QRLocationXY> QRCodes = new ArrayList<>();

    private ArrayList<Bitmap> coinSequence = new ArrayList<>();

    public static Integer[] moveSequence = {

            R.drawable.coin1_1, R.drawable.coin1_2, R.drawable.coin1_3, R.drawable.coin1_4, R.drawable.coin1_5, R.drawable.coin1_6

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_view_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawView = (DrawingView) findViewById(R.id.drawingView1);
        points = (TextView) findViewById(R.id.points);
        numberQRs = (TextView) findViewById(R.id.numberQR);
        button = (Button) findViewById(R.id.button);

        G = Assets.G;
        regions = Assets.regions;
        regionCoords = Assets.regionCoords;
        QRCodeLocations = Assets.QRLocations;

//        image = (ImageView)findViewById(R.id.coinImage);
        path = new ArrayList<>();

        String pathNumber = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            pathNumber = extras.getString("path");
        } else {
            pathNumber = (String) savedInstanceState.getSerializable("path");
        }

        initializeNodeList(pathNumber);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                start = "33.1.15";
                finish = "35.2.50";
            } else {
                start = extras.getString("start");
                finish = extras.getString("finish");

            }
        } else {
            start = (String) savedInstanceState.getSerializable("start");
            finish = (String) savedInstanceState.getSerializable("finish");
        }

        drawView.setColor("#66CCFF");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (first) {
                    buildPath();
                    first = false;
                    button.setText("Back");
                } else if (!first) {
                    finish();
                }

                // y interval now at 80px

//                drawView.drawStar(880,225);
//                drawView.drawStar(970,225);
//                drawView.drawStar(1060,225);
//                drawView.drawStar(1030,305);
//                drawView.drawStar(1030,385);
//                drawView.drawStar(1030,465);
//                drawView.drawStar(1030,545);

//                drawView.drawStar(440,315);
//                drawView.drawStar(550,315);
//                drawView.drawStar(660,315);

//                drawView.drawStar(500,395);
//                drawView.drawStar(500,475);
//                drawView.drawStar(500,555);
//                drawView.drawStar(500,635);
//
//                drawView.drawCoin(600,250);
//                drawView.drawCoin(600,325);
//
//                drawView.drawStartStar(145,635);
//                drawView.drawStartStar(145,555);
//                drawView.drawStartStar(145,475);
//                drawView.drawStartStar(145,395);

//                drawView.drawStartStar(120,315);
//                drawView.drawStartStar(210,315);
//                drawView.drawStartStar(300,315);

//                showPathHistory(10, 50);
//                showPathHistory(10, 30);
//                showPathHistory(300, 200);
            }
        });
    }

    public static void setup() {
        G = Assets.G;
        regions = Assets.regions;
        regionCoords = Assets.regionCoords;
        QRCodeLocations = Assets.QRLocations;
    }


    public void buildPath() {
        if (nodeList.size() != 0) {
            String first = nodeList.get(0);
            int[] pos = convertBuildFloor(first);
            updatePathView(pos[0], pos[1]);
            drawView.drawStartStar(pos[0], pos[1]);

            for (int i = 1; i < nodeList.size() - 1; i++) {
                pos = convertBuildFloor(nodeList.get(i));
                updatePathView(pos[0], pos[1]);
//            drawView.drawCoin(pos[0], pos[1]);
            }

            String last = nodeList.get(nodeList.size() - 1);
//            Log.d("tag build last",last);
            int[] lastCoords = convertBuildFloor(last);
            updatePathView(lastCoords[0], lastCoords[1]);
            drawView.drawGoldStar(lastCoords[0], lastCoords[1]);

//            for (int i = 1; i < nodeList.size() - 1; i++) {
//                pos = convertBuildFloor(nodeList.get(i));
//                drawView.drawCoin(pos[0], pos[1]);
//            }

            points.setText("Possible Points: " + predictedPoints);
            numberQRs.setText("Number of QRs: " + predictedQR);

            drawView.updateSidePath(path);
            showQRCodes();
        } else {
            points.setText("Error in loading path");
        }

    }

    public void initializeNodeList(String n) {

        String startPosition = "33:0:124";
        String goal = "37:4:60";

        //set goal location first, then mark neighbors
        G.setStartLocation(startPosition);
        G.setGoalLocation(goal);
        G.markNeighbors(goal, G);
        G.gradientGraph(G);

        HighPointPriority goalHigh = new HighPointPriority(G);
//        FloorRankingOrder goalLogic = new FloorRankingOrder(G, 200);
        int timeLeft=180;
//        Algo pathLogic = new Algo(G, start, goal, goalLogic.goalList,timeLeft);
//        Algo pathHigh = new Algo(G, start, goal, goalHigh.goalPoint,timeLeft);

//        nodeList = pathHigh.getPath();

//        predictedQR = String.valueOf(pathHigh.getQRCollected());
//        predictedPoints = String.valueOf(pathHigh.getCollectedPoints());

//        nodeList = new ArrayList<>();
//        nodeList.clear();
//
//        String filename = "SamplePath"+n;
//        try {
//            //initialize array of questions and choices from file MidBlockSurvey
//            AssetManager assetManager = getResources().getAssets();
//            InputStream is = assetManager.open(filename);
//            BufferedReader r = new BufferedReader(new InputStreamReader(is));
//            String line;
//            if (is != null) {
//                while ((line = r.readLine()) != null) {
//                    nodeList.add(line);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(nodeList.size()>0) {
//            predictedPoints = nodeList.get(0);
//            nodeList.remove(0);
//        }

    }

    public void showQRCodes() {

        QRCodes.clear();

        if (QRCodeLocations.size() > 0) {
            QRLocationXY QRPoint;
            for (String s : QRCodeLocations) {
                Log.d("tag qr 1", s);
                String[] pieces = s.split(":");
                int[] pos = convertBuildFloor(pieces[0] + ":" + pieces[1] + ":" + pieces[2]);
                QRPoint = new QRLocationXY(pieces[0] + ":" + pieces[1] + ":" + pieces[2],pos[0], pos[1], Integer.parseInt(pieces[3]));
                QRCodes.add(QRPoint);
            }
        }
        drawView.updateSideViewQR(QRCodes);
    }

    public void rotatingCoin(int x, int y) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(x, y, 0, 0);
//        image.setLayoutParams(params);

        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_side_view_preview, null);
//        layout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.coin1_1);
        image.setLayoutParams(params);

        layout.addView(image);
//        addContentView(image,params);
        setContentView(layout);
//        drawView.setSideView(this);


        final Handler handler1 = new Handler();
        Runnable runnable1 = new Runnable() {
            int i = 0;

            public void run() {
                image.setImageResource(moveSequence[i]);
                i++;
                if (i > moveSequence.length - 1) {
                    i = 0;
//                    return;
                }
                handler1.postDelayed(this, 200);  //for interval...
            }
        };
        //handler.postDelayed(runnable, 2000); //for initial delay..

        runnable1.run();
    }

    public void updatePathView(int x, int y) {
        Point point = new Point(x, y);
//        Log.d("position", String.valueOf(pos[0]) + "," + String.valueOf(pos[1]));
//        point.x = x;
//        point.y = y;
        Log.d("coords", point.toString());

        path.add(point);

        Log.d("path", path.toString());


//        drawView.updatePathHistory(path);

    }

    public static int[] convertBuildFloor(String node) {
        int regionNumber = 0;

        for (int i = 0; i < regions.size(); i++) {
            if (regions.get(i).contains(node)) {
                regionNumber = i;
                break;
            }
        }
        int[] pos = regionCoords.get(regionNumber);
//        Log.d("tag region",node);
//        Log.d("tag region",String.valueOf(regionNumber));
//        Log.d("tag region",String.valueOf(pos[0])+","+String.valueOf(pos[1]));
        return pos;
//
//        if (building.equals("33")) {
//            pos[0] = 145;
//        } else if (building.equals("35")) {
//            pos[0] = 500;
//        } else if (building.equals("37")){
//            pos[0] = 1000;
//        }
//        if (floor.equals("0")) {
//            pos[1] = 635;
//        } else if (floor.equals("1")) {
//            pos[1] = 555;
//        } else if (floor.equals("2")){
//            pos[1] = 475;
//        }else if (floor.equals("3")) {
//            pos[1] = 395;
//        } else if (floor.equals("4")){
//            pos[1] = 315;
//        }
//
//        if(building.equals("37")){
//            if (floor.equals("1")) {
//                pos[1] = 545;
//            } else if (floor.equals("2")){
//                pos[1] = 465;
//            }else if (floor.equals("3")) {
//                pos[1] = 385;
//            } else if (floor.equals("4")){
//                pos[1] = 305;
//            }else if (floor.equals("5")){
//                pos[1] = 225;
//            }
//        }
//
//        return pos;
    }

}
