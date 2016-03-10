package com.example.vincenzo.guessandcheckers.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.suggesting.Hint;
import com.example.vincenzo.guessandcheckers.core.support_libraries.SizeAdapter;

import org.opencv.core.Size;

import java.util.List;
import java.util.Vector;


public class SuggestActivity extends ActionBarActivity implements
        OnTabChangeListener, OnPageChangeListener {

    public static final String SUGGEST_KEY = "SUGGEST";
    public static final String CHESSBOARD_KEY = "chessboard";
    private TabHost tabHost;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myViewPagerAdapter;
    private Hint hint;
    private Chessboard chessboard;

    class DummyContent implements TabContentFactory {
        private final Context mContext;

        public DummyContent(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumHeight(0);
            v.setMinimumWidth(0);
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.hint = (Hint) getIntent().getExtras().getSerializable(SUGGEST_KEY);
        this.chessboard = (Chessboard) getIntent().getExtras().getSerializable(CHESSBOARD_KEY);

        setContentView(R.layout.tabs_viewpager_layout);

        this.initializeTabHost();
        this.initializeViewPager();

        setTabsStyle();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.logo_container);
        relativeLayout.addOnLayoutChangeListener(new MyRelativeLayoutChangeListener());
    }

    private class MyRelativeLayoutChangeListener implements View.OnLayoutChangeListener {

        private int containerHeight;

        public MyRelativeLayoutChangeListener() {
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            containerHeight = v.getHeight();
            adjustLogoSize(containerHeight);
            configureHomeButton(containerHeight);
            v.removeOnLayoutChangeListener(this);
        }


    }

    private void configureHomeButton(int wishedHeight) {
        Bitmap homeButtonImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.scaled_home_button);

        Size adaptedSize = SizeAdapter.adaptPreservingAspectRatio(homeButtonImage.getWidth(), homeButtonImage.getHeight(), Integer.MAX_VALUE, wishedHeight);
        final ImageButton homeButton = (ImageButton) findViewById(R.id.home_button);
        ViewGroup.LayoutParams logoImageParams = homeButton.getLayoutParams();
        logoImageParams.width = (int) Math.round(adaptedSize.width);
        logoImageParams.height = (int) Math.round(adaptedSize.height);
        homeButton.setLayoutParams(logoImageParams);

        if (homeButton != null) {
            homeButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            homeButton.setBackgroundResource(R.drawable.scaled_active_home_button);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            homeButton.setBackgroundResource(R.drawable.scaled_home_button);
                            SuggestActivity.this.finish();
                            return true; // if you want to handle the touch event
                    }
                    return false;
                }
            });
        }

    }

    private void adjustLogoSize(int wishedHeight) {
        Bitmap appLogo = BitmapFactory.decodeResource(this.getResources(), R.drawable.app_title);

        Size adaptedSize = SizeAdapter.adaptPreservingAspectRatio(appLogo.getWidth(), appLogo.getHeight(), Integer.MAX_VALUE, wishedHeight);
        ImageView logoImage = (ImageView) findViewById(R.id.app_logo);
        ViewGroup.LayoutParams logoImageParams = logoImage.getLayoutParams();
        logoImageParams.width = (int) Math.round(adaptedSize.width);
        logoImageParams.height = (int) Math.round(adaptedSize.height);
        logoImage.setLayoutParams(logoImageParams);
    }


    private void setTabsStyle() {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/riffic.otf");
            tv.setTypeface(tf);
            tv.setTextColor(this.getResources().getColorStateList(R.color.text_tab_indicator));
        }
    }

    private void initializeViewPager() {
        List<Fragment> fragments = new Vector<>();

        fragments.add(Tab1Activity.newInstance(hint, chessboard));
        fragments.add(Tab2Activity.newInstance(hint, chessboard));

        this.myViewPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(), fragments);
        this.viewPager = (ViewPager) super.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(this.myViewPagerAdapter);
        this.viewPager.setOnPageChangeListener(this);

        onRestart();

    }

    private void initializeTabHost() {

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec1;
        TabHost.TabSpec tabSpec2;

        String tab1Name = "Graphic Hint";
        String tab2Name = "Textual Hint";

        tabSpec1 = tabHost.newTabSpec(tab1Name);
        configureTab(tabSpec1, tab1Name);

        tabSpec2 = tabHost.newTabSpec(tab2Name);
        configureTab(tabSpec2, tab2Name);

        tabHost.setOnTabChangedListener(this);
    }

    private void configureTab(TabHost.TabSpec tabSpec, String tabName) {
        tabSpec.setIndicator(tabName);
        tabSpec.setContent(new DummyContent(this));
        tabHost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(String tabId) {
        int pos = this.tabHost.getCurrentTab();
        this.viewPager.setCurrentItem(pos);

        HorizontalScrollView hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        View tabView = tabHost.getCurrentTabView();
        int scrollPos = tabView.getLeft()
                - (hScrollView.getWidth() - tabView.getWidth()) / 2;
        hScrollView.smoothScrollTo(scrollPos, 0);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        this.tabHost.setCurrentTab(position);
    }

}
