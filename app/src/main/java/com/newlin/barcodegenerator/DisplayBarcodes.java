package com.newlin.barcodegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.newlin.barcodegenerator.Upc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DisplayBarcodes extends AppCompatActivity {
    ArrayList<Upc> upcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_barcodes);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

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
        upcs = Upc.createCodeList(fileInput);

        CodesAdapter adapter = new CodesAdapter(upcs);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
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

}