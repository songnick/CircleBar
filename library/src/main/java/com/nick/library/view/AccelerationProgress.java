package com.nick.library.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nick.library.AccAnimation;
import com.nick.library.AccTypeEvaluator;
import com.nick.library.R;

/**
 * Created by qfsong on 15/8/31.
 * here we need know the circle progress
 * (x-a)^2 + (y-b)^2 = r^2;
 */
public class AccelerationProgress extends View implements AccAnimation.AccAnimationListener{

    private static final int ACC_UPDATE_MSG = 1 << 0;
    private static final long DEFAULT_DURATION = 1500;

    private Paint circlePaint = null;
    private Paint accBallPaint = null;
    private RectF rectF = null;
    private int acc = 0;
    private float ratio = 0.0f;
    private AccAnimation mAccAnimation = null;

    private float accBallRadius = 0.0f;
    private int accBallBackground = Color.BLUE;
    private float bigCircleStroke = 0.0f;
    private long duration = DEFAULT_DURATION;


    public AccelerationProgress(Context context) {
        this(context, null);
    }

    public AccelerationProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccelerationProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AccelerationProgress, 0, 0);
        accBallRadius = a.getDimension(R.styleable.AccelerationProgress_accBallRadius, 14);
        accBallBackground = a.getColor(R.styleable.AccelerationProgress_accBallBackground, Color.BLUE);
        bigCircleStroke = a.getDimension(R.styleable.AccelerationProgress_bigCircleStroke, 7);
        a.recycle();
        init();
    }

    @Override
    public void applyTans(float interpolatedTime) {

    }

    private void init(){
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(bigCircleStroke);
        circlePaint.setStyle(Paint.Style.STROKE);

        accBallPaint = new Paint();
        accBallPaint.setAntiAlias(true);
        accBallPaint.setColor(accBallBackground);
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

        drawBigCircle(canvas);
        //draw acc ball
        drawAccBall(canvas);
    }

    private void drawBigCircle(Canvas canvas){
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), getBigCircleRadius(), circlePaint);
    }

    private void drawAccBall(Canvas canvas){
        //start position is in the (radius, 0)
        //so sweep angle must be started 270
        double sweepAngle = Math.PI/180 * ratio + Math.PI/180 * 270;
        float y = (float)Math.sin(sweepAngle)*(getBigCircleRadius());
        float x = (float)Math.cos(sweepAngle)*(getBigCircleRadius());
        int restoreCount = canvas.save();
        //change aix center position
        canvas.translate(rectF.centerX(), rectF.centerY());
        canvas.drawCircle(x, y, accBallRadius, accBallPaint);
        canvas.restoreToCount(restoreCount);
    }

    private float getBigCircleRadius(){

        return rectF.width() / 2 - accBallRadius / 2 - bigCircleStroke;
    }

    private ValueAnimator animator;

    private void startAnimation(){
//        if (mAccAnimation == null){
//            mAccAnimation = new AccAnimation();
//            mAccAnimation.setDuration(1000);
//            mAccAnimation.setInterpolator(new LinearInterpolator());
//            mAccAnimation.setRepeatCount(Animation.INFINITE);
//        }
//        mAccAnimation.setAccAnimationListener(this);
//        startAnimation(mAccAnimation);

        AccTypeEvaluator accCore = new AccTypeEvaluator();
        animator = ValueAnimator.ofObject(accCore, 0.0f, 360.0f);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float)animation.getAnimatedValue();
                ratio = value;
                invalidate();
            }
        });
        /**
         * as I know the animator's default interpolator is {@link #AccelerateDecelerateInterpolator}
         * if you want to modify the interpolator, use {@link ValueAnimator#setInterpolator(TimeInterpolator)}
         * */
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void stopAnimation(){
        animator.removeAllUpdateListeners();
        animator.cancel();
        animator.end();
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void setDuration(long duration){
        this.duration = duration;
    }

}
