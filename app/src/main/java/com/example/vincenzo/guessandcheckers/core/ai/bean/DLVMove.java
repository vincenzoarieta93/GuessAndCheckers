package com.example.vincenzo.guessandcheckers.core.ai.bean;

import it.unical.mat.embasp.mapper.Predicate;
import it.unical.mat.embasp.mapper.Term;

/**
 * Created by vincenzo on 21/12/2015.
 */
@Predicate("move")
public class DLVMove {

    @Term(0)
    private int srcRow;

    @Term(1)
    private int srcCol;

    @Term(2)
    private int dstRow;

    @Term(3)
    private int dstCol;

    public DLVMove(){
    }

    public DLVMove(int srcRow, int srcCol, int dstRow, int dstCol) {
        this.srcRow = srcRow;
        this.srcCol = srcCol;
        this.dstRow = dstRow;
        this.dstCol = dstCol;
    }

    public int getSrcRow() {
        return srcRow;
    }

    public void setSrcRow(int srcRow) {
        this.srcRow = srcRow;
    }

    public int getSrcCol() {
        return srcCol;
    }

    public void setSrcCol(int srcCol) {
        this.srcCol = srcCol;
    }

    public int getDstRow() {
        return dstRow;
    }

    public void setDstRow(int dstRow) {
        this.dstRow = dstRow;
    }

    public int getDstCol() {
        return dstCol;
    }

    public void setDstCol(int dstCol) {
        this.dstCol = dstCol;
    }

    @Override
    public String toString() {
        return "Move{" +
                "srcRow=" + srcRow +
                ", srcCol=" + srcCol +
                ", dstRow=" + dstRow +
                ", dstCol=" + dstCol +
                '}';
    }
}
