package com.example.vincenzo.myfirstofficialeopencvtest.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vincenzo.myfirstofficialeopencvtest.R;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardMementoOriginator;
import com.example.vincenzo.myfirstofficialeopencvtest.core.FromMementoToCareTaker;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardImpl;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.ChessboardItem;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.EmptyTile;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhiteDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincenzo on 01/11/2015.
 */
public class ChessboardItemButton extends ImageView {

    public static final String TAG = "CICCIOPASTICCIO";
    private int row;
    private int col;
    private Chessboard chessboard;
    private ResultDialogFragment dialog;
    private FlyWeightFactory flyWeightFactory = FlyWeightFactory.getInstance();

    public ChessboardItemButton(Context context, Chessboard chessboard, int row, int col, ResultDialogFragment dialog) {
        super(context);
        this.row = row;
        this.col = col;
        this.chessboard = chessboard;
        this.dialog = dialog;
        addOnClickListener();
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return row;
    }


    private void addOnClickListener() {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        Constructor[] constructors = flyWeightFactory.getNextClazz(chessboard.getCell(row, col).getClass()).getDeclaredConstructors();
                        Constructor ctor = null;
                        for (int i = 0; i < constructors.length; i++) {
                            ctor = constructors[i];
                            if (ctor.getGenericParameterTypes().length == 0)
                                break;
                        }
                        try {
                            assert ctor != null;
                            ctor.setAccessible(true);

                            //logic update
                            FromMementoToCareTaker memento;
                            if(chessboard instanceof ChessboardMementoOriginator) {
                                memento = ((ChessboardMementoOriginator) chessboard).createMemento(row, col);
                                dialog.addMemento(memento);
                            }
                            ChessboardItem item = (ChessboardItem) ctor.newInstance();
                            item.setPosition(row, col);
                            chessboard.setCell(row, col, item);
//
                            //graphics update
                            dialog.setEnableUndoButton();
                            setBackgroundResource(0);
                            updateBackgroundResource();
                            ((ChessboardImpl)chessboard).print();

                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return true;

                }
                return false;
            }
        });
    }

    public void updateBackgroundResource() {
        setBackgroundResource(flyWeightFactory.getCurrentResource(chessboard.getCell(row, col).getClass()));
    }
}
