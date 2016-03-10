package com.example.vincenzo.guessandcheckers.ui;

import android.util.Log;

/**
 * Created by vincenzo on 09/01/2016.
 */
public class Mediator {

    private static Mediator instance = null;
    private CameraActivity cameraActivity;

    private Mediator(){
    }

    public static Mediator getInstance(){
        if(instance == null)
            instance = new Mediator();
        return instance;
    }

    public void setCameraActivity(CameraActivity cameraActivity){
        this.cameraActivity = cameraActivity;
    }

    public void resetImageRecognitionParams(){
        this.cameraActivity.reset();
    }

    public void displayUserIndicators(){
        this.cameraActivity.displayUserIndicators();
    }

    public void hideUserIndicators(){
        Log.i("CICCIO", "hide!!!");
        if(this.cameraActivity != null)
        this.cameraActivity.hideUserIndicators();
    }
}
