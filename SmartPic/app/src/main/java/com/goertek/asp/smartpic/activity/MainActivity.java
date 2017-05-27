package com.goertek.asp.smartpic.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.adapter.MenuSetAdapter;
import com.goertek.asp.smartpic.adapter.PicGroupAdapter;
import com.goertek.asp.smartpic.fragment.PhotosListFragment;
import com.goertek.asp.smartpic.interfaces.PicItemActionListener;
import com.goertek.asp.smartpic.interfaces.PicItemPickedListener;
import com.goertek.asp.smartpic.util.SystemBarTintManager;
import com.goertek.asp.smartpic.util.ToolsUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MenuSetAdapter.IListAdapterListener
        ,PicItemPickedListener,PicGroupAdapter.PicItemSelectedListener  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    //private Toolbar mToolbar_selected;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mMenuActions;

    private PhotosListFragment mContentFrag;
    private MenuSetAdapter mContentAdapter;

    private MenuItem mShareMenuItem;
    private MenuItem mDelMenuItem;

    private ShareActionProvider mShareAction;

    private final int[] mMenuActionTitle  = new int[] {
            R.string.photos_title,
            R.string.settings_title,
            R.string.help_title
    };

    private final int[] mMenuActionImg = new int[] {
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_set_as,
            android.R.drawable.ic_menu_help
    };

    private boolean hasSelected = false;
    private boolean mBarVisible = true;

    private Map<PicItemActionListener,ArrayList<String>> mPicks = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar_menu);
      //  mToolbar_selected = (Toolbar)findViewById(R.id.toolbar_menu1);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.photos_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams
                        .FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams
                        .FLAG_TRANSLUCENT_STATUS);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d(TAG,"onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG,"onDrawerClosed");
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mContentAdapter = new MenuSetAdapter(this);

        mMenuActions = (RecyclerView)findViewById(R.id.menu_settings_recycler);
        mMenuActions.setAdapter(mContentAdapter);
        mContentAdapter.setList(mMenuActionTitle);

        mContentFrag = PhotosListFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.content_frame,mContentFrag).commit();

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
       // tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
       // tintManager.setNavigationBarTintEnabled(true);

        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        DrawerLayout.LayoutParams layout = (DrawerLayout.LayoutParams) findViewById(R.id.content_root)
                .getLayoutParams();
        layout.setMargins(0,config.getStatusBarHeight(),0,0);
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.drawer_slide_login_layout);
        linearLayout.setPaddingRelative(0,config.getStatusBarHeight(),0,0);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"***onPrepareOptionMenu***");
        if(hasSelected) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);

            if(mPicks == null) {
                getSupportActionBar().setTitle(R.string.selected_toolbar_title);
                mShareMenuItem.setVisible(false);
                mDelMenuItem.setVisible(false);
            }else {
                if(mPicks.size()<=0) {
                    getSupportActionBar().setTitle(R.string.selected_toolbar_title);
                    mShareMenuItem.setVisible(false);
                    mDelMenuItem.setVisible(false);
                }else {
                    getSupportActionBar().setTitle(String.valueOf(mPicks.size()));
                    mShareMenuItem.setVisible(true);
                    mDelMenuItem.setVisible(true);
                }
            }
        }else if(!mContentFrag.getZoomPicFlag()){
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }else {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setTitle(R.string.photos_title);
            mShareMenuItem.setVisible(false);
            mDelMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        mShareMenuItem = menu.findItem(R.id.main_menu_share);
        mDelMenuItem = menu.findItem(R.id.main_menu_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"item id: "+item.getItemId());
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.main_menu_delete:
                Log.d(TAG,"delete");
                deleteAction();
                break;
            case R.id.main_menu_share:
                Log.d(TAG,"***share***");
                shareAction();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActionItemSelected(int pos) {
        switch (pos) {
            case 0://photos
                Log.d(TAG,"photos");
                mDrawerLayout.closeDrawers();
                break;
            case 1://settings
                Log.d(TAG,"settings");
                mDrawerLayout.closeDrawers();
                break;
            case 2://help
                Log.d(TAG,"help");
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }

    @Override
    public int getMenuItemImg(int pos) {
        return mMenuActionImg[pos];
    }

    @Override
    public void onBackPressed() {
        if(mContentFrag.getCheckShowFlag()) {
            mContentFrag.setCheckShowFlag(false);
        }else if(!mContentFrag.getZoomPicFlag()){
            mContentFrag.onBackPressed();
        }else {
            super.onBackPressed();
        }
    }

    private void deleteAction() {
        Log.d(TAG,"***deleteAction called***");
        if(mPicks==null||mPicks.size()<=0)
            return;

        Set<PicItemActionListener> sets = mPicks.keySet();

        for(PicItemActionListener listener:sets) {
            listener.onActionDelete(mPicks.get(listener));
        }

        mContentFrag.setCheckShowFlag(false);
        mPicks.clear();
        hasSelected = false;
        invalidateOptionsMenu();
    }


    private void shareAction() {
        if(mPicks.size()<=0)
            return;

        Intent chooseIntent = new Intent();
        chooseIntent.setAction(Intent.ACTION_CHOOSER);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ToolsUtil.getUriArrayList(mPicks));
        shareIntent.setType("image/*");

        chooseIntent.putExtra(Intent.EXTRA_INTENT,shareIntent);
        startActivity(chooseIntent);
        mContentFrag.setCheckShowFlag(false);
        mPicks.clear();
        hasSelected = false;
        invalidateOptionsMenu();
    }

    @Override
    public void selectedListener(boolean on) {
        Log.d(TAG,"switch to selected ui***");
        hasSelected = on;
        invalidateOptionsMenu();
    }

    @Override
    public void onPicItemPicked(String path, PicItemActionListener listener) {

        Log.d(TAG,"onPicItemPicked");
        if(mPicks==null)
            mPicks = new HashMap<>();

        if(mPicks.containsKey(listener)) {
            ArrayList<String> paths = mPicks.get(listener);
            paths.add(path);
        }else {
            ArrayList<String> paths = new ArrayList<>();
            paths.add(path);
            mPicks.put(listener,paths);
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onPicItemUnPicked(String path, PicItemActionListener listener) {

        if(mPicks==null) {
            return;
        }
        if(mPicks.containsKey(listener)) {
            ArrayList<String> paths = mPicks.get(listener);
            if(paths.contains(path)) {
                paths.remove(path);
            }

            if(paths.size()<=0) {
                mPicks.remove(listener);
            }
        }
        invalidateOptionsMenu();

    }




    private static class MyHandler extends Handler {

        private WeakReference<MainActivity> mActivity;
        public MyHandler(MainActivity activity) {
            super();
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if(mainActivity==null)
                return;
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
