package com.newlin.barcodegenerator.ui.barcodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.newlin.barcodegenerator.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BarcodesFragment extends Fragment {

    ArrayList<Departments> departments;

    private BarcodesViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(BarcodesViewModel.class);
        //AppBarConfiguration appBarConfiguration =
        //        new AppBarConfiguration.Builder(navController.getGraph()).build();
        View root = inflater.inflate(R.layout.fragment_barcodes, container, false);

        RecyclerView rvItems = (RecyclerView) root.findViewById(R.id.rvContacts);

        //rvItems.addItemDecoration(new DividerItemDecoration(rvItems.getContext(), DividerItemDecoration.VERTICAL));

        String deptList = readFile("scan_list");

        departments = Departments.createDepartmentList(getContext(), deptList);




        ScannedBarcodesAdapter adapter = new ScannedBarcodesAdapter(departments);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(root.getContext()));


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


}