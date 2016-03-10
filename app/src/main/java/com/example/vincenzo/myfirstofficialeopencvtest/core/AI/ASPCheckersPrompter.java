package com.example.vincenzo.myfirstofficialeopencvtest.core.AI;

import android.content.Context;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardProvider;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.embasp.dlv.DLVHandler;

public class ASPCheckersPrompter extends ASPPrompter {

    private static ASPCheckersPrompter instance = null;
    private List<Move> bestMoves = new ArrayList<>();

    private ASPCheckersPrompter() {
        this.handler = new DLVHandler();
    }

    public static ASPCheckersPrompter getInstance() {
        if (instance == null)
            instance = new ASPCheckersPrompter();
        return instance;
    }


    @Override
    public void processMoves(final Chessboard chessboard, final PawnsColor color, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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

//        if (configurations.size() > 1) {
//
//            for (GameConfiguration conf : configurations)
//                gameEvaluation(conf, color, configurationsToEvaluate, countDepthLevels, depthEvaluation, context);
//
//            for (GameConfiguration gc : configurationsToEvaluate)
//                getFactsFromChessboard(handler, ((ChessboardImpl) gc.chessboard));
//
//            //TODO compare the game configurations of configurationsToEvaluate
//            return null;
//
//        } else
//            return configurations.get(0).move;
            }
        }).start();
    }

    @Override
    public List<Move> suggestMoves() {
        return null;
    }


    /**
     * This method must return jumps if any, otherwise it must find better moves
     *
     * @param chessboardImpl
     * @param color
     * @return
     */
    private List<GameConfiguration> betterMoves(final ChessboardImpl chessboardImpl, final PawnsColor color, final Context context) {

        List<GameConfiguration> confs = new ArrayList<>();
        final JumpsPrompter jumpsP = new JumpsPrompter(handler);

        jumpsP.processMoves(chessboardImpl, color, context);
        List<Move> foundJumps = jumpsP.suggestMoves();

        if(foundJumps.size() > 0)
        for (Move m : foundJumps)
            confs.add(new GameConfiguration(ChessboardProvider.buildNewConfiguration(m, chessboardImpl), m));
        else{
            //TODO find all moves (starting allMoves module)
        }

        return confs;
    }

    //metodo provvisorio e non debuggato
    // che simula le mosse di entrambi i giocatori fino a un dato livello di profondità
    private void gameEvaluation(GameConfiguration conf, PawnsColor color, List<GameConfiguration> confsToEvaluate, int countDepthLevels, int depthEvaluation, Context context) {

        if (isGameOver(conf) || countDepthLevels >= depthEvaluation) {
            confsToEvaluate.add(conf);
            return;
        }

        //invertire chessboard
        List<GameConfiguration> tmpConfs = betterMoves(((ChessboardImpl) conf.chessboard), PawnsColor.getOppositeColor(color), context);

        for (GameConfiguration gc : tmpConfs)
            gameEvaluation(gc, PawnsColor.getOppositeColor(color), confsToEvaluate, countDepthLevels + 1, depthEvaluation, context);

    }

    //metodo provvisorio
    private boolean isGameOver(GameConfiguration conf) {
        return false;
    }

    static class GameConfiguration {

        Chessboard chessboard;
        Move move;

        public GameConfiguration(Chessboard chessboard, Move move) {
            this.chessboard = chessboard;
            this.move = move;
        }
    }

}
