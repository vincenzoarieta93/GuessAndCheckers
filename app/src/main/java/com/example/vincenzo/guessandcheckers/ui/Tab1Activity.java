package com.example.vincenzo.guessandcheckers.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardItem;
import com.example.vincenzo.guessandcheckers.core.game_objects.EmptyTile;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;
import com.example.vincenzo.guessandcheckers.core.suggesting.Hint;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.support_libraries.BitmapManager;
import com.example.vincenzo.guessandcheckers.core.support_libraries.SizeAdapter;

import org.opencv.core.Size;

/**
 * Created by vincenzo on 04/12/2015.
 */
public class Tab1Activity extends android.support.v4.app.Fragment {

    private Hint hint;
    private View rootView;
    private ChessboardImpl chessboard;
    private static final int LINE_WIDTH = 12;
    private static final int LINES = 9;

    public static Tab1Activity newInstance(Hint hint, Chessboard chessboard) {
        Tab1Activity fragment = new Tab1Activity();
        Bundle args = new Bundle();
        args.putSerializable(SuggestActivity.SUGGEST_KEY, hint);
        args.putSerializable(SuggestActivity.CHESSBOARD_KEY, chessboard);
        fragment.setArguments(args);
        return fragment;
    }

    private class MyLayoutChangeListener implements View.OnLayoutChangeListener {

        public MyLayoutChangeListener() {
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            drawChessboardWithSuggest(v.getWidth(), v.getHeight());
            v.removeOnLayoutChangeListener(this);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.hint = (Hint) getArguments().getSerializable(SuggestActivity.SUGGEST_KEY);
        Chessboard tmpChessboard = (Chessboard) getArguments().getSerializable(SuggestActivity.CHESSBOARD_KEY);
        if (tmpChessboard instanceof ChessboardImpl)
            this.chessboard = ((ChessboardImpl) tmpChessboard);
        else
            throw new RuntimeException("No drawable chessboard found");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.tab1_layout, container, false);
        final RelativeLayout chessboardContainer = (RelativeLayout) this.rootView.findViewById(R.id.chessboard_container);
        MyLayoutChangeListener mlc = new MyLayoutChangeListener();
        chessboardContainer.addOnLayoutChangeListener(mlc);
        return rootView;
    }

    private void drawChessboardWithSuggest(int containerWidth, int containerHeight) {

        ImageView myImageView = (ImageView) this.rootView.findViewById(R.id.chessboard);
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_chessboard);

        Paint myRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myRectPaint.setStyle(Paint.Style.STROKE);
        myRectPaint.setStrokeWidth(10.0f);
        myRectPaint.setColor(Color.GREEN);

        int chessboardWidth = myBitmap.getWidth() - LINE_WIDTH * LINES;
        int chessboardHeight = myBitmap.getHeight() - LINE_WIDTH * LINES;
        int cellWidth = chessboardWidth / 8;
        int cellHeight = chessboardHeight / 8;

        //Create a new image bitmap and attach a brand new canvas to it
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        //Draw the image bitmap into the canvas
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //drawing hint
        if(hint.getSuggestedMove() != null)
        drawingSuggest(myRectPaint, cellWidth, cellHeight, tempCanvas);

        //Attach the canvas to the ImageView
        myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

        ViewGroup.LayoutParams pr = myImageView.getLayoutParams();
        Size adaptedSize = SizeAdapter.adaptPreservingAspectRatio(tempBitmap.getWidth(), tempBitmap.getHeight(), containerWidth * 0.9f, containerHeight * 0.9f);
        pr.width = (int) Math.round(adaptedSize.width);
        pr.height = (int) Math.round(adaptedSize.height);
        myImageView.setLayoutParams(pr);
    }

    private void drawingSuggest(Paint myRectPaint, int cellWidth, int cellHeight, Canvas tempCanvas) {
        for (int i = 0; i < chessboard.getLength(); i++)
            for (int j = 0; j < chessboard.getLength(); j++)
                drawCell(myRectPaint, cellWidth, cellHeight, tempCanvas, new Cell(i, j));
    }

    private void drawCell(Paint myRectPaint, int cellWidth, int cellHeight, Canvas tempCanvas, Cell cell) {

        ChessboardItem currentItem = chessboard.getCell(cell.getRow(), cell.getCol());

        int x1 = cellWidth * cell.getCol() + LINE_WIDTH * (cell.getCol() + 1);
        int y1 = cellHeight * cell.getRow() + LINE_WIDTH * (cell.getRow() + 1);
        int x2 = x1 + cellWidth + LINE_WIDTH;
        int y2 = y1 + cellHeight + LINE_WIDTH;
        float setOff = cellWidth * 0.1f;

        if (hint.getSuggestedMove().getMoveSteps().contains(cell))
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 5, 5, myRectPaint);

        if (!(currentItem instanceof EmptyTile)) {
            Bitmap newBitmap = getPawnBitmap(cellWidth, cellHeight, currentItem);
            tempCanvas.drawBitmap(newBitmap, x1 + setOff, y1 + setOff, myRectPaint);
            if (hint.getSuggestedMove().getEatenOpponentPawns() != null && hint.getSuggestedMove().getEatenOpponentPawns().contains(currentItem)) {
                Bitmap crossBitmap = adaptBitmap(cellWidth, cellHeight, BitmapFactory.decodeResource(getResources(), R.drawable.scaled_x));
                tempCanvas.drawBitmap(crossBitmap, x1 + setOff, y1 + setOff, myRectPaint);
            }
        }
    }

    private Bitmap getPawnBitmap(int cellWidth, int cellHeight, ChessboardItem chessboardItem) {
        Bitmap origBitmap;
        if (chessboardItem instanceof WhiteDama)
            origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_dama);
        else if (chessboardItem instanceof BlackDama)
            origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_dama);
        else if (chessboardItem instanceof WhitePawn)
            origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scaled_white_pawn);
        else
            origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scaled_black_pawn);

        return adaptBitmap(cellWidth, cellHeight, origBitmap);
    }

    private Bitmap adaptBitmap(int cellWidth, int cellHeight, Bitmap origBitmap) {
        return BitmapManager.scaleBitmap(origBitmap, cellWidth * 0.9f, cellHeight * 0.9f, false);
    }

}
