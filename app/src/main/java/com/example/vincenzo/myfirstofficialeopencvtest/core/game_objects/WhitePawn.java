package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class WhitePawn extends Pawn {

    private final String COLOR = WHITE_PAWN;

    public WhitePawn(int x, int y){
        super(x, y);
    }

    public WhitePawn() {
        super();
    }


    @Override
    public String getCOLOR() {
        return this.COLOR;
    }

    @Override
    public String toString() {
        return "W";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
