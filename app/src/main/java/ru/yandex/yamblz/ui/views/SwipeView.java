package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

/**
 * Created by Litun on 30.07.2016.
 */

public class SwipeView extends FrameLayout {
    public static final String LOG_TAG = "motion";
    private int mTouchSlop;
    private ViewDragHelper mDragHelper;
    Animator animator;

    public SwipeView(Context context) {
        super(context);
        init();
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findChild();
    }

    private void findChild() {
        animator = new Animator(findViewById(R.id.front), findViewById(R.id.back));
        front = findViewById(R.id.front);
    }

    View front;
    boolean swiping = false;
    float startX = 0f;
    float frontStartX = 0f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(LOG_TAG, "onInterceptTouchEvent " + MotionEvent.actionToString(ev.getAction()));
        boolean returnValue = super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                frontStartX = front.getX();
                swiping = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float dx = startX - x;
                if (!swiping && Math.abs(dx) > mTouchSlop)
                    swiping = true;
                if (swiping) {
//                    front.setX(frontStartX - dx);
                    returnValue = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                swiping = false;
                break;
        }

        return returnValue;
    }

    VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null)
                    mVelocityTracker = VelocityTracker.obtain();
                else mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null)
                    mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                if (swiping) {
                    float x = event.getX();
                    float dx = startX - x;
                    front.setX(frontStartX - dx);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1);
                float xVelocity = mVelocityTracker.getXVelocity();
                Log.i(LOG_TAG, "onTouchEvent velocity = " + xVelocity);
                startHideAnimation(xVelocity);
                mVelocityTracker.recycle();
                break;
        }

        return swiping;
    }

    private void startHideAnimation(float xVelocity) {
        float speed = Math.abs(xVelocity);
        if (speed < 0.1)
            xVelocity = 0.1f * Math.signum(xVelocity);
        float x = front.getX();
        int width = getWidth();
        float dX = frontStartX - x;
        float movedDistance = Math.abs(dX);
        float toMoveDistance = width - movedDistance;

        //if move back
        if (speed < 0.1 && movedDistance * 2 < front.getWidth() ||
                xVelocity < 0 && dX < 0 ||
                xVelocity > 0 && dX > 0) {
            TranslateAnimation animation = new TranslateAnimation(0, dX, 0, 0);
            animation.setDuration((long) (movedDistance / Math.abs(xVelocity)));
            animation.setFillAfter(false);
            animation.setAnimationListener(backAnimationListener);
            front.startAnimation(animation);
        } else { //if move away
            TranslateAnimation animation = new TranslateAnimation(0, -toMoveDistance * Math.signum(dX), 0, 0);
            animation.setDuration((long) (toMoveDistance / Math.abs(xVelocity)));
            animation.setFillAfter(false);
            animation.setAnimationListener(goneAnimationListener);
            front.startAnimation(animation);
        }
    }

    private Animation.AnimationListener goneAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            front.clearAnimation();
            front.setVisibility(GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    };

    private Animation.AnimationListener backAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            front.clearAnimation();
            front.setX(frontStartX);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    };

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return super.onHoverEvent(event);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return super.dispatchGenericMotionEvent(event);
    }

    class Animator {
        View front;
        View back;

        public Animator(View front, View back) {
            this.front = front;
            this.back = back;
        }

        /**
         * @param progress from -100f to 100f
         */
        public void onProgressChanged(float progress) {

        }

        public void onStopChanging(float velocity) {

        }
    }
}
