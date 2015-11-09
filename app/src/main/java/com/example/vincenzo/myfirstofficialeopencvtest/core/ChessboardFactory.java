package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.ui.CameraActivity;

/**
 * Created by vincenzo on 04/10/2015.
 */
public interface ChessboardFactory {

    public abstract boolean buildFrameCorners();

    public abstract boolean buildAllChessboardCorners();

    public abstract void buildCompleteChessboard();

    public abstract Chessboard getCompleteChessboard();

}
