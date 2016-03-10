package com.example.vincenzo.myfirstofficialeopencvtest.core.ai.prompter;

import android.content.Context;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_object.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_object.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_object.PawnsColor;

import java.util.List;

/**
 * Created by vincenzo on 25/11/2015.
 */
public interface Prompter {


    void processMoves(Chessboard chessboard, PawnsColor color, Context context);
    List<Move> suggestMoves();
}
