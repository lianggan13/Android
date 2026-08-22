package com.example.zhangliang.videoprojects.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class LicensePlateRecognizer {
    private static final String TAG = "LicensePlateRecognizer";
    private static boolean openCvLoaded = false;

    private Module yoloModel; // YOLO_OBB检测模型
    private Module lprModel; // LPRNet识别模型

    // 模型输入尺寸
    private static final int YOLO_INPUT_SIZE = 640;
    private static final int LPR_WIDTH = 94;
    private static final int LPR_HEIGHT = 24;

    // 字符映射表
    private static final String[] CHARS = {
            "#", "京", "沪", "津", "渝", "冀", "晋", "蒙", "辽", "吉", "黑",
            "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
            "琼", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新", "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
            "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", "港", "学", "使", "警", "澳", "挂"
    };

    public LicensePlateRecognizer(Context context) {
        try {
            ensureOpenCvLoaded();

            String yoloPath = assetFilePath(context, "yolo_obb_mobile.ptl");
            String lprPath = assetFilePath(context, "lprnet_mobile.ptl");

            if (yoloPath == null || lprPath == null) {
                throw new IOException("模型文件复制失败");
            }

            yoloModel = Module.load(yoloPath);
            lprModel = Module.load(lprPath);
        } catch (Exception e) {
            Log.e(TAG, "加载模型失败", e);
        }
    }

    private static synchronized void ensureOpenCvLoaded() {
        if (openCvLoaded) {
            return;
        }

        if (!OpenCVLoader.initDebug()) {
            System.loadLibrary("opencv_java4");
        }

        openCvLoaded = true;
    }

    /**
     * 车牌识别主函数
     */
    public String recognize(Bitmap inputImage) {
        // 1. YOLO_OBB检测车牌
        List<OBBDetection> detections = detectPlates(inputImage);

        // 2. 对每个检测到的车牌进行识别
        List<String> plateNumbers = new ArrayList<>();
        for (OBBDetection detection : detections) {
            // 透视变换矫正
            Bitmap correctedPlate = perspectiveTransform(inputImage, detection);

            // LPRNet识别
            String number = recognizePlateNumber(correctedPlate);
            plateNumbers.add(number);
        }

        return plateNumbers.isEmpty() ? "未检测到车牌" : plateNumbers.get(0);
    }

    private void logYoloOutput(IValue output) {
        if (output == null) {
            Log.d(TAG, "YOLO output is null");
            return;
        }

        if (output.isTensor()) {
            Tensor tensor = output.toTensor();
            Log.d(TAG, "YOLO output is Tensor, shape=" + Arrays.toString(tensor.shape()));
            return;
        }

        if (output.isTuple()) {
            IValue[] tuple = output.toTuple();
            Log.d(TAG, "YOLO output is Tuple, size=" + tuple.length);
            for (int i = 0; i < tuple.length; i++) {
                IValue item = tuple[i];
                if (item.isTensor()) {
                    Tensor tensor = item.toTensor();
                    Log.d(TAG, "tuple[" + i + "] shape=" + Arrays.toString(tensor.shape()));
                } else {
                    Log.d(TAG, "tuple[" + i + "] type=" + item.toString());
                }
            }
            return;
        }

        Log.d(TAG, "YOLO output type=" + output.toString());
    }

    /**
     * 检测车牌
     */
    private List<OBBDetection> detectPlates(Bitmap bitmap) {
        // 调整大小
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, YOLO_INPUT_SIZE, YOLO_INPUT_SIZE, true);

        // 转换为Tensor
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resized,
                new float[] { 0.0f, 0.0f, 0.0f },
                new float[] { 1.0f, 1.0f, 1.0f });

        // YOLO_OBB推理
        IValue output = yoloModel.forward(IValue.from(inputTensor));

        logYoloOutput(output);

        Tensor detectionResult = output.toTensor();

        // 解析检测结果
        return parseDetections(detectionResult, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 识别车牌号码
     */
    private String recognizePlateNumber(Bitmap plateImage) {
        // 调整到LPRNet输入尺寸
        Bitmap resized = Bitmap.createScaledBitmap(plateImage, LPR_WIDTH, LPR_HEIGHT, true);

        // 转换为Tensor
        Tensor plateTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resized,
                new float[] { 0.5f, 0.5f, 0.5f },
                new float[] { 0.5f, 0.5f, 0.5f });

        // LPRNet推理
        IValue output = lprModel.forward(IValue.from(plateTensor));
        Tensor recognitionResult = output.toTensor();

        // 解码识别结果
        return decodeResult(recognitionResult);
    }

    /**
     * 透视变换
     */
    private Bitmap perspectiveTransform(Bitmap srcImage, OBBDetection detection) {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(srcImage, srcMat);

        // 计算透视变换矩阵
        Point[] srcPoints = detection.getCornerPoints();
        Point[] dstPoints = {
                new Point(0, 0),
                new Point(LPR_WIDTH, 0),
                new Point(LPR_WIDTH, LPR_HEIGHT),
                new Point(0, LPR_HEIGHT)
        };

        Mat perspectiveMatrix = Imgproc.getPerspectiveTransform(
                new MatOfPoint2f(srcPoints),
                new MatOfPoint2f(dstPoints));

        Mat dstMat = new Mat();
        Imgproc.warpPerspective(srcMat, dstMat, perspectiveMatrix, new Size(LPR_WIDTH, LPR_HEIGHT));

        Bitmap result = Bitmap.createBitmap(LPR_WIDTH, LPR_HEIGHT, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dstMat, result);

        srcMat.release();
        dstMat.release();

        return result;
    }

    private List<OBBDetection> parseDetections(Tensor detectionResult, int imgWidth, int imgHeight) {
        List<OBBDetection> detections = new ArrayList<>();

        float[] data = detectionResult.getDataAsFloatArray();
        long[] shape = detectionResult.shape();

        if (shape.length != 3 || shape[0] != 1 || shape[1] != 6) {
            Log.e(TAG, "Unexpected YOLO output shape: " + Arrays.toString(shape));
            return detections;
        }

        int numCandidates = (int) shape[2];
        float confThreshold = 0.5f;

        for (int i = 0; i < numCandidates; i++) {
            float cx = data[0 * numCandidates + i];
            float cy = data[1 * numCandidates + i];
            float w = data[2 * numCandidates + i];
            float h = data[3 * numCandidates + i];
            float score = data[4 * numCandidates + i];
            float angle = data[5 * numCandidates + i];

            if (score < confThreshold) {
                continue;
            }

            // 如果 cx, cy, w, h 是归一化值，先缩放到原图
            cx *= imgWidth;
            cy *= imgHeight;
            w *= imgWidth;
            h *= imgHeight;

            Point[] corners = obbToCorners(cx, cy, w, h, angle);
            detections.add(new OBBDetection(corners, score));
        }

        return detections;
    }

    private Point[] obbToCorners(float cx, float cy, float w, float h, float angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        float halfW = w / 2.0f;
        float halfH = h / 2.0f;

        float[] xs = new float[] { -halfW, halfW, halfW, -halfW };
        float[] ys = new float[] { -halfH, -halfH, halfH, halfH };

        Point[] corners = new Point[4];
        for (int i = 0; i < 4; i++) {
            float x = xs[i];
            float y = ys[i];
            float rx = (float) (x * cos - y * sin) + cx;
            float ry = (float) (x * sin + y * cos) + cy;
            corners[i] = new Point(rx, ry);
        }
        return corners;
    }

    /**
     * 解码LPRNet结果
     */
    private String decodeResult(Tensor recognitionResult) {
        float[] data = recognitionResult.getDataAsFloatArray();
        long[] shape = recognitionResult.shape();

        int timeSteps = (int) shape[0];
        int numClasses = (int) shape[1];

        StringBuilder result = new StringBuilder();
        int prevClass = -1;

        for (int t = 0; t < timeSteps; t++) {
            int maxClass = 0;
            float maxProb = -Float.MAX_VALUE;

            for (int c = 0; c < numClasses; c++) {
                float prob = data[t * numClasses + c];
                if (prob > maxProb) {
                    maxProb = prob;
                    maxClass = c;
                }
            }

            // CTC解码
            if (maxClass != 0 && maxClass != prevClass) {
                if (maxClass < CHARS.length) {
                    result.append(CHARS[maxClass]);
                }
            }
            prevClass = maxClass;
        }

        return result.toString();
    }

    /**
     * 从assets加载模型文件
     */
    private String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        try (InputStream is = context.getAssets().open(assetName);
                OutputStream os = new FileOutputStream(file)) {

            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
            return file.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "加载模型失败: " + assetName, e);
        }
        return null;
    }
}

/**
 * 旋转框检测结果
 */
@Deprecated
class OBBDetection {
    private Point[] cornerPoints;
    private float confidence;

    public OBBDetection(Point[] cornerPoints, float confidence) {
        this.cornerPoints = cornerPoints;
        this.confidence = confidence;
    }

    public Point[] getCornerPoints() {
        return cornerPoints;
    }

    public float getConfidence() {
        return confidence;
    }
}
