package ru.yandex.yamblz.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class DismissableLayout extends FrameLayout {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int NO = 2;

    private static final int START = 0;
    private static final int END = 1;

    private int mTouchSlop;
    private float mLastX, mLastY;
    private float mDownX, mDownY;
    private int mDragCrossAxis;
    private int mDragMainAxis;
    private VelocityTracker mVelocityTracker;

    public DismissableLayout(Context context) {
        super(context);
        init(context);
    }

    public DismissableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DismissableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean draggingStart(float diff) {
        return mDragMainAxis != NO && mDragCrossAxis == START && diff < 0;
    }

    private boolean draggingEnd(float diff) {
        return mDragMainAxis != NO && mDragCrossAxis == END && diff > 0;
    }

    private boolean exceedsTouchSlop(float diff) {
        return Math.abs(diff) > mTouchSlop;
    }

    /*
     * Override the measureChild... implementations to guarantee that the child view
     * gets measured to be as large as it wants to be.  The default implementation will
     * force some children to be only as large as this view.
     */
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "ON TOUCH " + event.getAction());
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                final float x = event.getX();
                final float y = event.getY();
                final float diffX = x - mLastX;
                final float diffY = y - mLastY;

                if(mDragMainAxis == NO) {
                    if(exceedsTouchSlop(diffX) && exceedsTouchSlop(diffY)) {
                        break;
                    }
                    if(exceedsTouchSlop(diffX)) {
                        mDragMainAxis = HORIZONTAL;
                        mDragCrossAxis = (diffX < 0 ? START : END);
                    } else if(exceedsTouchSlop(diffY)) {
                        mDragMainAxis = VERTICAL;
                        mDragCrossAxis = (diffY < 0 ? START : END);
                    }
                }

                if(mDragMainAxis != NO) {
                    mLastX = x;
                    mLastY = y;
                    if(mDragMainAxis == HORIZONTAL) {
                        if (draggingStart(diffX) || draggingEnd(diffX)) {
                            final float width = getWidth();
                            setTranslationX(getTranslationX() + diffX);
                            setRotation(getRotation() + 0.2f * diffX / width * 180);
                        } else {
                            if (exceedsTouchSlop(diffX)) {
                                mDragCrossAxis = (diffX < 0 ? START : END);
                                setTranslationX(getTranslationX() + diffX);
                            }
                        }
                    } else {
                        if (draggingStart(diffY) || draggingEnd(diffY)) {
                            scrollBy(0, (int)-diffY);
                        } else {
                            if (exceedsTouchSlop(diffY)) {
                                mDragCrossAxis = (diffY < 0 ? START : END);
                                scrollBy(0, (int)-diffY);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragMainAxis = NO;
                mDragCrossAxis = NO;
                mVelocityTracker.clear();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("TAG", "ON INTERCEPT " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                mDownX = ev.getX();
                mDownY = ev.getY();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);
                return false;
            case MotionEvent.ACTION_MOVE:
                float diffX = ev.getX() - mLastX;
                float diffY = ev.getY() - mLastY;
                if(exceedsTouchSlop(diffX) && exceedsTouchSlop(diffY)) {
                    break;
                }
                if(exceedsTouchSlop(diffX)) {
                    mDragMainAxis = HORIZONTAL;
                    mDragCrossAxis = diffX < 0 ? START : END;
                    return true;
                } else if(exceedsTouchSlop(diffY)) {
                    mDragMainAxis = VERTICAL;
                    mDragCrossAxis = diffY < 0 ? START : END;
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                break;
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("TAG", "ON DISPATCH " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }
}
