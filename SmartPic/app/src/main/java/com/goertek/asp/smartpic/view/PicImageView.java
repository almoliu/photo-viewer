package com.goertek.asp.smartpic.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by almo.liu on 2017/3/24.
 */

public class PicImageView extends ImageView {

    private Context mContext;
    private boolean scaled = false;

    public PicImageView(Context context) {
        super(context);
        mContext  =context;
    }

    public PicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setScaled(boolean on) {
        scaled  = on;
        if(scaled)
            showScaledAnim();
        else
            showRestoreAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("ImageView","***onDraw***");
        /*
        if(scaled) {
            showRestoreAnim();
        }else {
            showScaledAnim();
        }*/
    }

    private void showScaledAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(this,"scale",1.0f,0.8f);
        anim.setDuration(3000);
        anim.start();
        /*
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim
                .pic_item_scaled_anim);
        anim.setFillAfter(true);
        startAnimation(anim);*/
    }

    private void  showRestoreAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(this,"scale",0.8f,1.0f);
        anim.setDuration(3000);
        anim.start();
        /*
        Animation anim = AnimationUtils.loadAnimation(mContext,R.anim
                .pic_item_scaled_anim_reverse);
        anim.setFillAfter(true);
        startAnimation(anim);
        */
    }


}
