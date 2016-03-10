package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

/**
 * Created by vincenzo on 04/10/2015.
 */
public interface ChessboardFactory {

    public abstract boolean buildFrameCorners();

    public abstract boolean buildAllChessboardCorners();

    public abstract void buildCompleteChessboard();

    public abstract Chessboard getCompleteChessboard();

}
