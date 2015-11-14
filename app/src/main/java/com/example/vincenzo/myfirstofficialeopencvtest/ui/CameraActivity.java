package com.example.vincenzo.myfirstofficialeopencvtest.ui;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;

import android.app.Activity;
import android.app.FragmentManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vincenzo.myfirstofficialeopencvtest.core.CameraController;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardForHandler;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardHandler;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardHandlerBridge;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardManager;
import com.example.vincenzo.myfirstofficialeopencvtest.R;
import com.example.vincenzo.myfirstofficialeopencvtest.core.MyNativeCameraWrapper;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;

import java.io.IOException;


public class CameraActivity extends Activity implements CvCameraViewListener2, ChessboardHandler {

    private static final String TAG = "CICCIOPASTICCIO";
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int START = 2;
    public static final int STOP = -1;

    private Mat mRgba;
    private MyNativeCameraWrapper mOpenCvCameraView;
    private CameraController cameraController;
    private Chessboard chessboardWithPawns;
    private MyHandler handler;
    private MediaPlayer mMediaPlayer;
    private ChessboardHandler handlerManager;
    private ProgressBar progressBarImgProc;
    FragmentManager fm = getFragmentManager();


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraActivity() {
        this.handlerManager = ChessboardHandlerBridge.getInstance();
        this.handlerManager.setNext(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new MyHandler();

        this.cameraController = new CameraController(new ChessboardManager(this), this.handler);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        this.progressBarImgProc = (ProgressBar) findViewById(R.id.progress_bar);

        if (this.progressBarImgProc != null) {
            this.progressBarImgProc.setVisibility(View.GONE);
        }

        mOpenCvCameraView = (MyNativeCameraWrapper) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }


    @Override
    public void handleState(int outCode, ChessboardForHandler chessboardForHandler) {
        sendMessageToHandler(outCode, chessboardForHandler);
    }

    private void sendMessageToHandler(int CODE, ChessboardForHandler obj) {
        Message msg = handler.obtainMessage(CODE, obj);
        msg.sendToTarget();
    }


    @Override
    public void setNext(ChessboardHandler handler) {
        //do nothing
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case CameraActivity.START:
                    progressBarImgProc.setVisibility(View.VISIBLE);
                    break;

                case CameraActivity.STOP:
                    progressBarImgProc.setVisibility(View.GONE);
                    break;

                case CameraActivity.SUCCESS:
                    ChessboardForHandler c = (ChessboardForHandler) msg.obj;
                    chessboardWithPawns = c.getChessboard();
                    vibrate(300);
                    progressBarImgProc.setVisibility(View.GONE);
                    playAudio(Uri.parse("android.resource://" + "com.example.vincenzo.myfirstofficialeopencvtest" + "/" + R.raw.capture_on));
                    ResultDialogFragment dial = ResultDialogFragment.newInstance(chessboardWithPawns);
                    dial.setStyle(0, R.style.MyAlertDialogTheme);
                    dial.show(fm, "CICCIO");
                    break;

                case CameraActivity.FAIL:
                    Log.i(TAG, "No chessboard was found");
                    break;
            }
        }
    }

    private void playAudio(Uri uri) {
        killMediaPlayer();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void vibrate(int time) {
        Vibrator v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            v.vibrate(time);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    //It's a thread which captures camera frames
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        cameraController.addFrame(mRgba.clone());
        return mRgba;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                boolean isCaptureActive = this.cameraController.toggleCaptureEnabled();
                if (isCaptureActive)
                    Toast.makeText(this, "Searching for Chessboard...", Toast.LENGTH_SHORT).show();
                else {
                    sendMessageToHandler(STOP, null);
                    Toast.makeText(this, "Stop Searching", Toast.LENGTH_SHORT).show();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }

    public void reset() {
        this.cameraController.reset();
    }

}
