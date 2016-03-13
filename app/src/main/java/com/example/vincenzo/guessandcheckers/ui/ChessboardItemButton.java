package com.example.vincenzo.guessandcheckers.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardMementoOriginator;
import com.example.vincenzo.guessandcheckers.core.image_processing.FromMementoToCareTaker;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by vincenzo on 01/11/2015.
 */
public class ChessboardItemButton extends ImageView {

    private int row;
    private int col;
    private Chessboard chessboard;
    private ResultDialogFragment dialog;
    private PawnsRepository pawnsRepository = PawnsRepository.getInstance();

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
                        if(((row == 0 || row % 2 == 0) && (col % 2 == 0 || col == 0)) || ((row % 2 != 0 && row != 0) && (col %2 != 0 && col != 0))) {
                            Constructor[] constructors = pawnsRepository.getNextClazz(chessboard.getCell(row, col).getClass()).getDeclaredConstructors();
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
                                if (chessboard instanceof ChessboardMementoOriginator) {
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

                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                }
                return false;
            }
        });
    }

    public void updateBackgroundResource() {
        setBackgroundResource(pawnsRepository.getCurrentResource(chessboard.getCell(row, col).getClass()));
    }
}
