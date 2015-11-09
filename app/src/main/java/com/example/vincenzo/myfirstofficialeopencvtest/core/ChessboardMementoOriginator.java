package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Cell;

/**
 * Created by vincenzo on 05/11/2015.
 */
public interface ChessboardMementoOriginator {

    FromMementoToCareTaker createMemento(int row, int col);

    Cell setMemento(FromMementoToCareTaker memento);
}
