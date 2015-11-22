package com.github.songnick.view;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.songnick.AccTypeEvaluator;
import com.github.songnick.LinearAnimation;
import com.nick.library.R;

/**
 * Created by SongNick on 15/8/31.
 * here we need know the circle progress
 * (x-a)^2 + (y-b)^2 = r^2;
 */
public class AccelerationProgress extends View implements LinearAnimation.LinearAnimationListener {

    private final String TAG = "AccelerationProgress";

    private static final int DEFAULT_DURATION = 1000;

    //paint
    private Paint mCirclePaint = null;
    private Paint mAccBallPaint = null;
    private Paint mHookPaint = null;
    private Paint mTextPaint = null;

    private RectF rectF = null;
    private int acc = 0;
    private float ratio = 0.0f;
    private LinearAnimation mAccAnimation = null;

    //some default params for this view
    private float mAccBallRadius = 0.0f;
    private int mAccBallBackground = Color.BLUE;
    private float mBigCircleStroke = 0.0f;
    private int mDuration = DEFAULT_DURATION;
    private int mBigCircleColor = Color.RED;
    private float mTimeTextSize = 20.0f;

    private boolean mLoadingCompleted = false;
    private boolean mNeedDrawTime = false;


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
        mTimeTextSize = a.getDimensionPixelSize(R.styleable.AccelerationProgress_timeTextSize, 20);
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
        mHookPaint.setColor(Color.parseColor("#FF4444"));
        mHookPaint.setStrokeWidth(15);
        mHookPaint.setStyle(Paint.Style.STROKE);

        //text paint
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);
        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int defaultSize = getResources().getDimensionPixelSize(R.dimen.acc_default_size);
        //confirm the max width
        switch (widthMode){
            case MeasureSpec.AT_MOST:
                Log.e(TAG, " width onMeasure mode is AT_MOST width = " + width);
                //here we must confirm mini size
                if (width < defaultSize){
                    width = defaultSize;
                }
                Log.e(TAG, " width onMeasure mode is AT_MOST");

                break;

            case MeasureSpec.UNSPECIFIED:
                Log.e(TAG, "width onMeasure mode is UNSPECIFIED");
                break;
            case MeasureSpec.EXACTLY:
                //match_parent and with confirmed size
                Log.e(TAG, "width onMeasure mode is EXACTLY");
                break;
        }

        //confirm the max height
        //if the height is not defined, set the default one
        switch (heightMode){
            case MeasureSpec.AT_MOST:
                Log.e(TAG, " height onMeasure mode is AT_MOST height = " + height);
                //wrap_content confirmed by parent
                if (height < defaultSize){
                    height = defaultSize;
                }
                Log.e(TAG, " height onMeasure mode is AT_MOST");
                break;

            case MeasureSpec.UNSPECIFIED:
                Log.e(TAG, " height onMeasure mode is UNSPECIFIED");
                break;
        }
        setMeasuredDimension(width, height);
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
//        drawHook(canvas);
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
        if (mNeedDrawTime){
            if (mTimeTextSize > mAccBallRadius){
                mAccBallRadius = mTimeTextSize;
            }
        }
        canvas.drawCircle(x, y, mAccBallRadius, mAccBallPaint);
        if (mNeedDrawTime){
            RectF textRect = new RectF(x - mAccBallRadius, y - mAccBallRadius, x + mAccBallRadius, y + mAccBallRadius);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float baseline = textRect.top + (textRect.bottom - textRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            String curTime = String.valueOf(animator.getDuration() / 1000 - animator.getCurrentPlayTime()/1000);
            if (animator.getCurrentPlayTime() == 0 && !animator.isRunning()) {
                curTime = String.valueOf(0);
            }
            canvas.drawText(curTime, textRect.centerX(), baseline, mTextPaint);
        }

        canvas.restoreToCount(restoreCount);
    }

    /**
     * There is some bug and it's not perfect.
     * so we should improve this path for our projection
     * @param canvas which to draw
     * */
    private void drawHook(Canvas canvas){
        Path hookPath = new Path();
        double sweepAngle = Math.PI/180 * 180;
        float y = (float) Math.sin(sweepAngle)*(getBigCircleRadius()) + rectF.height()/2;
        float x = (float)Math.cos(sweepAngle)*(getBigCircleRadius()) + rectF.width()/2;
//        int count = canvas.getSaveCount();
//        hookPath.moveTo(x, y);
        float i = getBigCircleRadius()/2;
//        hookPath.moveTo(rectF.centerX()/2, rectF.centerY());
//        hookPath.addRoundRect();
//        hookPath.lineTo(i + i*1.732f, rectF.centerY() + rectF.centerY() * 1.732f);
//        hookPath.setFillType(Path.FillType.EVEN_ODD);
//        hookPath.rLineTo(50, 150);
//        hookPath.addRoundRect(0,100,50,150,5, 5, Path.Direction.CCW);
        hookPath.addRoundRect(new RectF(i + i/2, i + i/2, i + i/2 +6, i + i + i/2), 3f, 3f, Path.Direction.CCW);
//        hookPath.addRect(new RectF(i, i + i, i + 15, i + i + 15f), Path.Direction.CCW);
        hookPath.addRoundRect(new RectF(i + i/2, (i + i + i/2) - 3f, i + i/2 + i + i, i + i + i/2 + 3f), 3f, 3f, Path.Direction.CCW);
        Matrix matrix = new Matrix();
        matrix.reset();

//        matrix.postRotate(260);
        int restoreCount = canvas.getSaveCount();

        hookPath.close();

        canvas.rotate(-45, rectF.centerX(), rectF.centerY());
//        canvas.translate(20, 0);
        canvas.drawPath(hookPath, mHookPaint);

        canvas.restoreToCount(restoreCount);
    }

    private float getBigCircleRadius(){

        return rectF.width() / 2 - mAccBallRadius - mBigCircleStroke;
    }

    private ValueAnimator animator;


    /**
     * here I use the {@link ValueAnimator}, this this API can be used after the Android3.0
     * if you want use it below that, you should dependency
     * <b>https://github.com/JakeWharton/NineOldAndroids</b>
     * */
    private void startAnimation(){
        AccTypeEvaluator accCore = new AccTypeEvaluator();
        animator = ValueAnimator.ofObject(accCore, 0.0f, 360.0f);
        animator.setDuration(mDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                ratio = value;
                invalidate();
            }
        });

        /**
         * as I know the animator's default interpolator is {@link #AccelerateDecelerateInterpolator}
         * if you want to modify the interpolator, use {@link ValueAnimator#setInterpolator(TimeInterpolator)}
         * */
        if (mNeedDrawTime){
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mCompleteListener != null) {
                        mCompleteListener.countdownComplete();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else {
            animator.setRepeatCount(ValueAnimator.INFINITE);
        }
        animator.start();
    }

    /**
     * stop rotating of the ball
     * */
    public void stopLoading(){
        animator.removeAllUpdateListeners();
        animator.cancel();
        animator.end();
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        stopLoading();
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == INVISIBLE || visibility == GONE){
            stopLoading();
        }else {
            clearAnimation();
            startAnimation();
        }
        super.setVisibility(visibility);
    }

    /**
     * set duration of this ball rotate a circle
     * @param duration time of this animation
     * */
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

    public void startLoading(){
        mLoadingCompleted = false;
        startAnimation();
        invalidate();
    }

    /**
     * set countdown time, use this time to animation
     * @param seconds
     * */
    public void setCountDownTime(int seconds){
        setDuration(seconds * 1000);
        mNeedDrawTime = true;
    }

    private CountdownCompleteListener mCompleteListener = null;

    public void setCountdownCompleteListener(CountdownCompleteListener listener){
        mCompleteListener = listener;
    }

    /**
     * when animation's time is over, call this interface to
     * notify caller
     * */
    public interface CountdownCompleteListener{
        void countdownComplete();
    }
}
