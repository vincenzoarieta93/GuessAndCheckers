package com.example.vincenzo.guessandcheckers.core.ai.prompter_callback;

import com.example.vincenzo.guessandcheckers.core.ai.bean.OrderedJumpStep;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.ChooserCapturingPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.AbstractASPManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;

/**
 * Created by vincenzo on 29/11/2015.
 */
public class MakeJumpsCallback implements AnswerSetCallback {

    private static final String TAG = "CICCIO";
    private List<List<OrderedJumpStep>> jumps = new ArrayList<>();
    private AbstractASPManager prompter;

    public MakeJumpsCallback(ChooserCapturingPrompter jumpsPrompter) {
        this.prompter = jumpsPrompter;
    }

    @Override
    public void callback(AnswerSets answerSets) {

        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        for (AnswerSet answerSet : answerSetList) {
            List<OrderedJumpStep> jump = new ArrayList<>();
            Set<Object> objSet;
            try {
                objSet = answerSet.getAnswerObjects();
            for (Object o : objSet) {
                    if (o instanceof OrderedJumpStep)
                        jump.add((OrderedJumpStep) o);
                }
                this.jumps.add(jump);
            } catch(Exception e){
                throw new RuntimeException("something wrong");
            }
        }
        this.prompter.signalSolved();
    }

    public List<List<OrderedJumpStep>> getOrderedJumps(){
        return this.jumps;
    }
}
