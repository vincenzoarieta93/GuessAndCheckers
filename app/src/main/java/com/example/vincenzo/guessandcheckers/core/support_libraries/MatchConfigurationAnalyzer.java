package com.example.vincenzo.guessandcheckers.core.support_libraries;

import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.EmptyTile;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;
import com.example.vincenzo.guessandcheckers.core.game_objects.Pawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;

import java.util.List;

/**
 * Created by vincenzo on 18/12/2015.
 */
public class MatchConfigurationAnalyzer {

    /**
     * This methods retrieves the resulting chessboard configuration after a certain move has been done
     * @param betterMove move we want to play on the chessboard
     * @param originalChessboard original configuration of the chessboard
     * @param color color of the player is moving
     * @return a Chessboard obj representing the resulting chessboard configuration after betterMove has been done
     */

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
                        newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(), new WhiteDama(dstCellPawn.getRow(), dstCellPawn.getCol()));
                    else if (color == PawnsColor.BLACK && dstCellPawn.getRow() == ChessboardImpl.LENGTH - 1)
                        newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(), new BlackDama(dstCellPawn.getRow(), dstCellPawn.getCol()));
                    else
                        newConf.setCell(dstCellPawn.getRow(), dstCellPawn.getCol(), originalChessboard.getCell(srcCellPawn.getRow(), srcCellPawn.getCol()));
                } else if (currentCell.equals(srcCellPawn))
                    // in the new chessboard configuration moving pawn's cell must be empty
                    newConf.setCell(srcCellPawn.getRow(), srcCellPawn.getCol(), new EmptyTile(srcCellPawn.getRow(), srcCellPawn.getCol()));
                else if (originalChessboard.getCell(i, j) instanceof Pawn && betterMove.getEatenOpponentPawns() != null && betterMove.getEatenOpponentPawns().contains(originalChessboard.getCell(i, j)))
                    // in the new chessboard configuration each eaten opponent pawn must be removed
                    newConf.setCell(currentCell.getRow(), currentCell.getCol(), new EmptyTile(currentCell.getRow(), currentCell.getCol()));
                else if (originalChessboard.getCell(i, j) instanceof Pawn)
                    newConf.setCell(i, j, originalChessboard.getCell(i, j));
            }
        return newConf;
    }

    public static int countPawns(Chessboard chessboard, PawnsColor color) {
        int pawnsCounter = 0;
        for (int i = 0; i < chessboard.getLength(); i++)
            for (int j = 0; j < chessboard.getLength(); j++)
                if ((color == PawnsColor.WHITE && chessboard.getCell(i, j) instanceof BlackDama || chessboard.getCell(i, j) instanceof BlackPawn) ||
                        (color == PawnsColor.BLACK && chessboard.getCell(i, j) instanceof WhiteDama || chessboard.getCell(i, j) instanceof WhitePawn))
                    pawnsCounter++;
        return pawnsCounter;
    }
}
