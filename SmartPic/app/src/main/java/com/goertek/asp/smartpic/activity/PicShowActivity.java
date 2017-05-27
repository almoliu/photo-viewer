package com.goertek.asp.smartpic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.goertek.asp.smartpic.R;
import com.goertek.asp.smartpic.util.PicCache;
import com.goertek.asp.smartpic.util.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;

public class PicShowActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = PicShowActivity.class.getSimpleName();

    private ImageSwitcher mShowImg;

    private ArrayList<String> mShowBitPaths;
    private int mShowCurPos = 0;
    private boolean mBarVisible = true;

    private View mControlView;
    private ImageButton mShareBtn;
    private ImageButton mEditBtn;
    private ImageButton mInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_show);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picshow_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.slide_show_menu_item:
                break;
            case R.id.use_as_menu_item:
                break;
            case R.id.print_menu_item:
                break;
            case R.id.del_menu_item:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Intent parent = getIntent();
        if(parent!=null) {
            Bundle bundle = parent.getExtras();
            if(bundle!=null) {
                mShowBitPaths = bundle.getStringArrayList("bitmaps");
                mShowCurPos = bundle.getInt("position");
            }
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        mShowImg = (ImageSwitcher) findViewById(R.id.pic_content);
        mShowImg.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(PicShowActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        PicCache picCache = PicCache.getInstance();
        Bitmap bitmap = null;

        /*prevent OOM*/
        try {
            bitmap = picCache.getRawBitmap(mShowBitPaths.get(mShowCurPos));
        }catch (OutOfMemoryError error) {
            bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap
                    .friends_sends_pictures_no);
        }

        mShowImg.setImageURI(Uri.fromFile(new File(mShowBitPaths.get(mShowCurPos))));
        mShowImg.setOnClickListener(this);

        mControlView = findViewById(R.id.fullscreen_content_controls);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        RelativeLayout.LayoutParams layoutParams = ( RelativeLayout.LayoutParams)findViewById(R
                .id.toolbar_menu).getLayoutParams();
        layoutParams.setMargins(0,config.getStatusBarHeight(),0,0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager
                .LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams
                        .FLAG_TRANSLUCENT_STATUS);
        /*getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager
                .LayoutParams.MATCH_PARENT);*/
        mShareBtn = (ImageButton)findViewById(R.id.dummy_button);
        mEditBtn = (ImageButton)findViewById(R.id.dummy_button1);
        mInfoBtn = (ImageButton)findViewById(R.id.dummy_button2);

        mShareBtn.setOnClickListener(this);
        mEditBtn.setOnClickListener(this);
        mInfoBtn.setOnClickListener(this);
    }

    private void hideBar() {
        mShowImg.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.hide();
        mBarVisible = false;
        mControlView.setVisibility(View.GONE);
    }

    private void showBar() {
        mShowImg.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.show();
        mBarVisible = true;
        mControlView.setVisibility(View.VISIBLE);
    }

    private void toggleBar() {
        if(mBarVisible) {
            hideBar();
        }else {
            showBar();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pic_content:
                toggleBar();
                break;
            case R.id.dummy_button://share
                Log.d(TAG,"share button");
                shareAction();
                break;
            case R.id.dummy_button1://edit
                break;
            case R.id.dummy_button2://information
                break;
            default:
                break;
        }
    }

    private void shareAction() {
        Log.d(TAG,"path is:"+mShowBitPaths.get(mShowCurPos));
        Intent chooseIntent = new Intent(Intent.ACTION_CHOOSER);
        Intent sendIntent =  new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mShowBitPaths.get(mShowCurPos))));
        sendIntent.setType("image/*");
        chooseIntent.putExtra(Intent.EXTRA_INTENT,sendIntent);
        startActivity(chooseIntent);
    }
}
