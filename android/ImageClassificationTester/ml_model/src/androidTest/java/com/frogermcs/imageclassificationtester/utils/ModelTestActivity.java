package com.frogermcs.imageclassificationtester.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frogermcs.imageclassificationtester.ClassificationResult;
import com.frogermcs.imageclassificationtester.ModelClassificator;
import com.frogermcs.imageclassificationtester.ResultsUtils;
import com.frogermcs.imageclassificationtester.configs.FlowersConfig;
import com.frogermcs.imageclassificationtester.configs.ModelConfig;

import java.io.IOException;
import java.util.List;

public class ModelTestActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private TextView tvClassification;
    private ModelClassificator modelClassificator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.frogermcs.imageclassificationtester.test.R.layout.activity_model_test);
        ivPreview = findViewById(com.frogermcs.imageclassificationtester.test.R.id.ivPreview);
        tvClassification = findViewById(com.frogermcs.imageclassificationtester.test.R.id.tvClassification);

        initClassification();
    }

    private void initClassification() {
        try {
            ModelConfig modelConfig = new FlowersConfig();
            modelClassificator = new ModelClassificator(this, modelConfig);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Frame Processor initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void classifyImage(Bitmap bitmap) {
        setImagePreview(bitmap);
        performClassification(bitmap);
    }

    public void setImagePreview(Bitmap bitmap) {
        Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
        runOnUiThread(() -> setImage(squareBitmap));
    }

    private void performClassification(Bitmap bitmap) {
        List<ClassificationResult> classificationResults = modelClassificator.process(bitmap);
        showClassificationResults(classificationResults);
    }

    public void showClassificationResults(List<ClassificationResult> classificationResults) {
        runOnUiThread(() -> tvClassification.setText(ResultsUtils.resultsToStr(classificationResults)));
    }

    public void setImage(Bitmap bmp) {
        ivPreview.setImageBitmap(bmp);
    }
}
