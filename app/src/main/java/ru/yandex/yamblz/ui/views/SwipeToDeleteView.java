package ru.yandex.yamblz.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

/**
 * Created by Volha on 01.08.2016.
 */

public class SwipeToDeleteView extends FrameLayout implements View.OnTouchListener {

    private final int layoutId = R.layout.swipable_layout;

    private View content;
    private View back;

    private float x, x0;
    boolean isContentVisible = true;

    public SwipeToDeleteView(Context context) {
        super(context);
        init(context);
    }

    public SwipeToDeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeToDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, layoutId, (ViewGroup) getRootView());
        content = findViewById(R.id.content);
        back = findViewById(R.id.back);
        back.setAlpha(0f);
        back.setOnClickListener(v -> {
                ObjectAnimator alphaContentAnim = ObjectAnimator.ofFloat(content, "translationX", content.getTranslationX(), getWidth() + 50);
                alphaContentAnim.setDuration(500);
                ObjectAnimator rotateContentAnim = ObjectAnimator.ofFloat(content, "rotation", content.getRotation(), 360);
                rotateContentAnim.setDuration(500);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(alphaContentAnim, rotateContentAnim);
                animatorSet.start();
        });
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = event.getX();
                ObjectAnimator alphaContentAnim = ObjectAnimator.ofFloat(back, "alpha", 1f);
                alphaContentAnim.start();
                isContentVisible = true;
                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                float translation = x - x0;
                float absTranslation = Math.abs(translation);
                content.setRotation(translation / 5);
                content.setTranslationX(translation);

                if (absTranslation > 30 && absTranslation < 250) {
                    back.setAlpha(absTranslation / getWidth() * 2f);
                    back.setTranslationX( -(translation + 250) / 5 + 30 );
                } else if (absTranslation >= 250 && absTranslation < 350) {
                    ObjectAnimator alphaBackAnim = ObjectAnimator.ofFloat(back, "alpha", 1f);
                    alphaBackAnim.start();
                    back.setTranslationX( (translation + 250) / 5 + 30 );
                } else if (absTranslation >= 350) {
                    isContentVisible = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if ( isContentVisible ) {
                    ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(content, "rotation", 0f).setDuration(300);
                    ObjectAnimator translateContentAnim = ObjectAnimator.ofFloat(content, "translationX", content.getTranslationX(), 0f).setDuration(300);
                    ObjectAnimator translateBackAnim = ObjectAnimator.ofFloat(back, "translationX", back.getTranslationX(), 0f).setDuration(300);
                    ObjectAnimator alphaBackAnim = ObjectAnimator.ofFloat(back, "alpha", back.getAlpha(), 0f).setDuration(300);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(rotateAnim, translateContentAnim, translateBackAnim, alphaBackAnim);
                    animatorSet.start();
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x0 = ev.getX();
                return false;

            case MotionEvent.ACTION_MOVE:
                x = ev.getX();
                return Math.abs(x - x0) > 20;

            default:
                return false;
        }
    }
}
