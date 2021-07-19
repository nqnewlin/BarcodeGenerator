package com.newlin.barcodegenerator.ui.barcodes;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import com.newlin.barcodegenerator.DepartmentListings.DepartmentListings;
import com.newlin.barcodegenerator.MainActivity;
import com.newlin.barcodegenerator.Upc;

import static android.content.ContentValues.TAG;

public class Departments {
    private static List<String> departments;
    private String mScanId;
    private String mScannedDepts;
    private String mScanTime;
    private String mScanSource;



    public Departments(String scanId, String scannedDepts, String scanTime, String scanSource) {
        mScanId = scanId;
        mScannedDepts = scannedDepts;
        mScanTime = scanTime;
        mScanSource = scanSource;
    }

    public String getmScanId() { return mScanId; }

    public String getmScannedDepts() { return mScannedDepts; }

    public String getmScanTime() { return mScanTime; }

    public String getmScanSource() { return mScanSource; }

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

        for (int i = 0; i < scans.length; i++) {
            if (scans[i].length() > 0) {
                String scan_number = scans[i];

                if (!(scan_number.matches("0"))) {

                    String values = readFile(context, scan_number);

                    String[] tempValues = values.split("\\.", 0);

                    departments.add(new Departments(tempValues[0], tempValues[1], tempValues[2], tempValues[3]));
                } else if (scan_number.matches("0") && scans.length == 1) {
                    departments.add(new Departments("0", null, null, null));
                }
            }
        }

        return departments;
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
