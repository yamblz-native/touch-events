package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.yandex.yamblz.R;


public class MyFrameLayout extends FrameLayout {
    private final GestureDetector gestureDetector;
    private final EventListener eventListener;
    private float mainImgCurX;
    private float delImgCurX;
    private final float displayWidth;

    private float initMainImgX;
    private float initDelImgX;


    ImageView main_image;
    ImageView delete_image;

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        LayoutInflater.from(context).inflate(R.layout.images_layout, this);

        main_image = (ImageView) findViewById(R.id.main_image);
        delete_image = (ImageView) findViewById(R.id.delete_image);

        displayWidth = outMetrics.heightPixels;

        initMainImgX = 0;
        initDelImgX = 360;

        eventListener = new EventListener();
        gestureDetector = new GestureDetector(getContext(), eventListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mainImgCurX = initMainImgX;
            delImgCurX = initDelImgX;
            delete_image.setX(delImgCurX);
            main_image.setX(mainImgCurX);
            main_image.setRotation(0);
        }
        return gestureDetector.onTouchEvent(ev);
    }

    public class EventListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mainImgCurX -= distanceX;
            main_image.setX(mainImgCurX);
            delete_image.setAlpha(Math.abs(2 * mainImgCurX /displayWidth));
            main_image.setRotation(((mainImgCurX - initMainImgX)/displayWidth)*100);

            int direction = 1;
            if (Math.abs(initMainImgX - mainImgCurX) > displayWidth/4) {
                direction = -1;
            }
            delImgCurX -= -direction*distanceX/2;
            delete_image.setX(delImgCurX);

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
