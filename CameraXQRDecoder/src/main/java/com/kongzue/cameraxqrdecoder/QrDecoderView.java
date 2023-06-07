package com.kongzue.cameraxqrdecoder;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;
import com.kongzue.cameraxqrdecoder.analyzer.ZxingQRCodeAnalyzer;
import com.kongzue.cameraxqrdecoder.util.QrDecoderPermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2021/8/28 9:32
 */
public class QrDecoderView extends FrameLayout {

    public static boolean DEBUGMODE = true;
    public static final String ERROR_NO_PERMISSION = "无法初始化：请检查权限，未获得权限：android.permission.CAMERA";

    boolean isFlashOpen;
    ExecutorService cameraExecutor;
    boolean finish;
    CameraInternal cameraInternal;
    boolean keepScan;
    List<ImageAnalysis.Analyzer> analyzeImageInterfaceList;

    public QrDecoderView(@NonNull Context context) {
        super(context);
    }

    public QrDecoderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public QrDecoderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QrDecoderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void start() {
        if (QrDecoderPermissionUtil.checkPermissions(getContext(), new String[]{"android.permission.CAMERA"})) {
            addScanQRView();
        } else {
            error(ERROR_NO_PERMISSION);
        }
    }

    public void start(OnWorkFinish<String> onWorkFinish) {
        if (QrDecoderPermissionUtil.checkPermissions(getContext(), new String[]{"android.permission.CAMERA"})) {
            if (analyzeImageInterfaceList == null) {
                analyzeImageInterfaceList = new ArrayList<>();
                analyzeImageInterfaceList.add(new ZxingQRCodeAnalyzer(onWorkFinish));
            }
            addScanQRView();
        } else {
            error(ERROR_NO_PERMISSION);
        }
    }

    public boolean isFlashOpen() {
        return isFlashOpen;
    }

    public QrDecoderView setFlashOpen(boolean flashOpen) {
        isFlashOpen = flashOpen;
        if (cameraInternal != null) {
            cameraInternal.getCameraControl().enableTorch(isFlashOpen);
        }
        return this;
    }

    String oldResult;

    private void addScanQRView() {
        PreviewView viewFinder = new PreviewView(getContext());
        addView(viewFinder, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        viewFinder.post(new Runnable() {
            @Override
            public void run() {
                cameraExecutor = Executors.newSingleThreadExecutor();

                CameraSelector.Builder builder = new CameraSelector.Builder();
                builder.requireLensFacing(CameraSelector.LENS_FACING_BACK);
                CameraSelector cameraSelector = builder.build();

                ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
                DisplayMetrics metrics = new DisplayMetrics();
                viewFinder.getDisplay().getRealMetrics(metrics);
                int screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels);     //屏幕纵横比
                int rotation = viewFinder.getDisplay().getRotation();

                cameraProviderFuture.addListener(new Runnable() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void run() {
                        try {
                            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                            Preview.Builder pBuilder = new Preview.Builder();
                            pBuilder.setTargetAspectRatio(screenAspectRatio);
                            pBuilder.setTargetRotation(rotation);
                            Preview preview = pBuilder.build();

                            preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                            ImageAnalysis.Builder iBuilder = new ImageAnalysis.Builder();
                            iBuilder.setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST);
                            iBuilder.setTargetAspectRatio(screenAspectRatio);
                            iBuilder.setTargetRotation(rotation);

                            ImageAnalysis analysis = iBuilder.build();
                            analysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                                @Override
                                public void analyze(@NonNull ImageProxy image) {
                                    if (analyzeImageInterfaceList != null && !analyzeImageInterfaceList.isEmpty()) {
                                        for (ImageAnalysis.Analyzer analyzer : analyzeImageInterfaceList) {
                                            analyzer.analyze(image);
                                        }
                                    }
                                }
                            });

                            cameraProvider.unbindAll();
                            cameraProvider.bindToLifecycle((AppCompatActivity) getContext(), cameraSelector, preview, analysis);

                            cameraInternal = preview.getCamera();
                        } catch (Exception e) {
                            if (DEBUGMODE) e.printStackTrace();
                            e.printStackTrace();
                        }
                    }
                }, ContextCompat.getMainExecutor(getContext()));
            }
        });
    }

    double RATIO_4_3_VALUE = 4.0 / 3.0;
    double RATIO_16_9_VALUE = 16.0 / 9.0;

    private int aspectRatio(int width, int height) {
        double previewRatio = Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void error(String errorNoPermission) {
        if (DEBUGMODE) {
            Log.e("QrDecoderView", errorNoPermission);
        }
    }

    public boolean isKeepScan() {
        return keepScan;
    }

    public QrDecoderView setKeepScan(boolean keepScan) {
        this.keepScan = keepScan;
        return this;
    }

    public QrDecoderView addAnalyzeImageImpl(ImageAnalysis.Analyzer analyzeImageInterface) {
        if (analyzeImageInterfaceList == null) {
            analyzeImageInterfaceList = new ArrayList<ImageAnalysis.Analyzer>();
        }
        analyzeImageInterfaceList.add(analyzeImageInterface);
        return this;
    }
}
