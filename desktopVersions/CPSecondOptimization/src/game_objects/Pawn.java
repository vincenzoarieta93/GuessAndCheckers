package game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public abstract class Pawn implements ChessboardItem {

	protected int x;
	protected int y;

	protected Pawn(int x, int y) {
		this.x = x;
		this.y = y;
	}

	protected Pawn() {
	}

	public abstract String getCOLOR();

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Cell getPosition() {
		return new Cell(x, y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Pawn))
			return false;

		Pawn pawn = (Pawn) o;

		if (x != pawn.x)
			return false;
		return y == pawn.y;

	}

}
