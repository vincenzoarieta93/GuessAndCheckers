package core;

import java.io.Serializable;

import game_objects.Move;

/**
 * Created by vincenzo on 07/12/2015.
 */
public abstract interface Suggest extends Serializable {

	Move getSuggestedMove();

	Move getPossibleMatchMoves();

}
