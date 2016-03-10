package com.example.vincenzo.guessandcheckers.core.ai.prompter;

import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;

import java.io.Serializable;

/**
 * Created by vincenzo on 01/02/2016.
 */
public class GameConfiguration implements Serializable {

    Chessboard chessboard;
    Move move;

    public GameConfiguration(Chessboard chessboard, Move move) {
        this.chessboard = chessboard;
        this.move = move;
    }
}
