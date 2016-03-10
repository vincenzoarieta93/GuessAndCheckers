package com.example.vincenzo.guessandcheckers.core.ai.prompter_callback;

import android.util.Log;

import com.example.vincenzo.guessandcheckers.core.ai.bean.Jump;
import com.example.vincenzo.guessandcheckers.core.ai.bean.Origin;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.AbstractASPManager;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.CapturingPrompter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;

/**
 * Created by vincenzo on 25/11/2015.
 */
public class CapturingCheckingCallback implements AnswerSetCallback {

    private static final String TAG = "CICCIO";

    private List<Origin> jumpingPawns = new ArrayList<>();
    private List<Jump> jumps = new ArrayList<>();
    private AbstractASPManager prompter;


    public CapturingCheckingCallback(CapturingPrompter prompter) {
        this.prompter = prompter;
    }

    @Override
    public void callback(AnswerSets answerSets) {
        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        for (AnswerSet answerSet : answerSetList) {
            Set<Object> objSet;
            try {
                objSet = answerSet.getAnswerObjects();
                for (Object o : objSet) {
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

        prompter.signalSolved();

    }

    public List<Origin> getJumpingPawns() {
        return this.jumpingPawns;
    }

    public List<Jump> getJumps() {
        return this.jumps;
    }

}
