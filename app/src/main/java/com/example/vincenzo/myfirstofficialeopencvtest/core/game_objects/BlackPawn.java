package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class BlackPawn extends Pawn {

    private final String COLOR = ChessboardItem.BLACK_PAWN;

    public BlackPawn(int x, int y){
        super(x, y);
    }

    public BlackPawn() {
        super();
    }


    @Override
    public String getCOLOR() {
        return this.COLOR;
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
