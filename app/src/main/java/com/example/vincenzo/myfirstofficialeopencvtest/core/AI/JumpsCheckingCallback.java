package com.example.vincenzo.myfirstofficialeopencvtest.core.AI;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;

/**
 * Created by vincenzo on 25/11/2015.
 */
public class JumpsCheckingCallback implements AnswerSetCallback {

    private static final String TAG = "CICCIO";

    private List<Origin> jumpingPawns = new ArrayList<>();
    private List<Jump> jumps = new ArrayList<>();
    private JumpsPrompter prompter;


    public JumpsCheckingCallback(JumpsPrompter prompter) {
        this.prompter = prompter;
    }

    @Override
    public void callback(AnswerSets answerSets) {

        Log.i(TAG, "jumpsCallback has been called");

        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        Log.i(TAG, "before analyzing answerSets. Number of answerSets = " + answerSetList.size());

        for (AnswerSet answerSet : answerSetList) {
            List<String> predicates = answerSet.getAnswerSet();
            for (String predicate : predicates) {
                Log.i(TAG, predicate);
            }
        }


        for (AnswerSet answerSet : answerSetList) {
            Set<Object> objSet;
            try {
                objSet = answerSet.getAnswerObjects();

            Log.i(TAG, "objects of answer set= " + objSet.size());
                for (Object o : objSet) {
                    Log.i(TAG, o.toString());
                    if (o instanceof Origin)
                        jumpingPawns.add((Origin) o);
                    else if(o instanceof Jump)
                        jumps.add((Jump) o);
                }
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        prompter.notifyPhaseCond(true);

    }

    public List<Origin> getJumpingPawns() {
        return this.jumpingPawns;
    }

    public List<Jump> getJumps() {
        return this.jumps;
    }

}
