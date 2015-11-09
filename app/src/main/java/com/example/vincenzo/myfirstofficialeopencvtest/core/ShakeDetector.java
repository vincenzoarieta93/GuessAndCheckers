package com.example.vincenzo.myfirstofficialeopencvtest.core;

/**
 * Created by vincenzo on 29/10/2015.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    // Minimum acceleration needed to count as a shake movement
    private static final int MIN_SHAKE_ACCELERATION = 3;

    // Minimum number of movements to register a shake
    private static final int MIN_MOVEMENTS = 5;

    // Maximum time (in milliseconds) for the whole shake to occur
    private static final int MAX_SHAKE_DURATION = 800;

    // Arrays to store gravity and linear acceleration values
    private float[] mGravity = {0.0f, 0.0f, 0.0f};
    private float[] mLinearAcceleration = {0.0f, 0.0f, 0.0f};

    // Indexes for x, y, and z values
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    // OnShakeListener that will be notified when the shake is detected
    private OnShakeListener mShakeListener;

    // Start time for the shake detection
    long startTime = 0;

    // Counter for shake movements
    int moveCount = 0;

    // Constructor that sets the shake listener
    public ShakeDetector() {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This method will be called when the accelerometer detects a change.
        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event);

        // Get the max linear acceleration in any direction
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            long now = System.currentTimeMillis();

            // Set the startTime if it was reset to zero
            if (startTime == 0) {
                startTime = now;
            }

            long elapsedTime = now - startTime;

            // Check if we're still in the shake window we defined
            if (elapsedTime > MAX_SHAKE_DURATION) {
                // Too much time has passed. Start over!
                resetShakeDetection();
            } else {
                // Keep track of all the movements
                moveCount++;

                // Check if enough movements have been made to qualify as a shake
                if (moveCount > MIN_MOVEMENTS) {
                    // It's a shake! Notify the listener.
                    mShakeListener.onShake();

                    // Reset for the next one!
                    resetShakeDetection();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Intentionally blank
    }

    private void setCurrentAcceleration(SensorEvent event) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;

        // Gravity components of x, y, and z acceleration
        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        mLinearAcceleration[X] = event.values[X] - mGravity[X];
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];
    }

    private float getMaxCurrentLinearAcceleration() {
        // Start by setting the value to the x value
        float maxLinearAcceleration = mLinearAcceleration[X];

        // Check if the y value is greater
        if (mLinearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Y];
        }

        // Check if the z value is greater
        if (mLinearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Z];
        }

        // Return the greatest value
        return maxLinearAcceleration;
    }

    private void resetShakeDetection() {
        startTime = 0;
        moveCount = 0;
    }


    public void setOnShakeListener(OnShakeListener listener){
        this.mShakeListener = listener;
    }

    /*
     * Definition for OnShakeListener definition. I would normally put this
     * into it's own .java file, but I included it here for quick reference
     * and to make it easier to include this file in our project.
     */
    public interface OnShakeListener {
        public void onShake();
    }
}

//call this method within onCreate of CameraActivity
//CameraActivity must implement ShakeDetector.OnShakeListener
//private void initializeShakeDetector() {
//    // ShakeDetector initialization
//    mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
//    mAccelerometer = mSensorManager
//            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    mShakeDetector = new ShakeDetector();
//    mShakeDetector.setOnShakeListener(this);
//}

//add the following members
//private SensorManager mSensorManager;
//private Sensor mAccelerometer;
//private ShakeDetector mShakeDetector;

//inside onPause put
//mSensorManager.unregisterListener(mShakeDetector);

//inside onResume put
// mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
//i manifest put
// <uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />