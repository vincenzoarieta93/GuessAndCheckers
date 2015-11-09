package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.ui.CameraActivity;

/**
 * Created by vincenzo on 30/10/2015.
 */
public interface ChessboardHandler {

   void handleState(int STATE, ChessboardForHandler obj);
    void setNext(ChessboardHandler handler);
}
