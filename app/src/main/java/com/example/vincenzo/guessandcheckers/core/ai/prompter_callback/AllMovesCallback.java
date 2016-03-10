package com.example.vincenzo.guessandcheckers.core.ai.prompter_callback;

import com.example.vincenzo.guessandcheckers.core.ai.bean.DLVMove;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.AllLegalMovesPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.AbstractASPManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;

/**
 * Created by vincenzo on 22/12/2015.
 */
public class AllMovesCallback implements AnswerSetCallback {


    private static final String TAG = "CICCIO";
    private List<DLVMove> moves = new ArrayList<>();
    private AbstractASPManager prompter;

    public AllMovesCallback(AllLegalMovesPrompter allLegalMovesPrompter) {
        this.prompter = allLegalMovesPrompter;
    }

    @Override
    public void callback(AnswerSets answerSets) {
        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        for (AnswerSet answerSet : answerSetList) {
            Set<Object> objSet;
            try {
                objSet = answerSet.getAnswerObjects();

                for (Object o : objSet)
                        this.moves.add((DLVMove) o);
            } catch(Exception e){
                throw new RuntimeException("something wrong");
            }
        }
        this.prompter.signalSolved();
    }

    public List<DLVMove> getMoves() {
        return moves;
    }
}
