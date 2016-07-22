//package ru.yandex.yamblz.ui.fragments;
//
//import android.animation.ArgbEvaluator;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.support.v4.app.ShareCompat;
//import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
//import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import org.xdty.preference.colorpicker.ColorPickerDialog;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
//import ru.yandex.yamblz.R;
//
//class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentHolder> implements ItemTouchHelperAdapter, RecyclerView.OnItemTouchListener {
//
//    static int pics[] = {R.drawable.black_widow, R.drawable.cap, R.drawable.iron_man, R.drawable.thor};
//    static int names[] = {R.string.black_widow, R.string.cap, R.string.iron_man, R.string.thor};
//    static int descs[] = {R.string.black_widow_desc, R.string.cap_desc, R.string.iron_man_desc, R.string.thor_desc};
//
//
//    ArrayList<Integer> rands = new ArrayList<>();
//    private Context context;
//
//    ContentAdapter(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(ContentHolder holder, int position) {
//        Random rand = new Random();
//        int cnt = rand.nextInt(pics.length);
//        Resources res = holder.itemView.getResources();
//        Bitmap src = BitmapFactory.decodeResource(res, pics[cnt]);
//        RoundedBitmapDrawable dr =
//                RoundedBitmapDrawableFactory.create(res, src);
//        dr.setCornerRadius(30);
//        dr.setCircular(true);
//        holder.img.setImageDrawable(dr);
//        holder.name.setText(names[cnt]);
//        holder.desc.setText(descs[cnt]);
//    }
//
//    @Override
//    public int getItemCount() {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public void onItemDismiss(int position) {
//        notifyItemRemoved(position);
//    }
//
//    @Override
//    public void onItemMove(int fromPosition, int toPosition) {
//
//    }
//
//
//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//    }
//
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//    }
//
//    static class ContentHolder extends RecyclerView.ViewHolder {
//        TextView name;
//        TextView desc;
//        ImageView img;
//        ContentHolder(View itemView) {
//            super(itemView);
//            name = ((TextView)itemView.findViewById(R.id.txtName));
//            img = ((ImageView)itemView.findViewById(R.id.avatar));
//            desc = ((TextView)itemView.findViewById(R.id.txtDesc));
//        }
//    }
//}
