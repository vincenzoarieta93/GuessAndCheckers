package com.example.vincenzo.myfirstofficialeopencvtest.core;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardItem;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.EmptyTile;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhiteDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.FileManager;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.dlv.DLVHandler;
import it.unical.mat.embasp.mapper.ASPMapper;

public class ASPPrompter implements Prompter {

    private static ASPPrompter instance = null;

    private ASPPrompter() {
        ASPMapper.getInstance().registerClass(Origin.class);
        ASPMapper.getInstance().registerClass(Jump.class);
    }

    public static ASPPrompter getInstance() {
        if (instance == null)
            instance = new ASPPrompter();
        return instance;
    }


    @Override
    public Move suggestMoves(Chessboard chessboard, PawnsColor color, Context context) {

        ChessboardImpl chessboardImpl = (ChessboardImpl) chessboard;

        //betterMoves sfrutta diversi moduli dlv per trovare le migliori mosse da fare
        //e restituisce le configurazioni della scacchiera che si otterrebbero da quelle mosse
        //GameConfiguration memorizza la configurazione della scacchiera e la mossa chl'ha originata
        List<GameConfiguration> configurations = betterMoves(chessboardImpl, color, context);
        List<GameConfiguration> configurationsToEvaluate = new ArrayList<>();

        //numero massimo di look-ahead (profondità di ricorsione)
        int depthEvaluation = 3;
        //contatore di look-ahead effettuate
        int countDepthLevels = 0;

        // se sono state trovate più mosse effettuabili
        // per ognua di esse gameAvaluation, che utilizza betterMoves (quindi DLV) e che è ricorsiva,
        //genera la possibile configurazione della scacchiera che si otterrebbe dopo depthEvaluation
        //chiamate ricorsive
        //Ottenute queste configurazioni, esse vengono tradotte in fatti e passate a un altro modulo dlv.
        //Questo modulo dlv sceglie la migliore configurazione e di conseguenza la migliore mossa da effettuare
        if (configurations.size() > 1) {

            for (GameConfiguration conf : configurations)
                gameEvaluation(conf, color, configurationsToEvaluate, countDepthLevels, depthEvaluation, context);

            for (GameConfiguration gc : configurationsToEvaluate)
                getFactsFromChessboard(((ChessboardImpl) gc.chessboard), "cicciopasticciodummyfile.txt");

            //TODO compare the game configurations of configurationsToEvaluate
            return null;

        } else
            return configurations.get(0).move;
//        return null;
    }


    /**
     * This method must return jumps if any, otherwise it must find better moves
     *
     * @param chessboardImpl
     * @param color
     * @return
     */
    private List<GameConfiguration> betterMoves(ChessboardImpl chessboardImpl, PawnsColor color, Context context) {

        ASPHandler handler = new DLVHandler();

        String fileToWrite = "dlv/finds_jumps.asp";
        String fileName = "jumps_module";
        String fileExtension = ".txt";
        String subDirName = "GuessAndCheckers";

        String filePath = FileManager.writeFileFromAssetsToExternalStorage(context, fileToWrite, fileName, fileExtension, subDirName);
        FileManager.appendRowToFile(filePath, "#maxint = " + 1000 + ".");
        FileManager.appendRowToFile(filePath, "userColor(" + color.getFullLabel() + ").");
        getFactsFromChessboard(chessboardImpl, filePath);

        JumpsPrompter jumpsP = new JumpsPrompter(handler, filePath, context, chessboardImpl);
        Log.i("CICCIO", "beforeFindJumps()");
        //fa partire il modulo dlv
        jumpsP.findJumps();

        //TODO auto generated code
        return null;
    }

    //metodo provvisorio e non debuggato
    // che simula le mosse di entrambi i giocatori fino a un dato livello di profondità
    private void gameEvaluation(GameConfiguration conf, PawnsColor color, List<GameConfiguration> confsToEvaluate, int countDepthLevels, int depthEvaluation, Context context) {

        if (isGameOver(conf) || countDepthLevels >= depthEvaluation) {
            confsToEvaluate.add(conf);
            return;
        }

        List<GameConfiguration> tmpConfs = betterMoves(((ChessboardImpl) conf.chessboard), PawnsColor.getOppositeColor(color),context);

        for (GameConfiguration gc : tmpConfs)
            gameEvaluation(gc, PawnsColor.getOppositeColor(color), confsToEvaluate, countDepthLevels + 1, depthEvaluation, context);

    }

//metodo provvisorio
    private boolean isGameOver(GameConfiguration conf) {
        return false;
    }


    class GameConfiguration {

        Chessboard chessboard;
        Move move;

        public GameConfiguration(Chessboard chessboard, Move move) {
            this.chessboard = chessboard;
            this.move = move;
        }
    }


    public static void getFactsFromChessboard(ChessboardImpl chessboardImpl, String filePath) {

        int pawnsCounter = 0;

        for (int i = 0; i < chessboardImpl.getLength(); i++) {
            for (int j = 0; j < chessboardImpl.getLength(); j++) {
                ChessboardItem selectedItem = chessboardImpl.getCell(i, j);
                if (selectedItem instanceof EmptyTile)
                    FileManager.appendRowToFile(filePath, "emptyTile(" + i + "," + j + ").");
                else {
                    pawnsCounter++;
                    if (selectedItem instanceof WhitePawn && !(selectedItem instanceof WhiteDama))
                        FileManager.appendRowToFile(filePath, "pawn(" + i + "," + j + "," + PawnsColor.WHITE.getFullLabel() + "," + "man" + "," + pawnsCounter + ").");
                    else if (selectedItem instanceof BlackPawn && !(selectedItem instanceof BlackDama))
                        FileManager.appendRowToFile(filePath, "pawn(" + i + "," + j + "," + PawnsColor.BLACK.getFullLabel() + "," + "man" + "," + pawnsCounter + ").");
                    else if (selectedItem instanceof WhiteDama)
                        FileManager.appendRowToFile(filePath, "pawn(" + i + "," + j + "," + PawnsColor.WHITE.getFullLabel() + "," + "king" + "," + pawnsCounter + ").");
                    else
                        FileManager.appendRowToFile(filePath, "pawn(" + i + "," + j + "," + PawnsColor.BLACK.getFullLabel() + "," + "king" + "," + pawnsCounter + ").");
                }
            }
        }

    }

}
