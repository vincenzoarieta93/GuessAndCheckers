package com.example.vincenzo.guessandcheckers.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.Cell;
import com.example.vincenzo.guessandcheckers.core.game_objects.ChessboardImpl;
import com.example.vincenzo.guessandcheckers.core.game_objects.Pawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;
import com.example.vincenzo.guessandcheckers.core.suggesting.Hint;

import java.util.List;

/**
 * Created by vincenzo on 08/12/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MoveViewHolder> {


    public static class MoveViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView textMove;
        ImageView pawnImage;


        MoveViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            textMove = (TextView) itemView.findViewById(R.id.text_move);
            pawnImage = (ImageView) itemView.findViewById(R.id.pawn_to_move);
        }
    }

    private List<Cell> steps;
    private Pawn jumper;
    private int idResource;

    public RVAdapter(Hint suggests, ChessboardImpl chessboard) {
        this.steps = (suggests.getSuggestedMove() != null) ? suggests.getSuggestedMove().getMoveSteps() : null;
        this.jumper = (steps != null) ? (Pawn) chessboard.getCell(steps.get(0).getRow(), steps.get(0).getCol()) : null;
        findIdResource();
    }

    private void findIdResource() {
        if (this.jumper != null) {
            if (this.jumper instanceof WhiteDama)
                idResource = R.drawable.white_dama;
            else if (this.jumper instanceof WhitePawn)
                idResource = R.drawable.scaled_white_pawn;
            else if (this.jumper instanceof BlackDama)
                idResource = R.drawable.black_dama;
            else
                idResource = (R.drawable.scaled_black_pawn);
        }
    }

    @Override
    public MoveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        MoveViewHolder pvh = new MoveViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MoveViewHolder holder, int position) {
        if (this.steps != null) {
            Cell move = this.steps.get(position);
            String text;
            if(position < getItemCount() -1 )
                text = new String(jumper.getCOLOR() + " pawn in (" + move.getRow() + ", " + move.getCol() + ") moves to (" + this.steps.get(position + 1).getRow() + ", " + this.steps.get(position + 1).getCol() + ")");
            else
             text = new String("Done !!!");
            holder.textMove.setText(text);
            holder.pawnImage.setImageResource(idResource);
        }
    }


    @Override
    public int getItemCount() {
        return (this.steps != null) ? this.steps.size() : 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
