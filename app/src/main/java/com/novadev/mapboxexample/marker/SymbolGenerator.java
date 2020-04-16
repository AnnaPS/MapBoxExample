package com.novadev.mapboxexample.marker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Utility class to generate Bitmaps for Symbol.
 */
public class SymbolGenerator {

    /**
     * Generate a Bitmap from an Android SDK View.
     *
     * @param view the View to be drawn to a Bitmap
     * @return the generated bitmap
     */
    static Bitmap generate(@NonNull View view) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpec, measureSpec);

        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();

        view.layout(0, 0, measuredWidth, measuredHeight);
        Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}