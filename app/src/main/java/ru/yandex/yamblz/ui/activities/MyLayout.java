package ru.yandex.yamblz.ui.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.yandex.yamblz.R;

/**
 * Created by GEORGY on 03.08.2016.
 */

public class MyLayout extends FrameLayout {

    private GestureListener eventListener = new GestureListener();
    private GestureDetector gestureDetector = new GestureDetector(getContext(), eventListener);
    private ImageView frontView;
    private ImageView backView;
    private float defaultFrontX, defaultEventX, defaultBackX;
    private float delta = 0;

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_layout, this);
        frontView = (ImageView) findViewById(R.id.image_front);
        backView = (ImageView) findViewById(R.id.image_back);
        defaultFrontX = frontView.getX();
        defaultBackX = backView.getX();
        //backView.setAlpha(0.0f);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (frontView.getVisibility() != INVISIBLE) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                delta = 0;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (delta > 350) {
                    removeView(frontView);
                    frontView.setVisibility(INVISIBLE);
                    backView.setAlpha(1f);
                    backView.setX(defaultBackX);
                } else {
                    if (frontView != null) {
                        frontView.setX(defaultFrontX);
                        frontView.setRotation(0);
                    }

                    backView.setAlpha(0f);
                    backView.setX(defaultBackX);

                }
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (frontView.getVisibility() != INVISIBLE) {
                if (delta == 0) {
                    defaultFrontX = frontView.getX();
                    defaultBackX = backView.getX();
                    defaultEventX = e1.getX();
                }
                delta += distanceX;
                if (delta >= 0) {

                    frontView.setX(frontView.getX() + distanceX * -1);
                    frontView.setRotation((float) ((frontView.getX() - defaultFrontX) / (frontView.getWidth() / 2 * Math.PI * 2) * 360));
                    Log.i("r", "" + (frontView.getX() - defaultFrontX) * 0.2f + " " + (frontView.getX() - defaultFrontX) / (frontView.getWidth() / 2 * Math.PI * 2) * 360);

                    backView.setAlpha(delta / defaultEventX);
                    backView.setX((float) (defaultBackX * (1 + Math.sin(delta / defaultEventX * Math.PI))));
                } else {
                    frontView.setX(defaultFrontX);
                    frontView.setRotation(0);

                    backView.setAlpha(0f);
                    backView.setX(defaultBackX);
                }
            }
            return true;

        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


}
