package core;

import java.util.List;

import game_objects.BlackDama;
import game_objects.BlackPawn;
import game_objects.Cell;
import game_objects.Chessboard;
import game_objects.ChessboardImpl;
import game_objects.EmptyTile;
import game_objects.Move;
import game_objects.Pawn;
import game_objects.PawnsColor;
import game_objects.WhiteDama;
import game_objects.WhitePawn;

/**
 * Created by vincenzo on 18/12/2015.
 */
public class MatchConfigurationAnalyzer {

	public static Chessboard buildNewConfiguration(Move betterMove, Chessboard originalChessboard, PawnsColor color) {

		Chessboard newConf = new ChessboardImpl();
		List<Cell> steps = betterMove.getMoveSteps();

		Cell srcCellPawn = steps.get(0);
		Cell dstCellPawn = steps.get(steps.size() - 1);

		for (int i = 0; i < originalChessboard.getLength(); i++)
			for (int j = 0; j < originalChessboard.getLength(); j++) {
				Cell currentCell = new Cell(i, j);
				if (currentCell.equals(dstCellPawn)) {
					if (color == PawnsColor.WHITE && dstCellPawn.getRow() == 0)
						newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(),
								new WhiteDama(dstCellPawn.getRow(), dstCellPawn.getCol()));
					else if (color == PawnsColor.BLACK && dstCellPawn.getRow() == ChessboardImpl.LENGTH - 1)
						newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(),
								new BlackDama(dstCellPawn.getRow(), dstCellPawn.getCol()));
					else
						newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(),
								originalChessboard.getCell(srcCellPawn.getRow(), srcCellPawn.getCol()));
				} else if (currentCell.equals(srcCellPawn))
					// in the new chessboard configuration moving pawn's cell
					// must be empty
					newConf.setCell(srcCellPawn.getRow(), srcCellPawn.getCol(),
							new EmptyTile(srcCellPawn.getRow(), srcCellPawn.getCol()));
				else if (originalChessboard.getCell(i, j) instanceof Pawn && betterMove.getEatenOpponentPawns() != null
						&& betterMove.getEatenOpponentPawns().contains(originalChessboard.getCell(i, j)))
					// in the new chessboard configuration each eaten opponent
					// pawn must be removed
					newConf.setCell(currentCell.getRow(), currentCell.getCol(),
							new EmptyTile(currentCell.getRow(), currentCell.getCol()));
				else if (originalChessboard.getCell(i, j) instanceof Pawn)
					newConf.setCell(i, j, originalChessboard.getCell(i, j));
			}
		return newConf;
	}

	public static int countPawns(Chessboard chessboard, PawnsColor color) {
		int pawnsCounter = 0;
		for (int i = 0; i < chessboard.getLength(); i++)
			for (int j = 0; j < chessboard.getLength(); j++)
				if ((color == PawnsColor.WHITE && chessboard.getCell(i, j) instanceof BlackDama
						|| chessboard.getCell(i, j) instanceof BlackPawn)
						|| (color == PawnsColor.BLACK && chessboard.getCell(i, j) instanceof WhiteDama
								|| chessboard.getCell(i, j) instanceof WhitePawn))
					pawnsCounter++;
		return pawnsCounter;
	}
}
