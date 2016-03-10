package com.example.vincenzo.guessandcheckers.core.game_objects;

import java.io.Serializable;

/**
 * Created by vincenzo on 30/10/2015.
 */
public abstract interface Chessboard<T> extends Serializable{

    void setCell(int x, int y, T item);

    T getCell(int x, int y);

    int getLength();

    T getMiddleItem(Cell srcCell, Cell dstCell);
}
