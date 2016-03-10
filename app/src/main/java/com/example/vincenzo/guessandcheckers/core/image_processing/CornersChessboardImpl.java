package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

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

    @Override
    public SquareCoords getMiddleItem(Cell srcCell, Cell dstCell) {
        //TODO implement method
        return null;
    }
}
