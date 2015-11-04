package com.github.songnick.viewgroup;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * Created by qfsong on 15/10/26.
 */
public class SlideViewPager extends ViewGroup {

    private static final int MARGIN_LEFT_RIGHT = 150;
    private static final int MARGIN_TOP_BOTTOM = 400;
    private static final float SCALE_RATIO = 0.8f;
    private static final int MOVE_SIZE = 1080 - MARGIN_LEFT_RIGHT * 2;

    /**
     * view slide direction left to right
     * */
    private static int LEFT_TO_RIGHT = 0x011;

    /***
     * view slide direction right to left
     * */

    private static int RIGHT_TO_LEFT = 0x022;

    /**
     * view slide direction invalid
     * */
    private static int INVALID_DIRECTION = 0x033;

    /**
     * A null/invalid pointer ID.
     */
    public static final int INVALID_POINTER = -1;

    /**
     * A view is not currently being dragged or animating as a result of a fling/snap.
     */
    public static final int STATE_IDLE = 0;

    /**
     * A view is currently being dragged. The position is currently changing as a result
     * of user input or simulated user input.
     */
    public static final int STATE_DRAGGING = 1;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    public static final int STATE_SETTLING = 2;

    public static int  SNAP_VELOCITY = 600 ;

    private static final String TAG = SlideViewPager.class.getSimpleName();
    private float mDownX = 0.0f;
    private float mOriginalX = 0.0f;

    // Last known position/pointer tracking
    private int mActivePointerId = INVALID_POINTER;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private int[] mInitialEdgesTouched;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mPointersDown;

    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private float mMinVelocity;
    private int mCurrentPosition = 0;
    private int mCurrentDir = INVALID_DIRECTION;

    private ScrollerCompat mScroller;

    public SlideViewPager(Context context) {

        this(context, null);
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mScroller = ScrollerCompat.create(context, sInterpolator);
    }

    /**
     * Interpolator defining the animation curve for mScroller
     */
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     *
     * @param value Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as <code>value</code>
     */
    private float clampMag(float value, float absMin, float absMax) {
        final float absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    private void releaseViewForPointerUp() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        final float xvel = clampMag(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity);
        if (xvel != 0){
            slidToDestination();
        }
    }

    /**
     * The result of a call to this method is equivalent to
     */
    public void cancel() {
        mActivePointerId = INVALID_POINTER;
//        clearMotionHistory();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int actionIndex = MotionEventCompat.getActionIndex(event);
        mActivePointerId = MotionEventCompat.getPointerId(event, 0);

        if (mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();

                break;
            case MotionEvent.ACTION_MOVE:
                if (getChildAt(0).getLeft() == 0 ){
                    Log.e(TAG, " dddddddddd");
                }
                float distance = -(event.getRawX() - mDownX);
                mDownX = event.getRawX();
                Log.e(TAG, " current distance == " + distance);
                scrollBy((int) distance, 0);
                if (distance < 0){
                    scaleChild(mCurrentPosition, LEFT_TO_RIGHT);
                }else {
                    scaleChild(mCurrentPosition, RIGHT_TO_LEFT);
                }

                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVel = mVelocityTracker.getXVelocity();
                if (xVel > SNAP_VELOCITY && mCurrentPosition > 0){
                    slidToScreen(mCurrentPosition - 1);
                }else if (xVel < -SNAP_VELOCITY && mCurrentPosition < getChildCount() - 1){
                    slidToScreen(mCurrentPosition + 1);
                }else {
                    slidToDestination();
                }
                if (mVelocityTracker != null){
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }

    private void slidToDestination(){
        int scrollX = getScrollX();
        int position = (scrollX + 1080/2)/1080;
        int dx = position * (1080 - MARGIN_LEFT_RIGHT * 2) - scrollX;
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx) * 2);
        invalidate();
    }

    private void slidToScreen(int position){
        mCurrentPosition = position;
        if (mCurrentPosition > getChildCount()-1){
            mCurrentPosition = getChildCount() - 1;
        }
        int dx = position * (1080 - MARGIN_LEFT_RIGHT * 2) - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dx, 0,Math.abs(dx) * 2);
        invalidate();
    }

    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scaleChild(mCurrentPosition, mCurrentDir);
            scrollTo(mScroller.getCurrX(), 0);
        }
    }

    private void scaleChild(int position, int direction){
        mCurrentPosition = position;
        mCurrentDir = direction;
        float r = (float)getScrollX() / MOVE_SIZE;
        float scaleRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
        float shrinkRatio = 1.0f - (1.0f - SCALE_RATIO)*r;
        View scaleView = null;
        View shrinkView = null;
        if (direction == LEFT_TO_RIGHT){
            if (position > 0){
                scaleView = getChildAt(position - 1);
                shrinkView = getChildAt(position);
            }
        }else if (direction == RIGHT_TO_LEFT){
            if (position < getChildCount() - 1){
                scaleView = getChildAt(position + 1);
                shrinkView = getChildAt(position);

            }
        }else {
            throw new IllegalStateException("this is illegal state");
        }
        if (scaleView != null){
            ViewCompat.setScaleX(scaleView, scaleRatio);
            ViewCompat.setScaleY(scaleView, scaleRatio);
            ViewCompat.postInvalidateOnAnimation(scaleView);
        }
        if (shrinkView != null){
            ViewCompat.setScaleX(shrinkView, shrinkRatio);
            ViewCompat.setScaleY(shrinkView, shrinkRatio);
            ViewCompat.postInvalidateOnAnimation(shrinkView);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int originLeft = MARGIN_LEFT_RIGHT;
        int originTop = MARGIN_TOP_BOTTOM;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int left = originLeft + child.getMeasuredWidth() * i;
            int right = originLeft + child.getMeasuredWidth() * (i + 1);
            int bottom = originTop + child.getMeasuredHeight();
            child.layout(left, originTop, right, bottom);
            if (i != 0){
                child.setScaleX(SCALE_RATIO);
                child.setScaleY(SCALE_RATIO);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int width = 1080 - MARGIN_LEFT_RIGHT * 2;
        int height = 1920 - MARGIN_TOP_BOTTOM * 2;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

}
