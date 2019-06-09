package com.frogermcs.imageclassificationtester.utils;

public class ValImageInfo {
    public int n;
    public int trueVal;
    public int tflitePredict;

    /**
     * Filename that is accepted in this constructor:
     * n{}_true{}_pred{}.jpg, e.g. n0_true1_pred1.jpg
     */
    public ValImageInfo(String imgPath) {
        String[] split = imgPath.replace(".jpg", "").split("_");
        this.n = Integer.parseInt(split[0].replace("n", ""));
        this.trueVal = Integer.parseInt(split[1].replace("true", ""));
        this.tflitePredict = Integer.parseInt(split[2].replace("pred", ""));
    }

    @Override
    public String toString() {
        return "ValImageInfo{" +
                "n=" + n +
                ", trueVal=" + trueVal +
                ", tflitePredict=" + tflitePredict +
                '}';
    }
}
