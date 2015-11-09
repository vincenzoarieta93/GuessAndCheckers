package com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects;

/**
 * Created by vincenzo on 30/10/2015.
 */
public class Cell {

    int row;
    int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Cell(){
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
