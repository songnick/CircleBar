package com.github.songnick.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.songnick.LinearAnimation;

/**
 * Created by qfsong on 15/9/10.
 */
public class SplashProgress extends View {

    private Paint paint = null;

    public SplashProgress(Context context) {
        this(context, null);
    }

    public SplashProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawPath(getRandomBallPath(startX,startY,mRadius), paint);
    }

    private Path getRandomBallPath(float x, float y, float radius){
        Path path = new Path();
        path.addCircle(x, y, radius, Path.Direction.CCW);
        return path;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    private float startX;
    private float startY;
    private float mRadius = 80;

    private void startAnimation(){
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(10 * 1000);
        animation.setRepeatCount(1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setLinearAnimationListener(new LinearAnimation.LinearAnimationListener() {
            @Override
            public void applyTans(float interpolatedTime) {
//                startAngle = 360 * interpolatedTime;
                Log.d("", " straaaaaaaa == " + interpolatedTime);
                float ratio = 1.0f - interpolatedTime;
                mRadius = (1.0f - interpolatedTime)*80;
                Log.d("", " straaaaaaaa == radius == " + mRadius);
                startX = getMeasuredWidth()/2;
                startY = getMeasuredHeight() - getMeasuredHeight()/2 * interpolatedTime;
                invalidate();
            }
        });

        startAnimation(animation);
    }

}
