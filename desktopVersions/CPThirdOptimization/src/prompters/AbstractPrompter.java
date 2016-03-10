package prompters;

import game_objects.BlackDama;
import game_objects.BlackPawn;
import game_objects.ChessboardImpl;
import game_objects.ChessboardItem;
import game_objects.EmptyTile;
import game_objects.PawnsColor;
import game_objects.WhiteDama;
import game_objects.WhitePawn;

public abstract class AbstractPrompter implements SuggestProvider, Prompter {

	protected String pathExecutable = AbstractPrompter.class.getClassLoader().getResource("dlv.exe").getPath();

	protected StringBuilder getFactsFromChessboard(ChessboardImpl chessboardImpl, Integer gameConfigurationID,
			boolean pawnsID) {

		StringBuilder rules = new StringBuilder();
		int pawnsCounter = 0;

		for (int i = 0; i < chessboardImpl.getLength(); i++) {
			for (int j = 0; j < chessboardImpl.getLength(); j++) {
				ChessboardItem selectedItem = chessboardImpl.getCell(i, j);
				if (!(selectedItem instanceof EmptyTile)) {
					pawnsCounter++;
					if (selectedItem instanceof WhitePawn && !(selectedItem instanceof WhiteDama))
						rules.append(
								parsePawn(i, j, PawnsColor.WHITE, "man", pawnsCounter, gameConfigurationID, pawnsID));
					else if (selectedItem instanceof BlackPawn && !(selectedItem instanceof BlackDama))
						rules.append(
								parsePawn(i, j, PawnsColor.BLACK, "man", pawnsCounter, gameConfigurationID, pawnsID));
					else if (selectedItem instanceof WhiteDama)
						rules.append(
								parsePawn(i, j, PawnsColor.WHITE, "king", pawnsCounter, gameConfigurationID, pawnsID));
					else
						rules.append(
								parsePawn(i, j, PawnsColor.BLACK, "king", pawnsCounter, gameConfigurationID, pawnsID));
				}
			}
		}
		return rules;
	}

	private String parsePawn(int row, int col, PawnsColor color, String status, int pawnID, Integer configurationID,
			boolean neededPawnsID) {
		String fact;
		if (configurationID != null) {
			if (neededPawnsID)
				fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + "," + pawnID
						+ "," + configurationID + ").");
			else
				fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + ","
						+ configurationID + ").");
		} else {
			if (neededPawnsID)
				fact = new String(
						"pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + "," + pawnID + ").");
			else
				fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + ").");
		}
		return fact;
	}
}
