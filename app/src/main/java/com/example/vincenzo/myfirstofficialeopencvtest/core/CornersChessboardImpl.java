package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;

import org.opencv.core.Point;

/**
 * Created by vincenzo on 04/10/2015.
 */
public class CornersChessboardImpl implements Chessboard<SquareCoords> {

    private SquareCoords[][] chessboardMatrix;

    public CornersChessboardImpl(final int CHESSBOARD_SIZE) {
        this.chessboardMatrix = new SquareCoords[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    }

    @Override
    public void setCell(int row, int col, SquareCoords coords) {
        this.chessboardMatrix[row][col] = coords;
    }

    @Override
    public SquareCoords getCell(int row, int col) {
        return this.chessboardMatrix[row][col];
    }

    @Override
    public int getLength() {
        return this.chessboardMatrix.length;
    }
}
