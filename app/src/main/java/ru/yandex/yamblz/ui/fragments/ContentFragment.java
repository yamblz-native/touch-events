package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import ru.yandex.yamblz.R;

public class ContentFragment extends BaseFragment {
    private RotateListener rotateListener;
    private GestureDetector gestureDetector;
    ImageView happySmile;
    ImageView sadSmile;
    int displayW;
    private float startHappyX;
    private float startSadX;
    private float happyX;
    private float sadX;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        happySmile = (ImageView) view.findViewById(R.id.happy);
        sadSmile = (ImageView) view.findViewById(R.id.sad);
        rotateListener = new RotateListener();
        gestureDetector = new GestureDetector(getContext(), rotateListener);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        displayW = displayMetrics.widthPixels;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    sadX = happyX;
                    sadSmile.setX(sadX);
                    happySmile.setRotation(0);
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        return view;
    }

    private class RotateListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            happyX -= distanceX;
            happySmile.setX(happyX);
            happySmile.setRotation((happyX / displayW) * 80);
            sadSmile.setAlpha(Math.abs(happyX / displayW));
            sadX += distanceX / 4;
            sadSmile.setX(sadX);
            return true;
        }
    }
}