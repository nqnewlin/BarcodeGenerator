package com.newlin.barcodegenerator.DepartmentListings;

import java.util.ArrayList;
import java.util.List;

public class ScannedDepartments {
    private String mDeptNumber;
    private List<String> mScans;

    public ScannedDepartments(String deptNumber, List<String> scans) {
        mDeptNumber = deptNumber;
        mScans = scans;
    }

    public String getmDeptNumber() { return mDeptNumber; }

    public List<String> getmScans() { return mScans; }




}
