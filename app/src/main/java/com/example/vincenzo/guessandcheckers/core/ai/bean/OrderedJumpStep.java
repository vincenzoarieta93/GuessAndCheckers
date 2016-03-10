package com.example.vincenzo.guessandcheckers.core.ai.bean;

import it.unical.mat.embasp.mapper.Predicate;
import it.unical.mat.embasp.mapper.Term;

/**
 * Created by vincenzo on 12/12/2015.
 */

//orderedPath(O,A,B,I,ID,S)
@Predicate("inPath")
public class OrderedJumpStep {

    @Term(0)
    private int incomingNode;
    @Term(1)
    private int srcNode;
    @Term(2)
    private int dstNode;
    @Term(3)
    private int id;
    @Term(4)
    private String eatenPawnStatus;
    @Term(5)
    private int order;


    public OrderedJumpStep(){
    }


    public OrderedJumpStep(int incomingNode, int srcNode, int dstNode, int id, String eatenPawnStatus, int order) {
        this.incomingNode = incomingNode;
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        this.id = id;
        this.eatenPawnStatus = eatenPawnStatus;
        this.order = order;
    }

    public int getIncomingNode() {
        return incomingNode;
    }

    public void setIncomingNode(int incomingNode) {
        this.incomingNode = incomingNode;
    }

    public int getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(int srcNode) {
        this.srcNode = srcNode;
    }

    public int getDstNode() {
        return dstNode;
    }

    public void setDstNode(int dstNode) {
        this.dstNode = dstNode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEatenPawnStatus() {
        return eatenPawnStatus;
    }

    public void setEatenPawnStatus(String eatenPawnStatus) {
        this.eatenPawnStatus = eatenPawnStatus;
    }



    @Override
    public String toString() {
        return "inPath(" + incomingNode + "," + srcNode + "," + dstNode + "," + eatenPawnStatus + "," + order + ").";
    }
}
