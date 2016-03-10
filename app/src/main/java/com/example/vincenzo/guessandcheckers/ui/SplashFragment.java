package com.example.vincenzo.guessandcheckers.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.support_libraries.SizeAdapter;

import org.opencv.core.Size;

/**
 * Created by vincenzo on 04/02/2016.
 */
public class SplashFragment extends Fragment {

    private View rootView;
    private LinearLayout creditsContainer;
    private ImageView creditsImage;
    public static String SPLASH_FRAGMENT_LABEL = "SplashFragment";

    public static SplashFragment newInstance() {
        SplashFragment instance = new SplashFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.splash_fragment_layout, container, false);
        this.creditsContainer = (LinearLayout) this.rootView.findViewById(R.id.splash_credits_container);
        this.creditsImage = (ImageView) this.rootView.findViewById(R.id.splash_credits_image);
        this.creditsContainer.addOnLayoutChangeListener(new MyOnLayoutChangeListener());
        return this.rootView;
    }

    private class MyOnLayoutChangeListener implements View.OnLayoutChangeListener {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            configureCreditsImage(new Size(v.getWidth(), v.getHeight()));
            v.removeOnLayoutChangeListener(this);
        }
    }

    private void configureCreditsImage(Size wishedSize) {
        Bitmap creditsBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.big_credit_2);

        Size adaptedSize = SizeAdapter.adaptPreservingAspectRatio(creditsBitmap.getWidth(), creditsBitmap.getHeight(), (int) Math.round(wishedSize.width * 0.9f), (int) wishedSize.height * 0.9f);
        ViewGroup.LayoutParams logoImageParams = creditsImage.getLayoutParams();
        logoImageParams.width = (int) Math.round(adaptedSize.width);
        logoImageParams.height = (int) Math.round(adaptedSize.height);
        creditsImage.setLayoutParams(logoImageParams);
    }
}
