package ru.yandex.yamblz.ui.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.yandex.yamblz.R;

/**
 * Created by dalexiv on 7/28/16.
 */

public class SwipeableCard extends CardView {
    private static final String TAG = SwipeableCard.class.getSimpleName();

    // Animation constants
    private static final int FADE_OUT_ROTATION = 90;
    private static final float ROTATION_COEF = 1.0f / 20;
    private static final int ANIMATION_DURATION = 200;
    private static final int MAX_RETURN_BACK_ROTATION = 15;


    private GestureDetectorCompat gd = new GestureDetectorCompat(getContext(), new FlingGestureListener());

    public SwipeableCard(Context context) {
        super(context);
    }

    public SwipeableCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void addImageView() {
        final ImageView child = new ImageView(getContext());
        child.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.anwerrr));

        FrameLayout.LayoutParams params
                = new FrameLayout.LayoutParams(getWidth() / 2, getHeight() / 2,
                Gravity.CENTER_HORIZONTAL ^ Gravity.CENTER_VERTICAL);
        child.setImageAlpha(0);

        // TODO place below cardview
        addView(child, params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getChildCount() == 1) {
            addImageView();
            requestLayout();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
//        Log.d(TAG, ev.toString());
        // Handle scroll
        gd.onTouchEvent(ev);

        // Handle ACTION_UP
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            // Card animation
            if (Math.abs(getRotation()) < MAX_RETURN_BACK_ROTATION) {
                // Return card back
//                Log.d(TAG, "Current rotation is " + getRotation());
                animate().rotation(0)
                        .translationX(0)
                        .setStartDelay(0)
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new FastOutSlowInInterpolator());
            } else {
                // Swipe card out
                float translationToGo = getTranslationX() > 0
                        ? getHeight() : -getHeight();
                float rotationToGo = getRotation() > 0
                        ? FADE_OUT_ROTATION : -FADE_OUT_ROTATION;

                animate().rotation(rotationToGo)
                        .translationX(translationToGo)
                        .alpha(0)
                        .setStartDelay(0)
                        .setDuration(ANIMATION_DURATION);
            }

//             Delete button animation
            if (getChildCount() > 1) {
                ImageView viewToAnimate = (ImageView) getChildAt(1);
                ObjectAnimator animator = ObjectAnimator.ofInt(viewToAnimate,
                        "alpha", viewToAnimate.getImageAlpha(), 0);
                animator.setInterpolator(new FastOutSlowInInterpolator());
                animator.setDuration(ANIMATION_DURATION);
                animator.start();
            }
        }

        return true;
    }

    private class FlingGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final float delta = e2.getX() - e1.getX();
            animate().translationXBy(delta)
                    .rotationBy(delta * ROTATION_COEF)
                    .setStartDelay(0)
                    .setDuration(0);

            if (getChildCount() > 1) {
                ImageView viewToAnimate = (ImageView) getChildAt(1);
                final int alpha = (int) (Math.abs(getRotation())
                        / (2 * MAX_RETURN_BACK_ROTATION) * 255.0);
                viewToAnimate.setImageAlpha(alpha);

                viewToAnimate.animate()
                        .translationXBy(-delta)
                        .setStartDelay(0)
                        .setDuration(0);
            }
            Log.d(TAG, "" + getRotation());

            return true;
        }


    }
}
