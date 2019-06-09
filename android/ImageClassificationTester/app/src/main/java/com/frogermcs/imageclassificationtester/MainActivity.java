package com.frogermcs.imageclassificationtester;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frogermcs.imageclassificationtester.configs.FlowersConfig;
import com.otaliastudios.cameraview.CameraView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ClassificationFrameProcessor.ClassificationListener {

    private CameraView cameraView;
    private TextView tvClassification;
    private ClassificationFrameProcessor classificationFrameProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvClassification = findViewById(R.id.tvClassification);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);

        initClassification();
    }

    private void initClassification() {
        try {
            ModelClassificator modelClassificator = new ModelClassificator(this, new FlowersConfig());
            classificationFrameProcessor = new ClassificationFrameProcessor(modelClassificator, this);
            cameraView.addFrameProcessor(classificationFrameProcessor);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Frame Processor initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassifiedFrame(List<ClassificationResult> classificationResults) {
        runOnUiThread(() -> tvClassification.setText(ResultsUtils.resultsToStr(classificationResults)));
    }
}