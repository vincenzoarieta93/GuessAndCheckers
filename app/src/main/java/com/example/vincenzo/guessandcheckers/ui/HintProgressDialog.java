package com.example.vincenzo.guessandcheckers.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.Chessboard;
import com.example.vincenzo.guessandcheckers.core.suggesting.CheckersHint;
import com.example.vincenzo.guessandcheckers.core.suggesting.Observer;
import com.example.vincenzo.guessandcheckers.core.game_objects.Move;
import com.example.vincenzo.guessandcheckers.core.suggesting.Prompter;
import com.example.vincenzo.guessandcheckers.core.suggesting.PrompterProvider;

import java.io.Serializable;

/**
 * Created by vincenzo on 31/12/2015.
 */
public class HintProgressDialog extends DialogFragment {

    public static final String TAG = "hintProcessDialog";
    private TextView progressMessage;
    private View rootView;
    private MyAsyncTask myAsyncTask;
    private Chessboard chessboard;

    public static HintProgressDialog newInstance(Chessboard chessboard) {
        HintProgressDialog dialog = new HintProgressDialog();
        Bundle args = new Bundle();
        args.putSerializable(SuggestActivity.CHESSBOARD_KEY, chessboard);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        this.chessboard = (Chessboard) getArguments().getSerializable(SuggestActivity.CHESSBOARD_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.suggest_progress_dialog_layout, container, false);
        this.progressMessage = (TextView) this.rootView.findViewById(R.id.progress_message);
        this.progressMessage.addOnLayoutChangeListener(new SuggestOnLayoutChangeListener());
        execute();
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("INTERRUPTED", myAsyncTask.getStatus().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //do-nothing
                    return true;
                }
                return false;
            }
        });
    }

    private void execute() {
        this.myAsyncTask = new MyAsyncTask();
        this.myAsyncTask.execute();
    }

    public class MyAsyncTask extends AsyncTask<String, String, Move> implements Observer {

        private Prompter prompter;

        @Override
        protected void onPreExecute() {
            prompter = PrompterProvider.getInstance().getPrompter();
            super.onPreExecute();
        }

        @Override
        protected Move doInBackground(String... params) {

            prompter.solve(this);
            return (Move) prompter.getSolution();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressMessage.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Move move) {
            Intent intent = new Intent(getActivity(), SuggestActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SuggestActivity.SUGGEST_KEY, new CheckersHint(move));
            bundle.putSerializable(SuggestActivity.CHESSBOARD_KEY, chessboard);
            intent.putExtras(bundle);
            HintProgressDialog.this.dismissAllowingStateLoss();
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        public void notify(String status) {
            publishProgress(status);
        }

    }

    private class SuggestOnLayoutChangeListener implements View.OnLayoutChangeListener, Serializable {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v instanceof TextView) {
                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/riffic.otf");
                ((TextView) v).setTypeface(tf);
            }
            v.removeOnLayoutChangeListener(this);
        }
    }

}
