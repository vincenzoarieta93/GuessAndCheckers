package com.example.vincenzo.guessandcheckers.ui;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.AbstractASPManager;
import com.example.vincenzo.guessandcheckers.core.image_processing.CameraController;
import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardForHandler;
import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardHandler;
import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardHandlerBridge;
import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardManager;
import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.image_processing.MyNativeCameraWrapper;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;

import java.io.IOException;


public class CameraActivity extends Activity implements CvCameraViewListener2, ChessboardHandler {

    public static final String INTRO_FIRST_START = "Guess&CheckersFirstStart";

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int START = 2;
    public static final int STOP = -1;
    private static final String CHESSBOARD_LABEL = "CHESSBOARD";

    private Mat mRgba;
    private MyNativeCameraWrapper mOpenCvCameraView;
    private CameraController cameraController;
    private Chessboard chessboardWithPawns = new ChessboardImpl();
    private MyHandler handler;
    private MediaPlayer mMediaPlayer;
    private ChessboardHandler handlerManager;
    private ProgressBar progressBarImgProc;
    private FragmentManager fm = getFragmentManager();
    private ResultDialogFragment dial;
    private TextView whiteIndicator;
    private TextView blackIndicator;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
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

    public void hideUserIndicators() {
        this.whiteIndicator.setVisibility(View.GONE);
        this.blackIndicator.setVisibility(View.GONE);
    }

    public void displayUserIndicators() {
        this.whiteIndicator.setVisibility(View.VISIBLE);
        this.blackIndicator.setVisibility(View.VISIBLE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            chessboardWithPawns = (Chessboard) savedInstanceState.getSerializable(CHESSBOARD_LABEL);

        this.handler = new MyHandler();
        initIntro();
        this.cameraController = new CameraController(new ChessboardManager(this));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        this.progressBarImgProc = (ProgressBar) findViewById(R.id.progress_bar);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/riffic.otf");

        this.whiteIndicator = (TextView) findViewById(R.id.white_indicator);
        this.blackIndicator = (TextView) findViewById(R.id.black_indicator);
        displayUserIndicators();

        this.whiteIndicator.setTypeface(tf);
        this.blackIndicator.setTypeface(tf);

        if (this.progressBarImgProc != null)
            this.progressBarImgProc.setVisibility(View.GONE);

        mOpenCvCameraView = (MyNativeCameraWrapper) findViewById(R.id.tutorial2_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        Mediator.getInstance().setCameraActivity(this);
    }

    private void initIntro() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean(INTRO_FIRST_START, true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(CameraActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean(INTRO_FIRST_START, false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
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
                    hideUserIndicators();
                    dial = ResultDialogFragment.newInstance(chessboardWithPawns);
                    hideUserIndicators();
                    showFragment(dial, ResultDialogFragment.LABEL_CLASS);
                    break;

                case CameraActivity.FAIL:
//                    Log.i(TAG, "No chessboard was found");
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
        Vibrator v = (Vibrator) this.getSystemService(CameraActivity.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            v.vibrate(time);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CHESSBOARD_LABEL, chessboardWithPawns);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        super.onPause();
        try {
            unregisterReceiver(AbstractASPManager.handler);
        } catch (IllegalArgumentException ie) {
            //do nothing
        }
        //Detaching resultDialogFragment make me sure that when activity restored backStack is empty
        Fragment mFragment = this.fm.findFragmentByTag(ResultDialogFragment.LABEL_CLASS);
        if (mFragment != null && !mFragment.isDetached()) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        Fragment mFragment = this.fm.findFragmentByTag(ResultDialogFragment.LABEL_CLASS);
        if (mFragment != null && mFragment.isDetached()) {
            showFragment(mFragment, ResultDialogFragment.LABEL_CLASS);
            Mediator.getInstance().setCameraActivity(this);
        }
    }

    private void showFragment(Fragment mFragment, String fragmentTag) {

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.add(mFragment, fragmentTag);
        ft.attach(mFragment);
        ft.commit();
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
