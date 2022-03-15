package com.newlin.barcodegenerator;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;



public class Upc {
    private String mItemCode;
    Bitmap mImage;

    public Upc(String itemCode) {
        mItemCode = itemCode;
    }

    public Upc(String itemCode, Bitmap image) {

        mItemCode = itemCode;
        mImage = image;
    }

    public String getmItemCode() {
        return mItemCode;
    }

    public Bitmap getImage() {
        return mImage;
    }

    private static int lastCodeId = 0;

    public static ArrayList<Upc> createCodeList(String file) {
        ArrayList<Upc> upcs = new ArrayList<Upc>();
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        file.replaceAll("\\[", "");
        String[] codes = file.split(",",0);

        //fix for new scanner
        long upcAdd;
        upcAdd = 40000000000L;

        String temp;
        for (int i = 0; i < codes.length; i++) {
            temp = codes[i].replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");

            //fix for new scanner
            try {
                temp = String.valueOf(Long.valueOf(temp) + upcAdd);

                int x = 0, y = 0;

                for (int j = 0; j < temp.length(); j++) {
                    if (j % 2 == 0) {
                        x += Integer.valueOf(temp.charAt(j));
                    } else {
                        y += Integer.valueOf(temp.charAt(j));
                    }
                }
                x = x * 3;
                x += y;
                x = x % 10;
                if (x > 0) {
                    x = 10 - x;
                }
                temp = temp + x;

                codes[i] = temp;
            } catch (Exception e) {

            }
        }

        Bitmap image = null;
        String test;
        for (int i = 0; i < codes.length; i++) {
            test = codes[i];
            try {
                image = generateBarcodeImage(test);
            } catch (Exception e) {
                e.printStackTrace();
            }
            upcs.add(new Upc(codes[i], image));
        }
        return upcs;
    }

    public static Bitmap generateBarcodeImage(String barcodeText) throws Exception {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcodeText, BarcodeFormat.CODE_128, 600, 150);
            bitmap = Bitmap.createBitmap(600, 150, Bitmap.Config.RGB_565);
            for (int i = 0; i < 600; i++) {
                for (int j = 0; j < 150; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i,j)? Color.BLACK: Color.WHITE);
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }




}
