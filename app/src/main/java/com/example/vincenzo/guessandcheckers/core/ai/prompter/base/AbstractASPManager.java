package com.example.vincenzo.guessandcheckers.core.ai.prompter.base;

import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardItem;
import com.example.vincenzo.guessandcheckers.core.game_objects.EmptyTile;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;

import java.io.FileNotFoundException;

import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.dlv.DLVHandler;
import it.unical.mat.embasp.mapper.ASPMapper;

/**
 * Created by vincenzo on 19/12/2015.
 */
public abstract class AbstractASPManager {

    private static final String TAG = "CICCIO";
    public static final ASPHandler handler = new DLVHandler();

    abstract protected void awaitTillSolved();

    abstract public void signalSolved();

    protected void addFileInputToHandler(String filePath) {
        try {
            handler.addFileInput(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void resetHandlerParameters() {
        handler.resetPredicatesToFilter();
        handler.resetOptions();
        handler.resetProgram();
        handler.resetInputFiles();
        ASPMapper.getInstance().resetRegisteredClasses();
    }

    protected int getFactsFromChessboard(ChessboardImpl chessboardImpl, Integer gameConfigurationID, boolean pawnsID) {

        int pawnsCounter = 0;

        for (int i = 0; i < chessboardImpl.getLength(); i++) {
            for (int j = 0; j < chessboardImpl.getLength(); j++) {
                ChessboardItem selectedItem = chessboardImpl.getCell(i, j);
                if (!(selectedItem instanceof EmptyTile)) {
                    pawnsCounter++;
                    if (selectedItem instanceof WhitePawn && !(selectedItem instanceof WhiteDama))
                        handler.addRawInput(parsePawn(i, j, PawnsColor.WHITE, "man", pawnsCounter, gameConfigurationID, pawnsID));
                    else if (selectedItem instanceof BlackPawn && !(selectedItem instanceof BlackDama))
                        handler.addRawInput(parsePawn(i, j, PawnsColor.BLACK, "man", pawnsCounter, gameConfigurationID, pawnsID));
                    else if (selectedItem instanceof WhiteDama)
                        handler.addRawInput(parsePawn(i, j, PawnsColor.WHITE, "king", pawnsCounter, gameConfigurationID, pawnsID));
                    else
                        handler.addRawInput(parsePawn(i, j, PawnsColor.BLACK, "king", pawnsCounter, gameConfigurationID, pawnsID));
                }
            }
        }
        return pawnsCounter;
    }


    private String parsePawn(int row, int col, PawnsColor color, String status, int pawnID, Integer configurationID, boolean neededPawnsID) {
        String fact;
        if (configurationID != null) {
            if (neededPawnsID)
                fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + "," + pawnID + "," + configurationID + ").");
            else
                fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + "," + configurationID + ").");
        } else {
            if (neededPawnsID)
                fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + "," + pawnID + ").");
            else
                fact = new String("pawn(" + row + "," + col + "," + color.getFullLabel() + "," + status + ").");
        }
        return fact;
    }

}
