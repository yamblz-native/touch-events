package ru.yandex.yamblz.ui.other;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Aleksandra on 22/07/16.
 */
public class SwipeViewTouchListener implements View.OnTouchListener {

    public static final String DEBUG_TAG = "SwipeViewTouchListener";

    private static final String ALPHA_FIELD_NAME = "alpha";
    private static final String TRANSLATIONX_FIELD_NAME = "translationX";
    private static final String ROTATION_FIELD_NAME = "rotation";
    private static final String VISIBILITY_FIELD_NAME = "visibility";

    //Constants were chosen manually
    private static final int SWIPE = 400;
    private static final float ALPHA_FACTOR = 0.3f;
    private static final float ROTATION_FACTOR = 20;

    private View deleteView;
    private boolean viewIsRemoved = false;
    private float mDownX;

    public SwipeViewTouchListener(View deleteView) {
        this.deleteView = deleteView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (viewIsRemoved) {
            return true;
        }
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                mDownX = event.getRawX();
                Log.d(DEBUG_TAG, "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                float deltaX = event.getRawX() - mDownX;
                ObjectAnimator moveToLeftAnimation;
                ObjectAnimator rotationAnimation;

                if (deltaX < -SWIPE) {
                    final int animationTime = 1000;
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(v, ALPHA_FIELD_NAME, 0f);
                    anim1.setDuration(animationTime);

                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, ROTATION_FIELD_NAME, -1.5f * ROTATION_FACTOR);
                    anim2.setDuration(animationTime);

                    ObjectAnimator anim4 = ObjectAnimator.ofFloat(v, TRANSLATIONX_FIELD_NAME, -1.5f * SWIPE);
                    anim4.setDuration(animationTime);

                    ObjectAnimator anim3 = ObjectAnimator.ofInt(v, VISIBILITY_FIELD_NAME, View.GONE);
                    anim3.setDuration(0);

                    ObjectAnimator anim5 = ObjectAnimator.ofFloat(deleteView, ALPHA_FIELD_NAME, 1f);
                    anim5.setDuration(animationTime);

                    viewIsRemoved = true;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(anim1).with(anim2);
                    animatorSet.play(anim3).after(anim1);
                    animatorSet.play(anim1).with(anim4);
                    animatorSet.start();

                    return true;
                }

                if (deltaX < 0) {
                    moveToLeftAnimation = ObjectAnimator.ofFloat(v, TRANSLATIONX_FIELD_NAME, deltaX);
                    moveToLeftAnimation.setDuration(0);

                    rotationAnimation = ObjectAnimator.ofFloat(v, ROTATION_FIELD_NAME, ROTATION_FACTOR * (deltaX / SWIPE));
                    rotationAnimation.setDuration(0);

                    ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(
                            deleteView,
                            ALPHA_FIELD_NAME,
                            ALPHA_FACTOR * Math.abs(deltaX / SWIPE) > 1 ? 1f : ALPHA_FACTOR * Math.abs(deltaX / SWIPE)
                    );
                    alphaAnimation.setDuration(0);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(moveToLeftAnimation).with(rotationAnimation);
                    animatorSet.play(moveToLeftAnimation).with(alphaAnimation);
                    animatorSet.start();
                }
                Log.d(DEBUG_TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "Action was UP");
                // If swipe wasn't performed, return to initial state
                if (Math.abs(event.getRawX() - mDownX) < SWIPE) {
                    moveToLeftAnimation = ObjectAnimator.ofFloat(v, TRANSLATIONX_FIELD_NAME, 0);
                    moveToLeftAnimation.setDuration(0);
                    moveToLeftAnimation.start();

                    rotationAnimation = ObjectAnimator.ofFloat(v, ROTATION_FIELD_NAME, 0);
                    rotationAnimation.setDuration(0);
                    rotationAnimation.start();
                }
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return false;
        }
    }
}
