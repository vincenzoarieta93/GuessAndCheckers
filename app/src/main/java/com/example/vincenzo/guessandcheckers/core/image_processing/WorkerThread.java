package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.util.Log;

import com.example.vincenzo.guessandcheckers.core.my_exceptions.ChessboardNotFoundException;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.ui.CameraActivity;

import org.opencv.core.Mat;

/**
 * Created by vincenzo on 21/10/2015.
 */
public class WorkerThread extends Thread {

    private CameraController cameraController;
    private ChessboardManager chessboardManager;
    private Chessboard chessboardWithPawns;
    private ChessboardHandler handler;

    public WorkerThread(CameraController core, Chessboard chessboardWithPawns) {
        this.cameraController = core;
        this.chessboardWithPawns = chessboardWithPawns;
        this.chessboardManager = cameraController.getChessboardManager();
        this.handler  = ChessboardHandlerBridge.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            Mat frame = cameraController.getFrame();
            try {
                handler.handleState(CameraActivity.START, null);
                this.chessboardWithPawns = this.chessboardManager.processImage(frame);
                handler.handleState(CameraActivity.SUCCESS, chessboardWithPawns);
                this.cameraController.setFoundChessboard(this.chessboardWithPawns);
            } catch (ChessboardNotFoundException e) {
                handler.handleState(CameraActivity.FAIL, null);
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
