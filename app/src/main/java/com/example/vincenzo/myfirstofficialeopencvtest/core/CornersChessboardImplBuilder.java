package com.example.vincenzo.myfirstofficialeopencvtest.core;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;

/**
 * Created by vincenzo on 04/10/2015.
 */
public class CornersChessboardImplBuilder implements CornersChessboardBuilder {

    private ChessboardFactory factory;

    public CornersChessboardImplBuilder(ChessboardFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean buildAllChessboardCorners() {
        return this.factory.buildAllChessboardCorners();
    }

    @Override
    public boolean buildFrameCorners() {
        return this.factory.buildFrameCorners();
    }

    @Override
    public void buildCompleteChessboardMatrix() {
        this.factory.buildCompleteChessboard();
    }

    @Override
    public Chessboard getCompleteChessboard() {
        return this.factory.getCompleteChessboard();
    }
}
