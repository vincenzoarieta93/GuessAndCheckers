package com.example.vincenzo.guessandcheckers.core.ai.prompter_callback;

import android.content.Context;

import com.example.vincenzo.guessandcheckers.core.ai.prompter.GameEvaluationPrompter;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.base.AbstractASPManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;

/**
 * Created by vincenzo on 26/12/2015.
 */
public class GameEvaluationCallback implements AnswerSetCallback {

    private static final String TAG = "CICCIO";
    private AbstractASPManager prompter;
    private Integer IDBestConfiguration;
    private Context context;
    private List<Integer> idBestConfs = new ArrayList<>();

    public GameEvaluationCallback(GameEvaluationPrompter gameEvaluationPrompter, Context context) {
        this.prompter = gameEvaluationPrompter;
        this.context = context;
    }

    @Override
    public void callback(AnswerSets answerSets) {
        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        if(answerSetList.isEmpty())
            this.IDBestConfiguration = 0;

            for (AnswerSet answerSet : answerSetList) {
                for(String as : answerSet.getAnswerSet()) {
                    Pattern p = Pattern.compile(GameEvaluationPrompter.CHOOSE_LABEL + "\\((\\d+)\\)");
                    Matcher m = p.matcher(as);
                    if (m.find())
                        this.idBestConfs.add(Integer.parseInt(m.group(1)));
                }
            }

        int randomAnswerSet = (int) (Math.random() * idBestConfs.size());
        this.IDBestConfiguration =  (idBestConfs.size() > 0) ? idBestConfs.get(randomAnswerSet) : null;
        this.prompter.signalSolved();
    }

    public Integer getIDBestConfiguration() {
        return IDBestConfiguration;
    }
}
