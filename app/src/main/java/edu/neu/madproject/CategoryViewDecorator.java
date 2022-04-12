//package edu.neu.madproject;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.view.View;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//public class CategoryViewDecorator extends RecyclerView.ItemDecoration {
//    private final int decorationHeight;
//    private Context context;
//
//    public CategoryViewDecorator(Context context) {
//        this.context = context;
//        decorationHeight = context.getResources().getDimensionPixelSize(R.dimen.decoration_height);
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
//
//        if (parent != null && view != null) {
//
//            int itemPosition = parent.getChildAdapterPosition(view);
//            int totalCount = parent.getAdapter().getItemCount();
//
//            if (itemPosition >= 0 && itemPosition < totalCount - 1) {
//                outRect.bottom = decorationHeight;
//            }
//
//        }
//
//    }
//}