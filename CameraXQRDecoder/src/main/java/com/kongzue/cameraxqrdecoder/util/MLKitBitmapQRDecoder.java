package com.kongzue.cameraxqrdecoder.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;

import java.io.IOException;
import java.util.List;

public class MLKitBitmapQRDecoder {

    static BarcodeScannerOptions options =
            new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                            Barcode.FORMAT_QR_CODE,
                            Barcode.FORMAT_AZTEC)
                    .build();

    public static void decodeSingleFromUri(Context context, @NonNull Uri fileUri, OnWorkFinish<String> onWorkFinish) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, fileUri);

            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes != null && !barcodes.isEmpty()) {
                                String rawValue = barcodes.get(0).getRawValue();
                                onWorkFinish.finish(rawValue);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void decodeSingleFromBitmap(Context context, @NonNull Bitmap originBitmap, OnWorkFinish<String> onWorkFinish) {
        InputImage image = InputImage.fromBitmap(originBitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes != null && !barcodes.isEmpty()) {
                            String rawValue = barcodes.get(0).getRawValue();
                            onWorkFinish.finish(rawValue);
                        }
                    }
                });
    }

    public static void decodeMultiFromUri(Context context, @NonNull Uri fileUri, OnWorkFinish<List<Barcode>> onWorkFinish) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, fileUri);

            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes != null && !barcodes.isEmpty()) {
                                onWorkFinish.finish(barcodes);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void decodeMultiFromBitmap(Context context, @NonNull Bitmap originBitmap, OnWorkFinish<List<Barcode>> onWorkFinish) {
        InputImage image = InputImage.fromBitmap(originBitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes != null && !barcodes.isEmpty()) {
                            onWorkFinish.finish(barcodes);
                        }
                    }
                });
    }
}
