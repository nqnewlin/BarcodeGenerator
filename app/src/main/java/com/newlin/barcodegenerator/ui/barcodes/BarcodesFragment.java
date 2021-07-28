package com.newlin.barcodegenerator.ui.barcodes;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.newlin.barcodegenerator.R;
import com.newlin.barcodegenerator.ScreenScanner.ProcessScans;
import com.newlin.barcodegenerator.Upc;
import com.newlin.barcodegenerator.ui.helpers.CRUD;
import com.newlin.barcodegenerator.ui.helpers.SwipeToDeleteCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import static android.content.ContentValues.TAG;

public class BarcodesFragment extends Fragment {

    ArrayList<Departments> departments;
    RecyclerView rvItems;
    ScannedBarcodesAdapter adapter;
    View root;
    String deptList;
    private static String KEY_DEPT_LIST = "departmentList";
    boolean isInitialized;
    private List<String> deleteFileList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_barcodes, container, false);

        rvItems = (RecyclerView) root.findViewById(R.id.rvContacts);

        enableSwipeToDeleteAndUndo();

        deptList = readFile("scan_list");

        if (deptList != null) {
            departments = Departments.createDepartmentList(getContext(), deptList);

            adapter = new ScannedBarcodesAdapter(departments);
            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(root.getContext()));
        } else {
            root = inflater.inflate(R.layout.fragment_barcodes_blank, container, false);
        }

        return root;
    }

    private String readFile(String name) {
        try {
            String fileName = name + ".json";
            FileInputStream fis = getContext().openFileInput(fileName);
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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new
                SwipeToDeleteCallback(getContext()) {
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        final Departments item = adapter.getData().get(position);

                        adapter.removeItem(position);

                        String removeDept = item.getmScanId();

                        /* Add undo option later
                        Snackbar snackbar = snackbar = Snackbar.make(root, "Item was removed from the list.",
                                Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.restoreItem(item, position);
                                rvItems.scrollToPosition(position);
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                        */

                        removeFromDeptList(removeDept);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(rvItems);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY_DEPT_LIST, departments);
    }

    @Override
    public void onPause() {
        super.onPause();
        deleteScannedList();
    }

    public boolean removeFromDeptList(String remove) {
        deptList = readFile("scan_list");
        String[] files = deptList.split(",", 0);
        List<String> strings = new ArrayList<>();
        String temp;
        for (int i = 0; i < files.length; i++) {
            temp = files[i].replaceAll("\"", "").replaceAll("\\[", "")
                    .replaceAll("\\]", "");
            if (!temp.equals(remove)) {
                strings.add(temp);
            }
        }

        if (strings.size() < 1) {
            File dir = getContext().getFilesDir();
            File fileList = new File(dir, "scan_list.json");
            boolean deleted = fileList.delete();
            saveFileDeleteList(getContext(), remove);
            return true;
        }

        try {
            File internalStorageDir = getContext().getFilesDir();
            String fileName = "scan_list.json";
            File list = new File(internalStorageDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(list);
            String jsonString = new Gson().toJson(strings);
            if (jsonString != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
            saveFileDeleteList(getContext(), remove);

        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
        return true;
    }

    private boolean saveFileDeleteList(Context context, String string) {
        String[] deleteFiles = CRUD.readScannedListFile(context, "delete_list");

        String tempString;
        if (deleteFiles != null) {
            for (int i = 0; i < deleteFiles.length; i ++) {
                tempString = deleteFiles[i].replaceAll("\"", "").replaceAll("\\[", "")
                        .replaceAll("\\]", "");
                deleteFileList.add(tempString);
            }
        }
        deleteFileList.add(string);

        try {
            File internalStorageDir = context.getFilesDir();
            String fileName = "delete_list.json";
            File list = new File(internalStorageDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(list);
            CRUD.deleteDuplicates(deleteFileList);
            String jsonString = new Gson().toJson(deleteFileList);
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

    public void deleteScannedList() {
        String temp = readFile("delete_list");
        if (temp != null) {
            String[] files = temp.split(",", 0);

            String tempFiles;
            File dir = getContext().getFilesDir();
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
            File fileList = new File(dir, "delete_list.json");
            boolean deleted = fileList.delete();
        }
    }

}