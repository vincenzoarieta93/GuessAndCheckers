package com.example.vincenzo.guessandcheckers.core.ai.bean;

import it.unical.mat.embasp.mapper.Predicate;
import it.unical.mat.embasp.mapper.Term;

/**
 * Created by vincenzo on 29/11/2015.
 */
@Predicate("jump")
public class Jump {
    @Term(0)
    private int startingRow;
    @Term(1)
    private int startingCol;
    @Term(2)
    private int nextRow;
    @Term(3)
    private int nextCol;
    @Term(4)
    private int id;
    @Term(5)
    private String jumperStatus;
    @Term(6)
    private String jumpedPawnStatus;
    @Term(7)
    private int numStep;

    public Jump() {
        super();
    }

    public Jump(int startingRow, int startingCol, int nextRow, int nextCol, int id, String status, String jumpedPawnStatus, int numStep) {
        this.startingRow = startingRow;
        this.startingCol = startingCol;
        this.nextRow = nextRow;
        this.nextCol = nextCol;
        this.id = id;
        this.jumperStatus = status;
        this.jumpedPawnStatus = jumpedPawnStatus;
        this.numStep = numStep;
    }


    @Override
    public String toString() {
        return "Jump{" +
                "startingRow=" + startingRow +
                ", startingCol=" + startingCol +
                ", nextRow=" + nextRow +
                ", nextCol=" + nextCol +
                ", id=" + id + " status= " + jumperStatus +
                ", jumpedPawnStatus=" + jumpedPawnStatus +
                ", numStep= " + numStep +'}';
    }

    public int getStartingRow() {
        return startingRow;
    }

    public void setStartingRow(int startingRow) {
        this.startingRow = startingRow;
    }

    public int getStartingCol() {
        return startingCol;
    }

    public void setStartingCol(int startingCol) {
        this.startingCol = startingCol;
    }

    public int getNextRow() {
        return nextRow;
    }

    public void setNextRow(int nextRow) {
        this.nextRow = nextRow;
    }

    public int getNextCol() {
        return nextCol;
    }

    public void setNextCol(int nextCol) {
        this.nextCol = nextCol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJumperStatus() {
        return jumperStatus;
    }

    public void setJumperStatus(String status) {
        this.jumperStatus = status;
    }

    public String getJumpedPawnStatus() {
        return jumpedPawnStatus;
    }

    public void setJumpedPawnStatus(String jumpedPawnStatus) {
        this.jumpedPawnStatus = jumpedPawnStatus;
    }

    public int getNumStep() {
        return numStep;
    }

    public void setNumStep(int numStep) {
        this.numStep = numStep;
    }
}