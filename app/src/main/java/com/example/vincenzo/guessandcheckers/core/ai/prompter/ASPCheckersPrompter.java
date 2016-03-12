package com.example.vincenzo.guessandcheckers.core.ai.prompter;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.ASPPrompter;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;
import com.example.vincenzo.guessandcheckers.core.suggesting.Observer;
import com.example.vincenzo.guessandcheckers.core.support_libraries.AIModulesProvider;
import com.example.vincenzo.guessandcheckers.core.support_libraries.MatchConfigurationAnalyzer;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;

import java.util.ArrayList;
import java.util.List;

public class ASPCheckersPrompter extends ASPPrompter {

    public static final String CICCIO = "CICCIO";
    public static final int HIGHEST_MAX_DEPTH_LEVEL = 1;
    private static final int CLUSTER_OF_CONFS_TO_EVALUATE = 20;

    private Move bestMoves = null;
    private Chessboard chessboard;
    private PawnsColor color;
    private Context context;

    public ASPCheckersPrompter(Chessboard chessboard, PawnsColor color, Context context) {
        this.chessboard = chessboard;
        this.color = color;
        this.context = context;
        AIModulesProvider.getInstance().initAIModules(context);
    }

    @Override
    public void solve(Observer observer) {
        ChessboardImpl chessboardImpl = (ChessboardImpl) chessboard;
        //betterMoves uses several dlv modules to find the best moves to do at a certain moment
        //and retrieves all the checkerboard configurations which we would obtain if we played those moves
        //GameConfiguration stores a checkerboard configuration and the originator move

        if (isAWrongPattern(chessboardImpl)) {
            bestMoves = null;
            Log.i("CICCIO", "Sorry, illegal pattern");
            return;
        }

        observer.notify("processing better moves...");
        List<GameConfiguration> configurations = betterMoves(chessboardImpl, null, color, context);

        //depth search level (number of look-ahead
        int depthEvaluation = HIGHEST_MAX_DEPTH_LEVEL;
        //look-ahead counter
        int countDepthLevels = 0;

        // if more than one legal move have been found
        // we start a match simulation process for each move found. This process finished when countDepthLevels is equals to depthEvaluation
        // Once simulation process finished we get a list of all the checkerboard configurations produced by each move
        // These configurations are parsed in dlv facts and passed to another ASP module which is used into GameEvaluationPrompter class
        // This module filters out the best configuration. Since that a GameConfiguration object stores also its originator move, we consequently get the best move to do.

        if (configurations.size() > 1) {

            observer.notify("simulation in progress...");
            List<GameConfiguration> configurationsToEvaluate = new ArrayList<>();

            int progressIncrement = 100 / configurations.size();
            int iterations = 0;

            for (GameConfiguration conf : configurations) {
                observer.notify("simulation progress: " + progressIncrement * iterations + "%");
                simulateMatch(conf, conf.move, color, configurationsToEvaluate, countDepthLevels, depthEvaluation, context);
                iterations++;
            }
            observer.notify("simulation finished");

            if (configurationsToEvaluate.size() > 1) {
                observer.notify("starting evaluation process...");
                GameConfiguration bestConfiguration = getBestGameConfiguration(configurationsToEvaluate);
                bestMoves = bestConfiguration.move;
            } else//there is at least one configuration
                bestMoves = configurationsToEvaluate.get(0).move;
        } else if (configurations.size() == 1)
            bestMoves = configurations.get(0).move;
        else
            bestMoves = null;
    }

    private GameConfiguration getBestGameConfiguration(List<GameConfiguration> configurationsToEvaluate) {

        int size = configurationsToEvaluate.size();

        if (size == 1)
            return configurationsToEvaluate.get(0);

        List<GameConfiguration> newConfs = new ArrayList<>();
        int itemsToEvaluate = size;
        int start = 0;
        int setOff;

        while (itemsToEvaluate > 0) {
            setOff = (itemsToEvaluate < CLUSTER_OF_CONFS_TO_EVALUATE) ? size : start + CLUSTER_OF_CONFS_TO_EVALUATE;
            List<GameConfiguration> tmpConfs = configurationsToEvaluate.subList(start, setOff);
            resetHandlerParameters();
            newConfs.add(findBestConfig(tmpConfs));
            itemsToEvaluate -= tmpConfs.size();
            start += tmpConfs.size();
        }
        return findBestConfig(newConfs);
    }


    private GameConfiguration findBestConfig(List<GameConfiguration> tmpConfs) {
        GameEvaluationPrompter confsEvaluator = new GameEvaluationPrompter(context, color, tmpConfs);
        confsEvaluator.solve(null);
        return confsEvaluator.getSolution();
    }


    @Override
    public Move getSolution() {
        return bestMoves;
    }


    /**
     * This method must return jumps if any, otherwise it must find better moves
     *
     * @param chessboardImpl current checkerboard configuration
     * @param color Color of the player who required the hint
     * @return a List of configurations created by the best suggested moves
     */
    private List<GameConfiguration> betterMoves(final ChessboardImpl chessboardImpl, Move originatorMove, final PawnsColor color, final Context context) {

        List<GameConfiguration> confs = new ArrayList<>();
        CapturingPrompter capturingPrompter = new CapturingPrompter(context, chessboardImpl, color);
        capturingPrompter.solve(null);
        List<Move> betterMoves = (List<Move>) capturingPrompter.getSolution();

        //if betterMoves is not empty then we have one or more jumps
        // else we have to determine all legal moves
        if (betterMoves.isEmpty()) {
            AllLegalMovesPrompter allMovesPrompter = new AllLegalMovesPrompter(context, chessboardImpl, color);
            allMovesPrompter.solve(null);
            betterMoves = (List<Move>) allMovesPrompter.getSolution();
        }

        for (Move m : betterMoves)
            if (originatorMove == null)
                confs.add(new GameConfiguration(MatchConfigurationAnalyzer.buildNewConfiguration(m, chessboardImpl, color), m));
            else
                confs.add(new GameConfiguration(MatchConfigurationAnalyzer.buildNewConfiguration(m, chessboardImpl, color), originatorMove));
        return confs;
    }

    // this method simulate a proper match till the specified depth search level
    private void simulateMatch(GameConfiguration conf, Move moveOriginator, PawnsColor color, List<GameConfiguration> confsToEvaluate, int countDepthLevels, int depthEvaluation, Context context) {

        if (countDepthLevels >= depthEvaluation ) {
            confsToEvaluate.add(conf);
            return;
        }

        List<GameConfiguration> tmpConfs = betterMoves(((ChessboardImpl) conf.chessboard), moveOriginator, PawnsColor.getOppositeColor(color), context);

        if (tmpConfs.isEmpty()) {
            confsToEvaluate.add(conf);
            return;
        }

        for(GameConfiguration gc : tmpConfs)
            simulateMatch(gc, moveOriginator, PawnsColor.getOppositeColor(color), confsToEvaluate, countDepthLevels + 1, depthEvaluation, context);
    }

    public boolean isAWrongPattern(ChessboardImpl chessboardImpl) {
        int whitePawns = 0;
        int blackPawns = 0;

        for (int i = 0; i < chessboardImpl.getLength(); i++)
            for (int j = 0; j < chessboardImpl.getLength(); j++)
                if (chessboardImpl.getCell(i, j) instanceof WhitePawn)
                    whitePawns++;
                else if (chessboardImpl.getCell(i, j) instanceof BlackPawn)
                    blackPawns++;

        return (whitePawns == 0 || blackPawns == 0 || whitePawns > 12 || blackPawns > 12);
    }

    @Override
    protected void awaitTillSolved() {
        //TODO auto generated code
    }

    @Override
    public void signalSolved() {
        //TODO auto generated code
    }

}
