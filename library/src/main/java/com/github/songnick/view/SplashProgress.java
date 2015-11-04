package com.github.songnick.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.songnick.LinearAnimation;

import java.util.Random;

/**
 * Created by qfsong on 15/9/10.
 */
public class SplashProgress extends View {

    private static final int SPLASH_MSG = 0x011;
    private static final int GATHER_MSG = 0x022;

    private Paint mBallPaint = null;
    private float mBigRadius = 0;

    private float[] mAngles = {45, 90,135, 180,225, 270, 315, 360};

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SPLASH_MSG:
                    startSplashAnimation();
                    mHandler.sendEmptyMessageDelayed(GATHER_MSG, 3000);
                    break;

                case GATHER_MSG:
                    startGatherAnimation();
                    mHandler.sendEmptyMessageDelayed(SPLASH_MSG, 3000);
                    break;
            }
        }
    };

    public SplashProgress(Context context) {
        this(context, null);
    }

    public SplashProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mBallPaint.setColor(Color.RED);
        mBallPaint.setStrokeWidth(5);
        mBallPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //save status
        int savCount = canvas.save();
        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        for (int i = 0; i < mAngles.length; i++) {
            double sweepAngle = Math.PI/180 * mAngles[i];
            startX = (float)Math.cos(sweepAngle)*mBigRadius;
            startY = (float)Math.sin(sweepAngle)*mBigRadius;
            canvas.drawPath(getRandomBallPath(startX, startY, mRadius), mBallPaint);
        }
        canvas.restoreToCount(savCount);
    }

    private Path getRandomBallPath(float x, float y, float radius){
        Path path = new Path();
        path.addCircle(x, y, radius, Path.Direction.CCW);
        return path;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.sendEmptyMessageDelayed(GATHER_MSG, 500);
    }

    private float startX;
    private float startY;
    private float mRadius = 80;

    private void startGatherAnimation(){
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(2 * 1000);
        animation.setRepeatCount(0);
        final Random random  = new Random();
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setLinearAnimationListener(new LinearAnimation.LinearAnimationListener() {
            @Override
            public void applyTans(float interpolatedTime) {
//                startAngle = 360 * interpolatedTime;
                Log.d("", " straaaaaaaa == " + interpolatedTime);
                float ratio = 1.0f - interpolatedTime;
                mBallPaint.setAlpha((int)(ratio*255));
                mRadius = (1.0f - interpolatedTime)*80;
                mBigRadius = getMeasuredHeight() / 2 * ratio;
                invalidate();
            }
        });
        startAnimation(animation);
    }

    private void startSplashAnimation(){
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(2 * 1000);
        animation.setRepeatCount(0);
        final Random random  = new Random();
        animation.setInterpolator(new LinearInterpolator());
        animation.setLinearAnimationListener(new LinearAnimation.LinearAnimationListener() {
            @Override
            public void applyTans(float interpolatedTime) {
//                startAngle = 360 * interpolatedTime;
                Log.d("", " straaaaaaaa == " + interpolatedTime);
                float ratio =interpolatedTime;
                mBallPaint.setAlpha((int)(ratio*255));
                mRadius = (interpolatedTime)*40;
                mBigRadius = getMeasuredWidth() / 2 * ratio;
                invalidate();
            }
        });
        startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        mHandler.removeCallbacksAndMessages(null);
    }

}
