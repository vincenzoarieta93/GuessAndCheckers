package com.example.vincenzo.guessandcheckers.ui;

import android.app.Fragment;
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
public class MainSplashFragment extends Fragment {

    private View rootView;
    private LinearLayout splashTitleContainer;
    private LinearLayout splashImageContainer;
    private ImageView splashImage;
    private ImageView splashTitle;
    public static String LABEL = "MainSplashFragment";


    public static MainSplashFragment newInstance() {
        MainSplashFragment instance = new MainSplashFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.main_splash_fragment_layout, null);
        splashTitleContainer = (LinearLayout) rootView.findViewById(R.id.splash_title_container);
        splashImageContainer = (LinearLayout) rootView.findViewById(R.id.splash_image_container);
        splashTitle = (ImageView) rootView.findViewById(R.id.splash_title);
        splashImage = (ImageView) rootView.findViewById(R.id.splash_image);
        splashTitle.addOnLayoutChangeListener(new MyOnLayoutChangeListener(splashTitleContainer, true, 0.7f));
        splashImage.addOnLayoutChangeListener(new MyOnLayoutChangeListener(splashImageContainer, false, 0.9f));
        return this.rootView;
    }

    private class MyOnLayoutChangeListener implements View.OnLayoutChangeListener {

        private LinearLayout container;
        private float coveringPercentage;
        private boolean adaptAccordingContainerHeight;

        public MyOnLayoutChangeListener(LinearLayout container, boolean containerHeight, float coveringPercentage) {
            this.container = container;
            this.coveringPercentage = coveringPercentage;
            this.adaptAccordingContainerHeight = containerHeight;
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            Size adaptedSize;
            if (adaptAccordingContainerHeight)
                adaptedSize = SizeAdapter.adaptPreservingAspectRatio(v.getWidth(), v.getHeight(), Integer.MAX_VALUE, container.getHeight() * coveringPercentage);
            else
                adaptedSize = SizeAdapter.adaptPreservingAspectRatio(v.getWidth(), v.getHeight(), container.getWidth() * coveringPercentage, container.getHeight() * coveringPercentage);

            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = (int) (Math.round(adaptedSize.height));
            params.width = (int) (Math.round(adaptedSize.width));
            v.setLayoutParams(params);
            v.removeOnLayoutChangeListener(this);
        }
    }


}
