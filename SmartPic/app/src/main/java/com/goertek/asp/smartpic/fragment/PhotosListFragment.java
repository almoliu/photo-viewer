package com.goertek.asp.smartpic.fragment;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.adapter.PicGroupAdapter;
import com.goertek.asp.smartpic.util.PicLoader;
import com.goertek.asp.smartpic.view.PicItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG  = PhotosListFragment.class.getSimpleName();

    private View mProcessBar;
    private RecyclerView mRecyclerView;
    private PicGroupAdapter mPicGroupAdapter;

    private Handler mHandler = new Handler();
    private Handler mWorkHandler = null;

    private static final String[] PROVIDER_IMAGE_SELECTION = new String[] {
            MediaStore.Images.Media.DATA
    };

    //private List<PicItem> mPicItems = new ArrayList<>();
    public static PhotosListFragment newInstance() {
        PhotosListFragment fragment = new PhotosListFragment();
        return fragment;
    }

    public PhotosListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread handlerThread = new HandlerThread("work_thread");
        handlerThread.start();
        mWorkHandler = new WorkHandler(handlerThread.getLooper(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos_list, container, false);
        this.init(view);
        getLoaderManager().initLoader(0,null,PhotosListFragment.this);
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
    }

    private void init(View view) {
        //mSpace = (TextView)view.findViewById(R.id.recycler_space);
        mProcessBar = view.findViewById(R.id.l_progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_pic_list);
        mRecyclerView.setHasFixedSize(true);
        mPicGroupAdapter = new PicGroupAdapter(getActivity());

        mRecyclerView.setAdapter(mPicGroupAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:

                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:

                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setVisibility(View.GONE);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mRecyclerView.setItemViewCacheSize(cacheSize);
    }

    public boolean getCheckShowFlag() {
        return mPicGroupAdapter.getShowCheck();
    }

    public void setCheckShowFlag(boolean value) {
        mPicGroupAdapter.setShowCheck(value);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"***onCreateLoader()***");
        loading(true);
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return new PicLoader(getActivity(), baseUri, PROVIDER_IMAGE_SELECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"onLoadFinished");
        try {
            mPicGroupAdapter.setList(initPicList(data));
            loading(false);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG,"***onLoaderReset***");
        mPicGroupAdapter.setList(null);
        loading(false);
    }

    public void onBackPressed() {
        mPicGroupAdapter.onBackPress();
    }

    public boolean getZoomPicFlag() {
        return mPicGroupAdapter.getZoomFlag();
    }

    private List<PicItem> initPicList(Cursor cursor) throws Exception{
        List<PicItem> picItems = new ArrayList<>();
        while(cursor.moveToNext()) {
            boolean has = false;
            int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            final String path = cursor.getString(index);
            Log.d(TAG,"path is:\n"+path);
            String[] parts = path.split("/");
            String title = parts[parts.length-2];
            PicItem picItem = PicItem.newInstance(getActivity(),title);
            picItem.addBitPath(path);
            for(PicItem item :picItems) {
                if(item.getTitle().equals(picItem.getTitle())) {
                    item.addBitPath(path);
                    has = true;
                }
            }
            if(!has)
                picItems.add(picItem);
        }
        return picItems;
    }

    private void loading(boolean on) {
        int state = on? View.VISIBLE:View.GONE;
        mProcessBar.setVisibility(state);
        int state1 = on?View.GONE:View.VISIBLE;
        mRecyclerView.setVisibility(state1);
    }

    public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
            return true;
        }else {
            return false;
        }
    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0,null,this);
    }

    public void notifyDataChanged(int value) {
        mPicGroupAdapter.notifyItemChanged(value);
    }

    private static class WorkHandler extends Handler {

        private WeakReference<PhotosListFragment> mParent;

        public WorkHandler(PhotosListFragment fragment) {
            super();
            mParent = new WeakReference<PhotosListFragment>(fragment);
        }

        public WorkHandler(Looper looper,PhotosListFragment fragment) {
            super(looper);
            mParent = new WeakReference<PhotosListFragment>(fragment);
        }

        public WorkHandler(Callback callback,PhotosListFragment fragment) {
            super(callback);
            mParent = new WeakReference<PhotosListFragment>(fragment);
        }

        public WorkHandler(Looper looper, Callback callback,PhotosListFragment fragment) {
            super(looper, callback);
            mParent = new WeakReference<PhotosListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                default:
                    super.handleMessage(msg);
                 break;
            }
        }
    }

}
