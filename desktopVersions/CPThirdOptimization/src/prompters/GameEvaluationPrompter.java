package prompters;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import core.DLVLauncher;
import core.RegexResolver;
import game_objects.ChessboardImpl;
import game_objects.PawnsColor;
import prompters.ASPCheckersPrompter.GameConfiguration;

public class GameEvaluationPrompter extends AbstractPrompter {

	private List<ASPCheckersPrompter.GameConfiguration> configurations;
	private ASPCheckersPrompter.GameConfiguration bestConfiguration;
	private PawnsColor color;
	private String filePath;

	public GameEvaluationPrompter(PawnsColor color,
			List<ASPCheckersPrompter.GameConfiguration> configurationsToEvaluate, String fileName) {
		this.color = color;
		this.configurations = configurationsToEvaluate;
		URL url = JumpsPrompter.class.getClassLoader().getResource(fileName);// "game_eval_compact_2.txt"
		this.filePath = url.getPath().substring(1, url.getPath().length());
	}

	@Override
	public int solve() {
		this.bestConfiguration = findSolution();
		return -1;
	}

	private ASPCheckersPrompter.GameConfiguration findSolution() {

		StringBuilder rules = new StringBuilder();
		StringBuilder options = new StringBuilder();

		options.append(" -silent -n=5 -filter=choose --");
		rules.append("userColor(" + color.getFullLabel() + ").");

		StringBuilder guess = new StringBuilder();

		Integer configurationCounter = 0;
		for (GameConfiguration gc : configurations) {
			rules.append(getFactsFromChessboard(((ChessboardImpl) gc.chessboard), configurationCounter, false));
			guess.append(RegexResolver.CHOOSE_LABEL + "(" + configurationCounter + ")");
			if (configurationCounter < configurations.size() - 1)
				guess.append("|");
			else
				guess.append(".");
			configurationCounter++;
		}
		rules.append(guess.toString());

		List<String> answerSets = new ArrayList<>();
		try {
			answerSets = DLVLauncher.getInstance().launchDLV(pathExecutable, this.filePath, options, rules);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integer idBestConfiguration = parseAnswerSets(answerSets);
		if (idBestConfiguration != null)
			return configurations.get(idBestConfiguration);
		throw new RuntimeException("no configurations to analyze found");
	}

	private Integer parseAnswerSets(List<String> answerSets) {
		List<Integer> results = new ArrayList<>();

		for (String answerset : answerSets) {
			try {
				results.add(RegexResolver.getIdBestConfiguration(answerset));

			} catch (IOException e) {
			}
		}
		int randomAnswerSet = (int) (Math.random() * results.size());
		return (results.size() > 0) ? results.get(randomAnswerSet) : null;
	}

	@Override
	public ASPCheckersPrompter.GameConfiguration getSolution() {
		return this.bestConfiguration;
	}

}
