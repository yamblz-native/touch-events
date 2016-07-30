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
    private float xStartPosition;
    private float yStartPosition;
    private float xViewPosition;
    private float yViewPosition;
    private float startRotation;
    private float x, y;
    private final float angleThreshold = 25;
    private float maxDistance = 500;

    private float revealAlpha, revealTranslateX, xRevealPosition;

    private WeakReference<View> revealView;

    private enum ActionType {
        SWIPE,
        SCROLL,
        UNDEFINED
    }

    public void setRevealView(View view) {
        this.revealView = new WeakReference<>(view);
        view.setAlpha(0.0f);
        maxDistance = revealView.get().getLayoutParams().width;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                v.setPivotX(v.getWidth() / 2);
                v.setPivotY(v.getHeight() * 2);

                xViewPosition = v.getX();
                yViewPosition = v.getY();
                startRotation = v.getRotation();

                if (xRevealPosition == 0) {
                    xRevealPosition = revealView.get().getX();
                }

                xStartPosition = x;
                yStartPosition = y;
                Timber.d("Action DOWN: [ " + xStartPosition + "; " + yStartPosition + " ]");
                break;
            case MotionEvent.ACTION_MOVE:
                Timber.d("Action MOVE: [ " + x + "; " + y + " ]");
                Timber.d("Distance moved: [ " + (x - xStartPosition) + " ]");

                float distance = x - xStartPosition;
                if (Math.abs(v.getRotation()) < angleThreshold
                        || v.getRotation() >= angleThreshold && distance < 0
                        || v.getRotation() <= -angleThreshold && distance > 0) {
                    v.setRotation(startRotation + distance / angleThreshold);
                }

                if (revealView != null && revealView.get() != null) {
                    Timber.d("Alpha: " + (xViewPosition + distance) / (2 * maxDistance));
                    revealAlpha = abs(v.getRotation() / angleThreshold);
                    revealView.get().setAlpha(revealAlpha);
                    if (distance > 0) {
                        revealView.get().setX((float) (xRevealPosition + v.getRotation() * 2 * sin(Math.toRadians(v.getRotation() * 12))));
                    } else {
                        revealView.get().setX((float) (xRevealPosition - v.getRotation() * 2 * sin(Math.toRadians(v.getRotation() * 12))));
                    }

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
