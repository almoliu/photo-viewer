package com.goertek.asp.smartpic.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.goertek.asp.smartpic.R;

/**
 * Created by almo.liu on 2017/3/17.
 */

public class PicGroupViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = PicGroupViewHolder.class.getSimpleName();

    public TextView mTextView;
    public GridView mGridView;
    public TextView mTvSpace;

    public PicGroupViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView)itemView.findViewById(R.id.pic_item_tv);
        mGridView = (GridView)itemView.findViewById(R.id.pic_item_grid);
        mTvSpace = (TextView)itemView.findViewById(R.id.recycler_item_space);
    }

    public GridView getGridView() {
        return mGridView;
    }

}
