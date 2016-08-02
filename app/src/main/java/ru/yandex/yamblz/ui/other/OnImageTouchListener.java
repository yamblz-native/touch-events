package ru.yandex.yamblz.ui.other;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class OnImageTouchListener implements View.OnTouchListener {

    float srcImageViewX;
    float srcImageViewY;

    float srcButtonViewX;
    float srcButtonViewY;

    float srcTouchX;
    float srcTouchY;

    float maxRotation = 30f;

    View imageView;
    View buttonView;
    ViewGroup parent;

    int touchId;

    public OnImageTouchListener(View imageView, View buttonView) {
        this.imageView = imageView;
        this.buttonView = buttonView;
        this.parent = (ViewGroup) this.imageView.getParent();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            v.clearAnimation();

            srcImageViewX = imageView.getX();
            srcImageViewY = imageView.getY();

            srcButtonViewX = buttonView.getX();
            srcButtonViewY = buttonView.getY();

            srcTouchX = event.getX();
            srcTouchY = event.getY();

            touchId = event.getPointerId(0);

        } else if (action == MotionEvent.ACTION_MOVE) {

            int pointerIndex = event.findPointerIndex(touchId);

            float destTouchX = event.getX(pointerIndex);

            float diffX = destTouchX - srcTouchX;

            //Image position changing
            float destViewX = v.getX() + diffX;
            float distance = destViewX - srcImageViewX;
            v.setX(destViewX);

            float rotation = maxRotation * distance / parent.getWidth();
            v.setRotation(rotation);

            //Button position changing
            float buttonDistance;
            if (Math.abs(distance) < srcButtonViewX / 2) {
                buttonDistance = srcButtonViewX - distance;
            } else {
                buttonDistance =
                        distance > 0 ?
                                Math.min(distance, srcButtonViewX) :
                                Math.max(srcButtonViewX * 2 + distance, srcButtonViewX);
            }
            float buttonAlpha = Math.abs(2 * distance / parent.getWidth());

            buttonView.setAlpha(buttonAlpha);
            buttonView.setX(buttonDistance);

        } else if (action == MotionEvent.ACTION_UP) {

            processView();

        } else {

            return false;

        }
        return true;
    }

    private void processView() {
        imageView.setX(srcImageViewX);
        imageView.setY(srcImageViewY);

        imageView.setRotation(0);

        buttonView.setX(srcButtonViewX);
        buttonView.setY(srcButtonViewY);

        buttonView.setAlpha(0);
    }
}
