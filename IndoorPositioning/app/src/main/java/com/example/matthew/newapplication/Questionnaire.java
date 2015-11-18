package com.example.matthew.newapplication;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.matthew.newapplication.MainActivity;
import com.example.matthew.newapplication.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;


public class Questionnaire extends Activity {

    private TextView question;
    private TextView leftText, middleText, rightText;
    private String userName;
    private String currentQ, surveyName;
    private Button nextQ;
    private Button pre,post, mid, between, sa, last;
    private FileWriter writer;
    private File logFile;
    private ArrayList<String> questionsList = new ArrayList<String>();
    private int questionNumber = 0;
    private EditText input, numberInput;
    private SeekBar slideBar;
    private RelativeLayout slider;
    private LinearLayout buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        pre = (Button) findViewById(R.id.preButton);
        post = (Button) findViewById(R.id.postButton);
        mid = (Button) findViewById(R.id.midBlockButton);
        between = (Button) findViewById(R.id.betweenBlockButton);
        sa = (Button) findViewById(R.id.saButton);
        last = (Button) findViewById(R.id.finalButton);
        buttons = (LinearLayout) findViewById(R.id.buttonLayout);
        question = (TextView) findViewById(R.id.question);
        nextQ = (Button) findViewById(R.id.nextQ);
        nextQ.setVisibility(View.GONE);
        input = (EditText) findViewById(R.id.editText1);
        numberInput = (EditText) findViewById(R.id.editText2);
        slideBar = (SeekBar) findViewById(R.id.slideBar);
        slider = (RelativeLayout) findViewById(R.id.sliderLayout);
        leftText = (TextView) findViewById(R.id.leftText);
        middleText = (TextView) findViewById(R.id.middleText);
        rightText = (TextView) findViewById(R.id.rightText);

        onlyButtons();

        pre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "PreTrainingSurvey";
                initializeQuestions("PreTrainingSurvey");
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "PostTrainingSurvey";
                initializeQuestions("PostTrainingSurvey");
            }
        });
        mid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "EveryThirdTrialSurvey";
                initializeQuestions("EveryThirdTrialSurvey");
            }
        });

        between.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "EveryTrialSurvey";
                initializeQuestions("EveryTrialSurvey");
            }
        });

        sa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "EverySixthTrialSurvey";
                initializeQuestions("EverySixthTrialSurvey");
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                surveyName = "FinalSurvey";
                initializeQuestions("FinalSurvey");
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

            }
        });

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    nextQ.setVisibility(View.VISIBLE);
                    clickNextQButton();
                }
                return false;
            }
        });

        numberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    nextQ.setVisibility(View.VISIBLE);
                    clickNextQButton();
                }
                return false;
            }
        });

        nextQ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                clickNextQButton();
            }
        });
    }

    private void clickNextQButton() {
        if (questionNumber == 0) {
            userName = numberInput.getText().toString();
            questionNumber = 1;
            numberInput.setText("");
            numberInput.setVisibility(View.GONE);
            updateQuestion();
            setupLog();
        } else {

            if (currentQ.startsWith("L")) {
                String text = currentQ.substring(2).toString() + ":\n";
                appendLog(text);
                updateQuestion();

            } else if (currentQ.startsWith("N")) {
                String text = "Question: " + question.getText().toString() + "\nResponse: " + numberInput.getText().toString();
                appendLog(text);
                numberInput.setText("");
                numberInput.setVisibility(View.GONE);
                updateQuestion();
            } else if (currentQ.startsWith("W")) {
                String text = "Question: " + question.getText().toString() + "\nResponse: " + input.getText().toString();
                appendLog(text);
                input.setText("");
                input.setVisibility(View.GONE);
                updateQuestion();
            } else {
                String text = "Question: " + question.getText().toString() + "\nResponse: " + String.valueOf(slideBar.getProgress()) + "/50";
                appendLog(text);
                updateQuestion();
            }
        }
    }

    private void onlyButtons() {
        buttons.setVisibility(View.VISIBLE);
        input.setVisibility(View.GONE);
        numberInput.setVisibility(View.GONE);
        question.setVisibility(View.GONE);
        slider.setVisibility(View.GONE);
        nextQ.setVisibility(View.GONE);
    }

    private void hideButtons() {
        buttons.setVisibility(View.GONE);
    }

    public void initializeQuestions(String filename) {
        try {
            hideButtons();
//            nextQ.setVisibility(View.VISIBLE);
            question.setVisibility(View.VISIBLE);
            numberInput.setVisibility(View.VISIBLE);
            //initialize array of questions and choices from file EveryThirdTrialSurvey
            AssetManager assetManager = getResources().getAssets();
            InputStream is = assetManager.open(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            if (is != null) {
                while ((line = r.readLine()) != null) {
                    questionsList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        slider.setVisibility(View.GONE);
        resetSlider();

    }

    private void resetSlider() {
        slideBar.setMax(50);
        slideBar.setProgress(25);
    }

    private void hideResponses() {
        input.setVisibility(View.GONE);
        slider.setVisibility(View.GONE);
    }

    public void updateQuestion() {

        if (questionNumber <= questionsList.size()) {
            resetSlider();
            String nextQuestion = questionsList.get(questionNumber - 1);
            currentQ = nextQuestion;
//            Log.d("updateQuestion", nextQuestion);

            if (questionNumber == questionsList.size()) {
                nextQ.setText("Finish");
            }

            if (nextQuestion.startsWith("L")) {
                question.setText(nextQuestion.substring(2));
//                questionTitle.setText("");
                nextQ.setVisibility(View.VISIBLE);
                hideResponses();
            } else if (nextQuestion.startsWith("S")) {
                question.setText(nextQuestion.substring(2));
                slider.setVisibility(View.VISIBLE);
                leftText.setText("1\nStrongly\nDisagree");
                middleText.setText("3\nNeutral");
                rightText.setText("5\nStrongly\nAgree");
                nextQ.setVisibility(View.VISIBLE);
            } else if (nextQuestion.startsWith("N")) {
                question.setText(nextQuestion.substring(2));
                numberInput.setVisibility(View.VISIBLE);
                slider.setVisibility(View.GONE);
                nextQ.setVisibility(View.GONE);
            } else if (nextQuestion.startsWith("Q")) {
                question.setText(nextQuestion.substring(2));
                slider.setVisibility(View.VISIBLE);
                leftText.setText("1\nLow");
                middleText.setText("3\nModerate");
                rightText.setText("5\nHigh");
                nextQ.setVisibility(View.VISIBLE);
            } else if (nextQuestion.startsWith("W")) {
                input.setText("");
                question.setText(nextQuestion.substring(2));
                slider.setVisibility(View.GONE);
                input.setVisibility(View.VISIBLE);
                nextQ.setVisibility(View.GONE);
            }


            questionNumber++;
        }

//        if all questions have been answered - return to home page
        else {
//            onlyButtons();
            Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(nextScreen);
        }

    }

    public void setupLog() {
        String time = formatTimeNice();
        String fileName = "sdcard/" + time + " - " + userName + " - " + surveyName + ".txt";
        logFile = new File(fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        appendLog(userName + "- " + surveyName + "\n" + time);
    }

    public void appendLog(String t) {
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(t);
            buf.newLine();
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
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
}
