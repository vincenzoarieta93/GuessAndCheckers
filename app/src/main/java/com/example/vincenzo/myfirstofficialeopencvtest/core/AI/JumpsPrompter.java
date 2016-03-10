package com.example.vincenzo.myfirstofficialeopencvtest.core.AI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Cell;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardItem;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ConcreteMove;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Pawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhiteDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.FileManager;

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

import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.mapper.ASPMapper;

public class JumpsPrompter extends ASPPrompter {

    private static final String TAG = "CICCIO";
    private String logicProgramPath;
    private Context context;
    private ChessboardImpl chessboard;
    private Set<Integer> idJumpingPawns = new HashSet<>();
    private HashMap<Cell, Integer> mapping = new HashMap<>();
    private HashMap<Integer, Cell> reverseMapping = new HashMap<>();
    private List<Move> jumps;

    private Lock lockProcessingPhase = new ReentrantLock();
    private Lock lockResults = new ReentrantLock();
    private Condition condResults = lockResults.newCondition();
    private Condition condProcessingPhase = lockProcessingPhase.newCondition();
    private Boolean jumpsChecking;
    private Boolean jumpChosen;

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
        handler.resetPredicatesToFilter();

        awaitNextPhase(true);

        Log.i(TAG, "obtaining results...");

        List<Origin> jumpingPawns = jumpsCheckingCallback.getJumpingPawns();
        List<Jump> jumps = jumpsCheckingCallback.getJumps();
        handler.resetProgram();
        handler.resetInputFiles();
        ASPMapper.getInstance().resetRegisteredClasses();

        List<Move> allJumps = new ArrayList<>();

        if (jumpingPawns.size() > 0) {
            allJumps = makeJumps(jumpingPawns, jumps, context);
            for (Move m : allJumps) {
                for (Cell c : m.getMoveSteps())
                    Log.i(TAG, "step = " + c);
                for (Pawn p : m.getEatenOpponentPawns())
                    Log.i(TAG, "eatenPawn = " + p.getPosition());
            }
        }
        this.jumps = allJumps;
        signalResults();
    }

    private void signalResults() {
        lockResults.lock();
        try {
            condResults.signalAll();
            Log.i(TAG, "found results");
        } finally {
            lockResults.unlock();
        }
    }

    @Override
    public List<Move> suggestMoves() {
        lockResults.lock();
        try {
            while (this.jumps == null) {
                Log.i(TAG, "results not found yet");
                condResults.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockResults.unlock();
        }
        return this.jumps;
    }


    private void awaitNextPhase(boolean varStatus) {

        lockProcessingPhase.lock();
        try {

            while (varStatus && !this.jumpsChecking)
                condProcessingPhase.await();
            while (!varStatus && !this.jumpChosen)
                condProcessingPhase.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockProcessingPhase.unlock();
        }
    }

    public void notifyPhaseCond(boolean varStatus) {
        lockProcessingPhase.lock();
        try {
            Log.i(TAG, "notify");
            if (varStatus)
                this.jumpsChecking = true;
            else {
                this.jumpChosen = true;
                Toast.makeText(context, "DLV HAS FINISHED", Toast.LENGTH_LONG).show();
            }
            condProcessingPhase.signalAll();
        } finally {
            lockProcessingPhase.unlock();
        }
    }


    private List<Move> makeJumps(List<Origin> jumpingPawns, List<Jump> jumps, Context context) {

        String fileToWrite = "dlv/choose_jump.asp";
        String fileName = "make_jumps_module";
        String fileExtension = ".txt";
        String subDirName = "GuessAndCheckers";
        ASPMapper.getInstance().registerClass(OrderedJumpStep.class);

        String filePath = FileManager.writeFileFromAssetsToExternalStorage(context, fileToWrite, fileName, fileExtension, subDirName);

        generateFactsForChooseJumpModule(jumpingPawns, jumps, filePath);

        handler.setFilter("orderedJumpStep");
        addFileInputToHandler(filePath);

        MakeJumpsCallback makeJumpsCallback = new MakeJumpsCallback(this);
        handler.start(context, makeJumpsCallback);

        awaitNextPhase(false);

        List<List<OrderedJumpStep>> orderedSteps = makeJumpsCallback.getOrderedJumps();
        List<Move> allJumps = new ArrayList<>();

        for (List<OrderedJumpStep> steps : orderedSteps)
            allJumps.add(parseOrderedJumpSteps(steps));

        return allJumps;

    }

    /**
     * Parse all jump's steps into a list of cell. It executes a mapping from the predicates of the answerset to real cell coordinates
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


    private void generateFactsForChooseJumpModule(List<Origin> jumpingPawns, List<Jump> jumps, String filePath) {
        int nodeCounter = 0;

        for (Origin j : jumpingPawns) {
            idJumpingPawns.add(j.getId());
            Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
            if (mapping.get(c) == null) {
                mapping.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                handler.addRawInput("rootNode(" + nodeCounter + "," + j.getId() + ").");
                String statusPawn = "";
                if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhitePawn || chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackPawn)
                    statusPawn = "man";
                else if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhiteDama || chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackDama)
                    statusPawn = "king";
                handler.addRawInput("pawn(" + nodeCounter + "," + statusPawn + ").");
                nodeCounter++;
            }
        }
        //assignment id to nodes
        for (Jump j : jumps) {
            Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
            if (mapping.get(c) == null) {
                mapping.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                nodeCounter++;
            }

            c = new Cell(j.getNextRow(), j.getNextCol());

            if (mapping.get(c) == null) {
                mapping.put(c, nodeCounter);
                reverseMapping.put(nodeCounter, c);
                nodeCounter++;
            }
        }
        //creating arch facts
        for (Jump j : jumps) {
            String pawnStatus = j.getJumpedPawnStatus();
            handler.addRawInput("arch(" + mapping.get(new Cell(j.getStartingRow(), j.getStartingCol())) + "," + mapping.get(new Cell(j.getNextRow(), j.getNextCol())) + "," + j.getId() + "," + pawnStatus + ").");
        }
    }

    private void initConditionsToFalse() {
        jumpsChecking = false;
        jumpChosen = false;
    }

}
