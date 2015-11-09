package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 18/10/2015.
 */
public enum PawnsColor {

    WHITE("W"), BLACK("B"), NONE("0");

    private String label;

    private PawnsColor(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }
}
