package com.example.vincenzo.guessandcheckers.core.support_libraries;

import android.content.Context;

/**
 * Created by vincenzo on 05/01/2016.
 */
public class AIModulesProvider {

    public String ALL_MOVES_MODULE_PATH;
    public String JUMPS_MODULE_PATH;
    public String BEST_JUMPS_MODULE_PATH;
    public String BEST_CONFIGURATION_MODULE_PATH;
    private boolean areAIModulesInit = false;


    public static final String ASSETS_ALL_MOVES_MODULE_PATH = "dlv/allmoves.asp";
    public static final String ALL_MOVES_NAME_IN_STORAGE = "all_moves_seeker";

    public static final String ASSETS_JUMPS_MODULE_PATH = "dlv/findJumps.asp";
    public static final String JUMPS_MODULE_NAME_IN_STORAGE = "jumps_module";

    public static final String ASSETS_BEST_JUMPS_MODULE_PATH = "dlv/dueJump.asp";
    public static final String BEST_JUMPS_MODULE_NAME_IN_STORAGE = "make_jumps_module";

    public static final String ASSETS_GAME_EVALUATION_PATH = "dlv/fast_evaluation.asp";
    public static final String BEST_CONFIGURATION_MODULE_NAME_IN_STORAGE = "game_evaluation_module";

    public static final String FILE_EXTENSION_IN_STORAGE = ".txt";
    public static final String SUB_DIR_NAME_IN_STORAGE = "GuessAndCheckers";

    private static AIModulesProvider instance = null;

    public static AIModulesProvider getInstance() {
        if (instance == null)
            instance = new AIModulesProvider();
        return instance;
    }

    private AIModulesProvider() {
    }

    public void initAIModules(Context context) {
        if(!areAIModulesInit) {
            BEST_CONFIGURATION_MODULE_PATH = FileManager.writeFileFromAssetsToExternalStorage(context, ASSETS_GAME_EVALUATION_PATH, BEST_CONFIGURATION_MODULE_NAME_IN_STORAGE, FILE_EXTENSION_IN_STORAGE, SUB_DIR_NAME_IN_STORAGE);
            JUMPS_MODULE_PATH = FileManager.writeFileFromAssetsToExternalStorage(context, ASSETS_JUMPS_MODULE_PATH, JUMPS_MODULE_NAME_IN_STORAGE, FILE_EXTENSION_IN_STORAGE, SUB_DIR_NAME_IN_STORAGE);
            BEST_JUMPS_MODULE_PATH = FileManager.writeFileFromAssetsToExternalStorage(context, ASSETS_BEST_JUMPS_MODULE_PATH, BEST_JUMPS_MODULE_NAME_IN_STORAGE, FILE_EXTENSION_IN_STORAGE, SUB_DIR_NAME_IN_STORAGE);
            ALL_MOVES_MODULE_PATH = FileManager.writeFileFromAssetsToExternalStorage(context, ASSETS_ALL_MOVES_MODULE_PATH, ALL_MOVES_NAME_IN_STORAGE, FILE_EXTENSION_IN_STORAGE, SUB_DIR_NAME_IN_STORAGE);
            areAIModulesInit = true;
        }
    }
}
