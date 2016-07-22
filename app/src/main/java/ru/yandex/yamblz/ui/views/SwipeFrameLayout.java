
package ru.yandex.yamblz.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

public class SwipeFrameLayout extends FrameLayout
{
    public static final int ANIM_DURATION = 200;
    private final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

    private View childView;
    private View deleteView;
    private GestureDetector gestureDetector;

    private float rotationLeft;
    private float translationX;
    private float scale = 1.0f;

    public SwipeFrameLayout(Context context)
    {
        this(context, null, 0);
    }

    public SwipeFrameLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SwipeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (deleteView == null) throw new IllegalStateException("SwipeView should contain delete view child");
        if (ev.getAction() == MotionEvent.ACTION_UP)
        {
            ObjectAnimator rotationAnim;
            ObjectAnimator translationXAnim;
            ObjectAnimator scaleXAnim;
            ObjectAnimator scaleYAnim;

            rotationAnim = ObjectAnimator
                    .ofFloat(childView, "rotation", rotationLeft, 0)
                    .setDuration(ANIM_DURATION);
            rotationAnim.setInterpolator(accelerateInterpolator);

            translationXAnim = ObjectAnimator
                    .ofFloat(childView, "translationX", translationX, 0)
                    .setDuration(ANIM_DURATION);
            translationXAnim.setInterpolator(accelerateInterpolator);

            scaleXAnim = ObjectAnimator
                    .ofFloat(deleteView, "scaleX", scale, 1.0f)
                    .setDuration(ANIM_DURATION);
            scaleXAnim.setInterpolator(accelerateInterpolator);

            scaleYAnim = ObjectAnimator
                    .ofFloat(deleteView, "scaleY", scale, 1.0f)
                    .setDuration(ANIM_DURATION);
            scaleYAnim.setInterpolator(accelerateInterpolator);

            AnimatorSet animSet = new AnimatorSet();
            animSet.play(rotationAnim).with(translationXAnim).with(scaleXAnim).with(scaleYAnim);
            animSet.start();

            rotationLeft = 0;
            translationX = 0;
            scale = 0;

            return false;
        }

        gestureDetector.onTouchEvent(ev);
        return true;
    }

    public void addSwipeView(View view)
    {
        childView = view;
    }

    public void addDeleteView(View view)
    {
        deleteView = view;
    }

    private SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            translationX = e2.getRawX() - e1.getRawX();
            rotationLeft = translationX / 40;
            scale = Math.abs(translationX / 150);

            childView.setTranslationX(translationX);
            childView.setRotation(rotationLeft);

            deleteView.setScaleX(scale);
            deleteView.setScaleY(scale);

            return true;
        }
    };
}
