package ru.yandex.yamblz.ui.views;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.lang.ref.WeakReference;

import timber.log.Timber;

import static java.lang.Math.*;

/**
 * Created by aleien on 30.07.16. */

public class SwipeToRevealTouchListener implements View.OnTouchListener {
    private float xStartPosition;
    private float startRotation;
    private final static float ANGLE_THRESHOLD = 25;

    private float xRevealPosition;

    private WeakReference<View> revealView;
    private boolean initScroll = true;

    public void setRevealView(View view) {
        this.revealView = new WeakReference<>(view);
        view.setAlpha(0.0f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (initScroll) {
                    startScrolling(v, x);
                    initScroll = false;
                }
                float distance = x - xStartPosition;
                animateSwipeView(v, distance);
                animateRevealView(v);
                Timber.d("Swipe to reveal MOVE");
                Timber.d("Distance: %s, rotation: %s", distance, xRevealPosition + v.getRotation() * 6 * cos(Math.toRadians(v.getRotation() * 10)));
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(v.getRotation()) < ANGLE_THRESHOLD) {
                    resetState(v);
                }

                initScroll = true;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    private void startScrolling(View v, float x) {
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() * 2);
        startRotation = v.getRotation();

        if (xRevealPosition == 0) {
            xRevealPosition = revealView.get().getX();
        }

        xStartPosition = x;
    }

    private void animateSwipeView(View v, float distance) {
        if (Math.abs(v.getRotation()) < ANGLE_THRESHOLD
                || v.getRotation() >= ANGLE_THRESHOLD && distance < 0
                || v.getRotation() <= -ANGLE_THRESHOLD && distance > 0) {
            v.setRotation(startRotation + distance / ANGLE_THRESHOLD);
        }
    }

    private void animateRevealView(View v) {
        if (revealView != null && revealView.get() != null) {
            float revealAlpha = abs(v.getRotation() / ANGLE_THRESHOLD);
            revealView.get().setAlpha(revealAlpha);
            revealView.get().setX((float) (xRevealPosition + v.getRotation() * 6 * cos(Math.toRadians(v.getRotation() * 10))));

        }
    }

    private void resetState(View v) {
        v.animate()
                .translationX(0)
                .rotation(0)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
        revealView.get().animate()
                .alpha(0)
                .translationX(0)
                .start();
    }
}
