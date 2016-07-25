package ru.yandex.yamblz.ui.other;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import timber.log.Timber;

/**
 * Created by shmakova on 25.07.16.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {
    private static final int ANIMATION_DURATION = 200;
    private View view;
    private View delButton;
    private GestureDetectorCompat gestureDetector;
    private float viewInitialX;
    private float viewInitialY;
    private float delButtonInitialX;
    private float delButtonInitialY;
    private float viewWidth = 0;

    /**
     * Constructor
     * @param context
     * @param view
     * @param delButton
     */
    public OnSwipeTouchListener(Context context, View view, View delButton) {
        this.view = view;
        this.delButton = delButton;
        gestureDetector = new GestureDetectorCompat(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_UP) :
                Timber.d("Action was UP");
                returnBackToInitialState();
                return true;
            default:
                return gestureDetector.onTouchEvent(event);
        }
    }


    /**
     * Returns back to initial state
     */
    private void returnBackToInitialState() {
        Timber.d("Back to initial state");

        view.animate()
                .x(viewInitialX)
                .y(viewInitialY)
                .rotation(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(ANIMATION_DURATION);

        delButton.animate()
                .x(delButtonInitialX)
                .y(delButtonInitialY)
                .setInterpolator(new LinearInterpolator())
                .alpha(0)
                .setDuration(ANIMATION_DURATION);
}

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Timber.d("Action was DOWN");

            if (viewWidth == 0) {
                viewInitialX = view.getX();
                viewInitialY = view.getY();
                delButtonInitialX = delButton.getX();
                delButtonInitialY = delButton.getY();
                viewWidth = view.getWidth();
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Timber.d("Action was SCROLL");
            final float diffX = e2.getX() - e1.getX();
            final float alpha = Math.abs(diffX + viewInitialX) / (viewWidth / 5f);

            view.animate()
                    .rotationBy(diffX / 50f)
                    .translationXBy(diffX)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(0);

            delButton.animate()
                    .translationXBy(diffX / -5f)
                    .setInterpolator(new LinearInterpolator())
                    .alpha(alpha)
                    .setDuration(0);

            return true;
        }
    }
}
