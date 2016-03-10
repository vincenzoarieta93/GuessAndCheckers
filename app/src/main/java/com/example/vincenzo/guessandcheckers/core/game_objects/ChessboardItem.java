package com.example.vincenzo.guessandcheckers.core.game_objects;

import java.io.Serializable;

/**
 * Created by vincenzo on 30/10/2015.
 */
public interface ChessboardItem extends Cloneable, Serializable {

    public static String WHITE_PAWN = "WHITE";
    public static String BLACK_PAWN = "BLACK";
    public static String EMPTY_TILE = "NONE";

    void setPosition(int x, int y);
    Cell getPosition();
}
