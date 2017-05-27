package com.goertek.asp.smartpic.util;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by almo.liu on 2017/3/17.
 */

public class PicLoader extends CursorLoader{

    private static final String TAG = PicLoader.class.getSimpleName();

    public PicLoader(Context context) {
        super(context);
    }

    public PicLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG,"***onStartLoading()***");
        super.onStartLoading();

    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG,"***onStopLoading()***");
        super.onStopLoading();
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}
