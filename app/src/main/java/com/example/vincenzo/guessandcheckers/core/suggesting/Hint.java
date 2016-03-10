package com.example.vincenzo.guessandcheckers.core.suggesting;

import com.example.vincenzo.guessandcheckers.core.game_objects.Move;

import java.io.Serializable;

/**
 * Created by vincenzo on 07/12/2015.
 */
public abstract interface Hint extends Serializable {

    Move getSuggestedMove();

}
