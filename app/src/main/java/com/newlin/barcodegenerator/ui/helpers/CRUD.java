package com.newlin.barcodegenerator.ui.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CRUD {

    private static final String TAG = "delete";
    private static CRUD sCRUD;
    private List<String> deleteFileList = new ArrayList<>();

    public static CRUD getInstance() {
        if (sCRUD == null) {
            sCRUD = new CRUD();
        }
        return sCRUD;
    }

    private CRUD() {
    }

    public boolean saveDeptScannedList(Context context, List<String> strings) {
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

    public static void deleteScannedList(Context context) {
        String temp = readFile(context);
        String[] files = temp.split(",", 0);

        String tempFiles;
        File dir = context.getFilesDir();
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
    }

    public static List deleteDuplicates(List<String> strings) {
        List<String> modifiedStrings = new ArrayList<>();
        Set<String> set = new HashSet<>(strings);
        strings.clear();
        strings.addAll(set);
        return strings;
    }

    public static String readFile(Context context) {
        try {
            String fileName = "scan_list.json";
            FileInputStream fis = context.openFileInput(fileName);
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

    public static boolean saveDeptDeleteList(Context context, String string) {
        try {
            File internalStorageDir = context.getFilesDir();

            String fileName = "delete_list.json";
            File list = new File(internalStorageDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(list);

            String jsonString = new Gson().toJson(string);
            if (jsonString != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
            fileOutputStream.close();
            return true;

        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public static String[] readScannedListFile(Context context, String file) {
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
    }
