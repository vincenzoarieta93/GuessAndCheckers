package com.example.vincenzo.guessandcheckers.core.image_processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by vincenzo on 23/10/2015.
 */
public class ImgprocWrapper {

    /**
     * This method wraps the Imgproc.HoughCircles method within a synchronized block.
     * In this way, the method can be called by different threads at the same time.
     *
     * @param image 8-bit, single-channel, grayscale input image.
     * @param circles Output vector of found circles. Each vector is encoded as a 3-element floating-point vector  (x, y, radius) .
     * @param method Detection method to use. Currently, the only implemented method is CV_HOUGH_GRADIENT
     * @param dp Inverse ratio of the accumulator resolution to the image resolution. For example, if dp=1 , the accumulator has the same resolution as the input image. If dp=2 , the accumulator has half as big width and height.
     * @param minDist Minimum distance between the centers of the detected circles. If the parameter is too small, multiple neighbor circles may be falsely detected in addition to a true one. If it is too large, some circles may be missed.
     * @param param1 First method-specific parameter. In case of CV_HOUGH_GRADIENT , it is the higher threshold of the two passed to the Canny() edge detector (the lower one is twice smaller).
     * @param param2 Second method-specific parameter. In case of CV_HOUGH_GRADIENT , it is the accumulator threshold for the circle centers at the detection stage. The smaller it is, the more false circles may be detected. Circles, corresponding to the larger accumulator values, will be returned first.
     * @param minRadius Minimum circle radius.
     * @param maxRadius Maximum circle radius.
     */
    public synchronized static void HoughCircles(Mat image, Mat circles, int method, double dp, double minDist, double param1, double param2, int minRadius, int maxRadius){
        Imgproc.HoughCircles(image, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
    }
}
