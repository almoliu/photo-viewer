package com.goertek.asp.smartpic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.util.PicCache;
import com.goertek.asp.smartpic.util.ToolsUtil;
import com.goertek.asp.smartpic.view.PicItem;
import com.goertek.asp.smartpic.view.ZoomTutorial;

import java.util.ArrayList;

/**
 * Created by almo.liu on 2017/4/10.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private ArrayList<String> mPaths;
    private Context mContext;
    private ZoomTutorial mZoomTutorial;

    public ViewPagerAdapter(Context context , PicItem[] picItems, ZoomTutorial zoomTutorial) {

        this.mContext = context;
        this.mZoomTutorial = zoomTutorial;
        mPaths  = new ArrayList<>();

        for(PicItem picItem:picItems) {
            mPaths.addAll(picItem.getBitPaths());
        }

    }



    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {

        Log.d(TAG,"***instantiateItem***"+position);

        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        PicCache picCache = PicCache.getInstance();
        final Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap
                .friends_sends_pictures_no);
        imageView.setImageBitmap(bitmap1);

        Bitmap bitmap = picCache.getThumbBitmap(ToolsUtil.getWindowPoint(mContext),mPaths.get(position),
                new PicCache
                .NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                if(!bitmap1.isRecycled())
                    bitmap1.recycle();
                imageView.setImageBitmap(bitmap);
            }
        });

        if(bitmap!=null) {
            imageView.setImageBitmap(bitmap);
            if(!bitmap1.isRecycled())
                bitmap1.recycle();
        }

        container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Log.d(TAG,"clear anim:"+position);
               // mZoomTutorial.closeZoomAnim(position);
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
/*
    private void hideBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.hide();
        mBarVisible = false;
        //  mControlView.setVisibility(View.GONE);
    }

    private void showBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.show();
        mBarVisible = true;
        // mControlView.setVisibility(View.VISIBLE);
    }

    private void toggleBar() {
        if(mBarVisible) {
            hideBar();
        }else {
            showBar();
        }
    }
*/
}
