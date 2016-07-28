package ru.yandex.yamblz.task;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

public class SwipeToDismissFrameLayout extends FrameLayout {

    private OnSwipedListener onSwipedListener;
    @IdRes private int swipeViewId;
    private View swipeView;
    @IdRes private int cancelViewId;
    private View cancelView;
    private float layoutWidth;
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
        reset();
    }

    public void setOnSwipedListener(OnSwipedListener onSwipedListener) {
        this.onSwipedListener = onSwipedListener;
    }

    private void reset() {
        swipeDetector = new SwipeDetector();
        gestureDetector = new GestureDetector(getContext(), swipeDetector);
    }

    private void animatedViewReset() {
        reset();
        final int RESET_TIME = 400;

        ObjectAnimator animator0 = ObjectAnimator
                .ofFloat(swipeView, "translationX", swipeView.getTranslationX(), 0)
                .setDuration(RESET_TIME);

        ObjectAnimator animator1 = ObjectAnimator
                .ofFloat(swipeView, "rotation", swipeView.getRotation(), 0)
                .setDuration(RESET_TIME);

        ObjectAnimator animator2 = ObjectAnimator
                .ofFloat(cancelView, "alpha", cancelView.getAlpha(), 0)
                .setDuration(RESET_TIME);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator0, animator1, animator2);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    private void viewReset() {
        reset();
        swipeView.setTranslationX(0);
        swipeView.setRotation(0);
        cancelView.setAlpha(0);
        cancelView.setScaleX(1);
        cancelView.setScaleY(1);
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
                swipeDetector.beginSwipe();
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
                    animatedViewReset();
                }
                break;
        }
        return true;
    }

    public interface OnSwipedListener {
        void OnSwiped(SwipeToDismissFrameLayout swipeToDismissFrameLayout);
    }

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        final int THRESHOLD = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity()
                / 20; // New British research has shown...

        boolean isScrolled = false;
        boolean isXDirection;
        boolean isSwiping = false;
        boolean isFading = false;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isSwiping || velocityX > -THRESHOLD || swipeView.getTranslationX() > 0) {
                return false;
            }

            Log.d(this.getClass().getSimpleName(), "fling");

            // time = distance / velocity
            float distance = swipeView.getTranslationX() + layoutWidth;
            long translateTime = (long) (distance * 1000 / -velocityX);

            ObjectAnimator animator0 = ObjectAnimator
                    .ofFloat(swipeView, "rotation",
                            swipeView.getRotation(), -90)
                    .setDuration(translateTime);
            ObjectAnimator animator1 = ObjectAnimator
                    .ofFloat(swipeView, "translationX",
                            swipeView.getTranslationX(), -layoutWidth)
                    .setDuration(translateTime);

            AnimatorSet translateAnimation = new AnimatorSet();
            translateAnimation.playTogether(animator0, animator1);
            Interpolator interpolator = new AccelerateInterpolator(0.8f);
            translateAnimation.setInterpolator(interpolator);

            long cancelTime = Math.max(600, translateTime);

            ObjectAnimator animator2 = ObjectAnimator
                    .ofFloat(cancelView, "alpha", 1, 0)
                    .setDuration(cancelTime);
            ObjectAnimator animator3 = ObjectAnimator
                    .ofFloat(cancelView, "scaleX", 1, 2)
                    .setDuration(cancelTime);
            ObjectAnimator animator4 = ObjectAnimator
                    .ofFloat(cancelView, "scaleY", 1, 2)
                    .setDuration(cancelTime);

            AnimatorSet cancelAnimation = new AnimatorSet();
            cancelAnimation.playTogether(animator2, animator3, animator4);
            cancelAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translateAnimation, cancelAnimation);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewReset();
                    if (onSwipedListener != null) {
                        onSwipedListener.OnSwiped(SwipeToDismissFrameLayout.this);
                    }
                }
            });
            animatorSet.start();

            isFading = true;

            return true;
        }

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
            }
            return true;
        }

        private void beginSwipe() {
            isSwiping = true;
        }
    }
}
