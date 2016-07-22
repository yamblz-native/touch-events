//package ru.yandex.yamblz.ui.fragments;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.helper.ItemTouchHelper;
//
//
///**
// * Created by vorona on 19.07.16.
// */
//public class TouchHelperCallback extends ItemTouchHelper.Callback {
//    private final ItemTouchHelperAdapter mAdapter;
//
//    public TouchHelperCallback(
//            ItemTouchHelperAdapter adapter) {
//        mAdapter = adapter;
//    }
//
//    @Override
//    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = 0;
//        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
//        return makeMovementFlags(dragFlags, swipeFlags);
//    }
//
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean onMove(RecyclerView recyclerView,
//                          RecyclerView.ViewHolder viewHolder,
//                          RecyclerView.ViewHolder target) {
//        mAdapter.onItemMove(viewHolder.getAdapterPosition(),
//                target.getAdapterPosition());
//        return true;
//    }
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder,
//                         int direction) {
//        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
//    }
//
//
//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView,
//                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                            int actionState, boolean isCurrentlyActive) {
//
////        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
////            float width = (float) viewHolder.itemView.getWidth();
////            float alpha = 1.0f - Math.abs(dX) / width;
////            int reddish = (int) (255 * (alpha));
////            Paint paint = new Paint();
////            paint.setColor(Color.rgb(255, reddish, reddish));
////            c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);
////            viewHolder.itemView.setTranslationX(dX);
////        } else {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
//                    actionState, isCurrentlyActive);
////        }
//    }
//}
