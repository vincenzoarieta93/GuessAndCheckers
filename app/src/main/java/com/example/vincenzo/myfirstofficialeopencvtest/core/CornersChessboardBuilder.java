package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.exceptions.ChessboardNotFoundException;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;

/**
 * Created by vincenzo on 04/10/2015.
 */
public interface CornersChessboardBuilder {

    Chessboard getCompleteChessboard();

    public abstract boolean buildFrameCorners() throws ChessboardNotFoundException;

    public abstract boolean buildAllChessboardCorners() throws ChessboardNotFoundException;

    public abstract void buildCompleteChessboardMatrix() throws ChessboardNotFoundException;

}
