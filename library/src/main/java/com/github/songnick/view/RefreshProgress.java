package com.github.songnick.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.github.songnick.LinearAnimation;
import com.nick.library.R;

/**
 * Created by SongNick on 15/9/8.
 */
public class RefreshProgress extends View{

    private static final String TAG = "RefreshProgress";
    private static final int ROTATE_MSG = 0x033;
    private static final int REFRESH_MSG = 0x044;
    private static final int SEEK_MSG = 0x055;

    private boolean isDrawAccProgress = false;
    private boolean mSeekAble = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ROTATE_MSG:
                    clearAnimation();
                    startRotate(10 * 1000, false);
                    mHandler.sendEmptyMessageDelayed(REFRESH_MSG, 3000);
                    break;
                case REFRESH_MSG:
                    stopRotate();
                    startRotate(1 * 1000, true);
                    mHandler.sendEmptyMessageDelayed(ROTATE_MSG, 3000);
                    break;
                case SEEK_MSG:
                    clearAnimation();
                    startRotate(10 * 1000, false);
                    break;
            }
        }
    };


    public RefreshProgress(Context context) {
        this(context, null);
    }

    public RefreshProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshProgress, 0, 0);

        mSeekAble = a.getBoolean(R.styleable.RefreshProgress_seekAble, false);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int defaultWidth = 0;
        int defaultHeight = 0;

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        defaultWidth = getPaddingLeft() + getPaddingRight() + defaultWidth;
        defaultHeight = defaultHeight + getPaddingTop() + getPaddingBottom();
        switch (widthMode){
            case  MeasureSpec.EXACTLY:
                //the size is confirmed - match parent or dd
                break;
            case MeasureSpec.AT_MOST:
                //wrap_content
                break;
            case MeasureSpec.UNSPECIFIED:
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
        setMeasuredDimension(width, height);
        rectF.set(17, 17, width - 17, height - 17);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSeekAble){
            drawSeekbar(canvas, startAngle);
        }else {
            if (isDrawAccProgress){
                drawAccProgressbar(startAngle, canvas);
            }else {
                drawSlowIndicator(startAngle, canvas);
            }
        }


    }

    private void drawSeekbar(Canvas canvas, float startAngle){
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#FF4444"));
        circlePaint.setStrokeWidth(7);
        circlePaint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.addArc(rectF, 0, startAngle);
        canvas.drawPath(path, circlePaint);
        int restoreCount = canvas.save();
        canvas.translate(rectF.centerX(), rectF.centerY());
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(getBallPath(startAngle), circlePaint);
        canvas.restoreToCount(restoreCount);
    }

    private void drawSlowIndicator(float startAngle, Canvas canvas){
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#A8D7A7"));
        circlePaint.setStrokeWidth(7);
        circlePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(getArcPath(), circlePaint);
        int restoreCount = canvas.save();
        canvas.translate(rectF.centerX(), rectF.centerY());
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(getBallPath(startAngle + 90), circlePaint);
        canvas.drawPath(getBallPath(startAngle + 90 + 30 + 90), circlePaint);
        canvas.drawPath(getBallPath(startAngle + 90 + 30 + 90 + 30 + 90), circlePaint);
        canvas.restoreToCount(restoreCount);
    }

    private void drawAccProgressbar(float startAngle, Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        int[] f = {Color.parseColor("#00A8D7A7"), Color.parseColor("#ffA8D7A7")};
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
        if (mSeekAble){

            mHandler.sendEmptyMessageDelayed(SEEK_MSG, 1000);
        }else
            mHandler.sendEmptyMessageDelayed(ROTATE_MSG, 500);
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
