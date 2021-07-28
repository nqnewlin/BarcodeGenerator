package com.newlin.barcodegenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.newlin.barcodegenerator.DepartmentListings.ScannedDepartments;
import com.newlin.barcodegenerator.ScreenScanner.ProcessScans;
import com.newlin.barcodegenerator.ScreenScanner.ScreenCaptureService;


import javax.security.auth.callback.Callback;

import Database.AppDatabase;
import Database.DepartmentInfo;
import Database.DeptDao;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements Callback {

    private static final int REQUEST_CODE = 100;

    private List<String> scanCodesList = new ArrayList<String>();
    private List<String> deptFileList = new ArrayList<String>();
    private List<String> deptScanCount = new ArrayList<>();
    private ArrayList<ScannedDepartments> scannedDepartments = new ArrayList<ScannedDepartments>();
    private List<String> scannedDeptsList = new ArrayList<>();
    private String testFile = "testFile";
    private String deptFileNumber;
    private String currentDeptNumber;
    private boolean newDept = true;
    public static final String EXTRA_MESSAGE = "com.newlin.barcodegenerator.MESSAGE";
    public static AppDatabase db;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        deptFileNumber = "99";

        EventBus.getDefault().register(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_scanner, R.id.navigation_barcode_enter, R.id.navigation_barcodes, R.id.infoFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // Called when service sends the string containing item numbers
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String string) {

        if (string.matches("[0-9]+\\.") && string.length() < 4) {
            string = string.replace(".", "");
            scannedDeptsList.add(string);
        } else {
            try {
                scanCodesList.add(string);
                Log.e(TAG, "captured string: " + string);
            } catch (Exception e) {
                e.printStackTrace();
            }
            scanCodesList.add(string);
        }

    }

    private boolean saveDeptScannedList(List<String> strings) {
        try {
            File internalStorageDir = getFilesDir();

            String fileName = "scan_list.json";
            File list = new File(internalStorageDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(list);
            deleteDuplicates(strings);
            String jsonString = new Gson().toJson(strings);
            if (jsonString != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
            fileOutputStream.close();
            strings.clear();
            return  true;

        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    // TODO delete code after implementing databse
    // Check list for duplicates
    public List deleteDuplicates(List<String> strings) {
        List<String> modifiedStrings = new ArrayList<>();
        Set<String> set = new HashSet<>(strings);
        strings.clear();
        strings.addAll(set);
        return strings;
    }

    public void startProjection(View view) {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        //TODO see if this is working
        scanCodesList.clear();
    }

    public void stopProjection(View view) {

        ProcessScans processScans = new ProcessScans(scanCodesList, scannedDeptsList, "Screen Scanner");
        processScans.saveScansToDatabase(this, processScans);

        scannedDepartments.clear();
        scannedDeptsList.clear();

        startService(ScreenCaptureService.getStopIntent(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }

    public void deleteScannedList() {
        String temp = readFile();
        String[] files = temp.split(",", 0);

        String tempFiles;
        File dir = getFilesDir();
        String fileName;
        String scanName;
        for (int i = 0; i < files.length; i++) {
            tempFiles = files[i].replaceAll("\"", "").replaceAll("\\[", "")
                    .replaceAll("\\]", "");
            files[i] = tempFiles;
            fileName = files[i] + ".json";
            scanName = files[i] + "_scans.json";
            File file = new File(dir, fileName);
            File scans = new File(dir, scanName);
            boolean deleted = file.delete();
            boolean deleted2 = scans.delete();
        }



        List<String> list = new ArrayList<String>();
        list.add("0");
        saveDeptScannedList(list);
    }

    public String readFile() {
        try {
            String fileName = "scan_list.json";
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