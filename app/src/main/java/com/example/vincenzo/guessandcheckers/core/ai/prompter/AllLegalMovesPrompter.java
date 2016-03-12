package com.example.vincenzo.guessandcheckers.core.ai.prompter;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.ai.bean.DLVMove;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.ASPPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter_callback.AllMovesCallback;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.ConcreteMove;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;
import com.example.vincenzo.guessandcheckers.core.suggesting.Observer;
import com.example.vincenzo.guessandcheckers.core.support_libraries.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.embasp.mapper.ASPMapper;

/**
 * Created by vincenzo on 19/12/2015.
 */
public class AllLegalMovesPrompter extends ASPPrompter {

    private static final String TAG = "CICCIO";
    private String logicProgramPath;
    private Chessboard chessboard;
    private Context context;
    private PawnsColor color;
    private List<Move> moves;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private Boolean solved = false;

    public AllLegalMovesPrompter(Context context, ChessboardImpl chessboardImpl, PawnsColor color) {
        this.context = context;
        this.chessboard = chessboardImpl;
        this.color = color;
    }

    private void configureJumpsPrompter(Context context) {
        String fileToWrite = "dlv/allmoves.asp";
        String fileName = "all_moves_seeker";
        String fileExtension = ".txt";
        String subDirName = "GuessAndCheckers";

        resetHandlerParameters();
        ASPMapper.getInstance().registerClass(DLVMove.class);

        this.logicProgramPath = FileManager.writeFileFromAssetsToExternalStorage(context, fileToWrite, fileName, fileExtension, subDirName);
    }


    @Override
    public void solve(Observer observer) {
        initConditionsToFalse();
        configureJumpsPrompter(context);

        handler.setFilter(DLVMove.class);
        handler.addRawInput("userColor(" + color.getFullLabel() + ").");
        getFactsFromChessboard(((ChessboardImpl) this.chessboard), null, true);
        addFileInputToHandler(logicProgramPath);

        AllMovesCallback allMovesCallback = new AllMovesCallback(this);
        handler.start(this.context, allMovesCallback);
        awaitTillSolved();
        this.moves = parsingToMoves(allMovesCallback.getMoves());
    }

    private void initConditionsToFalse() {
        this.solved = false;
        if (this.moves != null)
            this.moves.clear();
    }

    private List<Move> parsingToMoves(List<DLVMove> moves) {
        List<Move> allMoves = new ArrayList<>();
        for (DLVMove m : moves) {
            List<Cell> moveSteps = new ArrayList<>();
            moveSteps.add(new Cell(m.getSrcRow(), m.getSrcCol()));
            moveSteps.add(new Cell(m.getDstRow(), m.getDstCol()));
            allMoves.add(new ConcreteMove(moveSteps, null));
        }
        return allMoves;
    }

    @Override
    protected void awaitTillSolved() {
        lock.lock();
        try {
            while (!this.solved)
                cond.await();
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

    @Override
    public Object getSolution() {
        return this.moves;
    }

    public void setChessboard(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void setColor(PawnsColor color) {
        this.color = color;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
