package game_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincenzo on 25/11/2015.
 */
public class ConcreteMove implements Move {

	private List<Cell> steps = new ArrayList<>();
	private List<Pawn> eatenOpponentPawns = new ArrayList<>();

	public ConcreteMove() {
	}

	public ConcreteMove(List<Cell> steps, List<Pawn> eatenOpponentPawns) {
		this.steps = steps;
		this.eatenOpponentPawns = eatenOpponentPawns;
	}

	@Override
	public List<Cell> getMoveSteps() {
		return steps;
	}

	@Override
	public List<Pawn> getEatenOpponentPawns() {
		return eatenOpponentPawns;
	}
}