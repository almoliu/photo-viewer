package com.goertek.asp.smartpic.util;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.goertek.asp.smartpic.interfaces.PicItemActionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by almo.liu on 2017/3/31.
 */

public class ToolsUtil {

    public static ArrayList<Uri> getUriArrayList(Map<PicItemActionListener,ArrayList<String>> maps) {
        if(maps.size()<=0)
            return null;
        ArrayList<Uri> uriList = new ArrayList<>();
        Set<PicItemActionListener> sets = maps.keySet();
        for(PicItemActionListener listener:sets) {
            ArrayList<String> paths = maps.get(listener);
            if(paths.size()<=0)
                break;
            for(String path:paths) {
                Uri uriToImage = Uri.fromFile(new File(path));
                uriList.add(uriToImage);
            }
        }
        return uriList;
    }
    public static Point getWindowPoint(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return  new Point((int)displayMetrics.xdpi,(int)displayMetrics.ydpi);
    }

}
