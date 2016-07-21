package ru.yandex.yamblz.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class DismissableLayout extends FrameLayout {

    private int mTouchSlop;
    private float mLastX;
    private int mDragDirection;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private boolean mDragging;
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

    private boolean draggingLeft(float diff) {
        return mDragging && mDragDirection == LEFT && diff < 0;
    }

    private boolean draggingRight(float diff) {
        return mDragging && mDragDirection == RIGHT && diff > 0;
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
                float diffX = x - mLastX;
                if(!mDragging && Math.abs(diffX) > mTouchSlop) {
                    mDragging = true;
                    mDragDirection = (diffX < 0 ? LEFT : RIGHT);
                }
                if(draggingLeft(diffX) || draggingRight(diffX)) {
                    setTranslationX(getTranslationX() + diffX);
                    mLastX = x;
                } else {
                    if(mDragging && Math.abs(diffX) > mTouchSlop) {
                        mDragDirection = (diffX < 0 ? LEFT : RIGHT);
                        setTranslationX(getTranslationX() + diffX);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
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
                mLastX = (int)ev.getX();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);
                return false;
            case MotionEvent.ACTION_MOVE:
                float diffX = ev.getX() - mLastX;
                if(Math.abs(diffX) > mTouchSlop) {
                    mDragging = true;
                    mDragDirection = diffX < 0 ? LEFT : RIGHT;
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
