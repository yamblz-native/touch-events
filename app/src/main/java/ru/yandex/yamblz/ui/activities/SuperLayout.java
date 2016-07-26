package ru.yandex.yamblz.ui.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.yandex.yamblz.R;

/**
 * Created by dan on 23.07.16.
 */
public class SuperLayout extends FrameLayout {

    private EventListener eventListener = new EventListener();
    private GestureDetector gestureDetector = new GestureDetector(getContext(), eventListener);
    private ImageView pic;
    private float defaultMainX, defaultMainY, defaultDelX, defaultDelY;
    private boolean inited = false;
    private float delta = 0;
    private ImageView imageView;


    public SuperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!inited) {
            pic = (ImageView) findViewById(R.id.image1);
            defaultMainX = pic.getX();
            defaultMainY = pic.getY();
            pic = (ImageView) findViewById(R.id.image2);
            defaultDelX = pic.getX();
            defaultDelY = pic.getY();
            pic.setAlpha(0f);
            inited = true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            delta = 0;
        }

        if (event.getAction() == MotionEvent.ACTION_UP){
            Log.i("delta", " " + delta);
            if (delta > 350){
                Log.i("swipe beach", "tvar'");
                imageView = (ImageView) findViewById(R.id.image1);
                removeView(imageView);
            } else {
                imageView = (ImageView) findViewById(R.id.image1);
                imageView.setX(defaultMainX);
                imageView.setY(defaultMainY);
                imageView.setRotation(0);
                imageView.setAlpha(1f);


                imageView = (ImageView) findViewById(R.id.image2);
                //Animation lineTranslate;

                imageView.setX(defaultDelX);
                imageView.setY(defaultDelY);
                imageView.setAlpha(0f);
            }

        }

        return gestureDetector.onTouchEvent(event);
    }

    public class EventListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("x, y: ", " " + distanceX + " " + distanceY);
            delta+=distanceX;
            imageView = (ImageView) findViewById(R.id.image1);
            ImageView delpic = (ImageView) findViewById(R.id.image2);
            imageView.setX(imageView.getX() + distanceX*-1);
            imageView.setY(imageView.getY() + distanceY*-1*0.3f);
            imageView.setRotation((imageView.getX() - defaultMainX)*0.2f);
            delpic.setAlpha(imageView.getAlpha() + 0.025f);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    
}
