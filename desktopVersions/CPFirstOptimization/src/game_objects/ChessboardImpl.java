package game_objects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import core.ChessboardMementoOriginator;
import core.FromMementoToCareTaker;
import core.FromMementoToOriginator;
import core.Memento;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class ChessboardImpl implements Chessboard<ChessboardItem>, ChessboardMementoOriginator {

	public static final int LENGTH = 8;
	private ChessboardItem[][] matrix;
	private Lock lock = new ReentrantLock();

	public ChessboardImpl() {
		initMatrix();
	}

	private void initMatrix() {
		this.matrix = new ChessboardItem[LENGTH][LENGTH];

		for (int i = 0; i < LENGTH; i++)
			for (int j = 0; j < LENGTH; j++)
				matrix[i][j] = new EmptyTile(i, j);
	}

	@Override
	public void setCell(int x, int y, ChessboardItem item) {
		lock.lock();
		try {
			matrix[x][y] = null;
			matrix[x][y] = item;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public ChessboardItem getCell(int x, int y) {
		lock.lock();
		try {
			return matrix[x][y];
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int getLength() {
		return this.LENGTH;
	}

	@Override
	public ChessboardItem getMiddleItem(Cell srcCell, Cell dstCell) {
		int srcRow = srcCell.getRow();
		int srcCol = srcCell.getCol();

		int dstRow = dstCell.getRow();
		int dstCol = dstCell.getCol();

		// it's a jump
		if (Math.abs(srcRow - dstRow) == 2 && Math.abs(srcCol - dstCol) == 2) {
			int upperRow = srcRow;
			int leftMostCol = srcCol;

			if (upperRow > dstRow)
				upperRow = dstRow;
			if (leftMostCol > dstCol)
				leftMostCol = dstCol;

			return getCell(upperRow + 1, leftMostCol + 1);
		}

		throw new RuntimeException("it's not a jump");
	}

	@Override
	public Memento createMemento(int row, int col) {
		lock.lock();
		try {
			return new Memento(row, col, matrix[row][col]);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Cell setMemento(FromMementoToCareTaker memento) {
		int oldX = ((FromMementoToOriginator) memento).getX();
		int oldY = ((FromMementoToOriginator) memento).getY();
		ChessboardItem oldItem = ((FromMementoToOriginator) memento).getChessboardItem();
		setCell(oldX, oldY, oldItem);
		return new Cell(oldX, oldY);
	}

	public void print() {
		lock.lock();
		try {
			for (int i = 0; i < LENGTH; i++) {
				StringBuilder b = new StringBuilder();
				for (int j = 0; j < LENGTH; j++)
					b.append(matrix[i][j] + " ");
				System.out.println(b.toString());
			}
		} finally {
			lock.unlock();
		}
	}
}
