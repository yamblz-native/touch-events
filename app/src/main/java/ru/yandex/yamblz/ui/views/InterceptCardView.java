package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Created by aleien on 30.07.16.
 * ViewGroup, который обрабатывает скролл влево-вправо и передает управление вьюхе при скролле
 * вверх-вниз.
 */

public class InterceptCardView extends CardView  {
    private float xStartPosition, yStartPosition;

    public InterceptCardView(Context context) {
        super(context);
    }

    public InterceptCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xStartPosition = event.getRawX();
                yStartPosition = event.getRawY();
                Timber.d("Action DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                float distanceX = xStartPosition - event.getRawX();
                float distanceY = yStartPosition - event.getRawY();
                Timber.d("INTERCEPTOR: DistanceX: %s, distanceY: %s", distanceX, distanceY);
                return Math.abs(distanceY) < Math.abs(distanceX);

        }
        return true;
    }
}
