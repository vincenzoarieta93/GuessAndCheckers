package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

import com.example.vincenzo.myfirstofficialeopencvtest.core.FromMementoToCareTaker;
import com.example.vincenzo.myfirstofficialeopencvtest.core.Memento;

import java.io.Serializable;

/**
 * Created by vincenzo on 30/10/2015.
 */
public abstract interface Chessboard<T> extends Serializable{

    void setCell(int x, int y, T item);

    T getCell(int x, int y);

    int getLength();
}
