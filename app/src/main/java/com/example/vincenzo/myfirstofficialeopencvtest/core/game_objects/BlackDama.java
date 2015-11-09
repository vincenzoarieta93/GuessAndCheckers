package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 05/11/2015.
 */
public class BlackDama extends BlackPawn {

    public BlackDama(int x, int y) {

        super(x, y);
    }

    public BlackDama() {
        super();
    }

    @Override
    public String toString() {
        return "@";
    }
}
