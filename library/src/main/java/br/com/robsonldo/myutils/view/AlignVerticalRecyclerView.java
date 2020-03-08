package br.com.robsonldo.myutils.view;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class AlignVerticalRecyclerView extends RecyclerView {

    public AlignVerticalRecyclerView(android.content.Context context, android.util.AttributeSet attrs){
        super(context, attrs);
    }

    public AlignVerticalRecyclerView(android.content.Context context){
        super(context);
    }

    //Set not to scroll
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}