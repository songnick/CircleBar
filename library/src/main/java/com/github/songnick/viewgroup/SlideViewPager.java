package com.github.songnick.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.github.songnick.utils.LogUtils;
import com.nick.library.R;

/**
 * Created by qfsong on 15/10/26.
 */
public class SlideViewPager extends ViewGroup {

    private static final int MARGIN_LEFT_RIGHT = 150;
    private static final int MARGIN_TOP_BOTTOM = 400;
    private static final float SCALE_RATIO = 0.8f;

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

    private float mMarginLeftRight = 0.0f;
    private float mMarginTopBottom = 0.0f;

    private int mSwitchSize = 0;

    public SlideViewPager(Context context) {

        this(context, null);
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleViewPager, 0, 0);
        mMarginLeftRight = a.getDimension(R.styleable.ScaleViewPager_marginLeftRight, 0);
        mMarginTopBottom = a.getDimension(R.styleable.ScaleViewPager_marginTopBottom, 0);
        a.recycle();
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
            return t * t * t  + 1.0f;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
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
                //calculate moving distance
                float distance = -(event.getRawX() - mDownX);
                mDownX = event.getRawX();
                LogUtils.LogD(TAG, " current distance == " + distance);
                scrollBy((int) distance, 0);
                if (distance < 0){
                    scaleChild(mCurrentPosition, LEFT_TO_RIGHT);
                }else {
                    LogUtils.LogD(TAG, " current direction is right to left and current child position =  " + mCurrentPosition);
                    scaleChild(mCurrentPosition, RIGHT_TO_LEFT);
                }

                break;
            case MotionEvent.ACTION_UP:
                releaseViewForPointerUp();
                cancel();
                break;
        }
        return true;
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
//        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
//        final float xvel = clampMag(
//                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
//                mMinVelocity, mMaxVelocity);
//        if (xvel != 0){
//            smoothScrollToDes();
//        }
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVel = mVelocityTracker.getXVelocity();
        if (xVel > SNAP_VELOCITY && mCurrentPosition > 0){
            smoothScrollToView(mCurrentPosition - 1);
        }else if (xVel < -SNAP_VELOCITY && mCurrentPosition < getChildCount() - 1){
            smoothScrollToView(mCurrentPosition + 1);
        }else {
            smoothScrollToDes();
        }
    }

    private boolean isRelease = false;
    /**
     * when user touch up, invoke this method,
     * and scroll to confirmed view smoothly
     * */
    private void smoothScrollToDes(){
        isRelease  = true;
        int scrollX = getScrollX();
        int position = (scrollX + getMeasuredWidth() / 2) / getMeasuredWidth();
        int dx = position * (getMeasuredHeight() - (int)mMarginLeftRight * 2) - scrollX;
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx) * 2);
        invalidate();
    }

    private void smoothScrollToView(int position){
        isRelease = false;
        mCurrentPosition = position;
        if (mCurrentPosition > getChildCount()-1){
            mCurrentPosition = getChildCount() - 1;
        }
        int dx = position * (getMeasuredWidth() - (int)mMarginLeftRight * 2) - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dx, 0,Math.abs(dx) * 2);
        invalidate();
    }

    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (isRelease){
                restoreView(mCurrentPosition, mCurrentDir);
            }else {
                int position = mCurrentPosition;
                if (mCurrentDir == RIGHT_TO_LEFT){
                    LogUtils.LogD(TAG, " current direction == right to left" );
                    position = mCurrentPosition - 1;
                }else if (mCurrentDir == LEFT_TO_RIGHT){
                    LogUtils.LogD(TAG, " current direction == left to right " );
                    position = mCurrentPosition + 1;
                }
                scaleChild(position, mCurrentDir);
            }
            scrollTo(mScroller.getCurrX(), 0);
        }
    }

    /**
     * shrink the size of current sliding the child, and scale the next child which
     * will slide to middle position
     * @param position current sliding child's position
     * @param direction the slide direction{@link #RIGHT_TO_LEFT } {@link #LEFT_TO_RIGHT}
     * */
    private void scaleChild(int position, int direction){

        mCurrentDir = direction;
        int intervalSize = position;
        if (direction == LEFT_TO_RIGHT ){
            intervalSize = position - 1;
        }
        float r = (float)(getScrollX()- mSwitchSize *intervalSize) / mSwitchSize;
        LogUtils.LogD(TAG, " current position == " + position + "current ratio == " + r);
        float scaleRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
        float shrinkRatio = 1.0f - (1.0f - SCALE_RATIO)*r;
        View scaleView = null;
        View shrinkView = null;
        float scale = 0.0f;
        float shrink = 0.0f;
        if (direction == LEFT_TO_RIGHT){
            if (position > 0){
                scaleView = getChildAt(position - 1);
                shrinkView = getChildAt(position);
                shrink = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
                scale = 1.0f - (1.0f - SCALE_RATIO)*r;
            }
        }else if (direction == RIGHT_TO_LEFT){
            if (position < getChildCount() - 1){
                scaleView = getChildAt(position + 1);
                shrinkView = getChildAt(position);
                scale = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
                shrink = 1.0f - (1.0f - SCALE_RATIO)*r;
            }
        }else {
            throw new IllegalStateException("this is illegal state");
        }
        if (scaleView != null){
            ViewCompat.setScaleX(scaleView, scale);
            ViewCompat.setScaleY(scaleView, scale);
            scaleView.invalidate();
        }
        if (shrinkView != null){
            ViewCompat.setScaleX(shrinkView, shrink);
            ViewCompat.setScaleY(shrinkView, shrink);
            shrinkView.invalidate();
        }
    }

    private void restoreView(int position, int direction){

        float r = (float)(getScrollX()- mSwitchSize * position) / mSwitchSize;
        View scaleView = null;
        View shrinkView = null;
        float scale = 0.0f;
        float shrink = 0.0f;
        if (direction == RIGHT_TO_LEFT ){
            if (position < getChildCount() - 1){
                scaleView = getChildAt(position);
                shrinkView = getChildAt(position + 1);
                scale = 1.0f - (1.0f - SCALE_RATIO) * r;
                shrink = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
            }
        }else if (direction == LEFT_TO_RIGHT){
            if (position > 0){
                scaleView = getChildAt(position);
                shrinkView = getChildAt(position - 1);
                scale = SCALE_RATIO + (1.0f - SCALE_RATIO) * r;
                shrink = 1.0f - (1.0f - SCALE_RATIO) * r;
            }
        }else {
            throw  new IllegalStateException(" unexception direction is set ");
        }
        LogUtils.LogD(TAG, " release position " + position + " scale ratio = " + scale + " shrink ratio = " + shrink + " direction == " + direction);
        if (scaleView != null){
            ViewCompat.setScaleX(scaleView, scale);
            ViewCompat.setScaleY(scaleView, scale);
            scaleView.invalidate();
        }
        if (shrinkView != null){
            ViewCompat.setScaleX(shrinkView, shrink);
            ViewCompat.setScaleY(shrinkView, shrink);
            shrinkView.invalidate();
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int originLeft = (int)mMarginLeftRight;
        int originTop = (int)mMarginTopBottom;

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
        // For simple implementation, our internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view.  We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int childCount = getChildCount();
        int width = measuredWidth - (int)(mMarginLeftRight * 2);
        int height = measuredHeight - (int)mMarginTopBottom * 2;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        mSwitchSize = measuredWidth - (int)mMarginLeftRight * 2;
    }

}
