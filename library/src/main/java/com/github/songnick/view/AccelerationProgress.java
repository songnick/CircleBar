package com.github.songnick.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.github.songnick.AccTypeEvaluator;
import com.github.songnick.LinearAnimation;
import com.nick.library.R;

/**
 * Created by qfsong on 15/8/31.
 * here we need know the circle progress
 * (x-a)^2 + (y-b)^2 = r^2;
 */
public class AccelerationProgress extends View implements LinearAnimation.LinearAnimationListener {

    private static final int ACC_UPDATE_MSG = 1 << 0;
    private static final int DEFAULT_DURATION = 1000;

    //paint
    private Paint mCirclePaint = null;
    private Paint mAccBallPaint = null;
    private Paint mHookPaint = null;


    private RectF rectF = null;
    private int acc = 0;
    private float ratio = 0.0f;
    private LinearAnimation mAccAnimation = null;

    private float mAccBallRadius = 0.0f;
    private int mAccBallBackground = Color.BLUE;
    private float mBigCircleStroke = 0.0f;
    private int mDuration = DEFAULT_DURATION;
    private int mBigCircleColor = Color.RED;

    private boolean mLoadingCompleted = false;


    public AccelerationProgress(Context context) {
        this(context, null);
    }

    public AccelerationProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccelerationProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AccelerationProgress, 0, 0);
        mAccBallRadius = a.getDimension(R.styleable.AccelerationProgress_accBallRadius, 14);
        mAccBallBackground = a.getColor(R.styleable.AccelerationProgress_accBallBackground, Color.BLUE);
        mBigCircleStroke = a.getDimension(R.styleable.AccelerationProgress_bigCircleStroke, 7);
        mBigCircleColor = a.getColor(R.styleable.AccelerationProgress_bigCircleBackground, Color.RED);
        mDuration = a.getInt(R.styleable.AccelerationProgress_duration, DEFAULT_DURATION);
        a.recycle();
        init();
    }

    @Override
    public void applyTans(float interpolatedTime) {

    }

    private void init(){
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mBigCircleColor);
        mCirclePaint.setStrokeWidth(mBigCircleStroke);
        mCirclePaint.setStyle(Paint.Style.STROKE);


        mAccBallPaint = new Paint();
        mAccBallPaint.setAntiAlias(true);
        mAccBallPaint.setColor(mAccBallBackground);
        mAccBallPaint.setStyle(Paint.Style.FILL);

        //hook paint
        mHookPaint = new Paint();
        mHookPaint.setAntiAlias(true);
        mHookPaint.setColor(Color.WHITE);
        mHookPaint.setStrokeWidth(15);
        mHookPaint.setStyle(Paint.Style.STROKE);

        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //confirm the max width
        switch (widthMode){
            case MeasureSpec.AT_MOST:

                break;

            case MeasureSpec.UNSPECIFIED:

                break;
        }

        //confirm the max height
        //if the height is not defined, set the default one
        switch (heightMode){
            case MeasureSpec.AT_MOST:

                break;

            case MeasureSpec.UNSPECIFIED:

                break;
        }

        rectF.set(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBigCircle(canvas);
        if (!mLoadingCompleted){
            //draw acc ball
            drawAccBall(canvas);
        }else {
            drawHook(canvas);
        }
        drawHook(canvas);
    }

    private void drawBigCircle(Canvas canvas){
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), getBigCircleRadius(), mCirclePaint);
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
        canvas.drawCircle(x, y, mAccBallRadius, mAccBallPaint);
        canvas.restoreToCount(restoreCount);
    }

    private void drawHook(Canvas canvas){
        Path hookPath = new Path();
        double sweepAngle = Math.PI/180 * 180;
        float y = (float) Math.sin(sweepAngle)*(getBigCircleRadius()) + rectF.height()/2;
        float x = (float)Math.cos(sweepAngle)*(getBigCircleRadius()) + rectF.width()/2;
//        int count = canvas.getSaveCount();
//        hookPath.moveTo(x, y);
        float i = rectF.centerX()/2;
        hookPath.moveTo(rectF.centerX()/2, rectF.centerY());
        hookPath.lineTo(i + i*1.732f, rectF.centerY() + rectF.centerY() * 1.732f);
//        hookPath.setFillType(Path.FillType.EVEN_ODD);
//        hookPath.rLineTo(50, 150);
//        hookPath.addRoundRect(0,100,50,150,5, 5, Path.Direction.CCW);
        hookPath.close();
        canvas.drawPath(hookPath, mHookPaint);
    }

    private float getBigCircleRadius(){

        return rectF.width() / 2 - mAccBallRadius - mBigCircleStroke;
    }

    private ValueAnimator animator;

    private void startAnimation(){
//        if (mAccAnimation == null){
//            mAccAnimation = new LinearAnimation();
//            mAccAnimation.setDuration(1000);
//            mAccAnimation.setInterpolator(new LinearInterpolator());
//            mAccAnimation.setRepeatCount(Animation.INFINITE);
//        }
//        mAccAnimation.setAccAnimationListener(this);
//        startAnimation(mAccAnimation);

        AccTypeEvaluator accCore = new AccTypeEvaluator();
        animator = ValueAnimator.ofObject(accCore, 0.0f, 360.0f);
        animator.setDuration(mDuration);
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

    public void setDuration(int duration){
        this.mDuration = duration;
    }

    /**
     * if loading is completed, draw the hook
     *
     * @param complete loading is completed
     * */
    public void loadCompleted(boolean complete){
        mLoadingCompleted = complete;
        clearAnimation();
        invalidate();
    }
}
