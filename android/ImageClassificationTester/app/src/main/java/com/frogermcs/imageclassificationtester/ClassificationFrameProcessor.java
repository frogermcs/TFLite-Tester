package com.frogermcs.imageclassificationtester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class ClassificationFrameProcessor implements FrameProcessor {

    private final ModelClassificator modelClassificator;
    private final ClassificationListener classificationListener;

    public ClassificationFrameProcessor(ModelClassificator modelClassificator,
                                        ClassificationListener classificationListener) {
        this.modelClassificator = modelClassificator;
        this.classificationListener = classificationListener;
    }

    @Override
    public void process(@NonNull Frame frame) {
        Bitmap bitmap = frameToBitmap(frame);
        List<ClassificationResult> results = modelClassificator.process(bitmap);
        classificationListener.onClassifiedFrame(results);
    }

    private Bitmap frameToBitmap(Frame frame) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(
                frame.getData(),
                ImageFormat.NV21,
                frame.getSize().getWidth(),
                frame.getSize().getHeight(),
                null
        );
        Rect rectangle = new Rect(0, 0, frame.getSize().getWidth(), frame.getSize().getHeight());
        yuvImage.compressToJpeg(rectangle, 90, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        if (frame.getRotation() != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(frame.getRotation());
            Bitmap temp = bitmap;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            temp.recycle();
        }

        return bitmap;
    }

    public interface ClassificationListener {
        void onClassifiedFrame(List<ClassificationResult> classificationResults);
    }
}
