package com.example.vincenzo.guessandcheckers.core.suggesting;

import com.example.vincenzo.guessandcheckers.core.game_objects.ConcreteMove;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;

/**
 * Created by vincenzo on 07/12/2015.
 */
public class CheckersHint implements Hint {

    private Move suggestedMove;

    public CheckersHint(Move suggestedMove) {
        this.suggestedMove = suggestedMove;
    }

    @Override
    public Move getSuggestedMove() {
        return copyHint(suggestedMove);
    }

    private Move copyHint(Move move) {
        Move moveToRetrieve = null;
        if (suggestedMove != null)
            moveToRetrieve = new ConcreteMove(move.getMoveSteps(), move.getEatenOpponentPawns());
        return moveToRetrieve;
    }
}
