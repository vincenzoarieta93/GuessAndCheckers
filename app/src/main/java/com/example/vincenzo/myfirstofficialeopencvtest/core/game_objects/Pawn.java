package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public abstract class Pawn implements ChessboardItem {

    protected int x;
    protected int y;

    protected Pawn(int x, int y){
        this.x = x;
        this.y = y;
    }

    protected Pawn(){
    }

    public abstract String getCOLOR();

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Cell getPosition() {
        return new Cell(x, y);
    }

}
