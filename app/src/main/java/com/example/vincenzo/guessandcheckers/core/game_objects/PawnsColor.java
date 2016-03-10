package com.example.vincenzo.guessandcheckers.core.game_objects;

/**
 * Created by vincenzo on 18/10/2015.
 */
public enum PawnsColor{

    WHITE("W", "white"), BLACK("B", "black"), NONE("0","none");

    private String label;
    private String fullLabel;

    private PawnsColor(String label, String fullLabel){
        this.label = label;
        this.fullLabel = fullLabel;
    }

    public String getLabel(){
        return this.label;
    }
    public String getFullLabel(){
        return this.fullLabel;
    }

    public static PawnsColor getOppositeColor(PawnsColor color) {
        switch(color){
            case WHITE : return BLACK;
            case BLACK: return WHITE;
            default: throw new RuntimeException("color must be " + WHITE.getFullLabel() + " or " + BLACK.getFullLabel());
        }
    }


}
