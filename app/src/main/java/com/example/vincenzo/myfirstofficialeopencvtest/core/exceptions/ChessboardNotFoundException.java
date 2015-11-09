package com.example.vincenzo.myfirstofficialeopencvtest.core.exceptions;

/**
 * Created by vincenzo on 16/10/2015.
 */
public class ChessboardNotFoundException extends RuntimeException{

    public ChessboardNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
