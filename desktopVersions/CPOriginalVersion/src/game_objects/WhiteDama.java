package game_objects;

/**
 * Created by vincenzo on 05/11/2015.
 */
public class WhiteDama extends WhitePawn {

	public WhiteDama() {
	}

	public WhiteDama(int x, int y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return "X";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WhiteDama))
			return false;
		if (!super.equals(o))
			return false;
		return true;
	}
}
