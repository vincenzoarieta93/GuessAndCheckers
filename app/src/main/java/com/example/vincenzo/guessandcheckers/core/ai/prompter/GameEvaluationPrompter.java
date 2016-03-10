package com.example.vincenzo.guessandcheckers.core.ai.prompter;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.ASPPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter_callback.GameEvaluationCallback;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;
import com.example.vincenzo.guessandcheckers.core.suggesting.Observed;
import com.example.vincenzo.guessandcheckers.core.support_libraries.AIModulesProvider;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by vincenzo on 26/12/2015.
 */
public class GameEvaluationPrompter extends ASPPrompter {

    public static String CHOOSE_LABEL = "choose";
    private Context context;
    private List<GameConfiguration> configurations;
    private GameConfiguration bestConfiguration;
    private PawnsColor color;
    private Boolean solved = false;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private String filePath;


    public GameEvaluationPrompter(Context context, PawnsColor color, List<GameConfiguration> configurationsToEvaluate) {
        this.context = context;
        this.color = color;
        this.configurations = configurationsToEvaluate;
        this.filePath = AIModulesProvider.getInstance().BEST_CONFIGURATION_MODULE_PATH;
    }

    @Override
    public void solve(Observed obj) {
        initConditionsToFalse();
        this.bestConfiguration = findSolution();
    }

    private GameConfiguration findSolution() {

        StringBuilder guess = new StringBuilder();
        Integer configurationCounter = 0;
        for (GameConfiguration gc : configurations) {
            getFactsFromChessboard(((ChessboardImpl) gc.chessboard), configurationCounter, false);
            guess.append(CHOOSE_LABEL + "(" + configurationCounter + ")");
            if (configurationCounter < configurations.size() - 1)
                guess.append("|");
            else
                guess.append(".");
            configurationCounter++;
        }

        handler.setFilter(CHOOSE_LABEL);
        handler.addOption("-n=5");
        handler.addRawInput(guess.toString());
        handler.addRawInput("userColor(" + color.getFullLabel() + ").");
        addFileInputToHandler(filePath);

        GameEvaluationCallback gameEvaluationCallback = new GameEvaluationCallback(this, context);
        handler.start(context, gameEvaluationCallback);
        awaitTillSolved();

        Integer idBestConfiguration = gameEvaluationCallback.getIDBestConfiguration();
        if(idBestConfiguration != null)
        return configurations.get(idBestConfiguration);
        throw new RuntimeException("no configurations to evaluate were found");
    }


    @Override
    public GameConfiguration getSolution() {
        return this.bestConfiguration;
    }

    @Override
    protected void awaitTillSolved() {
        lock.lock();
        try {
            while (!solved) {
                cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void signalSolved() {
        lock.lock();
        try {
            this.solved = true;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void initConditionsToFalse() {
        this.solved = false;
    }

}
