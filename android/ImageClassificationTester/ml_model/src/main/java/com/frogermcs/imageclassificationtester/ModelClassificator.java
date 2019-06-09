package com.frogermcs.imageclassificationtester;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import com.frogermcs.imageclassificationtester.configs.ModelConfig;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ModelClassificator {
    private static final int MAX_CLASSIFICATION_RESULTS = 3;
    private static final float CLASSIFICATION_THRESHOLD = 0.2f;

    private final Interpreter interpreter;
    private final List<String> labels;
    private final ModelConfig modelConfig;

    public ModelClassificator(Context context,
                              ModelConfig modelConfig) throws IOException {
        ByteBuffer model = AssetsUtils.loadFile(context, modelConfig.getModelFilename());
        this.interpreter = new Interpreter(model);
        this.labels = AssetsUtils.loadLines(context, modelConfig.getLabelsFilename());
        this.modelConfig = modelConfig;
    }

    public List<ClassificationResult> process(Bitmap bitmap) {
        Bitmap toClassify = ThumbnailUtils.extractThumbnail(
                bitmap, modelConfig.getInputWidth(), modelConfig.getInputHeight()
        );

        ByteBuffer byteBufferToClassify = bitmapToModelsMatchingByteBuffer(toClassify);
        float[][] result = new float[1][labels.size()];
        interpreter.run(byteBufferToClassify, result);

        bitmap.recycle();
        toClassify.recycle();

        return getSortedResult(result);
    }

    private ByteBuffer bitmapToModelsMatchingByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelConfig.getInputSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[modelConfig.getInputWidth() * modelConfig.getInputHeight()];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < modelConfig.getInputWidth(); ++i) {
            for (int j = 0; j < modelConfig.getInputHeight(); ++j) {
                int pixelVal = intValues[pixel++];
                for (float channelVal : pixelToChannelValues(pixelVal)) {
                    byteBuffer.putFloat(channelVal);
                }
            }
        }
        return byteBuffer;
    }

    private float[] pixelToChannelValues(int pixel) {
        if (modelConfig.getChannelsCount() == 1) {
            float[] singleChannelVal = new float[1];
            float rChannel = (pixel >> 16) & 0xFF;
            float gChannel = (pixel >> 8) & 0xFF;
            float bChannel = (pixel) & 0xFF;
            singleChannelVal[0] = (rChannel + gChannel + bChannel) / 3 / modelConfig.getStd();
            return singleChannelVal;
        } else if (modelConfig.getChannelsCount() == 3) {
            float[] rgbVals = new float[3];
            rgbVals[0] = ((((pixel >> 16) & 0xFF) - modelConfig.getMean()) / modelConfig.getStd());
            rgbVals[1] = ((((pixel >> 8) & 0xFF) - modelConfig.getMean()) / modelConfig.getStd());
            rgbVals[2] = ((((pixel) & 0xFF) - modelConfig.getMean()) / modelConfig.getStd());
            return rgbVals;
        } else {
            throw new RuntimeException("Only 1 or 3 channels supported at the moment.");
        }
    }

    private List<ClassificationResult> getSortedResult(float[][] resultsArray) {
        PriorityQueue<ClassificationResult> sortedResults = new PriorityQueue<>(
                MAX_CLASSIFICATION_RESULTS,
                (lhs, rhs) -> Float.compare(rhs.confidence, lhs.confidence)
        );

        for (int i = 0; i < labels.size(); ++i) {
            float confidence = resultsArray[0][i];
            if (confidence > CLASSIFICATION_THRESHOLD) {
                sortedResults.add(new ClassificationResult(labels.get(i), i, confidence));
            }

            if (sortedResults.size() > MAX_CLASSIFICATION_RESULTS) {
                sortedResults.poll();
            }
        }

        return new ArrayList<>(sortedResults);
    }
}
