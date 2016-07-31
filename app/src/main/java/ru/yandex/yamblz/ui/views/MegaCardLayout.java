package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import ru.yandex.yamblz.R;

/**
 * Created by grin3s on 31.07.16.
 */

public class MegaCardLayout extends RelativeLayout {
    private static final String DEBUG_TAG = "MegaCardLayout";
    private static final float MIN_DELTA = 50;
    private float maxTransitionX;
    private float threshTransitionX;
    private final static float MAX_ROTATION = 20.0f;
    private float startShift;
    private float initialX;
    CardView mCardView;
    Button mButton;

    public MegaCardLayout(Context context) {
        super(context);
    }

    public MegaCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MegaCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        float curXEvent = ev.getRawX();
        float newX = startShift + curXEvent;
        float delta = newX - initialX;
        float absDelta = Math.abs(delta);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startShift = mCardView.getX() - curXEvent;
                break;
            case MotionEvent.ACTION_MOVE:
                if ((absDelta < maxTransitionX) && (absDelta > MIN_DELTA)) {
                    mCardView.setX(newX);
                    mCardView.setRotation(delta / maxTransitionX * MAX_ROTATION);
                    mButton.setTranslationX(-delta);
                    mButton.setTranslationY(-absDelta);
                    mButton.setAlpha(absDelta * absDelta / (maxTransitionX * maxTransitionX));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (absDelta < threshTransitionX) {
                    mCardView.animate().rotation(0).translationX(0).start();
                    mButton.animate().translationX(0).translationY(0).alpha(0).start();
                }
                else {
                    float signumDelta = Math.signum(delta);
                    mCardView.animate().rotation(signumDelta * MAX_ROTATION).translationX(signumDelta * maxTransitionX).start();
                    mButton.animate().translationX(-signumDelta * maxTransitionX).translationY(-maxTransitionX).alpha(1).start();
                }
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
        }
        return true;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Log.d(DEBUG_TAG, Float.toString(initialX));
        Log.d(DEBUG_TAG, Float.toString(getTranslationX()));
        Log.d(DEBUG_TAG, Float.toString(getX()));
        mCardView = (CardView) findViewById(R.id.maincardview);
        mCardView.setRadius(20);
        mButton = (Button) findViewById(R.id.delete_button);
        mButton.setAlpha(0);
        initialX = mCardView.getLeft();
        maxTransitionX = mCardView.getWidth() / 2.5f;
        threshTransitionX = maxTransitionX / 1.5f;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
