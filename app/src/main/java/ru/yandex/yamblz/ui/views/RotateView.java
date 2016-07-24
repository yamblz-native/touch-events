package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;


/**
 * Created by olegchuikin on 24/07/16.
 */

public class RotateView extends FrameLayout {

    private View foreground;
    private View background;
    private static final String DEBUG_TAG = "DEBUG_TAG";

    private int foregroundLeftMarginDefault;
    private int foregroundRigthMarginDefault;
    private int backgroundLeftMarginDefault;
    private GestureDetector mGestureDetector;

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.rotate_view, this);
        background = findViewById(R.id.background);
        foreground = findViewById(R.id.foreground);
        foregroundLeftMarginDefault = ((LayoutParams) foreground.getLayoutParams()).leftMargin;
        foregroundRigthMarginDefault = ((LayoutParams) foreground.getLayoutParams()).rightMargin;
        backgroundLeftMarginDefault = ((LayoutParams) background.getLayoutParams()).leftMargin;
        mGestureDetector = new GestureDetector(context, new GestureListener(context));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        if (eventConsumed) {
            return true;
        } else
            return false;

        /*switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(DEBUG_TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }*/
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private float delta = 0;
        private float displayWidth;

        private final float angle = 60;

        public GestureListener(Context context) {

            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(metrics);
            displayWidth = metrics.widthPixels;

        }

        /*@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            foregroundMotion(0);
            return true;
        }*/

        @Override
        public boolean onDown(MotionEvent e) {
            delta = 0;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            delta += distanceX;

            foregroundMotion(delta);
            backgrounMotion(delta);
            return true;
        }

        private void foregroundMotion(float delta) {
            //angle and left/right margin depends on delta of scrolling
            foreground.setRotation(-delta / displayWidth * angle);
            LayoutParams layoutParams = (LayoutParams) foreground.getLayoutParams();
            layoutParams.leftMargin = (int) (foregroundLeftMarginDefault + Math.max(0, -delta / 2));
            layoutParams.rightMargin = (int) (foregroundRigthMarginDefault + Math.max(0, delta / 2));
            foreground.setLayoutParams(layoutParams);
        }

        private void backgrounMotion(float delta) {

            int scale = 3;

            background.setAlpha(Math.min(1.0f, Math.abs(delta / displayWidth)));
            LayoutParams layoutParams = (LayoutParams) background.getLayoutParams();
            if (Math.abs(delta) > displayWidth / 2) {
                layoutParams.leftMargin = (int) (backgroundLeftMarginDefault + Math.signum(delta) * (displayWidth / scale - Math.abs(delta) / scale));
            } else {
                layoutParams.leftMargin = (int) (backgroundLeftMarginDefault + Math.signum(delta) * (Math.abs(delta) / scale));
            }
        }


    }
}
