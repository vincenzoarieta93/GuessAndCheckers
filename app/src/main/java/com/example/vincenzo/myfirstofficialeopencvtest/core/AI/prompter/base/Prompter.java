package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter.base;

import java.io.Serializable;

/**
 * Created by vincenzo on 25/11/2015.
 */
public interface Prompter extends Serializable, SuggestProvider {
    void solve();
}
