package com.example.vincenzo.guessandcheckers.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.vincenzo.guessandcheckers.R;

/**
 * Created by vincenzo on 03/02/2016.
 */
public class SplashActivity extends Activity {


    public static final int DELAY = 1000;
    private Integer duration = 7000;
    private ActivityLoader loader;
    private boolean mAllowCommit;
    private Integer remainingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_container);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new MainSplashFragment()).commit();
        mAllowCommit = true;
        this.loader = new ActivityLoader();
        this.loader.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mAllowCommit = false;
        remainingTime = duration;
        super.onSaveInstanceState(outState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //do-nothing
            return true;
        }
        return false;
    }

    @Override
    protected void onPostResume() {
        if (!mAllowCommit) {
            mAllowCommit = true;
            duration = remainingTime;
            new ActivityLoader().execute();
        }

        super.onPostResume();
    }

    private class ActivityLoader extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {

                while (duration > 0) {
                    if (!isCancelled())
                        duration -= 1000;
                    Thread.sleep(DELAY);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mAllowCommit) {
                replaceFragment();
                Intent i = new Intent(SplashActivity.this, CameraActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private void replaceFragment() {
            SplashFragment splashFragment = SplashFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, splashFragment, SplashFragment.SPLASH_FRAGMENT_LABEL).commit();
    }

}
