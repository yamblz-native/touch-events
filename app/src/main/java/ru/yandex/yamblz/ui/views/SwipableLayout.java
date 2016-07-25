package ru.yandex.yamblz.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class SwipableLayout extends FrameLayout {
    private MyGestureListener myListener;
    private GestureDetector mDetector;
    private final int fullWidth;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.avatar)
    ImageView img;
    @BindView(R.id.txtDesc)
    TextView desc;
    @BindView(R.id.txtName)
    TextView name;

    private static final int pics[] = {R.drawable.black_widow, R.drawable.cap, R.drawable.iron_man, R.drawable.thor};
    private static final int names[] = {R.string.black_widow, R.string.cap, R.string.iron_man, R.string.thor};
    private static final int descs[] = {R.string.black_widow_desc, R.string.cap_desc, R.string.iron_man_desc, R.string.thor_desc};

    private Context context;

    public SwipableLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.card, this);
        ButterKnife.bind(this);
        delete.animate().alpha(0).setDuration(0).start();
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        fullWidth = metrics.widthPixels;
        myListener = new MyGestureListener();
        mDetector = new GestureDetector(getContext(), myListener);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return myListener.onTouch(event);
    }

    private void newCard() {
        Random rand = new Random();
        int cnt = rand.nextInt(pics.length);
        Picasso.with(context).load(pics[cnt]).into(img);
        name.setText(names[cnt]);
        desc.setText(descs[cnt]);
        desc.scrollTo(0,0);

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private float direction = 0;
        private float height = 0;
        private boolean change = false;

        @Override
        public boolean onDown(MotionEvent event) {
            direction = 0;
            Log.w("Listener", "onDown");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.w("Listener", "onScroll" + cardView.getLeft());
            direction += (int) distanceX;
            height += distanceY;
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                float rot = direction / fullWidth * -50;
                float alpha = (direction / fullWidth);
                if (direction > 0) {
                    cardView.animate().rotation(rot).translationX(-direction).setDuration(0);
                    delete.setAlpha(alpha);
                    delete.setTranslationX(direction*0.1f);
                    delete.setTranslationY(-direction*0.1f);
                }

                if (direction > fullWidth / 2) {
                    Log.w("Listener", "longScroll");
                    change = true;
                }
            } else {
                desc.scrollTo(0, Math.max((int)(height*0.5f), 0));
            }
            return true;
        }

        boolean onTouch(MotionEvent event) {
            Log.w("Listener", "onTouch");
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.w("Listener", "onUp" + change);
                if (change) {
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotation", -90).setDuration(300);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "translationX", -fullWidth).setDuration(300);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(anim1, anim2);
                    animatorSet.start();
                    newCard();
                    anim1 = ObjectAnimator.ofFloat(cardView, "rotation", 90).setDuration(0);
                    anim2 = ObjectAnimator.ofFloat(cardView, "translationX", fullWidth).setDuration(0);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(anim1, anim2);
                    animatorSet.start();
                    direction = 0;
                    height = 0;
                }

                ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotation", 0).setDuration(300);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "translationX", 0).setDuration(300);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(anim1, anim2);
                animatorSet.start();
                delete.animate().alpha(0);
                delete.animate().alpha(0).setDuration(0);
                direction = 0;
                change = false;
                return true;
            }
            return mDetector.onTouchEvent(event);
        }

    }

}