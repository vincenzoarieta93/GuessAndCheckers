package game_objects;

/**
 * Created by vincenzo on 26/12/2015.
 */

public class ChosenPawn {

	private int row;
	private int col;
	private String color;
	private String status;
	private int idConfiguration;

	public ChosenPawn() {
	}

	public ChosenPawn(int row, int col, String color, String status, int idConfiguration) {
		this.row = row;
		this.col = col;
		this.color = color;
		this.status = status;
		this.idConfiguration = idConfiguration;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getIdConfiguration() {
		return idConfiguration;
	}

	public void setIdConfiguration(int idConfiguration) {
		this.idConfiguration = idConfiguration;
	}

	@Override
	public String toString() {
		return "ChosenPawn{" + "row=" + row + ", col=" + col + ", color='" + color + '\'' + ", status='" + status + '\''
				+ ", idConfiguration=" + idConfiguration + '}';
	}
}
