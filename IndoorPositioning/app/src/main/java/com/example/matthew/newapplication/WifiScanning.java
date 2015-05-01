package com.example.matthew.newapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class WifiScanning extends Activity {

    Button wifiButton;
    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    RouterObject wifis[];
    long startScanTime, timeForScan = 0l;
    int numberOfRoutersSaved = 9;
    TextView text1, text2, text3, text4, text5, text6, text7, text8, text0;
    TextView text0_extra, text1_extra, text2_extra, text3_extra, text4_extra, text5_extra, text6_extra, text7_extra, text8_extra;
    int previousLevel0, previousLevel1, previousLevel2, previousLevel3, previousLevel4, previousLevel5, previousLevel6, previousLevel7, previousLevel8 = 0;
    TextView RouterNumber, ScanTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scanning);

        startScanTime = System.currentTimeMillis();

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "rawr");

        text0 = (TextView) findViewById(R.id.textView0);
        text1 = (TextView) findViewById(R.id.textView1);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        text4 = (TextView) findViewById(R.id.textView4);
        text5 = (TextView) findViewById(R.id.textView5);
        text6 = (TextView) findViewById(R.id.textView6);
        text7 = (TextView) findViewById(R.id.textView7);
        text8 = (TextView) findViewById(R.id.textView8);

        text0_extra = (TextView) findViewById(R.id.textView0_);
        text1_extra = (TextView) findViewById(R.id.textView1_);
        text2_extra = (TextView) findViewById(R.id.textView2_);
        text3_extra = (TextView) findViewById(R.id.textView3_);
        text4_extra = (TextView) findViewById(R.id.textView4_);
        text5_extra = (TextView) findViewById(R.id.textView5_);
        text6_extra = (TextView) findViewById(R.id.textView6_);
        text7_extra = (TextView) findViewById(R.id.textView7_);
        text8_extra = (TextView) findViewById(R.id.textView8_);

        RouterNumber = (TextView) findViewById(R.id.number_of_routers);
        ScanTime = (TextView) findViewById(R.id.scan_time);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //mainWifiObj.disconnect();
        mainWifiObj.startScan();
    }

    @Override
    protected void onPause() {
        // unregister listener
        // mainWifiObj.reconnect();
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_scanning, menu);
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

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

            timeForScan = System.currentTimeMillis() - startScanTime;
            if (timeForScan < 10000) {
                ScanTime.setText("Scan Time:\n" + String.valueOf(timeForScan) + "\nmilliseconds");
            }
            RouterNumber.setText(String.valueOf(wifiScanList.size()) + " Routers");
            //Log.d("Scan Time", String.valueOf(timeForScan) + " milliseconds *********************8");
            //Log.d("Scan", String.valueOf(wifiScanList.size())+" routers found");

            startScanTime = System.currentTimeMillis();
            mainWifiObj.startScan();


            //startAlgTime = System.currentTimeMillis();

            wifis = new RouterObject[numberOfRoutersSaved];
            for (int i = 0; i < 9; i++) {
                int level = wifiScanList.get(i).level;
                //String name = wifiScanList.get(i).SSID;
                String id = wifiScanList.get(i).BSSID;
                //int strength = WifiManager.calculateSignalLevel(level,100);
                int strength = level;
                RouterObject router = new RouterObject(id, strength);
                wifis[i] = router;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(strength, 60);
                text1.setLayoutParams(layoutParams);
            }

            int height = convertToPx(60);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertToPx(100 + 4 * wifis[0].getStrength()), height);
            text0.setLayoutParams(layoutParams);
            text0.setText(wifis[0].printBSSID());
            text0_extra.setText(changeInLevel(previousLevel0, wifis[0].getStrength()));
            previousLevel0 = wifis[0].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[1].getStrength()), height);
            text1.setLayoutParams(layoutParams);
            text1.setText(wifis[1].printBSSID());
            text1_extra.setText(changeInLevel(previousLevel1, wifis[1].getStrength()));
            previousLevel1 = wifis[1].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[2].getStrength()), height);
            text2.setLayoutParams(layoutParams);
            text2.setText(wifis[2].printBSSID());
            text2_extra.setText(changeInLevel(previousLevel2, wifis[2].getStrength()));
            previousLevel2 = wifis[2].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[3].getStrength()), height);
            text3.setLayoutParams(layoutParams);
            text3.setText(wifis[3].printBSSID());
            text3_extra.setText(changeInLevel(previousLevel3, wifis[3].getStrength()));
            previousLevel3 = wifis[3].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[4].getStrength()), height);
            text4.setLayoutParams(layoutParams);
            text4.setText(wifis[4].printBSSID());
            text4_extra.setText(changeInLevel(previousLevel4, wifis[4].getStrength()));
            previousLevel4 = wifis[4].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[5].getStrength()), height);
            text5.setLayoutParams(layoutParams);
            text5.setText(wifis[5].printBSSID());
            text5_extra.setText(changeInLevel(previousLevel5, wifis[5].getStrength()));
            previousLevel5 = wifis[5].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[6].getStrength()), height);
            text6.setLayoutParams(layoutParams);
            text6.setText(wifis[6].printBSSID());
            text6_extra.setText(changeInLevel(previousLevel6, wifis[6].getStrength()));
            previousLevel6 = wifis[6].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[7].getStrength()), height);
            text7.setLayoutParams(layoutParams);
            text7.setText(wifis[7].printBSSID());
            text7_extra.setText(changeInLevel(previousLevel7, wifis[7].getStrength()));
            previousLevel7 = wifis[7].getStrength();

            layoutParams = new LinearLayout.LayoutParams(convertToPx(5 * wifis[8].getStrength()), height);
            text8.setLayoutParams(layoutParams);
            text8.setText(wifis[8].printBSSID());
            text8_extra.setText(changeInLevel(previousLevel8, wifis[8].getStrength()));
            previousLevel8 = wifis[8].getStrength();


            //Arrays.sort(wifis);
            //mostRecentScan = wifis;

            String[] wifiText = new String[numberOfRoutersSaved];
            for (int i = 0; i < numberOfRoutersSaved; i++) {
                wifiText[i] = wifis[i].print();
            }

            if (wifis != null || wifis.length != 0) {
                //dataList.add(new GridData(wifis, lastPosition, buildingnumber, floornumber, "0"));
            } else {
                //showCustomAlert("Routers not found :(");
//                Toast.makeText(getApplicationContext(), "Routers not found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    int convertToPx(int dp) {
        int answer = (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
        return answer;
    }

    String changeInLevel(int previous, int newest) {

        int difference = newest - previous;

        if (difference > 0) {
            String result = "+" + Integer.toString(difference);
            return result;
        } else if (difference < 0) {
            String result = Integer.toString(difference);
            return result;
        } else {
            String result = "";
            return result;
        }
    }

}
