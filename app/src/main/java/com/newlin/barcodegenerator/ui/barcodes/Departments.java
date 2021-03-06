package com.newlin.barcodegenerator.ui.barcodes;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Departments implements Parcelable {
    private static List<String> departments;
    private String mScanId;
    private String mScannedDepts;
    private String mScanTime;
    private String mScanSource;
    private int mScanCount;



    public Departments(String scanId, String scannedDepts, String scanTime, String scanSource, int scanCount) {
        mScanId = scanId;
        mScannedDepts = scannedDepts;
        mScanTime = scanTime;
        mScanSource = scanSource;
        mScanCount = scanCount;
    }

    public Departments(String scanId, String scannedDepts, String scanTime, String scanSource) {
        mScanId = scanId;
        mScannedDepts = scannedDepts;
        mScanTime = scanTime;
        mScanSource = scanSource;
    }

    private Departments(Parcel in) {
        mScanId = in.readString();
        mScannedDepts = in.readString();
        mScanTime = in.readString();
        mScanSource = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mScanId);
        dest.writeString(mScannedDepts);
        dest.writeString(mScanTime);
        dest.writeString(mScanSource);
    }

    public static final Parcelable.Creator<Departments> CREATOR = new Parcelable.Creator<Departments>() {
        public Departments createFromParcel(Parcel in) {
            return new Departments(in);
        }

        public Departments[] newArray(int size) {
            return new Departments[size];
        }
    };

    public String getmScanId() { return mScanId; }

    public String getmScannedDepts() { return mScannedDepts; }

    public String getmScanTime() { return mScanTime; }

    public String getmScanSource() { return mScanSource; }

    public String getmScanCount() { return String.valueOf(mScanCount); }

    private static int LastCodeId = 0;


    public static ArrayList<Departments> createDepartmentList(Context context, String file) {
        ArrayList<Departments> departments = new ArrayList<Departments>();
        ArrayList<String> counts = new ArrayList<String>();

        file.replaceAll("\\[", "");
        String[] scans = file.split(",", 0);

        String temp;
        for (int i = 0; i < scans.length; i++) {
            temp = scans[i].replaceAll("\"", "").replaceAll("\\[", "")
                    .replaceAll("\\]", "");
            scans[i] = temp;
        }

        // Sort scans by time
        Arrays.sort(scans);

        for (int i = 0; i < scans.length; i++) {
            if (scans[i].length() > 0) {
                String scan_number = scans[i];

                if (!(scan_number.matches("0"))) {

                    String values = readFile(context, scan_number);

                    try {
                        String[] tempValues = values.split("\\.", 0);



                    //if (!(Integer.valueOf(tempValues[4]) == 0)) {
                    //    Log.d("empty", "empty scan list");
                        departments.add(new Departments(tempValues[0], tempValues[1], tempValues[2], tempValues[3], Integer.valueOf(tempValues[4])));
                    //}
                    } catch (Exception e) {
                        Log.d("error", "file not found");
                    }

                } else if (scan_number.matches("0") && scans.length == 1) {
                    departments.add(new Departments("0", null, null, null));
                }
            }
        }

        sortDepartmentList(departments);

        return departments;
    }

    public void removeFromDeptList(Context context, String file) {

    }


    private static String readFile(Context context, String name) {
        try {
            String fileName = name + ".json";
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

    public static ArrayList<Departments> sortDepartmentList(ArrayList<Departments> deptList) {
        ArrayList<Departments> mDeptList = deptList;
        String tempValue;
        String compValue;
        Departments tempDept;
        long temp;

        for (int i = 0; i < (mDeptList.size() - 1); i++) {
            tempValue = mDeptList.get(i).getmScanId();
            tempValue.replaceAll("\"", "");
            temp = Long.parseLong(tempValue);
            int min_idx = i;
            for (int j = i + 1; j < mDeptList.size(); j++) {
                compValue = mDeptList.get(j).getmScanId();
                compValue.replaceAll("\"", "");
                if (Long.parseLong(compValue) < temp) {
                    min_idx = j;
                }
                tempDept = mDeptList.get(min_idx);
                mDeptList.set(min_idx, mDeptList.get(i));
                mDeptList.set(i, tempDept);
            }
        }
        return mDeptList;
    }

    public static String getDepartmentName(int department) {
        departments = Arrays.asList(
                "NULL",
                "Candy and Tobacco",
                "Personal Care",
                "Stationary",
                "Household Paper Products",
                "Media and Gaming",
                "Photo",
                "Toys",
                "Pets & Supplies",
                "Sporting Goods",
                "Automotive",
                "Hardware",
                "Do It Yourself",
                "Chemicals & Cleaning",
                "Cook & Dine",
                "NULL",
                "Lawn & Garden",
                "Home Decor",
                "Seasonal",
                "Arts, Crafts, & Sewing",
                "Bath & Shower",
                "Books & Magazines",
                "Bedding",
                "Mens Apparel",
                "Boys Apparel",
                "Shoes",
                "Baby & Toddler Apparel",
                "NULL",
                "NULL",
                "Intimate Apparel",
                "NULL",
                "Accessories",
                "Jewelry",
                "Girls Apparel",
                "Womens Apparel",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Pharmacy: OTC",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Beauty",
                "NULL",
                "NULL",
                "Optical",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Horticulture",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Celebration",
                "NULL",
                "NULL",
                "NULL",
                "Furniture",
                "Electronics",
                "NULL",
                "Home Management",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Infant Consumables",
                "Service Deli",
                "Commercial Bread",
                "Impulse",
                "NULL",
                "NULL",
                "NULL",
                "NULL",
                "Wireless",
                "NULL",
                "NULL",
                "Dairy",
                "Frozen Food",
                "Grocery",
                "Meat & Seafood",
                "Produce",
                "Snacks & Beverages",
                "Adult Beverages",
                "Meat/Deli Wall",
                "Fresh Bakery");


        String departmentName;
        departmentName = departments.get(department);

        return departmentName;
    }



}
