package com.newlin.barcodegenerator.ui.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
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

    private ScannerViewFragment scannerViewFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scannerViewFragment =
                new ViewModelProvider(this).get(ScannerViewFragment.class);
        View root = inflater.inflate(R.layout.fragment_scanner, container, false);


        Button startButton = (Button) root.findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               ((MainActivity) getActivity()).startProjection(root);
            }
        });

        Button stopButton = (Button) root.findViewById(R.id.button2);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).stopProjection(root);
            }
        });

        Button clearButton = (Button) root.findViewById(R.id.button3);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).deleteScannedList();
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




}