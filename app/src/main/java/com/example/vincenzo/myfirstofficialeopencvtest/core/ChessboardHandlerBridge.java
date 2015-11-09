package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.ui.CameraActivity;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class ChessboardHandlerBridge implements ChessboardHandler {

    private static ChessboardHandlerBridge instance = null;
    private ChessboardHandler next;

    private ChessboardHandlerBridge(){
    }

    public static ChessboardHandlerBridge getInstance(){
        if(instance == null)
            instance = new ChessboardHandlerBridge();
        return instance;
    }


    @Override
    public void handleState(int STATE, ChessboardForHandler obj) {
        this.next.handleState(STATE, obj);
    }

    @Override
    public void setNext(ChessboardHandler handler) {
        this.next = handler;
    }
}
