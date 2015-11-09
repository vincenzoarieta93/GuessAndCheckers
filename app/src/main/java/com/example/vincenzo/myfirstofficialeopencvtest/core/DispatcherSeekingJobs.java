package com.example.vincenzo.myfirstofficialeopencvtest.core;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;

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

        int threadReali = NUM_CORES;//0,252
        Log.i(TAG, "CORES = " + threadReali);

        int fetta = (MAX - MIN + 1) / threadReali;// 4

        while (fetta == 0) {
            threadReali--;
            fetta = (MAX - MIN + 1) / threadReali;
        }
        CyclicBarrier barrier = new CyclicBarrier(threadReali + 1);


        // creazione e invocazione thread.
        PawnsSeeker[] workers = new PawnsSeeker[threadReali];
        for (int i = 0; i < threadReali - 1; i++) {
            int min = MIN + i * fetta;
            int max = min + fetta - 1;
            workers[i] = new PawnsSeeker(result, min, max, barrier, this.context, this.cannyChessboard, this.binaryChessboard, this.cornersChessboard);
            workers[i].start();
        }
        workers[threadReali - 1] = new PawnsSeeker(result, MIN + (threadReali - 1) * fetta, MAX - 1, barrier, this.context, this.cannyChessboard, this.binaryChessboard, this.cornersChessboard);
        workers[threadReali - 1].start();

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
