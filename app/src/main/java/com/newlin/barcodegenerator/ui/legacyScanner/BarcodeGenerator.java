package com.newlin.barcodegenerator.ui.legacyScanner;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeGenerator {
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
