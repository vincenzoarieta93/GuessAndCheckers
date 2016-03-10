package game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class WhitePawn extends Pawn {

	private final String COLOR = WHITE_PAWN;

	public WhitePawn(int x, int y) {
		super(x, y);
	}

	public WhitePawn() {
		super();
	}

	@Override
	public String getCOLOR() {
		return this.COLOR;
	}

	@Override
	public String toString() {
		return "W";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WhitePawn))
			return false;
		if (!super.equals(o))
			return false;

		WhitePawn whitePawn = (WhitePawn) o;

		return getCOLOR().equals(whitePawn.getCOLOR());

	}

	@Override
	public int hashCode() {
		return getCOLOR().hashCode();
	}
}
