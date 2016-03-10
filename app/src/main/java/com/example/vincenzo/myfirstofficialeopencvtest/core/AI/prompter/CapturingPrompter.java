package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.bean.Jump;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.base.ASPPrompterDumb;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter_callback.JumpsCheckingCallback;
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

public class CapturingPrompter extends ASPPrompterDumb {

    private static final String TAG = "CICCIO";
    private String logicProgramPath;
    private Context context;
    private ChessboardImpl chessboard;
    private List<Move> jumps;
    private PawnsColor color;


    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private Boolean solved;

    public CapturingPrompter(Context context, ChessboardImpl chessboardImpl, PawnsColor color) {
        this.context = context;
        this.chessboard = chessboardImpl;
        this.color = color;
        this.logicProgramPath = AIModulesProvider.getInstance().JUMPS_MODULE_PATH;
    }

    private void configureJumpsPrompter() {
        resetHandlerParameters();
        ASPMapper.getInstance().registerClass(Origin.class);
        ASPMapper.getInstance().registerClass(Jump.class);
    }

    @Override
    public void solve() {
        configureJumpsPrompter();

        handler.addRawInput("userColor(" + color.getFullLabel() + ").");
        getFactsFromChessboard(this.chessboard, null, true);

        initConditionsToFalse();
        addFileInputToHandler(logicProgramPath);

        JumpsCheckingCallback jumpsCheckingCallback = new JumpsCheckingCallback(this);
        handler.setFilter("origin", "jump");
        handler.start(context, jumpsCheckingCallback);

        awaitTillSolved();

        List<Origin> jumpingPawns = jumpsCheckingCallback.getJumpingPawns();
        List<Jump> jumps = jumpsCheckingCallback.getJumps();

        resetHandlerParameters();

        List<Move> allJumps = new ArrayList<>();

        if (jumpingPawns.size() > 0) {

            ChooserJumpsPrompter chooser = new ChooserJumpsPrompter(chessboard, context, jumpingPawns, jumps);
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