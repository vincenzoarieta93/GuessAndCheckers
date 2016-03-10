package game_objects;

public class BlackDama extends BlackPawn {

	public BlackDama(int x, int y) {

		super(x, y);
	}

	public BlackDama() {
		super();
	}

	@Override
	public String toString() {
		return "@";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BlackDama))
			return false;
		if (!super.equals(o))
			return false;
		return true;
	}
}
