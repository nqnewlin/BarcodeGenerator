package com.newlin.barcodegenerator.ui.legacyScanner;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import org.greenrobot.eventbus.EventBus;

public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> graphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        graphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            Bitmap image = null;
            String test = item.getValue();
            if (item != null && item.getValue() != null) {
                if (test.matches("[0-9]+") && test.length() > 2) {
                    // TODO text detected log
                    // Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());

                    test = convertItemNumber(test);

                    try {
                        image = BarcodeGenerator.generateBarcodeImage(test);
                        test = test + ".C";
                        EventBus.getDefault().post(test);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    OcrGraphic graphic = new OcrGraphic(graphicOverlay, item, image);
                    graphicOverlay.add(graphic);
                }
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        graphicOverlay.clear();
    }

    public static String convertItemNumber(String item) {
        int x = 0, y = 0;
        long upcAdd = 40000000000L;

        item = String.valueOf(Long.valueOf(item) + upcAdd);

        for (int i = 0; i < item.length(); i++) {
            if (i % 2 == 0) {
                x += Integer.valueOf(item.charAt(i));
            } else {
                y += Integer.valueOf(item.charAt(i));
            }
        }
        x *= 3;
        x += y;
        x = x%10;
        if (x > 0) x = 10 - x;
        item = item + x;

        return item;
    }
}