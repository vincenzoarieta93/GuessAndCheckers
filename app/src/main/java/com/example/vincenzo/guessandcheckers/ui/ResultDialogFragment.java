package com.example.vincenzo.guessandcheckers.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.ai.prompter.ASPCheckersPrompter;
import com.example.vincenzo.guessandcheckers.core.image_processing.ChessboardMementoOriginator;
import com.example.vincenzo.guessandcheckers.core.image_processing.FromMementoToCareTaker;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.PawnsColor;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;
import com.example.vincenzo.guessandcheckers.core.suggesting.PrompterProvider;
import com.example.vincenzo.guessandcheckers.core.support_libraries.SizeAdapter;

import org.opencv.core.Size;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class ResultDialogFragment extends DialogFragment {

    private static final String TAG = "CICCIO";
    private static final int MAX_MEMENTOS = 60;
    public static final String LABEL_CLASS = "resultDialogFragment";
    public static final String CHESSBOARD_PARAMETER_LABEL = "Chessboard";

    private Context mActivity;
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
    private Rect buttonBounds;

    private static ResultDialogFragment instance = null;


    public static ResultDialogFragment newInstance(Chessboard chessboard) {

        if (instance == null) {
            instance = new ResultDialogFragment();
            Bundle args = new Bundle();
            args.putSerializable(CHESSBOARD_PARAMETER_LABEL, chessboard);
            instance.setArguments(args);
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, R.style.MyAlertDialogTheme);
        this.mementos = new ArrayDeque<>();

        if (savedInstanceState != null) {
            this.chessboard = (Chessboard) savedInstanceState.getSerializable(CHESSBOARD_PARAMETER_LABEL);
        } else
            this.chessboard = (Chessboard) getArguments().getSerializable(CHESSBOARD_PARAMETER_LABEL);
        Mediator.getInstance().setResultDialogFragment(this);
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
        this.mActivity = activity;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(CHESSBOARD_PARAMETER_LABEL, chessboard);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        Mediator.getInstance().hideUserIndicators();
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
                            buttonBounds = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            cancelButton.setBackgroundResource(R.drawable.dialog_button_pressed);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            cancelButton.setBackgroundResource(R.drawable.cancel_button);
                            if (buttonBounds.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                                Mediator.getInstance().resetImageRecognitionParams();
                                ResultDialogFragment.this.dismiss();
                                instance = null;
                            }
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
                            buttonBounds = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            if (buttonBounds.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
                                handleSuggestAction(PawnsColor.BLACK);
                            blackToMove.setBackgroundResource(R.drawable.black_to_move_button);
                            return true;
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
                            buttonBounds = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            whiteToMove.setBackgroundResource(R.drawable.dialog_button_pressed);
                            whiteToMove.setTextColor(Color.rgb(255, 255, 255));
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (buttonBounds.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
                                handleSuggestAction(PawnsColor.WHITE);
                            whiteToMove.setBackgroundResource(R.drawable.white_to_move_button);
                            whiteToMove.setTextColor(Color.rgb(0, 0, 0));
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
                            buttonBounds = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            undoButton.setBackgroundResource(R.drawable.dialog_button_pressed);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            if (buttonBounds.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
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

    private void showSuggestProgressDialog(PawnsColor clientColor) {

        final ASPCheckersPrompter prompter = new ASPCheckersPrompter(chessboard, clientColor, mActivity);
        PrompterProvider.getInstance().setPrompter(prompter);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        DialogFragment dialogFrag = HintProgressDialog.newInstance(chessboard);
        dialogFrag.setStyle(0, R.style.MyProgressDialogTheme);
        dialogFrag.show(fragmentManager, HintProgressDialog.TAG);
    }


    private void configureGridLayout() {
        Integer chessboardWidth = -1;
        this.gridListener = new MyGridOnLayoutChangeListener(chessboardWidth, this, gridLayout);
        gridLayout.addOnLayoutChangeListener(gridListener);
    }

    private void addMemento(FromMementoToCareTaker memento) {
        this.mementos.addFirst(memento);
        try {
            if (this.mementos.size() >= MAX_MEMENTOS)
                this.mementos.removeLast();
        } catch (NoSuchElementException e) {
        }
    }

    public synchronized void createMemento(int row, int col){
        if (this.chessboard instanceof ChessboardMementoOriginator) {
            FromMementoToCareTaker memento = ((ChessboardMementoOriginator) this.chessboard).createMemento(row, col);
            addMemento(memento);
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
            dialog.showPawns(chessboardWidth, gridLayout);
            v.removeOnLayoutChangeListener(this);
        }
    }

    /**
     * Displays pawns configuration on the checkerboard
     *
     * @param chessboardWidth width of the checkerboard
     * @param gridLayout      layout where pawns must be drawn
     */
    private void showPawns(Integer chessboardWidth, GridLayout gridLayout) {
        float squareSize = chessboardWidth / 8;
        int chessboardLength = chessboard.getLength();
        int buttonSize = Math.round(squareSize);

        for (int i = 0; i < chessboardLength; i++) {
            for (int j = 0; j < chessboardLength; j++) {
                ChessboardItemButton btn = new ChessboardItemButton(this.mActivity, chessboard, i, j);
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

        return SizeAdapter.adaptPreservingAspectRatio(realWidth, realHeight, chessboardWidth, chessboardHeight);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Mediator.getInstance().displayUserIndicators();
    }

    private void handleSuggestAction(PawnsColor clientColor) {
        Mediator.getInstance().resetImageRecognitionParams();
        this.dismiss();
        instance = null;
        showSuggestProgressDialog(clientColor);
    }

}
