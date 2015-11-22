package com.github.songnick.androidview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.songnick.utils.LogUtils;
import com.nick.library.R;

/**
 * Created by SongNick on 15/11/6.
 */
public class SlidProgressbar extends RelativeLayout {

    private static final String TAG = SlidProgressbar.class.getSimpleName();
    private static final int FIRST_BAR_ID = 0x011 + 11;
    private static final int THUMB_ID = 0x011 + 22;

    private static final int MAX_DURATION = 100;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int INVALID_POINTER_ID = -1;

    /**
     * the progress bar
     * */
    private LinearLayout mFirstBar = null;
    private RelativeLayout.LayoutParams mFirstBarLp = null;

    /**
     * the thumb to slide
     * */
    private TextView mThumb = null;
    private RelativeLayout.LayoutParams mThumbLp = null;
    private float mThumbOriginalLeft = 0;
    private float mThumbOriginalTop = 0;
    private float mHitDowX = 0;
    private float mHitDowY = 0;

    /**
     * the second bar to indicator current progress
     * */
    private LinearLayout mSecondBar = null;
    private RelativeLayout.LayoutParams mSecondBarLp = null;
    private float mSecondBarOriWidth = 0;
    private float mSecondBarOriHeight =0;

    /** the orientation of this progress bar */
    private int mOrientation = LinearLayout.HORIZONTAL;

    /**  when the orientation is {@link #VERTICAL }, the width of this progress bar*/
    private int mProgressWidth = 0;

    /** when the orientation is {@link #HORIZONTAL}, the height of this progress bar*/
    private int mProgressHeight = 0;

    /** the thumb's radius */
    private int mThumbRadius = 0;

    private float mThumbTextSize = 0;

    /** the total time of this progress bar*/
    private int mMaxProgress = MAX_DURATION;

    /** this parameters is for test */
    private float mLastDistance = 0;

    /** last motion touch x value **/
    private float mLastMotionX = 0.0f;
    private int mActivePointerId = INVALID_POINTER_ID;

    /*** current duration */
    private int mCurrentDuration = 0;

    public SlidProgressbar(Context context) {
        this(context, null);
    }

    public SlidProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidProgressbar, 0, 0);
        mOrientation = a.getInt(R.styleable.SlidProgressbar_orientation, LinearLayout.HORIZONTAL);
        mProgressWidth = a.getDimensionPixelSize(R.styleable.SlidProgressbar_progressWidth, 0);
        mProgressHeight = a.getDimensionPixelSize(R.styleable.SlidProgressbar_progressHeight, 0);
        mThumbRadius = a.getDimensionPixelSize(R.styleable.SlidProgressbar_thumbRadius, 0);
        mThumbTextSize = a.getDimensionPixelSize(R.styleable.AccelerationProgress_timeTextSize, 0);
        a.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mOrientation == HORIZONTAL){
            initHorizontal(getContext());
//            initHorizontalViewTouchEvent();
        }else if (mOrientation == VERTICAL){
            initVerticalView(getContext());
            initVerticalViewTouchEvent();
        }
    }

    private void initHorizontal(Context context){
        if (mProgressHeight == 0){
            throw new IllegalStateException("you should set progress height");
        }
        mFirstBar = new LinearLayout(context);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#FFBB33"));
        drawable.setCornerRadius((float) mProgressHeight / 2);
        mFirstBar.setBackgroundDrawable(drawable);
//        mFirstBar.setBackgroundResource(R.drawable.firtbar_bkg);

        mFirstBarLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mProgressHeight);
        mFirstBarLp.addRule(CENTER_IN_PARENT);
        mFirstBar.setClickable(false);
        addView(mFirstBar, mFirstBarLp);

        mThumb = new TextView(context);
        mThumb.setBackgroundResource(R.drawable.slid_thumb_bkg);
        mThumbLp = new RelativeLayout.LayoutParams(mThumbRadius*2, mThumbRadius*2);
        mThumbLp.addRule(CENTER_VERTICAL);
        mThumbLp.addRule(ALIGN_PARENT_LEFT);
        mThumbLp.leftMargin = 0;
        addView(mThumb, mThumbLp);
        mThumb.setId(THUMB_ID);
        mThumb.setGravity(Gravity.CENTER);
        if (mThumbTextSize != 0)
        mThumb.setTextSize(mThumbTextSize);
//        mThumb.setText("10%");

        mSecondBar = new LinearLayout(context);

        GradientDrawable secondDrawable = new GradientDrawable();
        secondDrawable.setColor(Color.parseColor("#99CC00"));
        secondDrawable.setCornerRadius((float)mProgressHeight/2);
        mSecondBar.setBackgroundDrawable(secondDrawable);
//        mSecondBar.setBackgroundResource(R.drawable.secondbar_bkg);
        mSecondBarLp = new RelativeLayout.LayoutParams(mThumbRadius, mProgressHeight);
//        mSecondBarLp.leftMargin = 0;
        mSecondBarLp.addRule(CENTER_VERTICAL);
//        mSecondBarLp.addRule(LEFT_OF, THUMB_ID);
        mSecondBarLp.addRule(ALIGN_PARENT_LEFT);
        addView(mSecondBar, 1, mSecondBarLp);
        mSecondBar.setClickable(false);
    }

    private void initVerticalView(Context context){
        if (mProgressWidth == 0){
            throw new IllegalStateException("you should set the progressWidth");
        }
        mFirstBar = new LinearLayout(context);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#FFBB33"));
        drawable.setCornerRadius((float) mProgressWidth / 2);
        mFirstBar.setBackgroundDrawable(drawable);
        mFirstBarLp = new RelativeLayout.LayoutParams(mProgressWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mFirstBarLp.addRule(CENTER_IN_PARENT);
        addView(mFirstBar, mFirstBarLp);

        mThumb = new TextView(context);
        mThumb.setBackgroundResource(R.drawable.slid_thumb_bkg);
        mThumbLp = new RelativeLayout.LayoutParams(mThumbRadius*2, mThumbRadius*2);
        mThumbLp.addRule(CENTER_HORIZONTAL);
        mThumbLp.addRule(ALIGN_PARENT_TOP);
        mThumbLp.topMargin = 0;
        addView(mThumb, mThumbLp);
        mThumb.setId(THUMB_ID);
        mThumb.setGravity(Gravity.CENTER);
        if (mThumbTextSize != 0){
            mThumb.setTextSize(mThumbTextSize);
        }
//        mThumb.setText("10%");

        mSecondBar = new LinearLayout(context);
        GradientDrawable secondDrawable = new GradientDrawable();
        secondDrawable.setColor(Color.parseColor("#99CC00"));
        secondDrawable.setCornerRadius((float) mProgressWidth / 2);
        mSecondBar.setBackgroundDrawable(secondDrawable);
        mSecondBarLp = new RelativeLayout.LayoutParams(mProgressWidth, mThumbRadius);
//        mSecondBarLp.leftMargin = 0;
        mSecondBarLp.addRule(CENTER_HORIZONTAL);
//        mSecondBarLp.addRule(LEFT_OF, THUMB_ID);
        mSecondBarLp.addRule(ALIGN_PARENT_TOP);
        addView(mSecondBar, 1, mSecondBarLp);
    }

    private void initHorizontalViewTouchEvent(){
        mThumb.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mHitDowX = event.getX();
                        mLastMotionX = event.getX();
                        mThumbOriginalLeft = mThumbLp.leftMargin;
                        mSecondBarOriWidth = mSecondBarLp.width;
                        mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Scroll to follow the motion event
                        final int activePointerIndex = MotionEventCompat.findPointerIndex(
                                event, mActivePointerId);
                        final float x = MotionEventCompat.getX(event, activePointerIndex);
                        float distance = (event.getX() - mLastMotionX);
                        mLastMotionX = x;
                        mThumbLp.leftMargin = (int) (mThumbLp.leftMargin + distance);
                        mSecondBarLp.width = (int) (mSecondBarLp.width + distance);
                        LogUtils.LogD(TAG, " horizontal current distance == " + distance);
                        //confirm this thumb is show, no anywhere is hide
                        if (mThumbLp.leftMargin <= 0) {
                            mThumbLp.leftMargin = 0;
                            mSecondBarLp.width = mThumbRadius;
                        } else if (mThumbLp.leftMargin >= getMeasuredWidth() - mThumbRadius * 2) {
                            mThumbLp.leftMargin = getMeasuredWidth() - mThumbRadius * 2;
                            mSecondBarLp.width = getMeasuredWidth() - mThumbRadius;
                        }
//                        if (mMDThumbRl.leftMargin > getMeasuredWidth() - mRTRl.rightMargin) {
//                            Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin);
//                            mMDThumbRl.leftMargin = getMeasuredWidth() - mRTRl.rightMargin;
//                        }
//                        Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin + " sss = " + (getMeasuredWidth() - getStartEndMargin()));
                        updateViewLayout(mThumb, mThumbLp);
                        updateViewLayout(mSecondBar, mSecondBarLp);
//                        seekTo(SlideType.PLAY, mMDThumbRl.leftMargin);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
    }

    private void initVerticalViewTouchEvent(){
        mThumb.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mHitDowY = event.getY();
                        mThumbOriginalTop = mThumbLp.topMargin;
                        mSecondBarOriHeight = mSecondBarLp.height;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //right thumb started position is right
                        //so the user slit this thumb will generate negative count
                        float distance = (event.getY() - mHitDowY);
                        mLastDistance = distance;
                        if (distance < mLastDistance) {
                            LogUtils.LogD(TAG, " last distance is bigger current distance ");
                        }
                        mThumbLp.topMargin = (int) (mThumbOriginalTop + distance);
                        mSecondBarLp.height = (int) (mSecondBarOriHeight + distance);
                        //confirm this thumb is show, no anywhere is hide
//                        if (mMDThumbRl.leftMargin < mLTRl.leftMargin) {
//                            mMDThumbRl.leftMargin = mLTRl.leftMargin;
//                        }
//                        if (mMDThumbRl.leftMargin > getMeasuredWidth() - mRTRl.rightMargin) {
//                            Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin);
//                            mMDThumbRl.leftMargin = getMeasuredWidth() - mRTRl.rightMargin;
//                        }
//                        Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin + " sss = " + (getMeasuredWidth() - getStartEndMargin()));
                        if (mThumbLp.topMargin <= 0) {
                            mThumbLp.topMargin = 0;
                            mSecondBarLp.height = mThumbRadius;

                        }
                        if (mThumbLp.topMargin >= getMeasuredHeight() - mThumbRadius * 2) {
                            mThumbLp.topMargin = getMeasuredHeight() - mThumbRadius * 2;
                            mSecondBarLp.height = getMeasuredHeight() - mThumbRadius;
                        }
                        updateViewLayout(mThumb, mThumbLp);
                        updateViewLayout(mSecondBar, mSecondBarLp);
//                        seekTo(SlideType.PLAY, mMDThumbRl.leftMargin);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        break;
                }

                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mOrientation == HORIZONTAL){
            switch (heightMode){
                case MeasureSpec.EXACTLY:

                    break;
                case MeasureSpec.AT_MOST:
                    //if use this mode, the height is confirmed by parent
                    //we should use default size
                    if (mProgressHeight <= mThumbRadius*2){
                        height = mThumbRadius*2;
                    }else {
                        throw new IllegalStateException("you should set thumb radius is bigger than progress height");
                    }
                    break;
            }
        }else if (mOrientation == VERTICAL){
            switch (widthMode){
                case MeasureSpec.EXACTLY:

                    break;

                case MeasureSpec.AT_MOST:
                    if (mProgressWidth <= mThumbRadius*2){
                        width = mThumbRadius*2;
                    }else {
                        throw new IllegalStateException(" you should set thumb radius is bigger than progress width");
                    }
                    break;
            }
        }
        setMeasuredDimension(width, height);
    }

    /**
     * update the position of thumb according to time
     * @param position time
     * */
    public void seek(int position){
        if (position > getMaxProgress()){
            return;
        }
        int secondProgressSize = (int)((float)position/getMaxProgress() * getProgressWidth());
        int thumbMargin = (int)((float)position/getMaxProgress() * getThumbProgressWidth());
        if (mOrientation == HORIZONTAL){
//            LogUtils.LogD(TAG, " current seek size = " + seekSize + " position = " + position);
            mSecondBarLp.width = mThumbRadius + secondProgressSize;
            mThumbLp.leftMargin = thumbMargin;

        }else if (mOrientation == VERTICAL){
            mSecondBarLp.height = mThumbRadius + secondProgressSize;
            mThumbLp.topMargin = thumbMargin;
        }
        updateViewLayout(mSecondBar, mSecondBarLp);
        updateViewLayout(mThumb, mThumbLp);
    }

    /**
     * retrieve the total width of progress bar
     * @return width
     * */
    private int getProgressWidth(){
        int width = 0;
        if (mOrientation == HORIZONTAL){
            width = getMeasuredWidth() - mThumbRadius;
        }else if (mOrientation == VERTICAL){
            width = getMeasuredHeight() - mThumbRadius;
        }
        return width;
    }

    private int getThumbProgressWidth(){
        if (mOrientation == HORIZONTAL){

            return getMeasuredWidth() - mThumbRadius*2;
        }else {

            return getMeasuredHeight() - mThumbRadius*2;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean isDraged = false;
        Rect rect = new Rect();
        mThumb.getHitRect(rect);
        LogUtils.LogD(TAG, "current rect == " + rect + " down " + event.getX() + " douwn y = " + event.getY());
        switch (action){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                boolean contain = rect.contains((int)x, (int)y);
                if (contain){
                    mLastMotionX = event.getX();
                    isDraged = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                dragThumb(event.getX());
                break;

            case MotionEvent.ACTION_UP:

                break;
        }


        return isDraged;
    }

    private void dragThumb(float x){
// Scroll to follow the motion event
//        final int activePointerIndex = MotionEventCompat.findPointerIndex(
//                event, mActivePointerId);
//        final float x = MotionEventCompat.getX(event, activePointerIndex);
        float distance = (x - mLastMotionX);
        mLastMotionX = x;
        mThumbLp.leftMargin = (int) (mThumbLp.leftMargin + distance);
        mSecondBarLp.width = (int) (mSecondBarLp.width + distance);
        LogUtils.LogD(TAG, " horizontal current distance == " + distance);
        //confirm this thumb is show, no anywhere is hide
        if (mThumbLp.leftMargin <= 0) {
            mThumbLp.leftMargin = 0;
            mSecondBarLp.width = mThumbRadius;
        } else if (mThumbLp.leftMargin >= getMeasuredWidth() - mThumbRadius * 2) {
            mThumbLp.leftMargin = getMeasuredWidth() - mThumbRadius * 2;
            mSecondBarLp.width = getMeasuredWidth() - mThumbRadius;
        }
//                        if (mMDThumbRl.leftMargin > getMeasuredWidth() - mRTRl.rightMargin) {
//                            Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin);
//                            mMDThumbRl.leftMargin = getMeasuredWidth() - mRTRl.rightMargin;
//                        }
//                        Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin + " sss = " + (getMeasuredWidth() - getStartEndMargin()));
        updateViewLayout(mThumb, mThumbLp);
        updateViewLayout(mSecondBar, mSecondBarLp);
    }

    /**
     * set the max duration of the progress
     * @param maxProgress max value
     * */
    public void setMaxProgress(int maxProgress){
        mMaxProgress = maxProgress;
    }

    /***
     * get the max value of progress bar
     * @return
     * */
    public int getMaxProgress(){

        return mMaxProgress;
    }

    public int getCurrentDuration(){

        return mCurrentDuration;
    }
}
