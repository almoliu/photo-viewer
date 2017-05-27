package com.goertek.asp.smartpic.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.activity.PicShowActivity;
import com.goertek.asp.smartpic.interfaces.PicItemActionListener;
import com.goertek.asp.smartpic.interfaces.PicItemPickedListener;
import com.goertek.asp.smartpic.util.PicCache;
import com.goertek.asp.smartpic.util.ToolsUtil;
import com.goertek.asp.smartpic.view.PicGroupViewHolder;
import com.goertek.asp.smartpic.view.PicItem;
import com.goertek.asp.smartpic.view.PicItemViewHolder;
import com.goertek.asp.smartpic.view.ZoomTutorial;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by almo.liu on 2017/3/17.
 */

public class PicGroupAdapter extends RecyclerView.Adapter<PicGroupViewHolder> {

    private static final String TAG = PicGroupAdapter.class.getSimpleName();
    private static Context mContext;

    private PicItem[] mList = new PicItem[0];
    private static boolean mShowCheck = false;

    private static HashMap<String,Integer> mSelectedPaths = new HashMap<>();
    private ZoomTutorial mZoomTutorial;

    private Handler mWorkHandler;

    private boolean mIsZoom = true;
    private int mPagePos = 0;

    public PicGroupAdapter(Context context) {
        super();
        mContext = context;
        HandlerThread handlerThread = new HandlerThread("picItemAdapter_work");
        handlerThread.start();
        mWorkHandler = new WorkHandler(handlerThread.getLooper(),this);
    }

    public void setList(final List<PicItem> list) {
        Log.d(TAG,"setList***");
        if(list!=null)
            mList = list.toArray(mList);
        else
            mList = new PicItem[0];
        notifyDataSetChanged();
    }

    public boolean getShowCheck() { return mShowCheck;}

    public void setShowCheck(boolean value) {
        mShowCheck = value;
        notifyDataSetChanged();
        if(mContext instanceof PicItemSelectedListener) {
            ((PicItemSelectedListener) mContext).selectedListener(value);
        }
    }
    @Override
    public PicGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pic_item, parent, false);
        return new PicGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PicGroupViewHolder holder, final int position) {
        Log.d("debug","onBindViewHolder: "+position);

        if(position==mList.length-1) {
            holder.mTvSpace.setVisibility(View.VISIBLE);
        } else {
            holder.mTvSpace.setVisibility(View.GONE);
        }
        holder.mTextView.setText(mList[position].getTitle());
        GridImgAdapter baseAdapter = new GridImgAdapter(mContext,position);
        baseAdapter.setList(mList[position].getPartBitPaths());
        holder.mGridView.setAdapter(baseAdapter);

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent disIntent = new Intent(mContext, PicShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("bitmaps",mList[position].getBitPaths());
                bundle.putInt("position",0);
                disIntent.putExtras(bundle);
                mContext.startActivity(disIntent);
            }
        });

        holder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                Log.d(TAG,"on Item click***");
                int pos = 0;
                for(int i=0;i<mList.length;i++) {
                    if(i==position) {
                        pos = pos + position1;
                        break;
                    } else {
                        pos = pos + mList[i].getItemNum();
                    }
                }
                Log.d(TAG,"position1:"+position1+"\npostion:"+position+"\npos:"+pos);

                ImageView imageView = (ImageView) view.findViewById(R.id.pic_list_cell_tv);
                setViewPagerAndZoom(imageView,pos);

                /*
                Intent disIntent = new Intent(mContext, PicShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("bitmaps",mList[position].getBitPaths());
                bundle.putInt("position",position1);
                disIntent.putExtras(bundle);
                mContext.startActivity(disIntent);*/
            }
        });

        holder.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mShowCheck = true;
                PicGroupAdapter.this.notifyDataSetChanged();
                if(mContext instanceof PicItemSelectedListener) {
                    ((PicItemSelectedListener) mContext).selectedListener(true);
                }
                return true;
            }
        });

        /**
         * in SimpleAdapter the element should be resource id, not the explicitly Bitmap or
         * ImageView, the solution is here
         */
        /*
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView&&data instanceof Bitmap) {
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                }else {
                    return false;
                }
            }
        });
        holder.mGridView.setAdapter(simpleAdapter);*/
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    public void onBackPress() {
        if(mZoomTutorial!=null)
            mZoomTutorial.closeZoomAnim(mPagePos);
    }

    public boolean getZoomFlag() {
        return mIsZoom;
    }

    public void setViewPagerAndZoom(View v ,int pos) {
        //得到要放大展示的视图界面
        mPagePos = pos;
        ViewPager expandedView = (ViewPager)((Activity)mContext).findViewById(R.id.detail_view);
        expandedView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagePos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //最外层的容器，用来计算
        View containerView = (FrameLayout)((Activity)mContext).findViewById(R.id.container);

        //实现放大缩小类，传入当前的容器和要放大展示的对象
        mZoomTutorial = new ZoomTutorial(containerView, expandedView,mList);

        ViewPagerAdapter adapter = new ViewPagerAdapter(mContext,mList,mZoomTutorial);
        expandedView.setAdapter(adapter);
        expandedView.setCurrentItem(pos);

        // 通过传入Id来从小图片扩展到大图，开始执行动画
        mZoomTutorial.zoomImageFromThumb(v);
        mZoomTutorial.setOnZoomListener(new ZoomTutorial.OnZoomListener() {

            @Override
            public void onThumbed() {
                // TODO 自动生成的方法存根
                System.out.println("现在是-------------------> 小图状态");
                mIsZoom = true;
            }

            @Override
            public void onExpanded() {
                // TODO 自动生成的方法存根
                System.out.println("现在是-------------------> 大图状态");
                mIsZoom = false;
            }
        });
    }



    private class GridImgAdapter extends BaseAdapter implements PicItemActionListener {

        private ArrayList<String> iList;
        private Context iContext;
        private final int iPosition;

        public GridImgAdapter(Context context,final int pos) {
            super();
            iContext = context;
            iPosition = pos;
        }

        public void setList(ArrayList<String> list) {
            iList = list;
        }
        @Override
        public int getCount() {
            return iList.size();
        }
        @Override
        public Object getItem(int position) {
            return iList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            PicItemViewHolder itemViewHolder = null;
            if(convertView==null) {
                itemViewHolder = new PicItemViewHolder();
                convertView = LayoutInflater.from(iContext).inflate(R.layout.layout_img_cell, null);
                itemViewHolder.mCheck = (CheckBox)convertView.findViewById(R.id.pic_item_checkbox);
                itemViewHolder.mImg = (ImageView)convertView.findViewById(R.id.pic_list_cell_tv);
                convertView.setTag(itemViewHolder);
            }else {
                itemViewHolder = (PicItemViewHolder)convertView.getTag();
            }

            final PicItemViewHolder finalItemViewHolder = itemViewHolder;


            Bitmap bitmap = PicCache.getInstance().getThumbBitmap(ToolsUtil.getWindowPoint(mContext),
                    iList.get(position),new PicCache.NativeImageCallBack() {
                        @Override
                        public void onImageLoader(Bitmap bitmap, String path) {
                            Bitmap thumb = Bitmap.createScaledBitmap(
                                    bitmap,mContext.getResources().getInteger(R.integer
                                            .pic_item_size),
                                    mContext.getResources().getInteger(R.integer
                                            .pic_item_size
                            ),false);
                            finalItemViewHolder.mImg.setImageBitmap(thumb);
                        }
                    });

            if(bitmap==null) {
                itemViewHolder.mImg.setImageResource(R.mipmap.friends_sends_pictures_no);
            } else {
                Bitmap thumb = Bitmap.createScaledBitmap(
                        bitmap,mContext.getResources().getInteger(R.integer
                                .pic_item_size),
                        mContext.getResources().getInteger(R.integer
                                .pic_item_size
                        ),false);
                itemViewHolder.mImg.setImageBitmap(thumb);
            }

            itemViewHolder.mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                       if(mContext instanceof PicItemPickedListener) {
                           ((PicItemPickedListener)mContext).onPicItemPicked(iList.get(position),
                                   GridImgAdapter.this);
                       }
                        showScaledAnim(finalItemViewHolder.mImg);
                    }else {
                        if(mContext instanceof PicItemPickedListener) {
                            ((PicItemPickedListener)mContext).onPicItemUnPicked(iList.get(position),
                                    GridImgAdapter.this);
                        }
                        showRestoreAnim(finalItemViewHolder.mImg);
                    }
                }
            });


            if(mShowCheck) {
                itemViewHolder.mCheck.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.mCheck.setVisibility(View.GONE);
                mSelectedPaths.clear();
            }
            return convertView;
        }

        private void showScaledAnim(View view) {
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f);
            PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f);
            ObjectAnimator.ofPropertyValuesHolder(view,pvhY,pvhZ).setDuration(500).start();
        /*
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim
                .pic_item_scaled_anim);
        anim.setFillAfter(true);
        startAnimation(anim);*/
        }

        private void  showRestoreAnim(View view) {
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.0f);
            PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.0f);
            ObjectAnimator.ofPropertyValuesHolder(view,pvhY,pvhZ).setDuration(500).start();
        /*
        Animation anim = AnimationUtils.loadAnimation(mContext,R.anim
                .pic_item_scaled_anim_reverse);
        anim.setFillAfter(true);
        startAnimation(anim);
        */
        }



        @Override
        public void onActionDelete(ArrayList<String> paths) {
            Log.d(TAG,"***onActionDelete  called***");
            for(String path:paths) {
                if(iList.contains(path)) {
                    iList.remove(path);
                    mList[iPosition].getBitPaths().remove(path);
                }
            }

            notifyDataSetChanged();
            Message msg = new Message();
            msg.obj = paths;
            msg.what = 0x23;

            mWorkHandler.sendMessage(msg);
        }
    }

    public interface PicItemSelectedListener {
        void selectedListener(boolean on);
    }

    private static class WorkHandler extends Handler {

        WeakReference<PicGroupAdapter> weakReference;

        public WorkHandler(PicGroupAdapter parent) {
            super();
            weakReference = new WeakReference<PicGroupAdapter>(parent);
        }

        public WorkHandler(Callback callback,PicGroupAdapter parent) {
            super(callback);
            weakReference = new WeakReference<PicGroupAdapter>(parent);
        }

        public WorkHandler(Looper looper,PicGroupAdapter parent) {
            super(looper);
            weakReference = new WeakReference<PicGroupAdapter>(parent);
        }

        public WorkHandler(Looper looper, Callback callback,PicGroupAdapter parent) {
            super(looper, callback);
            weakReference = new WeakReference<PicGroupAdapter>(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            PicGroupAdapter picGroupAdapter = weakReference.get();
            if(picGroupAdapter ==null)
                return;
            switch (msg.what) {
                case 0x23:
                    ArrayList<String> paths = (ArrayList<String>) msg.obj;
                    for(String path:paths) {
                        weakReference.get().mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media
                                .DATA + "=?", new String[]{path});
                        PicCache.getInstance().deleteFromChache(path);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}
