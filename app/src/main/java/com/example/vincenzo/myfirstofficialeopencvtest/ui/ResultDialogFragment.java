package com.example.vincenzo.myfirstofficialeopencvtest.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.vincenzo.myfirstofficialeopencvtest.R;
import com.example.vincenzo.myfirstofficialeopencvtest.core.ChessboardMementoOriginator;
import com.example.vincenzo.myfirstofficialeopencvtest.core.FromMementoToCareTaker;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Cell;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.Chessboard;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;

import org.opencv.core.Size;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Created by vincenzo on 05/11/2015.
 */
public class ResultDialogFragment extends DialogFragment {

    private static final String TAG = "CICCIOPASTICCIO";
    private static final int MAX_MEMENTOS = 60;
    private CameraActivity mActivity;
    private Chessboard chessboard;
    private View rootView;
    private RelativeLayout chessboardContainer;
    private RelativeLayout buttonsContainer;
    private Button cancelButton;
    private Button blackToMove;
    private Button undoButton;
    private Button whiteToMove;
    private GridLayout gridLayout;
    private MyGridOnLayoutChangeListener gridListener;
    private Deque<FromMementoToCareTaker> mementos;

    public static ResultDialogFragment newInstance(Chessboard chessboard) {

        ResultDialogFragment dialog = new ResultDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("chessboard", chessboard);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mementos = new ArrayDeque<>();
        this.chessboard = (Chessboard) getArguments().getSerializable("chessboard");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.test_fragment, container, false);
        this.cancelButton = (Button) this.rootView.findViewById(R.id.cancel_button);
        this.undoButton = (Button) this.rootView.findViewById(R.id.undo_button);
        this.blackToMove = (Button) this.rootView.findViewById(R.id.black_suggest);
        this.whiteToMove = (Button) this.rootView.findViewById(R.id.white_suggest);
        this.gridLayout = (GridLayout) this.rootView.findViewById(R.id.pawns_grid);
        this.chessboardContainer = (RelativeLayout) this.rootView.findViewById(R.id.chessboard_container);
        configureDialog();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (CameraActivity) activity;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    private void configureDialog() {

        ViewGroup.LayoutParams params = chessboardContainer.getLayoutParams();
        Bitmap chessboardBitmap = BitmapFactory.decodeResource(this.mActivity.getResources(), R.drawable.empty_chessboard);
        Size newSize = adaptContainerToDisplaySize(chessboardBitmap.getWidth(), chessboardBitmap.getHeight());
        params.width = (int) Math.round(newSize.width);
        params.height = (int) Math.round(newSize.height);
        chessboardContainer.setLayoutParams(params);

        this.buttonsContainer = (RelativeLayout) this.rootView.findViewById(R.id.buttons_container);
        ViewGroup.LayoutParams buttonsContainerParams = buttonsContainer.getLayoutParams();
        buttonsContainerParams.height = params.height * 80 / 100;
        buttonsContainerParams.width = params.width;
        buttonsContainer.setLayoutParams(buttonsContainerParams);

        final int buttonHeight = buttonsContainerParams.height / 4;

        /* adding action when image buttons of dialog are clicked */
        resizeButton(cancelButton, buttonHeight);
        resizeButton(blackToMove, buttonHeight);
        resizeButton(whiteToMove, buttonHeight);
        resizeButton(undoButton, buttonHeight);

        setEnableUndoButton();

        if (cancelButton != null) {
            cancelButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            cancelButton.setBackgroundResource(R.drawable.dialog_button_pressed);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            cancelButton.setBackgroundResource(R.drawable.cancel_button);
                            mActivity.reset();
                            ResultDialogFragment.this.dismiss();

                            return true; // if you want to handle the touch event
                    }
                    return false;
                }
            });
        }
        if (blackToMove != null) {
            blackToMove.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            blackToMove.setBackgroundResource(R.drawable.dialog_button_pressed);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            blackToMove.setBackgroundResource(R.drawable.black_to_move_button);
                            mActivity.reset();
                            ResultDialogFragment.this.dismiss();
                            return true; // if you want to handle the touch event
                    }
                    return false;
                }
            });
        }
        if (whiteToMove != null) {
            whiteToMove.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            whiteToMove.setBackgroundResource(R.drawable.dialog_button_pressed);
                            whiteToMove.setTextColor(Color.rgb(255, 255, 255));
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            whiteToMove.setBackgroundResource(R.drawable.white_to_move_button);
                            whiteToMove.setTextColor(Color.rgb(0, 0, 0));
                            mActivity.reset();
                            ResultDialogFragment.this.dismiss();
                            return true; // if you want to handle the touch event
                    }
                    return false;
                }
            });
        }
        if (undoButton != null) {
            undoButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            undoButton.setBackgroundResource(R.drawable.dialog_button_pressed);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            setMemento();
                            setEnableUndoButton();
                            return true; // if you want to handle the touch event
                    }
                    return false;
                }
            });
        }
        configureGridLayout();
    }


    private void configureGridLayout() {
        Integer chessboardWidth = -1;
        this.gridListener = new MyGridOnLayoutChangeListener(chessboardWidth, this, gridLayout);
        gridLayout.addOnLayoutChangeListener(gridListener);
    }

    public synchronized void addMemento(FromMementoToCareTaker memento) {
        this.mementos.addFirst(memento);
        try {
            if (this.mementos.size() >= MAX_MEMENTOS)
                this.mementos.removeLast();
        } catch (NoSuchElementException e) {
        }
    }

    public synchronized void setMemento() {
        Cell toReturn;
        try {
            if (this.chessboard instanceof ChessboardMementoOriginator) {
                toReturn = ((ChessboardMementoOriginator) this.chessboard).setMemento(this.mementos.removeFirst());
                if (toReturn != null)
                    ((ChessboardItemButton) gridLayout.getChildAt(toReturn.getCol() + (toReturn.getRow() * chessboard.getLength()))).updateBackgroundResource();
            }
        } catch (NoSuchElementException e) {
        }
    }

    public synchronized void setEnableUndoButton() {
        if (this.mementos.isEmpty()) {
            this.undoButton.setEnabled(false);
            this.undoButton.setBackgroundResource(R.drawable.undo_button_disabled);
        } else {
            this.undoButton.setEnabled(true);
            this.undoButton.setBackgroundResource(R.drawable.undo_button_enabled);
        }
    }


    private class MyGridOnLayoutChangeListener implements View.OnLayoutChangeListener {

        private Integer chessboardWidth;
        private ResultDialogFragment dialog;
        GridLayout gridLayout;

        public MyGridOnLayoutChangeListener(Integer chessboardWidth, ResultDialogFragment dialog, GridLayout gridLayout) {
            this.chessboardWidth = chessboardWidth;
            this.dialog = dialog;
            this.gridLayout = gridLayout;
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            this.chessboardWidth = v.getWidth();
            Log.i(TAG, "changed!!!");
            dialog.showPawns(chessboardWidth, gridLayout);
            v.removeOnLayoutChangeListener(this);
        }
    }

    private void showPawns(Integer chessboardWidth, GridLayout gridLayout) {
        float squareSize = chessboardWidth / 8;
        int chessboardLength = chessboard.getLength();
        int buttonSize = Math.round(squareSize);

        for (int i = 0; i < chessboardLength; i++) {
            for (int j = 0; j < chessboardLength; j++) {
                ChessboardItemButton btn = new ChessboardItemButton(this.mActivity, chessboard, i, j, this);
                btn.setEnabled(true);
                btn.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));
                if (chessboard.getCell(i, j) instanceof BlackPawn) {
                    btn.setBackgroundResource(R.drawable.scaled_black_pawn);
                } else if (chessboard.getCell(i, j) instanceof WhitePawn) {
                    btn.setBackgroundResource(R.drawable.scaled_white_pawn);
                } else {
                    btn.setBackgroundResource(android.R.color.transparent);
                }
                gridLayout.addView(btn);
            }
        }
    }


    private void resizeButton(Button button, int buttonHeight) {
        ViewGroup.LayoutParams buttonsParams = button.getLayoutParams();
        buttonsParams.height = buttonHeight;
        button.setLayoutParams(buttonsParams);
    }

    public Size adaptContainerToDisplaySize(int realWidth, int realHeight) {

        DisplayMetrics metrics = this.mActivity.getResources().getDisplayMetrics();

        int activityWidth = metrics.widthPixels;
        int activityHeight = metrics.heightPixels;

        float chessboardWidth = activityWidth * 80 / 100;
        float chessboardHeight = activityHeight * 80 / 100;

        float maxImageSize = Math.min(chessboardWidth, chessboardHeight);

        float ratio = Math.min(
                maxImageSize / realWidth,
                maxImageSize / realHeight);
        int width = Math.round(ratio * realWidth);
        int height = Math.round(ratio * realHeight);

        return new Size(width, height);
    }

}
