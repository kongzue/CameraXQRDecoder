package com.kongzue.cameraxqrdecoder.util;

import android.graphics.ImageFormat;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.kongzue.cameraxqrdecoder.QrDecoderView;
import com.kongzue.cameraxqrdecoder.interfaces.OnWorkFinish;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: hotstu
 * @github: https://github.com/hotstu/QRCodeCameraX
 */
public class QRcodeAnalyzerImpl implements ImageAnalysis.Analyzer {
    
    OnWorkFinish<String> onWorkFinish;
    
    public QRcodeAnalyzerImpl(OnWorkFinish<String> onWorkFinish) {
        this.onWorkFinish = onWorkFinish;
        Map<DecodeHintType, Collection<BarcodeFormat>> map = new HashMap();
        map.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
        reader.setHints(map);
    }
    
    byte[] mYBuffer = new byte[0];
    MultiFormatReader reader = new MultiFormatReader();
    
    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (ImageFormat.YUV_420_888 != image.getFormat()) {
            image.close();
            return;
        }
        int height = image.getHeight();
        int width = image.getWidth();
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(toYBuffer(image), width, height, 0, 0, width, height, false);
        image.close();
        BinaryBitmap bitmap =new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = reader.decode(bitmap);
            onWorkFinish.finish(result.getText());
        } catch ( Exception e) {
            if (QrDecoderView.DEBUGMODE)e.printStackTrace();
            onWorkFinish.failed(e);
        }
    }
    
    private byte[] toYBuffer(ImageProxy image) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ByteBuffer yBuffer = yPlane.getBuffer();
        yBuffer.rewind();
        int ySize = yBuffer.remaining();
        int position = 0;
        if (mYBuffer.length != ySize) {
            mYBuffer = new byte[ySize];
        }
        for (int row = 0; row < image.getHeight(); row++) {
            yBuffer.get(mYBuffer, position, image.getWidth());
            position += image.getWidth();
            yBuffer.position(Math.min(ySize, yBuffer.position() - image.getWidth() + yPlane.getRowStride()));
        }
        return mYBuffer;
    }
}
