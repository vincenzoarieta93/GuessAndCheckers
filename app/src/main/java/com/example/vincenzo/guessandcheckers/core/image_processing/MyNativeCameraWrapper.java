package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;

import java.util.List;

/**
 * Created by vincenzo on 27/10/2015.
 */
@SuppressWarnings("deprecation")
public class MyNativeCameraWrapper extends JavaCameraView {


    public MyNativeCameraWrapper(Context context, int cameraId) {
        super(context, cameraId);
    }

    public MyNativeCameraWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void releaseCamera(){
        super.releaseCamera();
    }

    @Override
    protected boolean connectCamera(int width, int height) {
        return super.connectCamera(width,height);
    }


    @Override
    protected void disconnectCamera() {
        super.disconnectCamera();
    }

    @Override
    public void onPreviewFrame(byte[] frame, Camera arg1) {
        super.onPreviewFrame(frame, arg1);
    }

    @Override
    protected boolean initializeCamera(int width, int height) {
       boolean ret = super.initializeCamera(width,height);
//        enableFocus();
        return ret;
    }


    public void enableFocus(){
        Camera.Parameters params = mCamera.getParameters();
        List<String> FocusModes = params.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            Log.i("CICCIOPASTICCIO", "enabling focus");
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(params);
    }
}
