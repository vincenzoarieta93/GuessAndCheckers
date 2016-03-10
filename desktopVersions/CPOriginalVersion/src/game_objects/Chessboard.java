package game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public abstract interface Chessboard<T> {

	void setCell(int x, int y, T item);

	T getCell(int x, int y);

	int getLength();

	ChessboardItem getMiddleItem(Cell srcCell, Cell dstCell);
}
