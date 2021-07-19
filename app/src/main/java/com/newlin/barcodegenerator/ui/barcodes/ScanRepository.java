package com.newlin.barcodegenerator.ui.barcodes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import Database.AppDatabase;
import Database.DepartmentInfo;
import Database.DeptDao;

public class ScanRepository {
    private DeptDao mDeptDao;
    private LiveData<List<DepartmentInfo>> mDepartmentInfo;

    ScanRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mDeptDao = db.deptDao();
        mDepartmentInfo = mDeptDao.findAll();
    }

    LiveData<List<DepartmentInfo>> getAllInfo() {
        return mDepartmentInfo;
    }

    void insert(DepartmentInfo departmentInfo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDeptDao.save(departmentInfo);
        });
    }
}
