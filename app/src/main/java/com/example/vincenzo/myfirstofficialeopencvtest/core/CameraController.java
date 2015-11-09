package com.example.vincenzo.myfirstofficialeopencvtest.core;

import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.ui.CameraActivity;

import org.opencv.core.Mat;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by vincenzo on 21/10/2015.
 */
public class CameraController {

    private boolean captureEnabled;
    private Mat frame;
    private ChessboardManager chessboardManager;
    private Chessboard chessboardWithPawns;
    private WorkerThread worker;
    private CameraActivity.MyHandler handler;

    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    public CameraController(ChessboardManager chessboardManager, CameraActivity.MyHandler handler) {
        this.chessboardManager = chessboardManager;
        this.captureEnabled = false;
        this.worker = new WorkerThread(this, this.chessboardWithPawns, handler);
        this.worker.start();
    }

    //invoked in camera activity by click on monitor
    public boolean toggleCaptureEnabled() {
        lock.lock();
        try {
            this.captureEnabled = !captureEnabled;
            Log.i("CICCIOPASTICCIO", "captureEnabled = " + captureEnabled);
            if (captureEnabled) {
                this.frame = null;
            }else{
                reset();
            }
            cond.signalAll();
            return this.captureEnabled;
        } finally {
            lock.unlock();
        }
    }

    //invoked by worker-thread when built a logic matrix
    public void setFoundChessboard(Chessboard matrix) {
        lock.lock();
        try {
            //it only signals that chessboardWithPawns is not null anymore
            this.chessboardWithPawns = matrix;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //invoked by cameraActivity
    public void addFrame(Mat frame) {
        lock.lock();
        try {
            if (this.frame != null)
                this.frame.release();
            this.frame = frame;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //invoked by worker-thread
    public Mat getFrame() {
        lock.lock();
        try {
            while (frame == null || !captureEnabled || this.chessboardWithPawns!=null) {
                try {
                    cond.await();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            Log.i("CICCIOPASTICCIO", " got frame");
            Mat copyOfFrame = this.frame;
            this.frame = null;
            cond.signalAll();
            return copyOfFrame;
        } finally {
            lock.unlock();
        }
    }


    public void reset() {
        lock.lock();
        try {
            this.captureEnabled = false;
            this.chessboardWithPawns = null;
            this.frame = null;
        } finally {
            lock.unlock();
        }
    }

    public ChessboardManager getChessboardManager() {
        return this.chessboardManager;
    }
}
