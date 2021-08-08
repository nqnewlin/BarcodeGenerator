package com.newlin.barcodegenerator.ui.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.newlin.barcodegenerator.MainActivity;
import com.newlin.barcodegenerator.R;
import com.newlin.barcodegenerator.ScreenScanner.ScreenCaptureService;
import com.newlin.barcodegenerator.ui.helpers.CRUD;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class ScannerFragment extends Fragment {
    Callback iCallback;
    private boolean legacy = false;

    private ScannerViewFragment scannerViewFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        scannerViewFragment =
                new ViewModelProvider(this).get(ScannerViewFragment.class);
        View root = inflater.inflate(R.layout.fragment_scanner, container, false);


        Button startButton = (Button) root.findViewById(R.id.button);
        startButton.setText("Start Scan");
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanButton(root, startButton);
            }
        });

        CardView infoCard = (CardView) root.findViewById(R.id.instruction_info);
        TextView instructions = (TextView)  root.findViewById(R.id.instructions);
        instructions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                infoCard.setVisibility(View.VISIBLE);
            }
        });

        TextView ok = (TextView) root.findViewById(R.id.ok_text);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.INVISIBLE);
            }
        });


        // TODO remove this button when debug not needed
        Button stopButton = (Button) root.findViewById(R.id.button2);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).stopProjection(root);
            }
        });

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            iCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public void scanButton(View view, Button button) {
        String name = button.getText().toString();
        switch (name){
            case "Start Scan":
                //button.setText("Stop Scan");
                ((MainActivity) getActivity()).startProjection(view);
                break;
            case "Stop Scan":
                button.setText("Start Scan");
                ((MainActivity) getActivity()).stopProjection(view);
                break;
            default:
                break;
        }
    }
}