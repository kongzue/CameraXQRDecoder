package com.kongzue.cameraxqrdecoder.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;

import java.util.Map;

public class ZxingBitmapQRDecoder extends AsyncTask<String, Integer, String> {

    public static boolean DEBUGMODE = true;

    Bitmap decodeBitmap;
    OnWorkFinish<String> callback;
    Map hints;

    public ZxingBitmapQRDecoder(Bitmap decodeBitmap, OnWorkFinish<String> callback) {
        this.decodeBitmap = decodeBitmap;
        this.callback = callback;
    }

    public ZxingBitmapQRDecoder(String filePath, OnWorkFinish<String> callback) {
        this.decodeBitmap = readBitmapFromFile(filePath);
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... anything) {
        return syncDecodeQRCode(decodeBitmap);
    }

    //二维码识别
    public String syncDecodeQRCode(Bitmap bitmap) {
        try {
            Result result = new MultiFormatReader().decode(bitmap2BinaryBitmap(bitmap),hints);
            String r = result.getText();
            log("#syncDecodeQRCode.ok: " + r);
            callback.finish(r);
            return r;
        } catch (Exception ex) {
            log("#syncDecodeQRCode.error: " + ex);
            if (DEBUGMODE){
                ex.printStackTrace();
            }
            callback.failed(ex);
            return "";
        }
    }

    private void log(String s) {
        if (DEBUGMODE) Log.d(">>>", s);
    }

    //解码位图并压缩
    public static Bitmap readBitmapFromFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private BinaryBitmap bitmap2BinaryBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        //获取像素
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        return new BinaryBitmap(new HybridBinarizer(source));
    }

    public Bitmap getDecodeBitmap() {
        return decodeBitmap;
    }

    public ZxingBitmapQRDecoder setDecodeBitmap(Bitmap decodeBitmap) {
        this.decodeBitmap = decodeBitmap;
        return this;
    }

    public OnWorkFinish<String> getCallback() {
        return callback;
    }

    public ZxingBitmapQRDecoder setCallback(OnWorkFinish<String> callback) {
        this.callback = callback;
        return this;
    }

    public Map getHints() {
        return hints;
    }

    public ZxingBitmapQRDecoder setHints(Map hints) {
        this.hints = hints;
        return this;
    }

    public void start() {
        execute();
    }
}
