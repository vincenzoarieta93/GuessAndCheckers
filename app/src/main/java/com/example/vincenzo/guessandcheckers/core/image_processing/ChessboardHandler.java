package com.example.vincenzo.guessandcheckers.core.image_processing;

/**
 * Created by vincenzo on 30/10/2015.
 */
public interface ChessboardHandler {

    void handleState(int STATE, ChessboardForHandler obj);

    void setNext(ChessboardHandler handler);
}
