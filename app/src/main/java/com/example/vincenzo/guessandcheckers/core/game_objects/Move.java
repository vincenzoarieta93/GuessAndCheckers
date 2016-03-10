package com.example.vincenzo.guessandcheckers.core.game_objects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vincenzo on 18/12/2015.
 */
public interface Move extends Serializable{

    List<Cell> getMoveSteps();
    List<Pawn> getEatenOpponentPawns();
}
