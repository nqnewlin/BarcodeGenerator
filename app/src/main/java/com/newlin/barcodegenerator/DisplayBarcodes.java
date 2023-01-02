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
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
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
import java.util.Timer;
import java.util.TimerTask;


public class DisplayBarcodes extends AppCompatActivity {
    ArrayList<Upc> upcs;
    private Object mPauseLock;
    private boolean mPaused = true;
    private boolean mFinished;
    CountDownTimer timer;
    int interval;
    ImageView barcode;
    int imageIterator = 1;
    private ProgressBar mLoadingProgressBar;
    MaterialButton playPauseButton;
    private Handler mHandler = new Handler();
    private SeekBar seekBar;
    private TextView seekProgress;
    private TextView seekTotal;


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

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(imageIterator-1);
        seekProgress = (TextView) findViewById(R.id.seekProgress);
        seekTotal = (TextView) findViewById(R.id.seekTotal);

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
                        seekBar.setMax(upcs.size());
                        seekTotal.setText(String.valueOf(upcs.size()));
                        seekProgress.setText("1");
                        Log.d("Size: ", String.valueOf(upcs.size()));
                    }
                });
            }
        }).start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                imageIterator = i;
                try {
                    barcode.setImageBitmap(upcs.get(imageIterator-1).getImage());
                    seekProgress.setText(String.valueOf(imageIterator + 1));
                } catch (Exception e) {
                    Log.e("Error: ", String.valueOf(e));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        interval = 3;

        barcode = (ImageView) findViewById(R.id.quickBarcode);

        playPauseButton = (MaterialButton) findViewById(R.id.startPause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Paused
                if(mPaused) {
                    mPaused = false;
                    playPauseButton.setIcon(getDrawable(R.drawable.ic_pause));
                    if (!upcs.isEmpty()) {
                        barcode.setImageBitmap(upcs.get(0).getImage());
                    }
                    timerStart();
                } else {
                    mPaused = true;
                    playPauseButton.setIcon(getDrawable(R.drawable.ic_play));
                    timer.cancel();
                }
            }
        });

        ExtendedFloatingActionButton play = (ExtendedFloatingActionButton) findViewById(R.id.floatingPlayButton);

        CardView playCard = (CardView) findViewById(R.id.flash_barcode);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playCard.setVisibility(View.VISIBLE);
                //playPauseButton.setText("Start");
                playPauseButton.setIcon(getDrawable(R.drawable.ic_play));
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
                imageIterator = 0;
                try {
                    timer.cancel();
                    seekBar.setProgress(imageIterator);
                    seekProgress.setText(String.valueOf(imageIterator+1));
                } catch (Exception e) {

                }
                play.setVisibility(View.VISIBLE);
                mPaused = true;
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
        mFinished = false;

        timer = new CountDownTimer(time, speed) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (imageIterator < upcs.size()) {
                    barcode.setImageBitmap(upcs.get(imageIterator).getImage());
                    imageIterator++;
                    seekBar.setProgress(imageIterator);
                    seekProgress.setText(String.valueOf(imageIterator));
                }
                if (imageIterator >= upcs.size()) {
                    imageIterator = 0;
                    playPauseButton.setIcon(getDrawable(R.drawable.ic_restart));
                    mPaused = true;
                    mFinished = true;
                    timer.cancel();
                }
            }

            @Override
            public void onFinish() {
                playPauseButton.setText("Start");
                if (mFinished) {
                    playPauseButton.setIcon(getDrawable(R.drawable.ic_restart));
                    imageIterator = 0;
                } else {
                    playPauseButton.setIcon(getDrawable(R.drawable.ic_play));
                }
                mPaused = true;
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