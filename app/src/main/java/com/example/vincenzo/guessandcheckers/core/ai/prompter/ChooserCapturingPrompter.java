package com.example.vincenzo.guessandcheckers.core.ai.prompter;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.ai.bean.Jump;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.ASPPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter_callback.MakeJumpsCallback;
import com.example.vincenzo.guessandcheckers.core.ai.bean.OrderedJumpStep;
import com.example.vincenzo.guessandcheckers.core.ai.bean.Origin;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardItem;
import com.example.vincenzo.guessandcheckers.core.game_objects.ConcreteMove;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;
import com.example.vincenzo.guessandcheckers.core.game_objects.Pawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;
import com.example.vincenzo.guessandcheckers.core.suggesting.Observer;
import com.example.vincenzo.guessandcheckers.core.support_libraries.AIModulesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.embasp.mapper.ASPMapper;

/**
 * Created by vincenzo on 21/12/2015.
 */
public class ChooserCapturingPrompter extends ASPPrompter {

    public static final String TAG = "CICCIO";
    private List<Origin> jumpingPawns;
    private List<Jump> jumps;
    private Context context;
    private List<Move> bestJumps = null;
    private Set<Integer> idJumpingPawns = new HashSet<>();
    private HashMap<Cell, Integer> cellsMap = new HashMap<>();
    private HashMap<Integer, Cell> reverseMapping = new HashMap<>();
    private Lock lockResults = new ReentrantLock();
    private Condition condResults = lockResults.newCondition();
    private Boolean solved = false;
    private ChessboardImpl chessboard;
    private String logicProgramPath;

    public ChooserCapturingPrompter(ChessboardImpl chessboard, Context context, List<Origin> jumpingPawns, List<Jump> jumps) {
        this.chessboard = chessboard;
        this.context = context;
        this.jumpingPawns = jumpingPawns;
        this.jumps = jumps;
        AIModulesProvider aiProvider = AIModulesProvider.getInstance();
        this.logicProgramPath = aiProvider.BEST_JUMPS_MODULE_PATH;
    }

    @Override
    public void solve(Observer observer) {
        initConditionsToFalse();
        this.bestJumps = makeJumps();
    }

    private void initConditionsToFalse() {
        this.solved = false;
        this.cellsMap.clear();
        this.reverseMapping.clear();
        this.idJumpingPawns.clear();
    }

    @Override
    public Object getSolution() {
        return this.bestJumps;
    }


    private List<Move> makeJumps() {

        ASPMapper.getInstance().registerClass(OrderedJumpStep.class);

        generateFactsForChoosingCapturingModule(jumpingPawns, jumps, this.logicProgramPath);

        if(jumps.size() > 100)
            handler.addOption("-n=1");

        handler.setFilter(OrderedJumpStep.class);
        addFileInputToHandler(this.logicProgramPath);

        MakeJumpsCallback makeJumpsCallback = new MakeJumpsCallback(this);
        handler.start(context, makeJumpsCallback);

        awaitTillSolved();

        List<List<OrderedJumpStep>> orderedSteps = makeJumpsCallback.getOrderedJumps();
        List<Move> allJumps = new ArrayList<>();

        for (List<OrderedJumpStep> steps : orderedSteps)
            allJumps.add(parseOrderedJumpSteps(steps));

        return allJumps;

    }

    @Override
    protected void awaitTillSolved() {
        lockResults.lock();
        try {
            while (!this.solved) {
                condResults.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockResults.unlock();
        }
    }

    @Override
    public void signalSolved() {
        lockResults.lock();
        try {
            this.solved = true;
            condResults.signalAll();
        } finally {
            lockResults.unlock();
        }
    }

    /**
     * Parse all jump's steps into a list of cell. It executes a cellsMap from the predicates of the answerset to real cell coordinates
     *
     * @param orderedSteps predicates of answer set which represent the steps of a jump
     * @return the parsed ordered steps of a jump
     */
    private Move parseOrderedJumpSteps(List<OrderedJumpStep> orderedSteps) {
        List<Cell> moveSteps = new ArrayList<>();
        List<Pawn> eatenOpponentPawns = new ArrayList<>();

        Collections.sort(orderedSteps, new Comparator<OrderedJumpStep>() {
            @Override
            public int compare(OrderedJumpStep o1, OrderedJumpStep o2) {
                if (o1.getOrder() < o2.getOrder())
                    return -1;
                else
                    return 1;
            }
        });
        int counter = 0;
        for (OrderedJumpStep o : orderedSteps) {

            Cell srcCell = reverseMapping.get(o.getSrcNode());
            Cell dstCell = reverseMapping.get(o.getDstNode());

            if (counter == 0) {
                moveSteps.add(srcCell);
                counter++;
            }

            moveSteps.add(dstCell);
            findEatenOpponentsPawn(srcCell, dstCell, eatenOpponentPawns);
        }
        return new ConcreteMove(moveSteps, eatenOpponentPawns);
    }

    private void findEatenOpponentsPawn(Cell srcCell, Cell dstCell, List<Pawn> eatenOpponentPawns) {

        ChessboardItem middleItem = chessboard.getMiddleItem(srcCell, dstCell);
        if (middleItem instanceof Pawn)
            eatenOpponentPawns.add((Pawn) middleItem);
    }


    private void generateFactsForChoosingCapturingModule(List<Origin> jumpingPawns, List<Jump> jumps, String filePath) {
        int nodeCounter = 0;

        for (Origin j : jumpingPawns) {
            idJumpingPawns.add(j.getId());
            Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
            if (cellsMap.get(c) == null) {
                cellsMap.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                String statusPawn = "";
                if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhiteDama || chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackDama)
                    statusPawn = "king";
                else if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhitePawn || chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackPawn)
                    statusPawn = "man";
                handler.addRawInput("rootNode(" + nodeCounter + "," + j.getId() + "," + statusPawn + ").");
                nodeCounter++;
            }
        }
        //assignment id to nodes
        for (Jump j : jumps) {
            Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
            if (cellsMap.get(c) == null) {
                cellsMap.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                nodeCounter++;
            }

            c = new Cell(j.getNextRow(), j.getNextCol());

            if (cellsMap.get(c) == null) {
                cellsMap.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                nodeCounter++;
            }
        }
        //creating arch facts
        for (Jump j : jumps) {
            String pawnStatus = j.getJumpedPawnStatus();
            int numStep = j.getNumStep();
            handler.addRawInput("arch(" + cellsMap.get(new Cell(j.getStartingRow(), j.getStartingCol())) + "," + cellsMap.get(new Cell(j.getNextRow(), j.getNextCol())) + "," + j.getId() + "," + pawnStatus + "," + numStep + ").");
        }
    }
}
