package com.example.zhangliang.videoprojects.ai;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.zhangliang.videoprojects.databinding.ActivityAiBinding;

import java.io.InputStream;

import com.example.zhangliang.videoprojects.R;
import com.hyperai.hyperlpr3.HyperLPR3;
import com.hyperai.hyperlpr3.bean.Parameter;
import com.hyperai.hyperlpr3.bean.Plate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.net.Uri;
import android.os.Environment;
import android.content.ContentValues;
import androidx.exifinterface.media.ExifInterface;
import java.io.InputStream;
import android.graphics.Matrix;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.widget.FrameLayout;
import android.view.ViewGroup;

public class AiActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private ImageView imageView;
    private TextView resultText;
    private Button cameraBtn, galleryBtn;

    private FrameLayout previewHost;
    private PreviewView previewView;
    private Button realtimeBtn;

    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private boolean isRealtimeMode = false;
    private long lastAnalyzeTime = 0L;

    private Uri currentPhotoUri;
    private Bitmap currentImage;
    //    private LicensePlateRecognizer recognizer;
    private Context mCtx;
    HyperLPR3 hyperLPR3;
    private ActivityAiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_ai);

        binding = ActivityAiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化UI
        imageView = findViewById(R.id.imageView);
        resultText = findViewById(R.id.resultText);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        previewHost = findViewById(R.id.previewHost);
        realtimeBtn = findViewById(R.id.realtimeBtn);

        // 初始化车牌识别器
        // recognizer = new LicensePlateRecognizer(this);

        mCtx = this;
        Parameter parameter = new Parameter();
        hyperLPR3 = new HyperLPR3(mCtx, parameter);

        // 设置按钮点击事件
        cameraBtn.setOnClickListener(v -> openCamera());
        galleryBtn.setOnClickListener(v -> openGallery());
        realtimeBtn.setOnClickListener(v -> startRealtimeRecognition());

        cameraExecutor = Executors.newSingleThreadExecutor();

        updateLandscapeVisibility();
    }

    private void updateLandscapeVisibility() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.titleText.setVisibility(View.GONE);
//            binding.realtimeBtn.setVisibility(View.GONE);
            binding.buttonRow.setVisibility(View.GONE);
        } else {
            binding.titleText.setVisibility(View.VISIBLE);
            binding.realtimeBtn.setVisibility(View.VISIBLE);
            binding.buttonRow.setVisibility(View.VISIBLE);
        }
    }

    private void startRealtimeRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
            return;
        }

        isRealtimeMode = true;
        if (previewView == null) {
            previewView = new PreviewView(this);
            previewView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            previewHost.addView(previewView);
        }
        previewHost.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindRealtimeCamera();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "启动实时识别失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindRealtimeCamera() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            try {
                if (!isRealtimeMode) {
                    return;
                }

                long now = System.currentTimeMillis();
                if (now - lastAnalyzeTime < 300) {
                    return;
                }
                lastAnalyzeTime = now;

                byte[] nv21 = yuv420888ToNv21(image);
                //                byte[] nv21 = imageToBytes(image);
                int rotation = rotationDegreesToHyperLpr(image.getImageInfo().getRotationDegrees());

                Plate[] plates = hyperLPR3.plateRecognition(
                        nv21,
                        image.getHeight(),
                        image.getWidth(),
                        rotation,
                        HyperLPR3.STREAM_YUV_NV21);

                if (plates != null && plates.length > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (Plate plate : plates) {
                        String type = "未知车牌";
                        if (plate.getType() != HyperLPR3.PLATE_TYPE_UNKNOWN) {
                            type = HyperLPR3.PLATE_TYPE_MAPS[plate.getType()];
                        }
                        builder.append("[").append(type).append("]").append(plate.getCode()).append("\n");
                    }

                    String result = builder.toString();
                    runOnUiThread(() -> resultText.setText("识别结果:\n" + result));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                image.close();
            }
        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
    }

    private byte[] imageToBytes(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        int totalSize = 0;
        for (ImageProxy.PlaneProxy plane : planes) {
            totalSize += plane.getBuffer().remaining();
        }

        byte[] bytes = new byte[totalSize];
        int offset = 0;
        for (ImageProxy.PlaneProxy plane : planes) {
            ByteBuffer buffer = plane.getBuffer();
            int remaining = buffer.remaining();
            buffer.get(bytes, offset, remaining);
            offset += remaining;
        }
        return bytes;
    }

    private int rotationDegreesToHyperLpr(int degrees) {
        // return HyperLPR3.CAMERA_ROTATION_270;
        switch (degrees) {
            case 90:
                //    return HyperLPR3.CAMERA_ROTATION_90;
                return HyperLPR3.CAMERA_ROTATION_270; // CameraX 的 90 度对应 HyperLPR3 的 270 度
            case 180:
                //    return HyperLPR3.CAMERA_ROTATION_180;
                return HyperLPR3.CAMERA_ROTATION_180; // CameraX 的 180 度对应 HyperLPR3 的 180 度
            case 270:
                //    return HyperLPR3.CAMERA_ROTATION_270;
                return HyperLPR3.CAMERA_ROTATION_90; // CameraX 的 270 度对应 HyperLPR3 的 90 度
            default:
                return HyperLPR3.CAMERA_ROTATION_0;
        }
    }

    /**
     * 将 CameraX 的 YUV_420_888 格式转换为 NV21 格式
     * 这是最高效的转换方法，避免了不必要的内存分配
     */
    private byte[] yuv420888ToNv21(ImageProxy image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 计算 NV21 需要的字节数：Y(宽×高) + UV(宽×高/2)
        int ySize = width * height;
        int uvSize = width * height / 2;
        byte[] nv21 = new byte[ySize + uvSize];

        // 获取三个平面的数据
        ImageProxy.PlaneProxy[] planes = image.getPlanes();

        // Y 平面
        ByteBuffer yBuffer = planes[0].getBuffer();
        int yRowStride = planes[0].getRowStride();
        int yPixelStride = planes[0].getPixelStride();

        // U 平面
        ByteBuffer uBuffer = planes[1].getBuffer();
        int uRowStride = planes[1].getRowStride();
        int uPixelStride = planes[1].getPixelStride();

        // V 平面
        ByteBuffer vBuffer = planes[2].getBuffer();
        int vRowStride = planes[2].getRowStride();
        int vPixelStride = planes[2].getPixelStride();

        // 复制 Y 分量（处理行跨度）
        int yOffset = 0;
        for (int row = 0; row < height; row++) {
            int rowOffset = row * yRowStride;
            for (int col = 0; col < width; col++) {
                nv21[yOffset++] = yBuffer.get(rowOffset + col * yPixelStride);
            }
        }

        // 复制 UV 分量（交错存储为 VU）
        int uvHeight = height / 2;
        int uvWidth = width / 2;
        int uvOffset = ySize; // UV 数据从 Y 数据之后开始

        for (int row = 0; row < uvHeight; row++) {
            int vRowOffset = row * vRowStride;
            int uRowOffset = row * uRowStride;

            for (int col = 0; col < uvWidth; col++) {
                // NV21 格式：先 V 后 U
                nv21[uvOffset++] = vBuffer.get(vRowOffset + col * vPixelStride);
                nv21[uvOffset++] = uBuffer.get(uRowOffset + col * uPixelStride);
            }
        }

        return nv21;
    }

    /**
     * 打开相机拍照
     */
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
            return;
        }

        // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // startActivityForResult(cameraIntent, REQUEST_CAMERA);

        try {
            currentPhotoUri = createImageUri();
            if (currentPhotoUri == null) {
                Toast.makeText(this, "创建图片Uri失败", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "打开相机失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "plate_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VideoProjects");

        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private Bitmap rotateBitmapIfNeeded(Uri uri, Bitmap bitmap) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return bitmap;

            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            } else {
                return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 打开相册选择
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    /**
     * 识别车牌
     */
    private void recognizeLicensePlate() {
        if (currentImage == null) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载中
        resultText.setText("识别中...");

        // 在新线程中识别
        new Thread(() -> {
            try {
                //                String result = recognizer.recognize(currentImage);
                String result = "";
                Bitmap bitmap = currentImage;
                if (bitmap != null) {

                    imageView.setImageBitmap(bitmap);
                    Plate[] plates = hyperLPR3.plateRecognition(bitmap, HyperLPR3.CAMERA_ROTATION_0,
                            HyperLPR3.STREAM_BGRA);
                    for (Plate plate : plates) {
                        String type = "未知车牌";
                        if (plate.getType() != HyperLPR3.PLATE_TYPE_UNKNOWN) {
                            type = HyperLPR3.PLATE_TYPE_MAPS[plate.getType()];
                        }
                        String pStr = "[" + type + "]" + plate.getCode() + "\n";
                        //                        showText += pStr;
                        //                        mResult.setText(showText);
                        result = pStr;

                    }
                }

                // 在主线程更新结果
                String finalResult = result;
                runOnUiThread(() -> {
                    resultText.setText("识别结果: " + finalResult);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    resultText.setText("识别失败: " + e.getMessage());
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                // Bundle extras = data.getExtras();
                // if (extras != null) {
                //     currentImage = (Bitmap) extras.get("data");
                //     imageView.setImageBitmap(currentImage);
                // }
                if (currentPhotoUri != null) {
                    try {
                        currentImage = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), currentPhotoUri);
                        currentImage = rotateBitmapIfNeeded(currentPhotoUri, currentImage);
                        imageView.setImageBitmap(currentImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "读取拍照图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_GALLERY) {
                try {
                    currentImage = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), data.getData());
                    currentImage = rotateBitmapIfNeeded(data.getData(), currentImage);
                    imageView.setImageBitmap(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 开始识别
            recognizeLicensePlate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "需要相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRealtimeMode = false;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}