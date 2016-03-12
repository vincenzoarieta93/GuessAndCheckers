package com.example.vincenzo.guessandcheckers.core.suggesting;

import java.io.Serializable;

/**
 * Created by vincenzo on 02/01/2016.
 */
public interface Observer extends Serializable {

    /**
     * This method allows to notify an observed object that something happened
     * @param status message which describes what happened
     */
    void notify(String status);

}
