package core;
import game_objects.ChessboardItem;

/**
 * Created by vincenzo on 02/11/2015.
 */
public class Memento implements FromMementoToCareTaker, FromMementoToOriginator {
	private int x;
	private int y;
	private ChessboardItem chessboardItem;

	public Memento(int x, int y, ChessboardItem chessboardItem) {
		this.x = x;
		this.y = y;
		this.chessboardItem = chessboardItem;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public ChessboardItem getChessboardItem() {
		return this.chessboardItem;
	}
}
