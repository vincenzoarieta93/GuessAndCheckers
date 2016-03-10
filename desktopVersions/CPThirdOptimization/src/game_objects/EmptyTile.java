package game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class EmptyTile implements ChessboardItem {

	private int x;
	private int y;
	private String STATUS = EMPTY_TILE;

	public EmptyTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public EmptyTile() {
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String getStatus() {
		return this.STATUS;
	}

	@Override
	public Cell getPosition() {
		return new Cell(x, y);
	}

	@Override
	public String toString() {
		return "0";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
