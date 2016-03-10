package game_objects;

/**
 * Created by vincenzo on 29/11/2015.
 */
public class Jump {
	private int startingRow;
	private int startingCol;
	private int nextRow;
	private int nextCol;
	private int id;
	private String jumperStatus;
	private String jumpedPawnStatus;

	public Jump() {
		super();
	}

	public Jump(int startingRow, int startingCol, int nextRow, int nextCol, int id, String status,
			String jumpedPawnStatus) {
		this.startingRow = startingRow;
		this.startingCol = startingCol;
		this.nextRow = nextRow;
		this.nextCol = nextCol;
		this.id = id;
		this.jumperStatus = status;
		this.jumpedPawnStatus = jumpedPawnStatus;
	}

	@Override
	public String toString() {
		return "Jump{" + "startingRow=" + startingRow + ", startingCol=" + startingCol + ", nextRow=" + nextRow
				+ ", nextCol=" + nextCol + ", id=" + id + " status= " + jumperStatus + ", jumpedPawnStatus="
				+ jumpedPawnStatus + '}';
	}

	public int getStartingRow() {
		return startingRow;
	}

	public void setStartingRow(int startingRow) {
		this.startingRow = startingRow;
	}

	public int getStartingCol() {
		return startingCol;
	}

	public void setStartingCol(int startingCol) {
		this.startingCol = startingCol;
	}

	public int getNextRow() {
		return nextRow;
	}

	public void setNextRow(int nextRow) {
		this.nextRow = nextRow;
	}

	public int getNextCol() {
		return nextCol;
	}

	public void setNextCol(int nextCol) {
		this.nextCol = nextCol;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJumperStatus() {
		return jumperStatus;
	}

	public void setJumperStatus(String status) {
		this.jumperStatus = status;
	}

	public String getJumpedPawnStatus() {
		return jumpedPawnStatus;
	}

	public void setJumpedPawnStatus(String jumpedPawnStatus) {
		this.jumpedPawnStatus = jumpedPawnStatus;
	}
}