package ru.yandex.yamblz.ui.touch;


import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class AnimationTouchListener implements View.OnTouchListener {
    private static float MAX_ANGLE = 25;
    private static int ROLL_BACK_DURATION = 200;

    private float backViewStartX;
    private float toDragViewStartX;
    private float screenCenterX;
    private View backView;


    private float x0;


    public AnimationTouchListener(WindowManager manager, @NonNull View backView) {
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screenCenterX = size.x / 2;
        this.backView = backView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (toDragViewStartX == 0) {
                    toDragViewStartX = v.getX();
                    backViewStartX = backView.getX();
                }
                x0 = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                float viewPivot = v.getX() + v.getPivotX();
                float dX = x0 - event.getRawX();

                v.animate()
                        .x(toDragViewStartX + dX)
                        .rotation(maptoRotation(viewPivot))
                        .setDuration(0)
                        .start();

                backView.animate()
                        .x(maptoBackView(dX))
                        .alpha(maptoAlfa(viewPivot))
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                rollback(v);
                break;
            default:
                return false;
        }
        return true;
    }

    private float maptoBackView(float offset) {
        float absOffset = Math.abs(offset);
        float result = 0;
        if (absOffset < screenCenterX / 2) {
            result = backViewStartX - offset;
        } else if (absOffset > screenCenterX) {
            result = backViewStartX;
        } else {
            float backOffset;
            if (offset > 0) {
                backOffset = -screenCenterX;
            } else {
                backOffset = screenCenterX;
            }
            result = backViewStartX + backOffset + offset;
        }
        return result;
    }


    private void rollback(View v) {
        v.animate()
                .x(toDragViewStartX)
                .rotation(0)
                .setDuration(ROLL_BACK_DURATION)
                .start();

        backView.animate()
                .x(backViewStartX)
                .alpha(0)
                .setDuration(ROLL_BACK_DURATION)
                .start();
    }

    private float maptoRotation(float pivotFromCenter) {
        return -(((screenCenterX - pivotFromCenter) / screenCenterX) * MAX_ANGLE);
    }

    private float maptoAlfa(float pivotFromCenter) {
        return Math.abs((screenCenterX - pivotFromCenter) / screenCenterX);
    }
}
