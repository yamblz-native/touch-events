package ru.yandex.yamblz.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import ru.yandex.yamblz.R;

public class ContentFragment extends BaseFragment {
    static int pics[] = {R.drawable.black_widow, R.drawable.cap, R.drawable.iron_man, R.drawable.thor};
    static int names[] = {R.string.black_widow, R.string.cap, R.string.iron_man, R.string.thor};
    static int descs[] = {R.string.black_widow_desc, R.string.cap_desc, R.string.iron_man_desc, R.string.thor_desc};
    //
    private GestureDetectorCompat mDetector;
    private CardView cardView;
    private ImageView delete;

    private TextView name;
    private TextView desc;
    private ImageView img;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        name = ((TextView) view.findViewById(R.id.txtName));
        img = ((ImageView) view.findViewById(R.id.avatar));
        desc = ((TextView) view.findViewById(R.id.txtDesc));
        newCard();

        desc.setMovementMethod(new ScrollingMovementMethod());
        mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());

        cardView = (CardView) view.findViewById(R.id.card_view);
        delete = (ImageView) view.findViewById(R.id.delete);
        cardView.setOnTouchListener(new MyGestureListener());
//        delete.setVisibility(View.INVISIBLE);
        return view;
    }

    void newCard() {
        Random rand = new Random();
        int cnt = rand.nextInt(pics.length);
        Resources res = getActivity().getResources();
        Bitmap src = BitmapFactory.decodeResource(res, pics[cnt]);
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(res, src);
        dr.setCornerRadius(30);
        dr.setCircular(true);
        img.setImageDrawable(dr);
        name.setText(names[cnt]);
        desc.setText(descs[cnt]);
        desc.scrollTo(0,0);

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private float direction = 0;
        private float fullWidth = 0;

        @Override
        public boolean onDown(MotionEvent event) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            fullWidth = size.x;
            direction = 0;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            direction += (int) distanceX;
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                float rot = direction / fullWidth * -50;
                float shift = (direction * 0.9f);
                float alpha = (direction / fullWidth);
                if (shift > 0) {
//                    cardView.animate().rotation(rot).translationX(-shift).setDuration(0);
                    delete.animate().alpha(alpha).setDuration(0);
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotation", rot).setDuration(0);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "translationX", -shift).setDuration(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(anim1, anim2);
                    animatorSet.start();
                }
//                flag = false;
                if (direction > fullWidth / 3) {
//                    flag = true;
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotation", 90).setDuration(500);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "translationX", -fullWidth).setDuration(500);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(anim1, anim2);
                    animatorSet.start();
                    newCard();
//                    anim1 = ObjectAnimator.ofFloat(cardView, "rotation", -90, 0).setDuration(500);
//                    anim2 = ObjectAnimator.ofFloat(cardView, "translationX", -fullWidth, 0).setDuration(500);
//                    animatorSet = new AnimatorSet();
//                    animatorSet.playTogether(anim1, anim2);
//                    animatorSet.start();
                    direction = 0;

                }
            } else {
                desc.computeScroll();
            }
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                cardView.animate().rotation(0).translationX(0).setDuration(500);
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotation", 0).setDuration(500);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "translationX", 0).setDuration(500);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(anim1, anim2);
                animatorSet.start();
                direction = 0;
                return true;
            }
            return mDetector.onTouchEvent(event);
        }
    }
}