package com.nick.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 20, circlePaint);
        canvas.translate(5, 5);
        canvas.drawPath(getArcPath(), circlePaint);
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(getBallPath(90), circlePaint);
    }

    RectF rectF = new RectF(0, 0, 200, 200);

    private Path getArcPath(){

        Path path = new Path();
        path.addArc(rectF, 0, 90);
        path.addArc(rectF, 120, 90);
        path.addArc(rectF, 240, 90);
//        path.addCircle(rectF.centerX(), rectF.centerY() + rectF.width() / 2 + 6, 10, Path.Direction.CCW);
//        path.setFillType(Path.FillType.EVEN_ODD);
        return path;
    }

    private Path getBallPath(int startAngle){
        double sweepAngle = Math.PI / 180 * startAngle;
        Path path = new Path();
        float y = (float)Math.sin(sweepAngle)*(rectF.width()/2);
        float x = (float)Math.cos(sweepAngle)*(rectF.width()/2);
        path.moveTo(rectF.centerX() + x, rectF.centerY() + y);
        path.addCircle(x, y, 10, Path.Direction.CCW);
        path.setFillType(Path.FillType.WINDING);
        return path;
    }
}
