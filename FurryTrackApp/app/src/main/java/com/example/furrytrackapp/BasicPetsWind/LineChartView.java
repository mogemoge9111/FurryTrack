package com.example.furrytrackapp.BasicPetsWind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {
    private List<Float> weightPoints = new ArrayList<>();
    private List<Float> activityPoints = new ArrayList<>();

    private Paint weightPaint;
    private Paint activityPaint;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        weightPaint = new Paint();
        weightPaint.setColor(Color.BLUE);
        weightPaint.setStrokeWidth(6f);
        weightPaint.setStyle(Paint.Style.STROKE);

        activityPaint = new Paint();
        activityPaint.setColor(Color.GREEN);
        activityPaint.setStrokeWidth(6f);
        activityPaint.setStyle(Paint.Style.STROKE);
    }

    public void setData(List<Float> weights, List<Float> activities) {
        this.weightPoints = weights;
        this.activityPoints = activities;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float spacing = weightPoints.size() > 1 ? width / (weightPoints.size() - 1) : width;

        float maxVal = 0f;
        for (float val : weightPoints) maxVal = Math.max(maxVal, val);
        for (float val : activityPoints) maxVal = Math.max(maxVal, val);

        if (maxVal == 0f) maxVal = 1f;

        // draw weight line
        for (int i = 0; i < weightPoints.size() - 1; i++) {
            float startX = i * spacing;
            float startY = height - (weightPoints.get(i) / maxVal) * height;
            float stopX = (i + 1) * spacing;
            float stopY = height - (weightPoints.get(i + 1) / maxVal) * height;
            canvas.drawLine(startX, startY, stopX, stopY, weightPaint);
        }

        // draw activity line
        for (int i = 0; i < activityPoints.size() - 1; i++) {
            float startX = i * spacing;
            float startY = height - (activityPoints.get(i) / maxVal) * height;
            float stopX = (i + 1) * spacing;
            float stopY = height - (activityPoints.get(i + 1) / maxVal) * height;
            canvas.drawLine(startX, startY, stopX, stopY, activityPaint);
        }
    }
}
