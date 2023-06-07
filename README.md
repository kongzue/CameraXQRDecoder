# CameraXQRDecoder

此库是基于 CameraX 的二维码扫描封装（支持扩展）。

此库仅含基础功能（扫码、闪光灯），不包含扫码框动画、相册选择识别等，如有需要请自行实现。

此库的目的是解决以往原始 Camera 实现的扫码再较长屏幕设备上可能出现因画面纵向被拉伸导致识别率低的问题，相比于传统的 Camera，CameraX 的优势更大，启动速度响应速度都更快。

此库基于[hotstu](https://github.com/hotstu)/**[QRCodeCameraX](https://github.com/hotstu/QRCodeCameraX)** ，由 Kotlin 改为 Java ，并加以封装。

额外新增工具 BitmapQRDecoder 可直接从 bitmap 或文件读取位图并解析其中的二维码，方便实现「从相册打开二维码」的功能。

当前版本仅保留 zxing 实现，不包含 firebase。

### 引入方法

1. 进入 build.gradle(Project) 添加 Jitpack 仓库（若没有请到 settings.gradle 文件中添加）

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在 build.gradle(app) 添加引入

最新版本： [![](https://jitpack.io/v/kongzue/CameraXQRDecoder.svg)](https://jitpack.io/#kongzue/CameraXQRDecoder)


```
dependencies {
    implementation 'com.github.kongzue:CameraXQRDecoder:1.0.2'
}
```

3. Sync 即可

### 扫码使用方法

进入 Activity 的 xml 界面编辑添加布局：

```
<com.kongzue.cameraxqrdecoder.QrDecoderView
    android:id="@+id/qrCodeView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

进入 Activity 的 Java 代码申请权限 `android.permission.CAMERA `，然后执行启动：

```
qrCodeView.start(new OnWorkFinish<String>() {
    @Override
    public void finish(String s) {
        //s 返回扫描结果
    }
});
```

若需要持续扫描：

```
qrCodeView.setKeepScan(true).start(new OnWorkFinish<String>() {
    @Override
    public void finish(String s) {
        //s 返回扫描结果
    }
});
```

若需要开启/关闭闪光灯：

```
qrCodeView.setFlashOpen(!qrCodeView.isFlashOpen());
```

### 从 Bitmap 位图解码

```java
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_decode);
new BitmapQRDecoder(bitmap, new OnWorkFinish<String>() {
    @Override
    public void finish(String s) {
        tip(s);
    }
    @Override
    public void failed(Object e) {
        tip(e.toString());
    }
}).start();
```

另外，BitmapQRDecoder 参数也可以是 filePath 图片文件路径，请确保自己的 app 已经获取文件读写相关权限。

### 文本识别

文本识别依赖 MLKit 实现，利用 QrDecoderView 扩展接口可实现一键接入，也可以实现同时进行二维码扫码和文字识别。

引入 MLKit：

```gradle
implementation 'com.google.mlkit:text-recognition-chinese:16.0.0'
```

使用：
```java
//文字识别
qrCodeView.setKeepScan(true)
        .addAnalyzeImageImpl(new TextRecognitionImpl(new OnWorkFinish<String>() {
            @Override
            public void finish(String s) {
                PopTip.show(s);
            }
        }))
        .start();
```

要和二维码组合识别：
```java
//文字识别
qrCodeView.setKeepScan(true)
        .addAnalyzeImageImpl(qrCodeView.getQRDecoderAnalyzeImageInterface())
        .addAnalyzeImageImpl(new TextRecognitionImpl(new OnWorkFinish<String>() {
        @Override
        public void finish(String s) {
                PopTip.show(s);
        }
        }))
        .start(new OnWorkFinish<String>() {
            @Override
            public void finish(String s) {
                    //s 返回二维码扫描结果
            }
        });
```

### 开源协议

DialogX 遵循 Apache License 2.0 开源协议。

```
Copyright Kongzue CameraXQRDecoder

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[hotstu](https://github.com/hotstu)/**[QRCodeCameraX](https://github.com/hotstu/QRCodeCameraX)** 的协议: Apache License 2.0

