package prompters;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import core.DLVLauncher;
import core.RegexResolver;
import game_objects.Chessboard;
import game_objects.ChessboardImpl;
import game_objects.ChessboardItem;
import game_objects.Move;
import game_objects.PawnsColor;

/**
 * Created by vincenzo on 19/12/2015.
 */
public class AllLegalMovesPrompter extends AbstractPrompter {

	private String logicProgramPath;
	private Chessboard<ChessboardItem> chessboard;
	private PawnsColor color;
	private List<Move> moves;

	public AllLegalMovesPrompter(ChessboardImpl chessboardImpl, PawnsColor color) {
		this.chessboard = chessboardImpl;
		this.color = color;
		URL url = JumpsPrompter.class.getClassLoader().getResource("allmoves.asp");// "conf1.txt"
		this.logicProgramPath = url.getPath().substring(1, url.getPath().length());
	}

	@Override
	public int solve() {
		initConditionsToFalse();
		StringBuilder rules = new StringBuilder();
		StringBuilder options = new StringBuilder();
		options.append("-silent -filter=move --");
		rules.append("userColor(" + color.getFullLabel() + ").");
		rules.append(getFactsFromChessboard(((ChessboardImpl) this.chessboard), null, true));

		List<String> answerSets = new ArrayList<>();
		try {
			answerSets = DLVLauncher.getInstance().launchDLV(pathExecutable, this.logicProgramPath, options, rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.moves = parseAnswerSets(answerSets);
		return 0;
	}

	private List<Move> parseAnswerSets(List<String> answerSets) {

		List<Move> moves = new ArrayList<>();

		for (String answerset : answerSets)
			moves.add(RegexResolver.getMove(answerset));

		return moves;
	}

	private void initConditionsToFalse() {
		if (this.moves != null)
			this.moves.clear();
	}

	@Override
	public Object getSolution() {
		return this.moves;
	}

	public void setChessboard(Chessboard chessboard) {
		this.chessboard = chessboard;
	}

	public void setColor(PawnsColor color) {
		this.color = color;
	}

}
