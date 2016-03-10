package com.example.vincenzo.guessandcheckers.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.suggesting.Hint;

/**
 * Created by vincenzo on 04/12/2015.
 */
public class Tab2Activity extends android.support.v4.app.Fragment {

    private View rootView;
    private Hint hint;
    private ChessboardImpl chessboard;


    public static Tab2Activity newInstance(Hint hint, Chessboard chessboard) {
        Tab2Activity fragment = new Tab2Activity();
        Bundle args = new Bundle();
        args.putSerializable(SuggestActivity.SUGGEST_KEY, hint);
        args.putSerializable(SuggestActivity.CHESSBOARD_KEY, chessboard);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.hint = (Hint) getArguments().getSerializable(SuggestActivity.SUGGEST_KEY);
        Chessboard tmpChessbaord = (Chessboard) getArguments().getSerializable(SuggestActivity.CHESSBOARD_KEY);
        if(tmpChessbaord instanceof ChessboardImpl)
            this.chessboard = (ChessboardImpl) tmpChessbaord;
        else
            throw new RuntimeException("unreadable chessboard");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.tab2_layout, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv); // retrieves null
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RVAdapter adapter = new RVAdapter(this.hint, this.chessboard);
        rv.setAdapter(adapter);
        return rootView;
    }
}