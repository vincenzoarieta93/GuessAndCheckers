package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.base.AbstractASPManager;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.suggesting.Observed;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.AIModulesProvider;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.MatchConfigurationAnalyzer;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;

import java.util.ArrayList;
import java.util.List;

public class AbstractASPCheckersManager extends AbstractASPManager implements ObserverPrompter {

    public static final String CICCIO = "CICCIO";
    public static final int HIGHEST_MAX_DEPTH_LEVEL = 1;

    private Move bestMoves = null;
    private Chessboard chessboard;
    private PawnsColor color;
    private Context context;

    public AbstractASPCheckersManager(Chessboard chessboard, PawnsColor color, Context context) {
        this.chessboard = chessboard;
        this.color = color;
        this.context = context;
        AIModulesProvider.getInstance().initAIModules(context);
    }

    @Override
    public void solve(Observed observed) {
        ChessboardImpl chessboardImpl = (ChessboardImpl) chessboard;
        //betterMoves uses several dlv modules to find the best moves to do at a certain moment
        //and retrieves all the checkerboard configurations which we would obtain if we played those moves
        //GameConfiguration stores a checkerboard configuration and the originator move

        if (isAWrongPattern(chessboardImpl)) {
            bestMoves = null;
            Log.i("CICCIO", "Sorry, illegal pattern");
            return;
        }

        observed.notify("processing better moves...");
        List<GameConfiguration> configurations = betterMoves(chessboardImpl, null, color, context);

        //depth search level (number of look-ahead
        int depthEvaluation = HIGHEST_MAX_DEPTH_LEVEL;
        //look-ahead counter
        int countDepthLevels = 0;

        // if more than one legal move have been found
        // we start a match simulation process for each move found. This process finished when countDepthLevels is equals to depthEvaluation
        // Once simulation process finished we get a list of all the checkerboard configurations produced by each move
        // These configurations are parsed in dlv facts and passed to another ASP module which is used into GameEvaluationManagerAbstract class
        // This module filters out the best configuration. Since that a GameConfiguration object stores also its originator move, we consequently get the best move to do.

        if (configurations.size() > 1) {

            observed.notify("simulation in progress...");
            List<GameConfiguration> configurationsToEvaluate = new ArrayList<>();

            int progressIncrement = 100 / configurations.size();
            int iterations = 0;

            for (GameConfiguration conf : configurations) {
                observed.notify("simulation progress: " + progressIncrement * iterations + "%");
                simulateMatch(conf, conf.move, color, configurationsToEvaluate, countDepthLevels, depthEvaluation, context);
                iterations++;
            }
            observed.notify("simulation finished");

            if (configurationsToEvaluate.size() > 1) {

                observed.notify("starting evaluation process...");
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
        resetHandlerParameters();
        return findBestConfig(configurationsToEvaluate);
    }


    private GameConfiguration findBestConfig(List<GameConfiguration> tmpConfs) {
        GameEvaluationManagerAbstract confsEvaluator = new GameEvaluationManagerAbstract(context, color, tmpConfs);
        confsEvaluator.solve();
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
        CapturingManagerAbstract capturingPrompter = new CapturingManagerAbstract(context, chessboardImpl, color);
        capturingPrompter.solve();
        List<Move> betterMoves = (List<Move>) capturingPrompter.getSolution();

        //if betterMoves is not empty then we have one or more jumps
        // else we have to determine all legal moves
        if (betterMoves.isEmpty()) {
            AllLegalMovesManagerAbstract allMovesPrompter = new AllLegalMovesManagerAbstract(context, chessboardImpl, color);
            allMovesPrompter.solve();
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
