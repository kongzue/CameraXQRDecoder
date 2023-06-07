package com.kongzue.cameraxqrdecoderdemo;

import static com.kongzue.dialogx.dialogs.PopTip.tip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.kongzue.baseframework.BaseActivity;
import com.kongzue.baseframework.interfaces.DarkNavigationBarTheme;
import com.kongzue.baseframework.interfaces.DarkStatusBarTheme;
import com.kongzue.baseframework.interfaces.Layout;
import com.kongzue.baseframework.util.JumpParameter;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;
import com.kongzue.cameraxqrdecoder.util.MLKitBitmapQRDecoder;
import com.kongzue.cameraxqrdecoder.util.ZxingBitmapQRDecoder;

import java.util.List;

@Layout(R.layout.activity_decode_bitmap)
@DarkStatusBarTheme(value = true)
@DarkNavigationBarTheme(value = true)
public class DecodeBitmapActivity extends BaseActivity {

    @Override
    public void initViews() {

    }

    @Override
    public void initDatas(JumpParameter parameter) {

    }

    @Override
    public void setEvents() {

    }

    public void TestClick(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_decode);
        new ZxingBitmapQRDecoder(bitmap, new OnWorkFinish<String>() {
            @Override
            public void finish(String s) {
                tip(s);
            }

            @Override
            public void failed(Object e) {
                tip(e.toString());
            }
        }).start();
    }
}