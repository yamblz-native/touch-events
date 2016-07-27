package ru.yandex.yamblz;

import android.view.MotionEvent;
import android.view.View;

public class CustomOnTouchListener implements View.OnTouchListener {

    float viewStartTranslationX;

    float touchStartX;
    int touchPointerId = -1;
    float prevX;

    View backView;
    float backViewStartTranslationX;

    public CustomOnTouchListener(View backView) {
        this.backView = backView; backView.setAlpha(0);
        backViewStartTranslationX = backView.getTranslationX();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int pointerIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(pointerIndex);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (touchPointerId == -1) {
                    viewStartTranslationX = view.getTranslationX();

                    prevX = touchStartX = motionEvent.getRawX();
                    touchPointerId = pointerId;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                touchPointerId = -1;
                view.animate().setDuration(150).translationX(viewStartTranslationX).rotation(0).start();
                backView.animate().setDuration(150).translationX(backViewStartTranslationX).alpha(0).start();
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = motionEvent.getRawX() - prevX;
                prevX = motionEvent.getRawX();

                if ((view.getTranslationX() - viewStartTranslationX < 300 && deltaX > 0) ||
                        (view.getTranslationX() - viewStartTranslationX > -300 && deltaX < 0)) {
                    view.setTranslationX(view.getTranslationX() + deltaX);

                    backView.setAlpha(Math.abs(view.getTranslationX() - viewStartTranslationX)/300);

                    if (Math.abs(view.getTranslationX() - viewStartTranslationX) < 150) {
                        backView.setTranslationX(backView.getTranslationX() - deltaX/2);
                    } else {
                        backView.setTranslationX(backView.getTranslationX() + deltaX/2);
                    }
                }

                view.setPivotX(view.getWidth()/2);
                view.setPivotY(view.getHeight());
                view.setRotation((view.getTranslationX() - viewStartTranslationX)/25);
                break;
        }

        return true;
    }

}
