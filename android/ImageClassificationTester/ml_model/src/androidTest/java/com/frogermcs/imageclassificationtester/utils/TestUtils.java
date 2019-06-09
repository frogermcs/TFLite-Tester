package com.frogermcs.imageclassificationtester.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
    /**
     * Retrieve a bitmap from assets.
     *
     * @param mgr  The {@link AssetManager} obtained via {@link Context#getAssets()}
     * @param path The path to the asset.
     * @return The {@link Bitmap} or {@code null} if we failed to decode the file.
     */
    public static Bitmap getBitmapFromAsset(AssetManager mgr, String path) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = mgr.open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (final IOException e) {
            bitmap = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }
}
