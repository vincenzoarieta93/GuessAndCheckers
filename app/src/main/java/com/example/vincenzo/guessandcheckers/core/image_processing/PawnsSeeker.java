package com.example.vincenzo.guessandcheckers.core.image_processing;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardItem;
import com.example.vincenzo.guessandcheckers.core.game_objects.EmptyTile;
import com.example.vincenzo.guessandcheckers.core.game_objects.Pawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by vincenzo on 14/10/2015.
 */
public class PawnsSeeker extends Thread {

    public static final String TAG = "CICCIOPASTICCIO";
    private Chessboard result;
    private Mat cannyChessboard;
    private Mat binaryChessboard;
    private CyclicBarrier barrier;
    private Chessboard completeCornersChessboard;
    private int min;
    private int max;
    private Context context;


    public PawnsSeeker(Chessboard result, int min, int max, CyclicBarrier barrier, Context context, Mat cannyChessboard, Mat binaryChessboard, Chessboard cornersCornersChessboard) {
        this.cannyChessboard = cannyChessboard;
        this.binaryChessboard = binaryChessboard;
        this.barrier = barrier;
        this.context = context;
        this.result = result;
        this.completeCornersChessboard = cornersCornersChessboard;
        this.min = min;
        this.max = max;
        initPawnsCoords();
    }

    /**
     * initialize the real Chessboard to empty
     */
    private void initPawnsCoords() {

        EmptyTile tile = new EmptyTile();

        for (int i = min; i < max + 1; i++)
            for (int j = 0; j < ChessboardImpl.LENGTH; j++)
                try {
                    EmptyTile clone = (EmptyTile) tile.clone();
                    clone.setPosition(i, j);
                    result.setCell(i, j, clone);
                } catch (CloneNotSupportedException c) {
                    c.printStackTrace();
                }
    }

    /**
     * For each cell of the chessboardImage, this method checks  if a pawn is present.
     * The i-th j-th cell of the chessboardImage is found according to the values of the i-th j-th cell of completeCornersChessboard.
     */
    private void findPawns() {

        Mat cannySquare;
        Mat binarySquare;
        Rect rect;

        for (int i = min; i < max + 1; i++) {
            for (int j = 0; j < completeCornersChessboard.getLength(); j++) {
                SquareCoords ptt = (SquareCoords) completeCornersChessboard.getCell(i, j);
                rect = new Rect(ptt.getLowerLeftCorner(), ptt.getUpperRightCorner());
                cannySquare = cannyChessboard.submat(rect);
                binarySquare = binaryChessboard.submat(rect);
//                Log.i("CICCIO", "mat[" + i + "][" + j + "]");
                ChessboardItem item = findPawn(cannySquare, binarySquare);
                item.setPosition(i, j);
                result.setCell(i, j, item);
                cannySquare.release();
                binarySquare.release();
            }
        }
    }

    /**
     * This method checks for the presence of a pawn on the cell passed as parameter
     *
     * @param cannySquare  cell to process extract from the grayScale chessboard image
     * @param binarySquare cell to process extract from the thresh chessboard image
     * @return a ChessboardItem which represents the object found on the cell
     */
    private ChessboardItem findPawn(Mat cannySquare, Mat binarySquare) {

        double iCannyUpperThreshold = 200;
        double iAccumulator = 17;
        int iMinRadius = 0;
        int iMaxRadius = 0;

        Mat circles = new Mat();
        //HoughCircles performs automatically a Canny method to detect edges
        //it passes iCannyUpperThreshold and iAccumulator to Imgproc.Canny methods
        ImgprocWrapper.HoughCircles(cannySquare, circles, Imgproc.CV_HOUGH_GRADIENT,
                1.0, cannySquare.rows(), iCannyUpperThreshold, iAccumulator, iMinRadius, iMaxRadius);

        if (circles.cols() > 0)
            for (int x = 0; x < circles.cols(); x++) {

                double vCircle[] = circles.get(0, x);

                if (vCircle == null) {
                    continue;
                }

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);
                circles.release();

                return isBlackOrWhite(binarySquare, pt, radius);
            }
        circles.release();
        return new EmptyTile();
    }

    /**
     * This method determines the color of the detected pawn.
     *
     * @param binarySquare Cell of binary chessboard image
     * @param center       center of the detected circle
     * @param radius       radius of the detected circle
     * @return white Pawn if most of pixels is white and black pawn otherwise
     */
    private Pawn isBlackOrWhite(Mat binarySquare, Point center, int radius) {
        int count_white = 0;
        int count_black = 0;

        //examines all the pixels within the detected circle
        for (double x = (center.x - radius); x <= center.x + radius; x++) {
            for (double y = (center.y - radius); y <= center.y + radius; y++) {
                if ((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radius * radius) {
                    double[] channels = binarySquare.get((int) x, (int) y);

                    if (channels != null) {
                        if (channels[0] == 255)
                            count_white++;
                        else
                            count_black++;
                    }
                }
            }
        }

        if (count_white >= count_black)
            return new WhitePawn();
        else
            return new BlackPawn();
    }

    @Override
    public void run() {
        findPawns();
        try {
            this.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public Chessboard getResult() {
        return this.result;
    }
}
