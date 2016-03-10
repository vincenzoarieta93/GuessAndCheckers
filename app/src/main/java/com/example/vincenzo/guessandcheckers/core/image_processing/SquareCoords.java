package com.example.vincenzo.guessandcheckers.core.image_processing;

import org.opencv.core.Point;

/**
 * Created by vincenzo on 04/10/2015.
 */
public class SquareCoords {

    private Point upperLeftCorner;
    private Point upperRightCorner;
    private Point lowerLeftCorner;
    private Point lowerRightCorner;

    public SquareCoords(){
    }

    public SquareCoords(Point upLeftCorn, Point upRightCorn, Point lowLeftCorn, Point lowRightCorn){
        set(upLeftCorn, upRightCorn, lowLeftCorn, lowRightCorn);
    }

    public Point getUpperLeftCorner() {
        return upperLeftCorner;
    }

    public Point getUpperRightCorner() {
        return upperRightCorner;
    }

    public Point getLowerLeftCorner() {
        return lowerLeftCorner;
    }

    public Point getLowerRightCorner() {
        return lowerRightCorner;
    }

    public Point get(int index){
        Point pointToReturn = new Point();
        switch(index){
            case 0: pointToReturn = lowerLeftCorner;break;
            case 1: pointToReturn = lowerRightCorner;break;
            case 2: pointToReturn = upperRightCorner;break;
            case 3: pointToReturn = upperLeftCorner;break;
            default:pointToReturn = null;break;
        }
        return pointToReturn;
    }

    public void set(Point upLeftCorn, Point upRightCorn, Point lowLeftCorn, Point lowRightCorn){
        this.upperLeftCorner = upLeftCorn;
        this.upperRightCorner = upRightCorn;
        this.lowerLeftCorner = lowLeftCorn;
        this.lowerRightCorner = lowRightCorn;
    }

    @Override
    public String toString() {
        return "SquareCoords{" +
                "upperLeftCorner=" + upperLeftCorner +
                ", upperRightCorner=" + upperRightCorner +
                ", lowerLeftCorner=" + lowerLeftCorner +
                ", lowerRightCorner=" + lowerRightCorner +
                '}';
    }

    public void setUpperLeftCorner(Point upperLeftCorner) {
        this.upperLeftCorner = upperLeftCorner;
    }

    public void setUpperRightCorner(Point upperRightCorner) {
        this.upperRightCorner = upperRightCorner;
    }

    public void setLowerLeftCorner(Point lowerLeftCorner) {
        this.lowerLeftCorner = lowerLeftCorner;
    }

    public void setLowerRightCorner(Point lowerRightCorner) {
        this.lowerRightCorner = lowerRightCorner;
    }
}
