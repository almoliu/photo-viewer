package com.goertek.asp.smartpic.interfaces;

/**
 * Created by almo.liu on 2017/4/6.
 */

public interface PicItemPickedListener {
    void onPicItemPicked(String path,PicItemActionListener listener);
    void onPicItemUnPicked(String path,PicItemActionListener listener);
}
