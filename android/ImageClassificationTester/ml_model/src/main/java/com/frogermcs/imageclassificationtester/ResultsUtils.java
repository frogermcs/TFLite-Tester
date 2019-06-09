package com.frogermcs.imageclassificationtester;

import java.util.List;

public class ResultsUtils {
    public static String resultsToStr(List<ClassificationResult> classificationResults) {
        StringBuilder results = new StringBuilder("Classification:\n");
        if (classificationResults.size() == 0) {
            results.append("No results");
        } else {
            for (ClassificationResult classificationResult : classificationResults) {
                results.append(classificationResult.title)
                        .append("(")
                        .append(classificationResult.confidence * 100)
                        .append("%)\n");
            }
        }

        return results.toString();
    }
}
