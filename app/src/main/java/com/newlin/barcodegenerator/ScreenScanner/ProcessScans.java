package com.newlin.barcodegenerator.ScreenScanner;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessScans {
    private String mScanId;
    private List<String> mScans;
    private List<String> mScannedDepts;
    private String mScanTime;
    private String mScanSource;
    private List<String> scanFileList = new ArrayList<>();

    public ProcessScans(List<String> scans, List<String> scannedDepts, String scanSource) {
        DateFormat date = new SimpleDateFormat("EEE, MMM dd yyy, h:mm aaa");
        String dateFormatted = date.format(Calendar.getInstance().getTime());
        long time = System.currentTimeMillis();
        mScans = scans;
        mScannedDepts = scannedDepts;
        mScanTime = dateFormatted;
        mScanSource = scanSource;
        mScanId = String.valueOf(time);
    }

    public boolean saveScansToDatabase(Context context, ProcessScans processScans) {
        String stringToSave;

        List<String> scans = processScans.getmScans();
        deleteDuplicates(scans);

        String jsonScanString = new Gson().toJson(scans);

        List<String> depts = processScans.getmScannedDepts();
        deleteDuplicates(depts);
        StringBuilder sb = new StringBuilder();
        int temp = 0;
        boolean oversize = false;
        for (int i = 0; i < depts.size(); i++) {
            if (i < 5) {
                sb.append(depts.get(i));
                if (depts.size() > (i + 1)) {
                    sb.append(", ");
                }
            } else {
                if (i > 5) {
                    temp = i + 1;
                    oversize = true;
                }
            }
        }
        if (oversize) {
            sb.append("and " + String.valueOf(depts.size() + 1) + " more.");
        }
        String deptScanned = sb.toString();

        // Save all parameters minus scans to single string
        String saveToFile = processScans.getmScanId() + "." + deptScanned + "." + processScans.getmScanTime()
                + "." + processScans.getmScanSource();

        String fileName = processScans.getmScanId() + ".json";
        String listName = processScans.getmScanId() + "_scans.json";
        try {
            //First save scanId, scanTime, scanDepts, scanSource to single file
            File internalStorageDir = context.getFilesDir();

            File list = new File(internalStorageDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(list);
            if (saveToFile != null) {
                fileOutputStream.write(saveToFile.getBytes());
            }
            fileOutputStream.close();

            //Second save scans to seperate file
            File scanList = new File(internalStorageDir, listName);
            FileOutputStream fos = new FileOutputStream(scanList);
            if (jsonScanString != null) {
                fos.write(jsonScanString.getBytes());
            }
            fos.close();
            scans.clear();

        } catch (FileNotFoundException fileNotFount) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

        //add scanId to master list of files
        String[] savedFiles = readScannedListFile(context, "scan_list");

        String tempString;
        if (savedFiles != null) {
            for (int i = 0; i < savedFiles.length; i ++) {
                tempString = savedFiles[i].replaceAll("\"", "").replaceAll("\\[", "")
                        .replaceAll("\\]", "");
                scanFileList.add(tempString);
            }
        }
        if (scanFileList != null) {
            Collections.sort(scanFileList);
        }
        /*
        if (scanFileList.size() == 0) {
            scanFileList.add("0");
        }

         */

        scanFileList.add(processScans.getmScanId());

        saveFileScannedList(context, scanFileList);

        return true;
    }

    public String[] readScannedListFile(Context context, String file) {
        try {
            String fileName = file + ".json";
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            isr.close();
            String string = sb.toString();
            string.replaceAll("\\[", "");
            String[] files = string.split(",", 0);

            String temp;
            for (int i = 0; i < files.length; i++) {
                temp = files[i].replaceAll("\"", "").replaceAll("\\[", "")
                        .replaceAll("\\]", "");
                files[i] = temp;
            }

            return files;

        } catch (FileNotFoundException fileNotFount) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean saveFileScannedList(Context context, List<String> strings) {
        try {
            File internalStorageDir = context.getFilesDir();
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
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public List<String> getmScans() { return mScans; }

    public List<String> getmScannedDepts() { return mScannedDepts; }

    public String getmScanSource() { return mScanSource; }

    public String getmScanId() { return mScanId; }

    public String getmScanTime() { return mScanTime; }

    // Check list for duplicates and delete
    public List deleteDuplicates(List<String> strings) {
        Set<String> set = new HashSet<>(strings);
        strings.clear();
        strings.addAll(set);
        return strings;
    }




}
