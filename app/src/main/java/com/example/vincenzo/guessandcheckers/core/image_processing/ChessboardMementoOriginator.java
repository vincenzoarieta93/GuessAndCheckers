package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;

/**
 * Created by vincenzo on 05/11/2015.
 */
public interface ChessboardMementoOriginator {

    FromMementoToCareTaker createMemento(int row, int col);

    Cell setMemento(FromMementoToCareTaker memento);
}
