package com.example.matthew.newapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Switch;


public class ChooseNavModeAndGoal extends Activity {

    Button QRScan, AdaptableB, AdaptiveB;
    Switch scanOptionSwitch, matchOptionSwitch, sideViewOptionSwitch;

    enum NavMode {
        NONE, ADAPTIVE, ADAPTABLE, WALK
    }

    NavMode currentMode = NavMode.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_nav_mode_and_goal);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        QRScan = (Button) findViewById(R.id.goalQRButton);
        AdaptableB = (Button) findViewById(R.id.adaptableButton);
        AdaptiveB = (Button) findViewById(R.id.adaptiveButton);
        scanOptionSwitch = (Switch) findViewById(R.id.scanOptionSwitch);
        matchOptionSwitch = (Switch) findViewById(R.id.matchOptionSwitch);
        sideViewOptionSwitch = (Switch) findViewById(R.id.sideViewOptionSwitch);

        scanOptionSwitch.setSwitchTextAppearance(getApplicationContext(), R.style.SwitchTextAppearance);
        matchOptionSwitch.setSwitchTextAppearance(getApplicationContext(), R.style.SwitchTextAppearance);
        sideViewOptionSwitch.setSwitchTextAppearance(getApplicationContext(), R.style.SwitchTextAppearance);

        //TODO - this class isn't being used anymore - replaced by ChooseTrial

        AdaptableB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                currentMode = NavMode.ADAPTABLE;
                Assets.setMode("adaptable");
//                currentMode = NavMode.WALK;
//                Assets.setMode("walk");

                AdaptableB.setBackgroundResource(R.drawable.round_button_highlighted);
                AdaptiveB.setBackgroundResource(R.drawable.round_button_outline);
            }
        });

        AdaptiveB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                currentMode = NavMode.ADAPTIVE;
                Assets.setMode("adaptive");
                AdaptiveB.setBackgroundResource(R.drawable.round_button_highlighted);
                AdaptableB.setBackgroundResource(R.drawable.round_button_outline);
            }
        });

        QRScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (currentMode != NavMode.NONE) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(ChooseNavModeAndGoal.this);
                    adb.setCancelable(true);
                    adb.setTitle("QR code scanner");
                    adb.setMessage("Scan goal location");
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    adb.setPositiveButton("Scan!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        if(!firstQRscan){appendLog("QR","pressed",true);}
                            IntentIntegrator integrator = new IntentIntegrator(ChooseNavModeAndGoal.this);
                            integrator.initiateScan();
                        }
                    });
                    adb.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please first select a navigation mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        QRScan.setOnTouchListener(new View.OnTouchListener() {
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
    }

    //Method for handling result of QR Code scan
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {

            // handle scan result
            Log.d("scan result: ", scanResult.getContents());
            String id = scanResult.getContents();
            Assets.setGoal(id);

            if (scanOptionSwitch.isChecked()) {
                Assets.setScanMode("2");
            } else if (!scanOptionSwitch.isChecked()) {
                Assets.setScanMode("1");
            }

            if (matchOptionSwitch.isChecked()) {
                Assets.setMatchingMode("exclude");
            } else if (!matchOptionSwitch.isChecked()) {
                Assets.setMatchingMode("include");
            }

            if (sideViewOptionSwitch.isChecked()) {
                Assets.setSideViewMode("dynamic");
            } else if (!sideViewOptionSwitch.isChecked()) {
                Assets.setSideViewMode("static");
            }

            Intent nextScreen = new Intent(getApplicationContext(), RunMode.class);
            startActivity(nextScreen);
        }
    }
}
