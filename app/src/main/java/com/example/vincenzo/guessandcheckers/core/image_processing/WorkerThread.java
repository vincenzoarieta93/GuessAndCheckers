package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.util.Log;

import com.example.vincenzo.guessandcheckers.core.my_exceptions.ChessboardNotFoundException;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

import org.opencv.core.Mat;

/**
 * Created by vincenzo on 21/10/2015.
 */
public class WorkerThread extends Thread {

    private CameraController cameraController;
    private ChessboardManager chessboardManager;
    private Chessboard chessboardWithPawns;
    public static final int BUILDING_SUCCESS = 1;
    public static final int BUILDING_FAILED = 0;
    public static final int BUILDING_START = 2;

    public WorkerThread(CameraController core, Chessboard chessboardWithPawns) {
        this.cameraController = core;
        this.chessboardWithPawns = chessboardWithPawns;
        this.chessboardManager = cameraController.getChessboardManager();
    }

    @Override
    public void run() {
        ChessboardForHandler obj = new ChessboardForHandler();
        while (true) {
            Mat frame = cameraController.getFrame();
            try {
                obj.setChessboard(null);
                obj.handleState(BUILDING_START);
                this.chessboardWithPawns = this.chessboardManager.processImage(frame);
                obj.setChessboard(this.chessboardWithPawns);
                obj.handleState(BUILDING_SUCCESS);
                this.cameraController.setFoundChessboard(this.chessboardWithPawns);
            } catch (ChessboardNotFoundException e) {
                obj.setChessboard(null);
                obj.handleState(BUILDING_FAILED);
                Log.i("CICCIOPASTICCIO", "not chessboard was found");
            }
            quietlySleep(200);
        }
    }

    private void quietlySleep(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
