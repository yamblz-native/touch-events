package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class CustomView extends FrameLayout {
    @BindView(R.id.view_img)
    ImageView imageView;
    @BindView(R.id.delete_img)
    ImageView delete;
    public CustomView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.card_view, this);
        ButterKnife.bind(this);
        System.out.print("");
    }





}
