package com.example.vincenzo.guessandcheckers.core.suggesting;

/**
 * Created by vincenzo on 31/01/2016.
 */
public class PrompterProvider {

    private static Prompter prompter;
    private static PrompterProvider instance = null;

    public static PrompterProvider getInstance() {
        if (instance == null)
            instance = new PrompterProvider();
        return instance;
    }

    public void setPrompter(Prompter prompter) {
        PrompterProvider.prompter = prompter;
    }

    public Prompter getPrompter() {
        return prompter;
    }

}
