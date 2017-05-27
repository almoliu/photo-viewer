package com.goertek.asp.smartpic.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goertek.asp.smartpic.R;

/**
 * Created by almo.liu on 2017/3/16.
 */

public class MenuSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static final String TAG = MenuSetViewHolder.class.getSimpleName();

    public final TextView mActionTitle;
    public final ImageView mActionImg;

    private IViewHolder mListener;

    public MenuSetViewHolder(View itemView,IViewHolder listener) {
        super(itemView);
        mActionTitle = (TextView)itemView.findViewById(R.id.menu_item_tv);
        mActionImg = (ImageView)itemView.findViewById(R.id.menu_item_iv);
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClickItem(getAdapterPosition());
    }

    /**
     * The interface to allow this class to interact with its parent.
     */
    public interface IViewHolder {
        /**
         * This method is called when the user clicks on the main view of an item.
         * @param position
         *              The position of the item in the list.
         */
        void onClickItem(int position);
    }
}
