package com.example.vincenzo.myfirstofficialeopencvtest.core.AI;

import android.content.Context;

import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ConcreteMove;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Move;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.PawnsColor;

import java.util.List;

/**
 * Created by vincenzo on 25/11/2015.
 */
public interface Prompter {


    void processMoves(Chessboard chessboard, PawnsColor color, Context context);
    List<Move> suggestMoves();
}
