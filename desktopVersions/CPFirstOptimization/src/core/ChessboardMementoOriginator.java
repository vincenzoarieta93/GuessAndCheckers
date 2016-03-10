package core;
import game_objects.Cell;

public interface ChessboardMementoOriginator {

	FromMementoToCareTaker createMemento(int row, int col);

	Cell setMemento(FromMementoToCareTaker memento);
}
