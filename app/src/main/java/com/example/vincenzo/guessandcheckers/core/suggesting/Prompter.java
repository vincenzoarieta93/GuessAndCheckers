package com.example.vincenzo.guessandcheckers.core.suggesting;

import java.io.Serializable;

/**
 * Created by vincenzo on 25/11/2015.
 */
public abstract interface Prompter extends Serializable {

    /**
     * This method allows to solve a task and to notify something to an observed object by invoking notify method on it
     * @param obj observed object
     */
    public abstract void solve(Observed obj);
    public abstract Object getSolution();
}
