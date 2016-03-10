package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.my_exceptions.ChessboardNotFoundException;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

/**
 * Created by vincenzo on 04/10/2015.
 */
public interface CornersChessboardBuilder {

    public abstract Chessboard getCompleteChessboard();

    public abstract boolean buildFrameCorners() throws ChessboardNotFoundException;

    public abstract boolean buildAllChessboardCorners() throws ChessboardNotFoundException;

    public abstract void buildCompleteChessboardMatrix() throws ChessboardNotFoundException;

}
