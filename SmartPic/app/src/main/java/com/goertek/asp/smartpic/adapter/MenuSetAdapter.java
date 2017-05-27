package com.goertek.asp.smartpic.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.view.MenuSetViewHolder;

/**
 * Created by almo.liu on 2017/3/16.
 */

public class MenuSetAdapter extends RecyclerView.Adapter<MenuSetViewHolder> implements MenuSetViewHolder.IViewHolder{

    private static final String TAG = MenuSetAdapter.class.getSimpleName();

    private final int ITEM_NULL = -1;

    /**
     * The position for the item selected by the user.
     */
    private int mSelectedItem = ITEM_NULL;

    /**
     * The listener for all user interaction.
     */
    private final IListAdapterListener mListAdapterListener;
    /**
     * To know the state of the list: enabled or disabled.
     */
    private boolean mEnabled;

    private int[] mList;

    public MenuSetAdapter(IListAdapterListener listener) {
        super();
        mListAdapterListener = listener;
        mEnabled = true;
        mList = new int[0];
    }

    public void setList(int[] list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public MenuSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_settings_item, parent, false);
        return new MenuSetViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(MenuSetViewHolder holder, int position) {

        holder.mActionTitle.setText(mList[position]);
        holder.mActionImg.setImageResource(mListAdapterListener.getMenuItemImg(position));

        holder.itemView.setActivated(position == mSelectedItem);
        holder.itemView.setEnabled(mEnabled);
        if (mEnabled) {
            if (position == mSelectedItem) {
                //holder.mImageViewTick.setVisibility(View.VISIBLE);
            } else {
               // holder.mImageViewTick.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    @Override
    public void onClickItem(int position) {
        Log.d(TAG,"onClickItem: "+position);
        mListAdapterListener.onActionItemSelected(position);
    }

    /**
     * This interface allows the adapter to communicate with the element which controls the RecyclerView. Such as a
     * fragment or an activity.
     */
    public interface IListAdapterListener {
        /**
         * This method is called by the adapter when the user selects or deselects an item of the list.
         */
        void onActionItemSelected(int pos);
        int getMenuItemImg(int pos);
    }
}
