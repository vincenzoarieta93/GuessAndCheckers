package com.example.vincenzo.myfirstofficialeopencvtest.core;

import android.content.Context;
import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.BitmapManager;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.ChessboardCornersManager;
import com.example.vincenzo.myfirstofficialeopencvtest.core.support_libraries.EquationOfLineManager;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincenzo on 04/10/2015.
 */
public class CornersChessboardImplFactory implements ChessboardFactory {

    public static final String TAG = "CICCIOPASTICCIO";
    public static final int CHESSBOARD_INNER_CORNERS = 7;
    public static final int SCALED_IMAGE_SIZE = 450;
    public static final int CHESSBOARD_SIZE = 8;
    public static final int MAIN_CORNERS = 4;

    private Mat threshChessboard;
    private Mat grayScaleChessboard;
    private Mat rgbaChessboard;//it's only used to print images for debugging
    private Context context; //it's only used to print images for debugging

    private Point[] cornersFrame;

    private List<Point> allChessboardCorners;
    private Point[][] cornersMatrix = new Point[CHESSBOARD_INNER_CORNERS + 2][CHESSBOARD_INNER_CORNERS + 2];
    private Chessboard cornersChessboard = null;

    public CornersChessboardImplFactory(Context context, Mat threshChessboard, Mat grayImage, Mat rgbaImage) {
        this.threshChessboard = threshChessboard;
        this.context = context;
        this.grayScaleChessboard = grayImage;
        this.rgbaChessboard = rgbaImage;
        this.cornersFrame = new Point[MAIN_CORNERS];
        this.allChessboardCorners = new ArrayList<>();
    }

    /**
     * This method finds the four outer corners of the chessboard
     * and uses them to crop the chessboard from the entire image
     *
     * @return true if four corners were found, false otherwise
     */
    @Override
    public boolean buildFrameCorners() {
        //contours stores all connected component of the image
        List<MatOfPoint> contours = new ArrayList<>();
        //find connected components in a binary image
        Imgproc.findContours(threshChessboard, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f biggest = new MatOfPoint2f();
        MatOfPoint2f approx = new MatOfPoint2f();
        double max_area = 0;
        double area = 0;
        double peri = 0;

        //find the cornersChessboard frame
        //the cornersChessboard frame is the maximum connected component having 4 sides
        for (MatOfPoint mp : contours) {
            MatOfPoint2f mp2f = new MatOfPoint2f(mp.toArray());
            //calculates the area of the first connected component
            area = Imgproc.contourArea(mp2f);
            //find the maximum area
            if (area > max_area) {
                //Calculates a contour perimeter or a curve length.
                peri = Imgproc.arcLength(mp2f, true);
                //Approximates a polygonal curve with the specified precision.
                Imgproc.approxPolyDP(mp2f, approx, 0.02 * peri, true);
                //I find the cornersChessboard frame only if the maximum approximated curve is made up by 4 points (corners)
                if (approx.rows() == MAIN_CORNERS) {
                    max_area = area;
                    approx.copyTo(biggest);
                }
            }
        }

        Point[] points = biggest.toArray();
        //if 4 corners were not exactly found then we cannot continue to analyze the image
        //because we didn't find any cornersChessboard
        if (points.length != 4)
            return false;
        //Rendering of the 4 corners of the cornersChessboard frame
        for (int i = 0; i < points.length; i++) {
            cornersFrame[i] = points[i].clone();
//            Imgproc.circle(rgbaChessboard, cornersFrame[i], 2, new Scalar(255, 0, 0), 2);
        }
        //Now i have the 4 main corners of the chessboard, so i can crop the image and operate only on the cut out piece.
        cropChessboard();

        return true;
    }

    /**
     * This method finds all squares corners coordinates.
     * It applies HoughLines algorithm to get the chessboard lines. To obtain squares coordinates, it finds all the intersection points between these lines.
     *
     * @return true if 9x9 corners were found, false otherwise
     */
    @Override
    public boolean buildAllChessboardCorners() {

        Mat lines = new Mat();
        Mat cannyChessboard = new Mat();
        int threshold = 100;
        int minLineSize = 30;
        int maxLineGap = 20;

        //image pre-processing - in order to reduce noise
        //Canny method show only the edges of the image

        //The smallest value between threshold1 and threshold2 is used for edge linking. The largest value is used to find initial segments of strong edges
        //A threshold set too high can miss important information.
        // On the other hand, a threshold set too low will falsely identify irrelevant information (such as noise) as important.
        // It is difficult to give a generic threshold that works well on all images.
        // No tried and tested approach to this problem yet exists.
        Imgproc.Canny(grayScaleChessboard, cannyChessboard, 50, 200);
        //applying of The Probabilistic HoughLineTransform algorithm: http://docs.opencv.org/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html
        Imgproc.HoughLinesP(cannyChessboard, lines, 1, Math.PI / 180, threshold, minLineSize, maxLineGap);

        //allChessboardCorners stores all squares corners coordinates
        //For this purpose i add to the list the four chessboard outer corners
        allChessboardCorners.add(new Point(0, 0));
        allChessboardCorners.add(new Point(grayScaleChessboard.cols(), 0));
        allChessboardCorners.add(new Point(grayScaleChessboard.rows(), grayScaleChessboard.cols()));
        allChessboardCorners.add(new Point(0, grayScaleChessboard.cols()));

        double lengthOfChessboard = SCALED_IMAGE_SIZE;


        //rendering lines:START
        for (int i = 0; i < lines.rows() - 1; i++) {
            for (int x = i + 1; x < lines.rows(); x++) {
                Point pt = computeIntersect(lines.get(i, 0), lines.get(x, 0));
                //exclude points out of range and points too close to other points (inside the radius of a confirmed point)
                if (outsidePointRadius(pt, allChessboardCorners, lengthOfChessboard) && pt.x >= 0 && pt.y >= 0)
                    allChessboardCorners.add(pt.clone());
            }
        }


        //rendering of points
        for (Point p : allChessboardCorners)
            Imgproc.circle(grayScaleChessboard, p, 1, new Scalar(255, 255, 255), 3);
        //rendering lines:END

        Log.i(TAG, "All corners found = " + allChessboardCorners.size());
        if (allChessboardCorners.size() != (CHESSBOARD_SIZE + 1) * (CHESSBOARD_SIZE + 1))
            return false;
        return true;
    }

    /**
     * Discards points which are too close to a confirmed point
     *
     * @param pt               new point
     * @param crosses          confirmed points
     * @param chessboardLength length of the cornersChessboard size.
     *                         The distance of tolerance must be greater than chessboardLength/(number of tiles long a line*2)
     * @return False if the point is too close to another point. True otherwise.
     */
    private boolean outsidePointRadius(Point pt, List<Point> crosses, double chessboardLength) {
        double tileSize = chessboardLength / CHESSBOARD_SIZE;
        double radius = tileSize / 2 + tileSize / 4;
        for (Point p : crosses) {
            double res = Math.pow((pt.x - p.x), 2) + Math.pow((pt.y - p.y), 2);
            if (res <= radius * radius)
                return false;
        }
        return true;
    }


    Point computeIntersect(double[] a, double[] b) {
        double x1 = a[0], y1 = a[1], x2 = a[2], y2 = a[3];
        double x3 = b[0], y3 = b[1], x4 = b[2], y4 = b[3];

        double d = ((double) (x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (d != 0) {
            Point pt = new Point();
            pt.x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            pt.y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
            return pt;
        } else {
            return new Point(-1, -1);
        }
    }

    /**
     * This method builds a matrix of SquareCoords.
     * The i-th j-th element of the generated matrix represents the coordinates of the i-th j-th cell of the found chessboard
     */
    @Override
    public void buildCompleteChessboard() {
        cornersMatrix = ChessboardCornersManager.getChessboardMatrix(allChessboardCorners, CHESSBOARD_INNER_CORNERS);
        this.cornersChessboard = new CornersChessboardImpl(CHESSBOARD_SIZE);
        int chessboardSize = this.cornersChessboard.getLength();

        //build matrix of squareCoords
        for (int i = 0; i < chessboardSize; i++) {
            for (int j = 0; j < chessboardSize; j++) {
                SquareCoords newPoint = new SquareCoords(this.cornersMatrix[i][j], this.cornersMatrix[i][j + 1], this.cornersMatrix[i + 1][j], this.cornersMatrix[i + 1][j + 1]);
                this.cornersChessboard.setCell(i, j, newPoint);
            }
        }
    }

    @Override
    public Chessboard getCompleteChessboard() {
        return this.cornersChessboard;
    }


    /**
     * This method crops the chessboard from the entire image and overwrite the original image with the cut out piece
     */
    private void cropChessboard() {
        List<Point> mPoints = new ArrayList<>();
        SquareCoords chessboardFrameBorders = generateFrameCoords();

        mPoints.add(chessboardFrameBorders.getUpperLeftCorner());
        mPoints.add(chessboardFrameBorders.getUpperRightCorner());
        mPoints.add(chessboardFrameBorders.getLowerRightCorner());
        mPoints.add(chessboardFrameBorders.getLowerLeftCorner());
        MatOfPoint2f realContours = new MatOfPoint2f();
        realContours.fromList(mPoints);

        List<Point> mappingPoints = new ArrayList<>();
        mappingPoints.add(new Point(0, 0));
        mappingPoints.add(new Point(SCALED_IMAGE_SIZE - 1, 0));
        mappingPoints.add(new Point(SCALED_IMAGE_SIZE - 1, SCALED_IMAGE_SIZE - 1));
        mappingPoints.add(new Point(0, SCALED_IMAGE_SIZE - 1));
        MatOfPoint2f mapping = new MatOfPoint2f();
        mapping.fromList(mappingPoints);

        Mat lambda = Imgproc.getPerspectiveTransform(realContours, mapping);
        Imgproc.warpPerspective(grayScaleChessboard, grayScaleChessboard, lambda, new Size(SCALED_IMAGE_SIZE - 1, SCALED_IMAGE_SIZE - 1));//outputImage.size());

        //cut out end
    }

    /**
     * This method orders the four chessboard outer corners
     *
     * @return a SquareCoords object which contains the 4 outer corners of the chessboard
     */
    private SquareCoords generateFrameCoords() {
        SquareCoords frameCoords = new SquareCoords();
        ChessboardCornersManager.sortCornersArrayAccordingToX(this.cornersFrame);

        if (this.cornersFrame[0].y < this.cornersFrame[1].y) {
            frameCoords.setUpperLeftCorner(this.cornersFrame[0]);
            frameCoords.setLowerLeftCorner(this.cornersFrame[1]);
        } else {
            frameCoords.setUpperLeftCorner(this.cornersFrame[1]);
            frameCoords.setLowerLeftCorner(this.cornersFrame[0]);
        }

        if (this.cornersFrame[2].y < this.cornersFrame[3].y) {
            frameCoords.setUpperRightCorner(this.cornersFrame[2]);
            frameCoords.setLowerRightCorner(this.cornersFrame[3]);
        } else {
            frameCoords.setUpperRightCorner(this.cornersFrame[3]);
            frameCoords.setLowerRightCorner(this.cornersFrame[2]);
        }
        return frameCoords;
    }

}
