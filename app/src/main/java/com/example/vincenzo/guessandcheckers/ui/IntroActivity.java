package com.example.vincenzo.guessandcheckers.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.example.vincenzo.guessandcheckers.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by vincenzo on 10/01/2016.
 */
public class IntroActivity extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("Start/Stop Capture", getResources().getString(R.string.slide1Descr), R.drawable.tapping_screen_samsungs3, Color.parseColor("#FFFFFF"),Color.parseColor("#222222"),Color.parseColor("#222222")));
        addSlide(AppIntroFragment.newInstance("Change cell status", getResources().getString(R.string.slide2Descr), R.drawable.chessboard_tutorial, Color.parseColor("#FFFFFF"),Color.parseColor("#222222"),Color.parseColor("#222222")));
        addSlide(AppIntroFragment.newInstance("Take Care", getResources().getString(R.string.slide3Descr), R.drawable.chessboard_with_indicators, Color.parseColor("#FFFFFF"),Color.parseColor("#222222"),Color.parseColor("#222222")));
        addSlide(AppIntroFragment.newInstance("Some Recommendations", getResources().getString(R.string.slide4Descr), R.drawable.note_border, Color.parseColor("#FFFFFF"),Color.parseColor("#222222"),Color.parseColor("#222222")));
        addSlide(AppIntroFragment.newInstance("Some Recommendations", getResources().getString(R.string.slide5Descr), R.drawable.shading, Color.parseColor("#FFFFFF"),Color.parseColor("#222222"),Color.parseColor("#222222")));
        // Override bar/separator color
        setBarColor(Color.parseColor("#31C3FF"));//indaco(background)
        setSeparatorColor(Color.parseColor("#31C3FF"));

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Edit the color of the nav bar on Lollipop+ devices
        setNavBarColor(Color.parseColor("#2196F3"));//(boarder)

        // Hide Skip/Done button
        showSkipButton(false);
        showDoneButton(true);

        // Animations -- use only one of the below. Using both could cause errors.
//        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
//        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
//        setCustomTransformer(yourCustomTransformer);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }
}
