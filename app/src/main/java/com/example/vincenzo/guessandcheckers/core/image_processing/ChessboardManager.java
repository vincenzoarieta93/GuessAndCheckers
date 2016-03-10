package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.my_exceptions.ChessboardNotFoundException;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.Serializable;

/**
 * Created by vincenzo on 27/09/2015.
 */

public class ChessboardManager implements Serializable {

    public static final int MAX_SIZE_FRAME = 800;
    private Context context;


    public ChessboardManager(Context context) {
        this.context = context;
    }


    public Chessboard processImage(Mat rgbaChessboardImage) throws ChessboardNotFoundException {

        Mat grayImage = new Mat();
        Mat threshChessboard = new Mat();
        Mat simpleGrayChessboardImage = new Mat();

        scaleImage(rgbaChessboardImage, rgbaChessboardImage, MAX_SIZE_FRAME);
        //converts the frame to grayscale and save it into grayImage
        Imgproc.cvtColor(rgbaChessboardImage, grayImage, Imgproc.COLOR_BGRA2GRAY);
        //copies the content of grayImage into simpleGrayChessboardImage
        grayImage.copyTo(simpleGrayChessboardImage);

        //noise removal (x and y of Size must be odd)
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(7, 7), 0);
        Imgproc.adaptiveThreshold(grayImage, threshChessboard, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 2);

        Director director = new Director(new CornersChessboardImplBuilder(new CornersChessboardImplFactory(threshChessboard, simpleGrayChessboardImage)));
        director.buildChessboard();
        //chessboard with corners coordinates
        Chessboard completeCornersChessboard = director.getCompleteChessboard();

        Mat cannyChessboard = new Mat(simpleGrayChessboardImage.rows(), simpleGrayChessboardImage.cols(), CvType.CV_8UC1);
        //increases the sharpness of simpleGrayChessboardImage and save it into cannyChessboard
        Core.addWeighted(simpleGrayChessboardImage, 1.5, cannyChessboard, -0.5, 0, cannyChessboard);

        Mat binaryChessboard = new Mat();
        Imgproc.GaussianBlur(cannyChessboard, binaryChessboard, new Size(7, 7), 0);

//      Transforming binaryChessboard in a binary image. The special value THRESH_OTSU may be combined with THRESH_BINARY.
//      In this case,the function determines the optimal threshold value using the Otsu’s algorithm and uses it instead of the specified thresh .
        double highThresh = Imgproc.threshold(binaryChessboard, binaryChessboard, 85, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        double lowThresh = highThresh*0.5;

        Imgproc.GaussianBlur(cannyChessboard, cannyChessboard, new Size(3, 3), 0);
        Imgproc.Canny(cannyChessboard, cannyChessboard, lowThresh, highThresh);

        Chessboard chessboardWithPawns = new ChessboardImpl();
        //cannyChessboard will be use to determine the contours of pawns
        //binaryChessboard will be use to determine the color of a pawn
        new DispatcherSeekingJobs(context, cannyChessboard, binaryChessboard, chessboardWithPawns, completeCornersChessboard).findPawns();
//        ((ChessboardImpl)chessboardWithPawns).print();

        return chessboardWithPawns;
    }


    private void scaleImage(Mat src, Mat dst, int maxSize) {

        int originalWidth = src.width();
        int originalHeight = src.height();

        float ratio = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
        int newWidth = Math.round(ratio * originalWidth);
        int newHeight = Math.round(ratio * originalHeight);

        Imgproc.resize(src, dst, new Size(newWidth, newHeight), 0, 0, Imgproc.INTER_CUBIC);
    }

}
