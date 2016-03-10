package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;

import org.opencv.core.Mat;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by vincenzo on 23/10/2015.
 */
public class DispatcherSeekingJobs {

    public static final String TAG = "CICCIOPASTICCIO";
    private Context context;
    private Mat cannyChessboard;
    private Mat binaryChessboard;
    private Chessboard result;
    private Chessboard cornersChessboard;
    private final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    public DispatcherSeekingJobs(Context context, Mat cannyChessboard, Mat binaryChessboard, Chessboard chessboardWithPawns, Chessboard completeCornersChessboard) {
        this.context = context;
        this.cannyChessboard = cannyChessboard;
        this.binaryChessboard = binaryChessboard;
        this.result = chessboardWithPawns;
        this.cornersChessboard = completeCornersChessboard;
    }


    public Chessboard findPawns() {

        int MAX = ChessboardImpl.LENGTH;
        int MIN = 0;

        int realThreads = NUM_CORES;//0,252
//        Log.i(TAG, "CORES = " + threadReali);

        int slice = (MAX - MIN + 1) / realThreads;// 4

        while (slice == 0) {
            realThreads--;
            slice = (MAX - MIN + 1) / realThreads;
        }
        CyclicBarrier barrier = new CyclicBarrier(realThreads + 1);


        //threads creation and invocation
        PawnsSeeker[] workers = new PawnsSeeker[realThreads];
        for (int i = 0; i < realThreads - 1; i++) {
            int min = MIN + i * slice;
            int max = min + slice - 1;
            workers[i] = new PawnsSeeker(result, min, max, barrier, this.context, this.cannyChessboard, this.binaryChessboard, this.cornersChessboard);
            workers[i].start();
        }
        workers[realThreads - 1] = new PawnsSeeker(result, MIN + (realThreads - 1) * slice, MAX - 1, barrier, this.context, this.cannyChessboard, this.binaryChessboard, this.cornersChessboard);
        workers[realThreads - 1].start();

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        return this.result;
    }

}
