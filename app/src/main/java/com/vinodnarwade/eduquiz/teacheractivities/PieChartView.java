package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private float correctPercent = 0f;
    private float incorrectPercent = 0f;
    private float unattemptedPercent = 0f;

    private final Paint slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF rectF = new RectF();

    private static final int COLOR_CORRECT = Color.parseColor("#85E48A");
    private static final int COLOR_INCORRECT = Color.parseColor("#E67373");
    private static final int COLOR_UNATTEMPTED = Color.parseColor("#99E0EA");

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(dpToPx(15));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    public void setData(int correct, int incorrect, int unattempted) {

        int total = correct + incorrect + unattempted;

        if (total == 0) {
            correctPercent = 0;
            incorrectPercent = 0;
            unattemptedPercent = 0;
        } else {
            correctPercent = (correct * 100f) / total;
            incorrectPercent = (incorrect * 100f) / total;
            unattemptedPercent = (unattempted * 100f) / total;
        }

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);

        // Fixed, stable size — chart never resizes/jumps when data changes.
        int size = Math.min(width, dpToPx(220));

        setMeasuredDimension(width, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        float diameter = Math.min(viewWidth, viewHeight) - dpToPx(20);
        float left = (viewWidth - diameter) / 2f;
        float top = (viewHeight - diameter) / 2f;

        rectF.set(left, top, left + diameter, top + diameter);

        float startAngle = -90f;

        startAngle = drawSlice(canvas, startAngle, correctPercent, COLOR_CORRECT);
        startAngle = drawSlice(canvas, startAngle, incorrectPercent, COLOR_INCORRECT);
        drawSlice(canvas, startAngle, unattemptedPercent, COLOR_UNATTEMPTED);
    }

    private float drawSlice(Canvas canvas, float startAngle, float percent, int color) {

        if (percent <= 0) {
            return startAngle;
        }

        float sweepAngle = (percent / 100f) * 360f;

        slicePaint.setColor(color);
        canvas.drawArc(rectF, startAngle, sweepAngle, true, slicePaint);

        if (percent >= 8) {

            float midAngle = (float) Math.toRadians(startAngle + sweepAngle / 2);
            float radius = rectF.width() / 2f * 0.65f;
            float cx = rectF.centerX() + radius * (float) Math.cos(midAngle);
            float cy = rectF.centerY() + radius * (float) Math.sin(midAngle);

            String label = Math.round(percent) + "%";
            canvas.drawText(label, cx, cy, textPaint);
        }

        return startAngle + sweepAngle;
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}