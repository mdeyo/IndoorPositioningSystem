package com.example.matthew.newapplication;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Matthew on 6/19/2015.
 */
public class Assets {

    public static Graph G;
    public static String trialNumber, goal, updateString, mode="adaptable";
    private In in;
    public static Map<String, QRCodeLocation> QRMap = new HashMap<String, QRCodeLocation>();
    public static ArrayList<String> QRLocations = new ArrayList<>();
    public static ArrayList<String> nodeList;
    public static ArrayList<String> floor35_2  = new ArrayList<>();;
    public static ArrayList<String> floor35_3  = new ArrayList<>();;

    public static ArrayList<GridData> strongerDataList = new ArrayList<GridData>();

    public static Bitmap arrowUp, arrowRight, arrowDown, arrowLeft;

    public static ArrayList<ArrayList<String>> regions = new ArrayList<>();

//    public static ArrayList<String> region3301 = new ArrayList<>();
//    public static ArrayList<String> region3302 = new ArrayList<>();
//    public static ArrayList<String> region3303 = new ArrayList<>();
//    public static ArrayList<String> region3311 = new ArrayList<>();
//    public static ArrayList<String> region3312 = new ArrayList<>();
//    public static ArrayList<String> region3313 = new ArrayList<>();

    public static ArrayList<String> region3321 = new ArrayList<>();
    public static ArrayList<String> region3322 = new ArrayList<>();
    public static ArrayList<String> region3323 = new ArrayList<>();
    public static ArrayList<String> region3324 = new ArrayList<>();
    public static ArrayList<String> region3325 = new ArrayList<>();

    public static ArrayList<String> region3331 = new ArrayList<>();
    public static ArrayList<String> region3332 = new ArrayList<>();
    public static ArrayList<String> region3333 = new ArrayList<>();
    public static ArrayList<String> region3334 = new ArrayList<>();
    public static ArrayList<String> region3335 = new ArrayList<>();

    public static ArrayList<String> region3341 = new ArrayList<>();
    public static ArrayList<String> region3342 = new ArrayList<>();
    public static ArrayList<String> region3343 = new ArrayList<>();
    public static ArrayList<String> region3344 = new ArrayList<>();
    public static ArrayList<String> region3345 = new ArrayList<>();

    public static ArrayList<String> region3501 = new ArrayList<>();
    public static ArrayList<String> region3502 = new ArrayList<>();
    public static ArrayList<String> region3503 = new ArrayList<>();
    public static ArrayList<String> region3504 = new ArrayList<>();
    public static ArrayList<String> region3505 = new ArrayList<>();

    public static ArrayList<String> region3511 = new ArrayList<>();
    public static ArrayList<String> region3512 = new ArrayList<>();
    public static ArrayList<String> region3513 = new ArrayList<>();
    public static ArrayList<String> region3514 = new ArrayList<>();
    public static ArrayList<String> region3515 = new ArrayList<>();

    public static ArrayList<String> region3521 = new ArrayList<>();
    public static ArrayList<String> region3522 = new ArrayList<>();
    public static ArrayList<String> region3523 = new ArrayList<>();
    public static ArrayList<String> region3524 = new ArrayList<>();
    public static ArrayList<String> region3525 = new ArrayList<>();

    public static ArrayList<String> region3531 = new ArrayList<>();
    public static ArrayList<String> region3532 = new ArrayList<>();
    public static ArrayList<String> region3533 = new ArrayList<>();
    public static ArrayList<String> region3534 = new ArrayList<>();
    public static ArrayList<String> region3535 = new ArrayList<>();

    public static ArrayList<String> region3541 = new ArrayList<>();
    public static ArrayList<String> region3542 = new ArrayList<>();
    public static ArrayList<String> region3543 = new ArrayList<>();
    public static ArrayList<String> region3544 = new ArrayList<>();
    public static ArrayList<String> region3545 = new ArrayList<>();

    public static ArrayList<String> region3711 = new ArrayList<>();
    public static ArrayList<String> region3712 = new ArrayList<>();
    public static ArrayList<String> region3713 = new ArrayList<>();
    public static ArrayList<String> region3714 = new ArrayList<>();
    public static ArrayList<String> region3715 = new ArrayList<>();

    public static ArrayList<String> region3721 = new ArrayList<>();
    public static ArrayList<String> region3722 = new ArrayList<>();
    public static ArrayList<String> region3723 = new ArrayList<>();
    public static ArrayList<String> region3724 = new ArrayList<>();
    public static ArrayList<String> region3725 = new ArrayList<>();

    public static ArrayList<String> region3731 = new ArrayList<>();
    public static ArrayList<String> region3732 = new ArrayList<>();
    public static ArrayList<String> region3733 = new ArrayList<>();
    public static ArrayList<String> region3734 = new ArrayList<>();
    public static ArrayList<String> region3735 = new ArrayList<>();

    public static ArrayList<String> region3741 = new ArrayList<>();
    public static ArrayList<String> region3742 = new ArrayList<>();
    public static ArrayList<String> region3743 = new ArrayList<>();
    public static ArrayList<String> region3744 = new ArrayList<>();
    public static ArrayList<String> region3745 = new ArrayList<>();

    public static ArrayList<String> region3751 = new ArrayList<>();
    public static ArrayList<String> region3752 = new ArrayList<>();
    public static ArrayList<String> region3753 = new ArrayList<>();
    public static ArrayList<String> region3754 = new ArrayList<>();
    public static ArrayList<String> region3755 = new ArrayList<>();


    public static ArrayList<int[]> regionCoords = new ArrayList<>();

    public static ArrayList<String> connections = new ArrayList<>();

    public static RunMode.scanFrequencyMode scanMode = RunMode.scanFrequencyMode.USE_EACH_ONE;
    public static RunMode.fingerprintMatchingMode matchingMode = RunMode.fingerprintMatchingMode.INCLUDE_PREVIOUS;
    public static RunMode.sidePathMode sideMode = RunMode.sidePathMode.DYNAMIC;

    public String inputFileName = "Trial1R";

    public static Activity act;
    public static SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
    public static int updateSound,alertSound;

    final static int numberOfQRCodes = 37;

    static RunMode currentRunMode;

    public Assets(AssetManager as, Activity a) {

        this.act = a;

//        try {
//            //set base and stair weights
//            int baseWeight = 10;
//            int stairWeight = 15;
//            G = new Graph(",", as.open(inputFileName), baseWeight, stairWeight);
//            G.gradientGraph(G);
//
//            InputStream QRis = as.open(inputFileName);
//            BufferedReader r = new BufferedReader(new InputStreamReader(QRis));
//
//            int lineNumber = 0;
//
//            String line;
//            if (QRis != null) {
//                while ((line = r.readLine()) != null && lineNumber < numberOfQRCodes) {
//                    lineNumber++;
//                    Pattern pattern1 = Pattern.compile(Pattern.quote(","));
//                    String[] splitRaw = pattern1.split(line);
//
//                    if (line.startsWith("//")) {
//                        lineNumber--;
//                        //nothing
//                    } else {
//                        int points = Integer.parseInt(splitRaw[1]);
////                        if(points>0){
//                        String full = splitRaw[0];
//                        QRMap.put(full, new QRCodeLocation(full, points));
//                        QRLocations.add(full + ":" + splitRaw[1]);
//                    }
//
//                }
//            }
//            r.close();
//            QRis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        regions.add(region3301);
//        regions.add(region3302);
//        regions.add(region3303);
//
//        regions.add(region3311);
//        regions.add(region3312);
//        regions.add(region3313);

        regions.add(region3321);
        regions.add(region3322);
        regions.add(region3323);
        regions.add(region3324);
        regions.add(region3325);

        regions.add(region3331);
        regions.add(region3332);
        regions.add(region3333);
        regions.add(region3334);
        regions.add(region3335);

        regions.add(region3341);
        regions.add(region3342);
        regions.add(region3343);
        regions.add(region3344);
        regions.add(region3345);


        regions.add(region3501);
        regions.add(region3502);
        regions.add(region3503);
        regions.add(region3504);
        regions.add(region3505);

        regions.add(region3511);
        regions.add(region3512);
        regions.add(region3513);
        regions.add(region3514);
        regions.add(region3515);

        regions.add(region3521);
        regions.add(region3522);
        regions.add(region3523);
        regions.add(region3524);
        regions.add(region3525);

        regions.add(region3531);
        regions.add(region3532);
        regions.add(region3533);
        regions.add(region3534);
        regions.add(region3535);

        regions.add(region3541);
        regions.add(region3542);
        regions.add(region3543);
        regions.add(region3544);
        regions.add(region3545);


        regions.add(region3711);
        regions.add(region3712);
        regions.add(region3713);
        regions.add(region3714);
        regions.add(region3715);

        regions.add(region3721);
        regions.add(region3722);
        regions.add(region3723);
        regions.add(region3724);
        regions.add(region3725);

        regions.add(region3731);
        regions.add(region3732);
        regions.add(region3733);
        regions.add(region3734);
        regions.add(region3735);

        regions.add(region3741);
        regions.add(region3742);
        regions.add(region3743);
        regions.add(region3744);
        regions.add(region3745);

        regions.add(region3751);
        regions.add(region3752);
        regions.add(region3753);
        regions.add(region3754);
        regions.add(region3755);


        try {
            InputStream is = as.open("NodeMapRegions");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            int regionNumber = -1;

            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    if (line.startsWith("//")) {
                        regionNumber++;
                    } else {
                        regions.get(regionNumber).add(line);
                    }

                }
            }
            r.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = as.open("Floor35_2");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    if (line.startsWith("//")) {
                        //nothing
                    } else {
                        floor35_2.add(line);
                    }

                }
            }
            r.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = as.open("Floor35_3");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    if (line.startsWith("//")) {
                        //nothing
                    } else {
                        floor35_3.add(line);
                    }

                }
            }
            r.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = as.open("RegionCoords");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String[] brokenLine;

            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    brokenLine = line.split(",");
                    if (line.startsWith("//")) {
                        //
                    } else if (brokenLine.length == 1) {
                        //not done editing txt file
                    } else {
                        int[] pointxy = new int[2];
                        pointxy[0] = Integer.parseInt(brokenLine[0]);
                        pointxy[1] = Integer.parseInt(brokenLine[1]);
                        regionCoords.add(pointxy);
                    }
                }
            }
            r.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = as.open("StairConnections");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    if (line.startsWith("//")) {
                        //
                    } else {
                        connections.add(line);
                    }
                }
            }
            r.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateSound = soundPool.load(act,R.raw.industrial_alarm,1);
        alertSound = soundPool.load(act,R.raw.red_alert_2,1);
    }

    static void setRunModeActivity(RunMode rm){currentRunMode=rm;}

    static void setActivity(Activity a){
        act = a;
    }

    static void playUpdateSound(){
        soundPool.play(updateSound, 0.5f, 0.5f,1, 0, 1.0f);
    }

    static void playAlertSound(){
        soundPool.play(alertSound, 0.5f, 0.5f,1, 0, 1.0f);
    }

    public static void saveData(ArrayList<GridData> dataList) {
        strongerDataList = dataList;
    }

    public static void setGoal(String s) {
        goal = s;
    }

    public static void setUpdate(String s){updateString=s;}

    public static void setMode(String s) {
        mode = s;
    }

    public static void setScanMode(String s) {
        if (s.equals("1")) {
            scanMode = RunMode.scanFrequencyMode.USE_EACH_ONE;
        } else if (s.equals("2")) {
            scanMode = RunMode.scanFrequencyMode.AVERAGE_TWO;
        }
    }

    public static void setTrialNumber(String s){
        trialNumber=s;
    }

    public static void setGraph(String filename,AssetManager as){

        try {
            //set base and stair weights
            int baseWeight = 10;
            int stairWeight = 15;
            G = new Graph(",", as.open(filename), baseWeight, stairWeight);
//            G.gradientGraph(G);

            InputStream QRis = as.open(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(QRis));

            int lineNumber = 0;

            QRMap.clear();
            QRLocations.clear();

            String line;
            if (QRis != null) {
                while ((line = r.readLine()) != null && lineNumber < numberOfQRCodes) {
                    lineNumber++;
                    Pattern pattern1 = Pattern.compile(Pattern.quote(","));
                    String[] splitRaw = pattern1.split(line);

                    if (line.startsWith("//")) {
                        lineNumber--;
                        //nothing
                    } else {
                        int points = Integer.parseInt(splitRaw[1]);
                        String full = splitRaw[0];
                        QRMap.put(full, new QRCodeLocation(full, points));
                        QRLocations.add(full + ":" + splitRaw[1]);
                    }

                }
            }
            r.close();
            QRis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void setMatchingMode(String s) {
        if (s.equals("include")) {
            matchingMode = RunMode.fingerprintMatchingMode.INCLUDE_PREVIOUS;
        } else if (s.equals("exclude")) {
            matchingMode = RunMode.fingerprintMatchingMode.EXCLUDE_PREVIOUS;
        }
    }

    public static void setSideViewMode(String s) {
        if (s.equals("static")) {
            sideMode = RunMode.sidePathMode.STATIC;
        } else if (s.equals("dynamic")) {
            sideMode = RunMode.sidePathMode.DYNAMIC;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

//        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        RectF rectF = new RectF(rect);
//        float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        canvas.drawRoundRect(new RectF(rect), (float) pixels, (float) pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
