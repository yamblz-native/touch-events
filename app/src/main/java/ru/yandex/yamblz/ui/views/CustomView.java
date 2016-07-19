package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class CustomView extends FrameLayout {
    private final GestureDetector gestureDetector;
    private final ScrollListener scrollListener;
    private final int totalWidth;
    @BindView(R.id.view_img)
    ImageView imageView;
    @BindView(R.id.delete_img)
    ImageView delete;
    public CustomView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.card_view, this);
        ButterKnife.bind(this);
        delete.animate().alpha(0).setDuration(0).start();
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        totalWidth=metrics.widthPixels;
        scrollListener=new ScrollListener();
        gestureDetector=new GestureDetector(getContext(),scrollListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            scrollListener.onUp(event);
        }
        return gestureDetector.onTouchEvent(event);

    }

    private class ScrollListener extends GestureDetector.SimpleOnGestureListener {
        private float totalDistanceX;

        private ScrollListener() {
            super();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            totalDistanceX+=distanceX;
            if(totalDistanceX<0)return true;

            float rotation=-totalDistanceX/totalWidth*45;
            float movement= -totalDistanceX/totalWidth* totalWidth*1.3f;
            float alpha=Math.min(1,totalDistanceX/totalWidth*2f);
            imageView.setRotation(rotation);
            imageView.animate().rotation(rotation).translationX(movement).setDuration(0);
            delete.animate().alpha(alpha).setDuration(0);
            Log.d("ScrollLIstener",String.format("distanceX:%s rotation:%s movement:%s alpha:%s",
                    totalDistanceX,rotation,movement,alpha));
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            totalDistanceX=0;
            return true;
        }

        private boolean onUp(MotionEvent e){
            Log.d("ScrollListener","up");
            imageView.animate().setDuration(0).rotation(0).translationX(0);
            delete.animate().alpha(0).setDuration(0);
            return true;
        }

    }


}
