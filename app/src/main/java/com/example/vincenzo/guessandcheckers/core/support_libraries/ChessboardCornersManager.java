package com.example.vincenzo.guessandcheckers.core.support_libraries;

import org.opencv.core.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vincenzo on 29/09/2015.
 */
public class ChessboardCornersManager {

    /**
     *This method creates an ordered corners matrix (CHESSBOARD_INNER_CORNERS+2 x CHESSBOARD_INNER_CORNERS+2)
     * from the received vector of corners.
     * @param allCorners List<Point> containing all chessboard corners
     * @param CHESSBOARD_INNER_CORNERS Integer which represents the size of a chessboard line
     * @return A matrix (CHESSBOARD_SIZE+2 x CHESSBOARD_INNER_CORNERS+2) containing all the chessboard corners
     */

    public static Point[][] getChessboardMatrix(List<Point> allCorners, int CHESSBOARD_INNER_CORNERS) {
        Point[] allCornersVec = new Point[allCorners.size()];
        allCornersVec = allCorners.toArray(allCornersVec);

        //I order the array according to Y value in order to obtain a line of the chessboard each 7 elements
        sortCornersArrayAccordingToY(allCornersVec);
        Point[][] matrixOfCorners = new Point[CHESSBOARD_INNER_CORNERS + 2][CHESSBOARD_INNER_CORNERS + 2];

        int indexOfCornersVector = 0;

        for (int i = 0; i < matrixOfCorners.length; i++) {
            for (int j = 0; j < matrixOfCorners[i].length; j++, indexOfCornersVector++)
                matrixOfCorners[i][j] = allCornersVec[indexOfCornersVector];

            sortCornersArrayAccordingToX(matrixOfCorners[i]);
        }
        return matrixOfCorners;
    }

    /**
     *Sorts the array of corners (arrayOfPoints) according to y value
     * @param arrayOfPoints array of corners
     */
    public static void sortCornersArrayAccordingToY(Point[] arrayOfPoints) {
        Arrays.sort(arrayOfPoints, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                if(a == null || b == null)
                    return 0;
                int yComp = Double.compare(a.y, b.y);
                if (yComp == 0)
                    return Double.compare(a.x, b.x);
                else
                    return yComp;
            }
        });
    }

    /**
     *Sorts the array of corners (arrayOfPoints) according to x value
     * @param arrayOfPoints array of corners
     */
    public static void sortCornersArrayAccordingToX(Point[] arrayOfPoints) {
        Arrays.sort(arrayOfPoints, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                if(a == null || b == null)
                    return 0;
                int xComp = Double.compare(a.x, b.x);
                if (xComp == 0)
                    return Double.compare(a.y, b.y);
                else
                    return xComp;
            }
        });
    }
}
