package ru.yandex.yamblz.task;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

public class SwipeToDismissFrameLayout extends FrameLayout {

    @IdRes private int swipeViewId;
    private View swipeView;
    @IdRes private int cancelViewId;
    private View cancelView;
    private float layoutWidth;

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        boolean isScrolled = false;
        boolean isXDirection;
        boolean isSwiping = false;
        boolean isFading = false;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isSwiping) {
                isScrolled = true;
                isXDirection = Math.abs(distanceX) > Math.abs(distanceY);
            } else if (!isFading) {
                float ratio = swipeView.getTranslationX() / layoutWidth;

                float oldTranslation = swipeView.getTranslationX();
                swipeView.setTranslationX(oldTranslation - distanceX);
                swipeView.setRotation(swipeView.getTranslationX() / layoutWidth * 90);

                final float START_ALPHA = (float) 0.15;
                final float FINISH_ALPHA = (float) 0.3;

                if (ratio < -START_ALPHA) {
                    cancelView.setAlpha((-ratio - START_ALPHA) / (FINISH_ALPHA - START_ALPHA));
                } else {
                    cancelView.setAlpha(0);
                }

                if (ratio < -FINISH_ALPHA) {
                    ObjectAnimator animator0 = ObjectAnimator
                            .ofFloat(swipeView, "rotation",
                                    swipeView.getRotation(), -135)
                            .setDuration(600);

                    ObjectAnimator animator1 = ObjectAnimator
                            .ofFloat(swipeView, "translationX",
                                    swipeView.getTranslationX(), -layoutWidth)
                            .setDuration(600);

                    ObjectAnimator animator2 = ObjectAnimator
                            .ofFloat(cancelView, "alpha", 1, 0)
                            .setDuration(600);

                    ObjectAnimator animator3 = ObjectAnimator
                            .ofFloat(cancelView, "scaleX", (float) 1.0, (float) 1.5)
                            .setDuration(600);

                    ObjectAnimator animator4 = ObjectAnimator
                            .ofFloat(cancelView, "scaleY", (float) 1.0, (float) 1.5)
                            .setDuration(600);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animator0, animator1, animator2, animator3, animator4);
                    animatorSet.start();

                    isFading = true;
                }
            }
            return true;
        }

        private void beginSwiping() {
            isSwiping = true;
        }
    }

    private SwipeDetector swipeDetector;
    private GestureDetector gestureDetector;

    public SwipeToDismissFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SwipeToDismissFrameLayout, 0, 0);
        swipeViewId = typedArray
                .getResourceId(R.styleable.SwipeToDismissFrameLayout_swipe_view_id, 0);
        cancelViewId = typedArray
                .getResourceId(R.styleable.SwipeToDismissFrameLayout_cancel_view_id, 0);
        set();
    }

    private void set() {
        swipeDetector = new SwipeDetector();
        gestureDetector = new GestureDetector(getContext(), swipeDetector);
    }

    private void reset() {
        set();

        ObjectAnimator animator0 = ObjectAnimator
                .ofFloat(swipeView, "translationX", swipeView.getTranslationX(), 0)
                .setDuration(400);

        ObjectAnimator animator1 = ObjectAnimator
                .ofFloat(swipeView, "rotation", swipeView.getRotation(), 0)
                .setDuration(400);

        ObjectAnimator animator2 = ObjectAnimator
                .ofFloat(cancelView, "alpha", cancelView.getAlpha(), 0)
                .setDuration(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator0, animator1, animator2);
        animatorSet.start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        swipeView = findViewById(swipeViewId);
        cancelView = findViewById(cancelViewId);
        layoutWidth = right - left;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(this.getClass().getSimpleName(), "onInterceptTouchEvent");
        gestureDetector.onTouchEvent(event);
        if (swipeDetector.isScrolled) {
            //noinspection RedundantIfStatement
            if (swipeDetector.isXDirection) {
                Log.d(this.getClass().getSimpleName(), "intercepted");
                swipeDetector.beginSwiping();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(this.getClass().getSimpleName(), "canceling");
                if (!swipeDetector.isFading) {
                    reset();
                }
                break;
        }
        return true;
    }
}
