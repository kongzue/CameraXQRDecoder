package com.kongzue.cameraxqrdecoderdemo;

import android.view.View;
import android.widget.Button;

import com.kongzue.baseframework.BaseActivity;
import com.kongzue.baseframework.interfaces.DarkNavigationBarTheme;
import com.kongzue.baseframework.interfaces.DarkStatusBarTheme;
import com.kongzue.baseframework.interfaces.Layout;
import com.kongzue.baseframework.util.JumpParameter;
import com.kongzue.baseframework.util.OnPermissionResponseListener;
import com.kongzue.cameraxqrdecoder.QrDecoderView;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;
import com.kongzue.cameraxqrdecoder.util.TextRecognitionImpl;
import com.kongzue.dialogx.dialogs.PopTip;

@Layout(R.layout.activity_main)
@DarkStatusBarTheme(value = true)
@DarkNavigationBarTheme(value = true)
public class MainActivity extends BaseActivity {
    
    private QrDecoderView qrCodeView;
    private Button btnFlash;
    
    @Override
    public void initViews() {
        qrCodeView = findViewById(R.id.qrCodeView);
        btnFlash = findViewById(R.id.btn_flash);
    }
    
    @Override
    public void initDatas(JumpParameter parameter) {
        requestPermission(new String[]{"android.permission.CAMERA"}, new OnPermissionResponseListener() {
            @Override
            public void onSuccess(String[] permissions) {
                //默认二维码识别
                qrCodeView.setKeepScan(true)
                        .start(new OnWorkFinish<String>() {
                    @Override
                    public void finish(String s) {
                        PopTip.show(s);
                    }
                });

//                //文字识别
//                qrCodeView.setKeepScan(true)
//                        .addAnalyzeImageImpl(new TextRecognitionImpl(new OnWorkFinish<String>() {
//                            @Override
//                            public void finish(String s) {
//                                PopTip.show(s);
//                            }
//                        }))
//                        .start();
            }
        
            @Override
            public void onFail() {
            
            }
        });
    }
    
    @Override
    public void setEvents() {
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeView.setFlashOpen(!qrCodeView.isFlashOpen());
                btnFlash.setText(qrCodeView.isFlashOpen()?"闪光灯：开":"闪光灯：关");
            }
        });
    }
}