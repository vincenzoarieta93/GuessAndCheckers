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
	public static final int LOWEST_MAX_DEPTH_LEVEL = 1;

	private Move bestMoves = null;
	private Chessboard chessboard;
	private PawnsColor color;
	private int evalatedConfs = 0;

	public ASPCheckersPrompter(Chessboard chessboard, PawnsColor color) {
		this.chessboard = chessboard;
		this.color = color;
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
		int depthEvaluation = LOWEST_MAX_DEPTH_LEVEL;
		if (configurations.size() <= 4)
			depthEvaluation = HIGHEST_MAX_DEPTH_LEVEL;
		// contatore di look-ahead effettuate
		int countDepthLevels = 0;

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

		return this.evalatedConfs;

		// if (bestMoves != null) {
		// for (Cell c : bestMoves.getMoveSteps())
		// b.append(c.toString() + ", ");
		// System.out.println("best move is " + b.toString());
		// }
	}

	private GameConfiguration getBestGameConfiguration(List<GameConfiguration> configurationsToEvaluate) {

		return findBestConfig(configurationsToEvaluate);

	}

	private GameConfiguration findBestConfig(List<GameConfiguration> tmpConfs) {
		GameEvaluationPrompter confsEvaluator = new GameEvaluationPrompter(color, tmpConfs);
		this.evalatedConfs = confsEvaluator.solve();
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
	private List<GameConfiguration> betterMoves(final ChessboardImpl chessboardImpl, Move originatorMove,
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
	private void simulateMatch(GameConfiguration conf, Move moveOriginator, PawnsColor color,
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

		if (tmpConfs.size() <= 4)
			depthEvaluation = HIGHEST_MAX_DEPTH_LEVEL;
		else
			depthEvaluation = LOWEST_MAX_DEPTH_LEVEL;

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
