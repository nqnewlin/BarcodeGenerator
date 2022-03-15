package com.newlin.barcodegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.newlin.barcodegenerator.Upc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DisplayBarcodes extends AppCompatActivity {
    ArrayList<Upc> upcs;
    private Object mPauseLock;
    private boolean mPaused;
    private boolean mFinished;
    CountDownTimer timer;
    int interval;
    ImageView barcode;
    int imageIterator = 1;
    private ProgressBar mLoadingProgressBar;
    Button playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_barcodes);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        mLoadingProgressBar = findViewById(R.id.loadingProgressBar);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        // sets the scrolling recyclerview
        RecyclerView rvItems = (RecyclerView) findViewById(R.id.rvContacts);

        String deptString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                deptString = null;
            } else {
                deptString = extras.getString("EXTRA_DEPT_NUMBER");
            }
        }else {
            deptString = (String) savedInstanceState.getSerializable("EXTRA_DEPT_NUMBER");
        }

        String fileInput = readFile(deptString);

        new Thread(new Runnable() {
            @Override
            public void run() {
                upcs = Upc.createCodeList(fileInput);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        CodesAdapter adapter = new CodesAdapter(upcs);
                        rvItems.setAdapter(adapter);
                        rvItems.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();

        Slider speedSlider = (Slider) findViewById(R.id.speedSlider);
        speedSlider.addOnChangeListener(seekBarChangeListener);
        interval = Math.round(speedSlider.getValue());

        barcode = (ImageView) findViewById(R.id.quickBarcode);

        playPauseButton = (Button) findViewById(R.id.startPause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playPauseButton.getText().equals("Start")) {
                    playPauseButton.setText("Pause");
                    if (!upcs.isEmpty()) {
                        barcode.setImageBitmap(upcs.get(0).getImage());

                        timerStart();
                    }

                } else if (playPauseButton.getText().equals("Pause")) {
                    timer.cancel();

                    playPauseButton.setText("Resume");
                } else if (playPauseButton.getText().equals("Resume")) {
                    timerStart();

                    playPauseButton.setText("Pause");
                }

            }
        });

        ExtendedFloatingActionButton play = (ExtendedFloatingActionButton) findViewById(R.id.floatingPlayButton);

        CardView playCard = (CardView) findViewById(R.id.flash_barcode);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playCard.setVisibility(View.VISIBLE);
                playPauseButton.setText("Start");
                try {
                    barcode.setImageBitmap(upcs.get(0).getImage());
                } catch (Exception e) {

                }
                play.setClickable(false);
                play.setVisibility(View.INVISIBLE);

            }
        });



        Button exitButton = (Button) findViewById(R.id.exitPlay);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playCard.setVisibility(View.INVISIBLE);
                play.setClickable(true);
                imageIterator = 1;
                try {
                    timer.cancel();
                } catch (Exception e) {

                }
                play.setVisibility(View.VISIBLE);
            }
        });
    }

    private String readFile(String deptNumber) {
        try {

            String fileName = deptNumber + "_scans.json";
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);

            }
            isr.close();
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public void timerStart() {
        int speed = 1000/interval;
        int time = upcs.size() * speed;

        timer = new CountDownTimer(time, speed) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (imageIterator < upcs.size()) {
                    barcode.setImageBitmap(upcs.get(imageIterator).getImage());
                    imageIterator++;
                }
            }

            @Override
            public void onFinish() {
                playPauseButton.setText("Start");
                imageIterator = 1;
            }
        };
        timer.start();

    }

    Slider.OnChangeListener seekBarChangeListener = new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            interval = Math.round(value);
        }
    };

}