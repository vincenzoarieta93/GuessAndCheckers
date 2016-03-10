package com.example.vincenzo.guessandcheckers.core.my_exceptions;

/**
 * Created by vincenzo on 16/10/2015.
 */
public class ChessboardNotFoundException extends RuntimeException{

    public ChessboardNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
