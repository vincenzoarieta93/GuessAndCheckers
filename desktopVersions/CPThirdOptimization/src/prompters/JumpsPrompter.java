package prompters;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import core.DLVLauncher;
import core.RegexResolver;
import game_objects.ChessboardImpl;
import game_objects.Jump;
import game_objects.Move;
import game_objects.PawnsColor;

public class JumpsPrompter extends AbstractPrompter {

	private String logicProgramPath;
	private ChessboardImpl chessboard;
	private List<Move> jumps;
	private PawnsColor color;

	public JumpsPrompter(ChessboardImpl chessboardImpl, PawnsColor color) {
		this.chessboard = chessboardImpl;
		this.color = color;
		URL url = JumpsPrompter.class.getClassLoader().getResource("find_jumps.asp");// "conf1.txt"
		this.logicProgramPath = url.getPath().substring(1, url.getPath().length());
	}

	@Override
	public int solve() {

		StringBuilder rules = new StringBuilder();
		StringBuilder options = new StringBuilder();

		rules.append("userColor(" + color.getFullLabel() + ").");
		rules.append(getFactsFromChessboard(this.chessboard, null, true));

		options.append("-silent -filter=origin,jump --");

		List<String> answerSets = new ArrayList<>();
		try {
			answerSets = DLVLauncher.getInstance().launchDLV(pathExecutable, this.logicProgramPath, options, rules);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Jump> jumpingPawns = new ArrayList<>();
		List<Jump> jumps = new ArrayList<>();
		parseAnswerSets(answerSets, jumpingPawns, jumps);

		List<Move> allJumps = new ArrayList<>();

		if (jumpingPawns.size() > 0) {

			ChooserJumpsPrompter chooser = new ChooserJumpsPrompter(chessboard, jumpingPawns, jumps);
			chooser.solve();

			allJumps = (List<Move>) chooser.getSolution();
		}
		this.jumps = allJumps;
		return -1;

	}

	private void parseAnswerSets(List<String> answerSets, List<Jump> jumpingPawns, List<Jump> jumps) {
		for (String answerSet : answerSets)
			RegexResolver.getJumpingPawnsAndJumps(answerSet, jumpingPawns, jumps);
	}

	@Override
	public Object getSolution() {
		return this.jumps;
	}

}