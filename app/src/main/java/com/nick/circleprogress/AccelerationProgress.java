package com.nick.circleprogress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by qfsong on 15/8/31.
 * here we need know the circle progress
 * (x-a)^2 + (y-b)^2 = r^2;
 */
public class AccelerationProgress extends View{

    private Paint circlePaint = null;
    private Paint accBallPaint = null;
    private RectF rectF = null;

    public AccelerationProgress(Context context) {
        super(context);
        init();
    }

    public AccelerationProgress(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public AccelerationProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(4.0f);
        circlePaint.setStyle(Paint.Style.STROKE);

        accBallPaint = new Paint();
        accBallPaint.setAntiAlias(true);
        accBallPaint.setColor(Color.BLUE);
        accBallPaint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        rectF.set(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() / 3, circlePaint);
        canvas.drawCircle(rectF.centerX(), rectF.centerY() - rectF.width() / 3, 14f, accBallPaint);

    }

    private void getCircleLocation(int sweepAngle){

    }
}
