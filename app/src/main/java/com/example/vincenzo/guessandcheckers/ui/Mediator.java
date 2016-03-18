package com.example.vincenzo.guessandcheckers.ui;

import android.util.Log;

import com.example.vincenzo.guessandcheckers.core.image_processing.FromMementoToCareTaker;

/**
 * Created by vincenzo on 09/01/2016.
 */
public class Mediator {

    private static Mediator instance = null;
    private CameraActivity cameraActivity;
    private ResultDialogFragment resultDialogFragment;

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

    public void setResultDialogFragment(ResultDialogFragment resultDialogFragment){
        this.resultDialogFragment = resultDialogFragment;
    }

    public void setEnableUndoButton(){
        this.resultDialogFragment.setEnableUndoButton();
    }

    public void createMemento(int row, int col){
        this.resultDialogFragment.createMemento(row, col);
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
