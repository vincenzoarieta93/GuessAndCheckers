package com.example.vincenzo.guessandcheckers.core.image_processing;

import com.example.vincenzo.guessandcheckers.core.my_exceptions.ChessboardNotFoundException;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;

import org.opencv.core.Mat;

/**
 * Created by vincenzo on 04/10/2015.
 */
public class Director {
    private CornersChessboardBuilder builder;

    public Director(CornersChessboardBuilder builder) {
        this.builder = builder;
    }

    public void buildChessboard() {

        if (this.builder.buildFrameCorners() && this.builder.buildAllChessboardCorners())
            this.builder.buildCompleteChessboardMatrix();
        else
            throw new ChessboardNotFoundException("No CornersChessboard was found");
    }

    public Chessboard getCompleteChessboard() {
        return this.builder.getCompleteChessboard();
    }

}
