package com.goertek.asp.smartpic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.goertek.asp.smartpic.util.PicCache;

import java.util.ArrayList;

/**
 * Created by almo.liu on 2017/3/17.
 */

public class PicItem implements PicCache.NativeImageCallBack {

    private static final String TAG = PicItem.class.getSimpleName();

    private static final int PIC_MAX_SHOW_SIZE = 8;

    private Context mContext;

    private String mTitle;

    private ArrayList<String> mBitPaths;



    public static PicItem newInstance(Context context, String title) {
        PicItem picItem = new PicItem(context,title);
        return picItem;
    }

    public PicItem(Context context, String title) {
        mContext = context;
        mTitle = title;
        mBitPaths = new ArrayList<>();
    }

    public String getTitle() {
        return mTitle;
    }

    public int getItemNum() {
        return mBitPaths.size();
    }
/*
    public void preLoadMaps() {
        PicCache picCache = PicCache.getInstance();
        for(int i = 0;i<mBitPaths.size();i++) {
            String path = mBitPaths.get(i);
            Point point = new Point(mContext.getResources().getInteger(R.integer.pic_item_size),
                    mContext.getResources().getInteger(R.integer.pic_item_size));
            picCache.cacheNativeImg(path,point,this);
        }
    }
*/
    public ArrayList<String> getPartBitPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for(int i = 0;i<mBitPaths.size()&&i<PIC_MAX_SHOW_SIZE;i++) {
            paths.add(mBitPaths.get(i));
        }
        return paths;
    }

    public ArrayList<String> getBitPaths() {
        return mBitPaths;
    }

/*
    public List<Bitmap> getBitMaps() {

        PicCache picCache = PicCache.getInstance();
        List<Bitmap> bitmaps = new ArrayList<>();
        for(int i = 0;i<mBitPaths.size()&&i<PIC_MAX_SHOW_SIZE;i++) {
            String path = mBitPaths.get(i);
            Point point = new Point(mContext.getResources().getInteger(R.integer.pic_item_size),
                    mContext.getResources().getInteger(R.integer.pic_item_size));
            Bitmap bitmap = picCache.cacheNativeImg(path,point,this);
            if(bitmap==null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.null_pic);
            }
            bitmaps.add(bitmap);
        }
        return bitmaps;
    }
*/
    public void addBitPath(String path) {
        /*
        PicCache picCache = PicCache.getInstance();
        Point point = new Point(mContext.getResources().getInteger(R.integer.pic_item_size),
                mContext.getResources().getInteger(R.integer.pic_item_size));
        Bitmap bitmap = picCache.cacheNativeImg(path,point,this);
        if(bitmap==null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.null_pic);
        }
        mBitMaps.add(bitmap);*/
        mBitPaths.add(path);
    }

    @Override
    public String toString() {
        return mTitle+":"+mBitPaths;
    }

    @Override
    public void onImageLoader(Bitmap bitmap, String path) {
        Log.d("debug","onImageLoader");
    }

}
