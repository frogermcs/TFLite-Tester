package com.frogermcs.imageclassificationtester;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.frogermcs.imageclassificationtester.configs.FlowersConfig;
import com.frogermcs.imageclassificationtester.utils.ModelTestActivity;
import com.frogermcs.imageclassificationtester.utils.TestUtils;
import com.frogermcs.imageclassificationtester.utils.ValImageInfo;
import com.google.common.truth.Truth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MLModelTest {

    @Rule
    public ActivityTestRule<ModelTestActivity> mainActivityActivityRule = new ActivityTestRule<>(ModelTestActivity.class);

    @Test
    public void testClassificationUI() {
        ModelTestActivity activity = mainActivityActivityRule.getActivity();
        Bitmap bmp = TestUtils.getBitmapFromAsset(activity.getAssets(), "tulip.jpg");
        activity.classifyImage(bmp);

        onView(withId(com.frogermcs.imageclassificationtester.test.R.id.tvClassification))
                .check(matches(withSubstring("Tulips")));
    }

    @Test
    public void testClassificationBatch() throws IOException {
        ModelTestActivity activity = mainActivityActivityRule.getActivity();
        ModelClassificator modelClassificator = new ModelClassificator(activity, new FlowersConfig());

        Map<Integer, Integer> batchClassificationResults = new HashMap<>();
        Map<Integer, Integer> batchClassificationExepectedResults = new HashMap<>();

        String[] valBatchImages = activity.getAssets().list("val_batch");
        for (String valImagePath : valBatchImages) {
            ValImageInfo valImageInfo = new ValImageInfo(valImagePath);
            Bitmap bmp = TestUtils.getBitmapFromAsset(activity.getAssets(), "val_batch/" + valImagePath);

            //Does nothing, it's just test's preview
            activity.setImagePreview(bmp);

            List<ClassificationResult> classificationResults = modelClassificator.process(bmp);
            ClassificationResult topClassificationResult = classificationResults.get(0);
            batchClassificationResults.put(valImageInfo.n, topClassificationResult.labelIndex);
            batchClassificationExepectedResults.put(valImageInfo.n, valImageInfo.tflitePredict);

            //Does nothing, it's just test's preview
            activity.showClassificationResults(classificationResults);
        }

        Truth.assertThat(batchClassificationResults).isNotEmpty();
        Truth.assertThat(batchClassificationResults).containsExactlyEntriesIn(batchClassificationExepectedResults);
    }
}