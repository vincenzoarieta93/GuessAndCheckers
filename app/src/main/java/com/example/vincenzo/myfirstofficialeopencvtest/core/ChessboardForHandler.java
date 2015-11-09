package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.ui.CameraActivity;

/**
 * Created by vincenzo on 31/10/2015.
 */
public class ChessboardForHandler {

    private Chessboard chessboard;

    public ChessboardForHandler() {
    }

    public void handleState(int state) {
        int outCode;

        switch (state) {
            case WorkerThread.BUILDING_START:
                outCode = CameraActivity.START;
                break;
            case WorkerThread.BUILDING_SUCCESS:
                outCode = CameraActivity.SUCCESS;
                break;
            case WorkerThread.BUILDING_FAILED:
                outCode = CameraActivity.FAIL;
                break;
            default:
                throw new RuntimeException("not supported state");
        }
        ChessboardHandlerBridge.getInstance().handleState(outCode, this);

    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public void setChessboard(Chessboard chessboard) {
        this.chessboard = chessboard;
    }
}
