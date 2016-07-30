package ru.yandex.yamblz.ui.views;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.lang.ref.WeakReference;

import timber.log.Timber;

import static java.lang.Math.*;

/**
 * Created by aleien on 30.07.16.
 */

public class SwipeToRevealTouchListener implements View.OnTouchListener {
    private float xStartPosition, yStartPosition;
    private float startRotation;
    private float x, y;
    private final float angleThreshold = 25;

    private float revealAlpha, xRevealPosition;

    private WeakReference<View> revealView;

    private enum ActionType {
        SWIPE,
        SCROLL,
        UNDEFINED
    }

    public void setRevealView(View view) {
        this.revealView = new WeakReference<>(view);
        view.setAlpha(0.0f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                v.setPivotX(v.getWidth() / 2);
                v.setPivotY(v.getHeight() * 2);
                startRotation = v.getRotation();

                if (xRevealPosition == 0) {
                    xRevealPosition = revealView.get().getX();
                }

                xStartPosition = x;
                yStartPosition = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float scroll = y - yStartPosition;

                float distance = x - xStartPosition;
                if (Math.abs(v.getRotation()) < angleThreshold
                        || v.getRotation() >= angleThreshold && distance < 0
                        || v.getRotation() <= -angleThreshold && distance > 0) {
                    v.setRotation(startRotation + distance / angleThreshold);
                }

                if (revealView != null && revealView.get() != null) {
                    revealAlpha = abs(v.getRotation() / angleThreshold);
                    revealView.get().setAlpha(revealAlpha);
                    revealView.get().setX((float) (xRevealPosition + v.getRotation() * 3 * cos(Math.toRadians(v.getRotation() * 12))));

                }
                break;
            case MotionEvent.ACTION_UP:
                Timber.d("Action UP");
                if (Math.abs(v.getRotation()) < angleThreshold) {
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
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    private ActionType determineActionType(float startX, float startY, float endX, float endY) {
        float xDistance = abs(endX - startX);
        float yDistance = abs(endY - startY);

        if (xDistance > angleThreshold && xDistance > yDistance) return ActionType.SWIPE;
        if (yDistance > angleThreshold && yDistance > xDistance) return ActionType.SCROLL;

        return ActionType.UNDEFINED;
    }
}
