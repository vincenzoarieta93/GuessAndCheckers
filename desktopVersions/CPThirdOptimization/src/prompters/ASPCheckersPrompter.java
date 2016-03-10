package prompters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import core.MatchConfigurationAnalyzer;
import game_objects.Chessboard;
import game_objects.ChessboardImpl;
import game_objects.ConcreteMove;
import game_objects.Move;
import game_objects.PawnsColor;

public class ASPCheckersPrompter extends AbstractPrompter {

	public static final int HIGHEST_MAX_DEPTH_LEVEL = 1;
	public Integer totalConfs = 0;

	protected Move bestMoves = null;
	protected Chessboard chessboard;
	protected PawnsColor color;
	private String evalfileName;

	public ASPCheckersPrompter(Chessboard chessboard, PawnsColor color, String evalFileName) {
		this.chessboard = chessboard;
		this.color = color;
		this.evalfileName = evalFileName;
	}

	@Override
	public int solve() {
		ChessboardImpl chessboardImpl = (ChessboardImpl) chessboard;
		// betterMoves sfrutta diversi moduli dlv per trovare le migliori mosse
		// da fare
		// e restituisce le configurazioni della scacchiera che si otterrebbero
		// da quelle mosse
		// GameConfiguration memorizza la configurazione della scacchiera e la
		// mossa chl'ha originata

		List<GameConfiguration> configurations = betterMoves(chessboardImpl, null, color);

		// numero massimo di look-ahead (profondità di ricorsione)
		int countDepthLevels = 0;
		int depthEvaluation = HIGHEST_MAX_DEPTH_LEVEL;

		// se sono state trovate più mosse effettuabili
		// per ognua di esse gameAvaluation, che utilizza betterMoves (quindi
		// DLV) e che è ricorsiva,
		// genera la possibile configurazione della scacchiera che si otterrebbe
		// dopo depthEvaluation
		// chiamate ricorsive
		// Ottenute queste configurazioni, esse vengono tradotte in fatti e
		// passate a un altro modulo dlv.
		// Questo modulo dlv sceglie la migliore configurazione e di conseguenza
		// la migliore mossa da effettuare

		StringBuilder b = new StringBuilder();

		if (configurations.size() > 1) {

			List<GameConfiguration> configurationsToEvaluate = new ArrayList<>();

			for (GameConfiguration conf : configurations)
				simulateMatch(conf, conf.move, color, configurationsToEvaluate, countDepthLevels, depthEvaluation);

			if (configurationsToEvaluate.size() > 1) {
				GameConfiguration bestConfiguration = getBestGameConfiguration(configurationsToEvaluate);
				bestMoves = bestConfiguration.move;
			} else
				bestMoves = configurationsToEvaluate.get(0).move;
			// todo check configurations.size == 0

		} else if (configurations.size() == 1)
			bestMoves = configurations.get(0).move;
		else
			bestMoves = new ConcreteMove();
		// System.out.println("TOTAL CONFS = " + totalConfs);
		return totalConfs;

	}

	protected GameConfiguration getBestGameConfiguration(List<GameConfiguration> configurationsToEvaluate) {
		totalConfs = configurationsToEvaluate.size();
		return findBestConfig(configurationsToEvaluate);
	}

	protected GameConfiguration findBestConfig(List<GameConfiguration> tmpConfs) {
		GameEvaluationPrompter confsEvaluator = new GameEvaluationPrompter(color, tmpConfs, this.evalfileName);
		confsEvaluator.solve();
		return confsEvaluator.getSolution();
	}

	@Override
	public Move getSolution() {
		return bestMoves;
	}

	/**
	 * This method must return jumps if any, otherwise it must find better moves
	 *
	 * @param chessboardImpl
	 * @param color
	 * @return a List of configurations created by the best suggested moves
	 */
	protected List<GameConfiguration> betterMoves(final ChessboardImpl chessboardImpl, Move originatorMove,
			final PawnsColor color) {

		List<GameConfiguration> confs = new ArrayList<>();
		JumpsPrompter jumpsPrompter = new JumpsPrompter(chessboardImpl, color);
		jumpsPrompter.solve();
		List<Move> betterMoves = (List<Move>) jumpsPrompter.getSolution();

		// if betterMoves is not empty then we have one or more jumps
		// else we have to determine all legal moves
		if (betterMoves.isEmpty()) {
			AllLegalMovesPrompter allMovesPrompter = new AllLegalMovesPrompter(chessboardImpl, color);
			allMovesPrompter.solve();
			betterMoves = (List<Move>) allMovesPrompter.getSolution();
		}

		for (Move m : betterMoves) {
			if (originatorMove == null)
				confs.add(new GameConfiguration(
						MatchConfigurationAnalyzer.buildNewConfiguration(m, chessboardImpl, color), m));
			else
				confs.add(new GameConfiguration(
						MatchConfigurationAnalyzer.buildNewConfiguration(m, chessboardImpl, color), originatorMove));
		}
		return confs;
	}

	// metodo che simula le mosse di entrambi i giocatori fino a un dato livello
	// di profondità
	protected void simulateMatch(GameConfiguration conf, Move moveOriginator, PawnsColor color,
			List<GameConfiguration> confsToEvaluate, int countDepthLevels, int depthEvaluation) {

		if (countDepthLevels >= depthEvaluation) {
			confsToEvaluate.add(conf);
			return;
		}

		List<GameConfiguration> tmpConfs = betterMoves(((ChessboardImpl) conf.chessboard), moveOriginator,
				PawnsColor.getOppositeColor(color));

		if (tmpConfs.isEmpty()) {
			confsToEvaluate.add(conf);
			return;
		}

		for (GameConfiguration gc : tmpConfs)
			simulateMatch(gc, moveOriginator, PawnsColor.getOppositeColor(color), confsToEvaluate, countDepthLevels + 1,
					depthEvaluation);

	}

	public static class GameConfiguration implements Serializable {

		Chessboard chessboard;
		Move move;

		public GameConfiguration(Chessboard chessboard, Move move) {
			this.chessboard = chessboard;
			this.move = move;
		}
	}

}
