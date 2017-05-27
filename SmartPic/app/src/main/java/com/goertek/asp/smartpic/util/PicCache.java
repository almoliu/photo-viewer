package com.goertek.asp.smartpic.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by almo.liu on 2017/3/20.
 */

public class PicCache {

    public static PicCache getInstance() {
        return mPicCache;
    }

    private static PicCache mPicCache = new PicCache();
    private LruCache<String,Bitmap> mMemoryCache;
    private ExecutorService mImageThreadPool  = Executors.newFixedThreadPool(3);

    private PicCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public Bitmap getThumbBitmap(Point point, String path, NativeImageCallBack callBack) {
        return mPicCache.cacheNativeImg(path,point,callBack);
    }

    public Bitmap getRawBitmap(String  path) {
        return decodeThumbBitmapForFile(path,0,0);
    }

    public Bitmap cacheNativeImg(final String path, NativeImageCallBack mCallback) {
       return this.cacheNativeImg(path,null,mCallback);
    }

    public Bitmap cacheNativeImg(final String path, final Point mPoint, final NativeImageCallBack
            mCallBack) {

        Bitmap bitmap =  getBitmapFromMemCache(path);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap)msg.obj, path);
            }
        };

        if(bitmap == null){
            mImageThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap1 = decodeThumbBitmapForFile(path, mPoint == null ? 0: mPoint.x,
                            mPoint == null ? 0: mPoint.y);
                    /*
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1,mPoint==null?0:mPoint.x,
                            mPoint==null?0:mPoint.y,false);*/
                    addBitmapToMemoryCache(path, bitmap1);
                    Message msg = handler.obtainMessage();
                    msg.obj = bitmap1;
                    handler.sendMessage(msg);
                }
            });
        }
       // if(!bitmap.isRecycled())
       //     bitmap.recycle();
        return bitmap;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        } else{
            mMemoryCache.remove(key);
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);
        return BitmapFactory.decodeFile(path, options);
    }

    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){

        int inSampleSize = 1;
        if(viewWidth == 0 || viewWidth == 0){
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        if(bitmapWidth > viewWidth || bitmapHeight > viewHeight){
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewHeight);
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    public void deleteFromChache(String path) {
        if(path==null)
            return;
        Bitmap bitmap = mMemoryCache.get(path);
        if(bitmap!=null) {
            mMemoryCache.remove(path);
            if(!bitmap.isRecycled())
                bitmap.recycle();
        }
    }

    public void resetChache() {
       mPicCache = new PicCache();
    }

    public interface NativeImageCallBack {
        public void onImageLoader(Bitmap bitmap, String path);
    }
}
