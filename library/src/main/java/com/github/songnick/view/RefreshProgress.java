package com.github.songnick.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.github.songnick.LinearAnimation;

/**
 * Created by qfsong on 15/9/8.
 */
public class RefreshProgress extends ViewGroup {
    public RefreshProgress(Context context) {
        this(context, null);
    }

    public RefreshProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int defaultWidth = 0;
        int defaultHeight = 0;
        int finalWidthMeasureSpec = 0;
        int finalHeightMeasureSpec = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            defaultWidth = child.getMeasuredWidth();
//            defaultHeight = child.getMeasuredHeight();
        }
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        defaultWidth = getPaddingLeft() + getPaddingRight() + defaultWidth;
        defaultHeight = defaultHeight + getPaddingTop() + getPaddingBottom();
        switch (widthMode){
            case  MeasureSpec.EXACTLY:
                Log.d("", " onLayout is size == mode exactly");
                break;
            case MeasureSpec.AT_MOST:
                Log.d("", " onLayout is size == mode AT_MOST");
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.d("", " onLayout is size == mode UNSPECIFIED");
                break;

        }

        switch (heightMode){
            case  MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:

                break;
            case MeasureSpec.UNSPECIFIED:

                break;

        }
        Log.d("", " onLayout is size == mode UNSPECIFIED default height = " + defaultHeight);
//        setMeasuredDimension(defaultWidth, defaultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("", " onLayout is size == " + "left == " + l + " top " + t + "right " + r + " bottom = " + b);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);

            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            Log.d("", " onLayout is size == height" + child.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(7);
        circlePaint.setStyle(Paint.Style.STROKE);
//        drawAccProgressbar(startAngle, canvas);
//        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 120, paint);
        canvas.translate(5, 5);
        canvas.drawPath(getArcPath(), circlePaint);
        canvas.translate(rectF.centerX(), rectF.centerY());
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(getBallPath(startAngle + 90), circlePaint);
        canvas.drawPath(getBallPath(startAngle + 90 + 30 + 90), circlePaint);
        canvas.drawPath(getBallPath(startAngle + 90 + 30 + 90 + 30 + 90), circlePaint);
    }

    private void drawAccProgressbar(float startAngle, Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        int[] f = {Color.parseColor("#00000000"), Color.parseColor("#ff000000")};
        float[] p = {.0f, 1.0f};
//        paint.setShader(new LinearGradient(getMeasuredWidth() / 2 - 120, getMeasuredHeight() / 2, getMeasuredWidth() / 2 , getMeasuredHeight()/2 - 120, Color.parseColor("#000000"), Color.parseColor("#ffffff"), LinearGradient.TileMode.CLAMP));
        SweepGradient sweepGradient = new SweepGradient(getMeasuredWidth()/2 - 8, getMeasuredHeight()/2 - 8, f, p);
        Matrix matrix = new Matrix();
        sweepGradient.getLocalMatrix(matrix);
        matrix.postRotate(startAngle, getMeasuredWidth() / 2 - 8, getMeasuredHeight() / 2 - 8);
        sweepGradient.setLocalMatrix(matrix);
        paint.setShader(sweepGradient);

        canvas.drawArc(new RectF(8, 8, getMeasuredWidth()-8, getMeasuredHeight()-8),0, 360, false, paint);

        int count = canvas.save();
        canvas.translate(getMeasuredWidth()/2 - 8, getMeasuredHeight() / 2 - 8);
        double sweepAngle = Math.PI / 180 * startAngle;
        Path path = new Path();
        float y = (float)Math.sin(sweepAngle)*(getMeasuredWidth()/2 - 8);
        float x = (float)Math.cos(sweepAngle)*(getMeasuredHeight() / 2 - 8);
//        path.moveTo(rectF.centerX(), rectF.centerY());
        path.moveTo(x, y);
        path.addCircle(x, y, 10, Path.Direction.CCW);
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(7);
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, circlePaint);
        canvas.restoreToCount(count);
    }

    RectF rectF = new RectF(0, 0, 200, 200);

    private Path getArcPath(){

        Path path = new Path();
        path.addArc(rectF, startAngle, 90);
        path.addArc(rectF, startAngle + 90 + 30, 90);
        path.addArc(rectF, startAngle + 90 + 90 + 30 + 30, 90);
//        path.addCircle(rectF.centerX(), rectF.centerY() + rectF.width() / 2 + 6, 10, Path.Direction.CCW);
//        path.setFillType(Path.FillType.EVEN_ODD);
        return path;
    }

    private float startAngle = 0.0f;

    private Path getBallPath(float startAngle){
        double sweepAngle = Math.PI / 180 * startAngle;
        Path path = new Path();
        float y = (float)Math.sin(sweepAngle)*(rectF.width()/2);
        float x = (float)Math.cos(sweepAngle)*(rectF.width()/2);
//        path.moveTo(rectF.centerX(), rectF.centerY());
        path.moveTo(x, y);
        path.addCircle(x, y, 10, Path.Direction.CCW);
        return path;
    }

    private Path getAccProgressPath(float startAngle , float cx, float cy, float radius){
        Path path = new Path();
        path.addCircle(cx, cy, radius, Path.Direction.CCW);

        return null;
    }

    private void startRotate(){
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(10 * 1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setLinearAnimationListener(new LinearAnimation.LinearAnimationListener() {
            @Override
            public void applyTans(float interpolatedTime) {
                startAngle = 360 * interpolatedTime;
                invalidate();
            }
        });

        startAnimation(animation);
    }

    private void stopRotate(){
        clearAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRotate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }
}
