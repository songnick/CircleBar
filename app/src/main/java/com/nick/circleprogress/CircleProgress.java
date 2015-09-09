package com.nick.circleprogress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by qfsong on 15/8/16.
 */
public class CircleProgress extends View {

    private static final int CIRCLE_MSG = 0x01;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            if (startAngle + (15 + durationAngle)*startCount >= 270 && !decrease){
//                removeMessages(CIRCLE_MSG);
////                startCount = 0;
//                decrease = true;
//                handler.sendEmptyMessageDelayed(CIRCLE_MSG, 1000);
//            }else {
                invalidate();
//                sendEmptyMessageDelayed(CIRCLE_MSG, 1000);
//                if (decrease){
//                    if (endCount > startCount){
//                        startCount = 0;
//                        endCount = 0;
//                        decrease = false;
//                        handler.sendEmptyMessageDelayed(CIRCLE_MSG, 1000);
//                    }else {
//                        endCount++;
//                    }
//
//                }else {
////                    startCount++;
//                }
//            }
        }
    };

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        handler.sendEmptyMessageDelayed(CIRCLE_MSG, 400);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawProgress(canvas);
    }

    private static final int DEFAUTL_SWEEP_ANGLE = 15;

    private int startAngle = 10;
    private int durationAngle = 10;
    private int sweepAngle = DEFAUTL_SWEEP_ANGLE;
    private int startCount = 4;
    private boolean decrease = false;
    private int endCount = 0;
    private Paint redPaint = null;
    private Paint transparentPaint = null;


    private void initPaint(){
        redPaint = new Paint();
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setAntiAlias(true);
        redPaint.setStrokeWidth(10);
        redPaint.setColor(Color.RED);

        transparentPaint = new Paint();
        transparentPaint.setStyle(Paint.Style.STROKE);
        transparentPaint.setAntiAlias(true);
        transparentPaint.setStrokeWidth(10);
        transparentPaint.setColor(Color.TRANSPARENT);
    }

    private void drawArc(Canvas canvas){
        Path path = new Path();
        RectF rectF = new RectF(100, 10, 400, 310);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        if (decrease){
            paint.setColor(Color.TRANSPARENT);
            for (int i = 0; i <= startCount; i++){
                float du = (15 + durationAngle)*i + 2.5f*i;
//                if (i < endCount){
//                    canvas.drawArc(rectF, startAngle + du, sweepAngle,false, transparentPaint);
//                }else {
                    canvas.drawArc(rectF, startAngle + du, sweepAngle,false, redPaint);
//                }
                //i more than big , start angle should big
//                canvas.drawArc(rectF, -90, 90,false, paint);
                Log.e("", " startcount = " + startCount + " i =" + i);
//                path.addArc(rectF, startAngle + du, 15);
            }
//            sweepAngle--;
        }else{
            for (int i = 0; i <= 4; i++){
                float du = (15 + durationAngle)*i;
//            if (du >= 270 ){
////                handler.removeMessages(CIRCLE_MSG);
//                startCount = 0;
//                decrease = true;
//            }else {
//
//            }
                Log.e("", " startcount = " + startCount + " i =" + i);
//                path.addArc(rectF, startAngle + du, 15);
                if (startAngle + (15 + durationAngle)*startCount >= 270){
                    du += 2.5f*i;
                }
                canvas.drawArc(rectF, startAngle + du, sweepAngle,false, redPaint);

            }
//            if (startAngle + (15 + durationAngle)*startCount < 270){
//                du += 2.5f*i;
//            }
            startAngle += 5;
            paint.setColor(Color.RED);
        }

//        path.addArc(rectF, -90, 90);
//        path.addArc(rectF, startAngle, 15);

//        canvas.drawPath(path,paint);
//        startAngle = startAngle + 5;

//        path.addArc(rectF, startAngle + 15 + durationAngle, 15);
//        path.addArc(rectF, startAngle + 15*2 + durationAngle*2, 15);
//        path.addArc(rectF, startAngle + 15*3 + durationAngle*3, 15);

//        canvas.draw
//        path.setFillType(FIl);
    }

    private boolean progressIsDraw = false;

    private void drawProgress(Canvas canvas){
        if (!progressIsDraw){
            RectF rectF = new RectF(100, 10, 400, 310);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);
            canvas.drawArc(rectF, -90, 90,false, paint);
        }
    }

}
