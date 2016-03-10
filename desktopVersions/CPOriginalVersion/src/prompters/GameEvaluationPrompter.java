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

/**
 * Created by vincenzo on 26/12/2015.
 */
public class GameEvaluationPrompter extends AbstractPrompter {

	private List<ASPCheckersPrompter.GameConfiguration> configurations;
	private ASPCheckersPrompter.GameConfiguration bestConfiguration;
	private PawnsColor color;
	private String filePath;

	public GameEvaluationPrompter(PawnsColor color,
			List<ASPCheckersPrompter.GameConfiguration> configurationsToEvaluate) {
		this.color = color;
		this.configurations = configurationsToEvaluate;
		URL url = JumpsPrompter.class.getClassLoader().getResource("game_evaluation_module.asp");
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

		options.append(" -silent -n=1 -filter=choose --");
		rules.append("userColor(" + color.getFullLabel() + ").");

		Integer configurationCounter = 0;
		for (GameConfiguration gc : configurations) {
			rules.append(getFactsFromChessboard(((ChessboardImpl) gc.chessboard), configurationCounter, false));
			configurationCounter++;
		}

		List<String> answerSets = new ArrayList<>();
		try {
			answerSets = DLVLauncher.getInstance().launchDLV(pathExecutable, this.filePath, options, rules);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integer idBestConfiguration = parseAnswerSets(answerSets);
		return configurations.get(idBestConfiguration);
	}

	private Integer parseAnswerSets(List<String> answerSets) {

		Integer bestIdConf = null;

		for (String answerset : answerSets) {
			try {
				bestIdConf = RegexResolver.getIdBestConfiguration(answerset);
			} catch (IOException e) {
			}
		}
		return bestIdConf;
	}

	@Override
	public ASPCheckersPrompter.GameConfiguration getSolution() {
		return this.bestConfiguration;
	}

}
