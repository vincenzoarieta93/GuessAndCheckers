package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

/**
 * Created by vincenzo on 30/10/2015.
 */
public interface ChessboardHandler {

    void handleState(int STATE, Chessboard obj);

    void setNext(ChessboardHandler handler);
}
