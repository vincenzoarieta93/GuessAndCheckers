package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.Jump;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.JumpsCheckingCallback;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.Origin;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.ASPPrompter;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.ChooserJumpsPrompter;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Cell;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Pawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.mapper.ASPMapper;

public class JumpsPrompter extends ASPPrompter {

    private static final String TAG = "CICCIO";
    private String logicProgramPath;
    private Context context;
    private ChessboardImpl chessboard;
    private List<Move> jumps;

    private Lock lockProcessingPhase = new ReentrantLock();
    private Condition condProcessingPhase = lockProcessingPhase.newCondition();
    private Boolean solved;

    public JumpsPrompter(ASPHandler handler) {
        this.handler = handler;
    }

    private void configureJumpsPrompter(Context context) {
        String fileToWrite = "dlv/find_jumps_module_with_pawns_status.asp";
        String fileName = "jumps_module";
        String fileExtension = ".txt";
        String subDirName = "GuessAndCheckers";

        handler.resetProgram();
        handler.resetInputFiles();
        handler.resetPredicatesToFilter();
        ASPMapper.getInstance().resetRegisteredClasses();

        ASPMapper.getInstance().registerClass(Origin.class);
        ASPMapper.getInstance().registerClass(Jump.class);

        this.logicProgramPath = FileManager.writeFileFromAssetsToExternalStorage(context, fileToWrite, fileName, fileExtension, subDirName);
    }

    @Override
    public void processMoves(Chessboard chessboard, PawnsColor color, Context context) {
        this.chessboard = ((ChessboardImpl) chessboard);
        this.context = context;
        configureJumpsPrompter(this.context);

        handler.addRawInput("userColor(" + color.getFullLabel() + ").");
        getFactsFromChessboard(this.chessboard);

        initConditionsToFalse();
        Log.i(TAG, "FIND JUMPS()!!!");
        addFileInputToHandler(logicProgramPath);

        JumpsCheckingCallback jumpsCheckingCallback = new JumpsCheckingCallback(this);
        handler.setFilter("origin", "jump");
        handler.start(context, jumpsCheckingCallback);

        awaitTillSolved();

        Log.i(TAG, "obtaining results...");

        List<Origin> jumpingPawns = jumpsCheckingCallback.getJumpingPawns();
        List<Jump> jumps = jumpsCheckingCallback.getJumps();

        handler.resetPredicatesToFilter();
        handler.resetProgram();
        handler.resetInputFiles();

        ASPMapper.getInstance().resetRegisteredClasses();

        List<Move> allJumps = new ArrayList<>();

        if (jumpingPawns.size() > 0) {
            ChooserJumpsPrompter chooser = new ChooserJumpsPrompter(handler, jumpingPawns, jumps, context);
            chooser.processMoves(chessboard, color, context);
            allJumps = chooser.suggestMoves();
            for (Move m : allJumps) {
                for (Cell c : m.getMoveSteps())
                    Log.i(TAG, "step = " + c);
                for (Pawn p : m.getEatenOpponentPawns())
                    Log.i(TAG, "eatenPawn = " + p.getPosition());
            }
        }
        this.jumps = allJumps;
    }

    @Override
    public List<Move> suggestMoves() {
        return this.jumps;
    }


    private void awaitTillSolved() {

        lockProcessingPhase.lock();
        try {
            while (!this.solved)
                condProcessingPhase.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockProcessingPhase.unlock();
        }
    }

    public void notifySolved() {
        lockProcessingPhase.lock();
        try {
            Log.i(TAG, "notify");
            this.solved = true;
            Toast.makeText(context, "DLV HAS FINISHED", Toast.LENGTH_LONG).show();
            condProcessingPhase.signalAll();
        } finally {
            lockProcessingPhase.unlock();
        }
    }


    private void initConditionsToFalse() {
        solved = false;
    }

}
