package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter;

import android.content.Context;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.bean.Jump;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.base.ASPPrompter;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter_callback.CapturingCheckingCallback;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.bean.Origin;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.AIModulesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.embasp.mapper.ASPMapper;

public class CapturingManager extends ASPPrompter {

    private static final String TAG = "CICCIO";
    private String logicProgramPath;
    private Context context;
    private ChessboardImpl chessboard;
    private List<Move> jumps;
    private PawnsColor color;


    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private Boolean solved;

    public CapturingManager(Context context, ChessboardImpl chessboardImpl, PawnsColor color) {
        this.context = context;
        this.chessboard = chessboardImpl;
        this.color = color;
        this.logicProgramPath = AIModulesProvider.getInstance().JUMPS_MODULE_PATH;
    }

    private void configureCapturingPrompter() {
        resetHandlerParameters();
        ASPMapper.getInstance().registerClass(Origin.class);
        ASPMapper.getInstance().registerClass(Jump.class);
    }

    @Override
    public void solve() {
        configureCapturingPrompter();

        handler.addRawInput("userColor(" + color.getFullLabel() + ").");
        getFactsFromChessboard(this.chessboard, null, true);

        initConditionsToFalse();
        addFileInputToHandler(logicProgramPath);

        CapturingCheckingCallback capturingCheckingCallback = new CapturingCheckingCallback(this);
        handler.setFilter("origin", "jump");
        handler.start(context, capturingCheckingCallback);

        awaitTillSolved();

        List<Origin> jumpingPawns = capturingCheckingCallback.getJumpingPawns();
        List<Jump> jumps = capturingCheckingCallback.getJumps();

        resetHandlerParameters();

        List<Move> allJumps = new ArrayList<>();

        if (jumpingPawns.size() > 0) {

            ChooserCapturingManager chooser = new ChooserCapturingManager(chessboard, context, jumpingPawns, jumps);
            chooser.solve();

            allJumps = (List<Move>) chooser.getSolution();
        }
        this.jumps = allJumps;
    }

    @Override
    public Object getSolution() {
        return this.jumps;
    }


    @Override
    protected void awaitTillSolved() {

        lock.lock();
        try {
            while (!this.solved) {
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
//            Toast.makeText(context, "DLV HAS FINISHED", Toast.LENGTH_SHORT).show();
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }


    private void initConditionsToFalse() {
        solved = false;
    }

}