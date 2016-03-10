package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter_callback;

import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.OrderedJumpStep;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.ChooserJumpsPrompter;

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
    private ChooserJumpsPrompter prompter;
    private List<List<OrderedJumpStep>> jumps = new ArrayList<>();

    public MakeJumpsCallback(ChooserJumpsPrompter jumpsPrompter) {
        this.prompter = jumpsPrompter;
        Log.i(TAG, "created makeJumpsCallback");
    }

    @Override
    public void callback(AnswerSets answerSets) {

        Log.i(TAG, "makeJumpsCallback has been called");
        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();
        int answerSetsFound = answerSetList.size();
        Log.i(TAG, "answersets found = " + answerSetsFound);

        for (AnswerSet answerSet : answerSetList) {
            List<OrderedJumpStep> jump = new ArrayList<>();
            Set<Object> objSet;
            try {
                objSet = answerSet.getAnswerObjects();

            for (Object o : objSet) {
                    Log.i(TAG, o.toString());
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
