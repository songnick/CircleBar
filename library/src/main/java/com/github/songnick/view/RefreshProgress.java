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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.github.songnick.LinearAnimation;

/**
 * Created by qfsong on 15/9/8.
 */
public class RefreshProgress extends ViewGroup {

    private static final String TAG = "RefreshProgress";

    private boolean isDrawAccProgress = false;


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
                //the size is confirmed - match parent or dd
                Log.d("", " onLayout is size == mode exactly");
                break;
            case MeasureSpec.AT_MOST:
                //wrap_content
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
        setMeasuredDimension(width, height);
        rectF.set(17, 17, width - 17, height - 17);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("", " onLayout is size == " + "left == " + l + " top " + t + "right " + r + " bottom = " + b);
        int childCount = getChildCount();
        if (childCount >= 2){
            throw new IllegalStateException("this layout must have one child ");
        }
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);

            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            Log.d("", " onLayout is size == height" + child.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawAccProgress){
            drawAccProgressbar(startAngle, canvas);
        }else {
            drawSlowIndicator(startAngle, canvas);
        }

    }

    private void drawSlowIndicator(float startAngle, Canvas canvas){
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(7);
        circlePaint.setStyle(Paint.Style.STROKE);
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
        int[] f = {Color.parseColor("#00FF0000"), Color.parseColor("#ffFF0000")};
        float[] p = {.0f, 1.0f};
//        paint.setShader(new LinearGradient(getMeasuredWidth() / 2 - 120, getMeasuredHeight() / 2, getMeasuredWidth() / 2 , getMeasuredHeight()/2 - 120, Color.parseColor("#000000"), Color.parseColor("#ffffff"), LinearGradient.TileMode.CLAMP));
        SweepGradient sweepGradient = new SweepGradient(rectF.centerX(), rectF.centerX(), f, p);
        Matrix matrix = new Matrix();
        sweepGradient.getLocalMatrix(matrix);
        matrix.postRotate(startAngle, rectF.centerX(), rectF.centerY());
        sweepGradient.setLocalMatrix(matrix);
        paint.setShader(sweepGradient);

        canvas.drawArc(rectF,0, 360, true, paint);

        int count = canvas.save();
        canvas.translate(rectF.centerX(), rectF.centerY());
        double sweepAngle = Math.PI / 180 * startAngle;
        Path path = new Path();
        float y = (float)Math.sin(sweepAngle)*(rectF.width()/2);
        float x = (float)Math.cos(sweepAngle)*(rectF.width()/2);
//        path.moveTo(rectF.centerX(), rectF.centerY());
        path.moveTo(x, y);
        path.addCircle(x, y, 10, Path.Direction.CCW);
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);
//        circlePaint.setStrokeWidth(7);
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

    private void startRotate(long duration, boolean acc){
//        clearAnimation();
        isDrawAccProgress = acc;
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(duration);
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
        startRotate(10 * 1000, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrawAccProgress){
            stopRotate();
            startRotate(10 * 1000, false);
        }else {
            stopRotate();
            startRotate(1 * 1000, true);
        }
        return super.onTouchEvent(event);
    }
}
