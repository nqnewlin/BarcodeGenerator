package com.newlin.barcodegenerator.ui.barcodes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.AppDatabase;
import Database.DepartmentInfo;
import Database.DeptDao;

public class BarcodesViewModel extends AndroidViewModel {

    private final LiveData<List<DepartmentInfo>> departmentInfos;

    private ScanRepository mRepository;



    public BarcodesViewModel(Application application) {
        super(application);

        mRepository = new ScanRepository(application);
        departmentInfos = mRepository.getAllInfo();
    }

    LiveData<List<DepartmentInfo>> getAllData() {
        return departmentInfos;
    }

    public void insert(DepartmentInfo departmentInfo) { mRepository.insert(departmentInfo); }
}