package com.newlin.barcodegenerator.ui.barcodes;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.newlin.barcodegenerator.MainActivity;
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
    TextView text;
    TextView emptyText;
    String deptList;
    private static String KEY_DEPT_LIST = "departmentList";
    private List<String> deleteFileList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // initialize the view and inflate the layout
        root = inflater.inflate(R.layout.fragment_barcodes, container, false);
        text = (TextView) root.findViewById((R.id.textView));

        // set the view when empty list as hidden
        emptyText = (TextView) root.findViewById(R.id.text_dashboard);
        emptyText.setVisibility(View.GONE);

        // Set imagebutton to clear list and set OnClickListener
        ImageButton clear = (ImageButton) root.findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).deleteScannedList();
                if (departments != null && deptList != null && departments.size() > 0) {
                    deleteAllItems();
                    Toast.makeText(getContext(), "List cleared", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Nothing to clear", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvItems = (RecyclerView) root.findViewById(R.id.rvContacts);

        enableSwipeToDeleteAndUndo();

        deptList = readFile("scan_list");

        if (deptList != null) {
            departments = Departments.createDepartmentList(getContext(), deptList);

            adapter = new ScannedBarcodesAdapter(departments);
            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(root.getContext()));
        } else {
            emptyText.setVisibility(View.VISIBLE);
        }

        setHasOptionsMenu(true);

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

                        getSwipeThreshold(viewHolder);

                        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            Log.d("vibrate", "vibrated");
                        } else {
                            //deprecated in API 26
                            v.vibrate(200);
                        }

                        String removeDept = item.getmScanId();

                        /* TODO Add undo option later
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

                        if(departments.size() == 0) {
                            emptyText.setVisibility(getView().VISIBLE);
                            //text.setVisibility(getView().GONE);
                        }

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

    private void deleteItem(View rowView, final int position) {

        Animation anim = AnimationUtils.loadAnimation(requireContext(),
                android.R.anim.slide_out_right);
        anim.setDuration(300);
        rowView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (departments.size() == 0) {
                    //addEmptyView(); // adding empty view instead of the RecyclerView
                    return;
                }
                departments.remove(position); //Remove the current content from the array
                adapter.notifyDataSetChanged(); //Refresh list
            }

        }, anim.getDuration());
    }

    boolean mStopHandler = false;

    private void deleteAllItems() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (departments.size() == 0) {
                    mStopHandler = true;

                    emptyText.setVisibility(getView().VISIBLE);
                }

                if (!mStopHandler) {
                    RecyclerView.LayoutManager manager = rvItems.getLayoutManager();
                    LinearLayoutManager llm = (LinearLayoutManager) manager;
                    int visible = llm.findFirstVisibleItemPosition();
                    if (visible > -1) {
                        View v = rvItems.findViewHolderForAdapterPosition(visible).itemView;
                        deleteItem(v, visible);
                    }
                } else {
                    handler.removeCallbacksAndMessages(null);
                }

                handler.postDelayed(this, 350);
            }
        };
        requireActivity().runOnUiThread(runnable);

    }

}