package prompters;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.DLVLauncher;
import core.RegexResolver;
import game_objects.BlackDama;
import game_objects.BlackPawn;
import game_objects.Cell;
import game_objects.ChessboardImpl;
import game_objects.ChessboardItem;
import game_objects.ConcreteMove;
import game_objects.Jump;
import game_objects.Move;
import game_objects.OrderedJumpStep;
import game_objects.Pawn;
import game_objects.WhiteDama;
import game_objects.WhitePawn;

/**
 * Created by vincenzo on 21/12/2015.
 */
public class ChooserJumpsPrompter extends AbstractPrompter {

	private List<Jump> jumpingPawns;
	private List<Jump> jumps;
	private List<Move> bestJumps = null;
	private Set<Integer> idJumpingPawns = new HashSet<>();
	private HashMap<Cell, Integer> mapping = new HashMap<>();
	private HashMap<Integer, Cell> reverseMapping = new HashMap<>();
	private ChessboardImpl chessboard;
	private String logicProgramPath;

	public ChooserJumpsPrompter(ChessboardImpl chessboard, List<Jump> jumpingPawns, List<Jump> jumps) {
		this.chessboard = chessboard;
		this.jumpingPawns = jumpingPawns;
		this.jumps = jumps;
		URL url = JumpsPrompter.class.getClassLoader().getResource("choose_jump.asp");
		this.logicProgramPath = url.getPath().substring(1, url.getPath().length());
	}

	@Override
	public int solve() {
		initConditionsToFalse();
		this.bestJumps = makeJumps();
		return -1;
	}

	private void initConditionsToFalse() {
		this.mapping.clear();
		this.reverseMapping.clear();
		this.idJumpingPawns.clear();
	}

	@Override
	public Object getSolution() {
		return this.bestJumps;
	}

	private List<Move> makeJumps() {

		StringBuilder rules = generateFactsForChooseJumpModule();
		StringBuilder options = new StringBuilder();
		options.append("-silent -filter=orderedJumpStep --");

		List<String> answerSets = new ArrayList<>();
		try {
			answerSets = DLVLauncher.getInstance().launchDLV(pathExecutable, this.logicProgramPath, options, rules);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<List<OrderedJumpStep>> orderedSteps = parseAnswerSets(answerSets);

		List<Move> allJumps = new ArrayList<>();
		for (List<OrderedJumpStep> steps : orderedSteps)
			allJumps.add(parseOrderedJumpSteps(steps));

		return allJumps;

	}

	private List<List<OrderedJumpStep>> parseAnswerSets(List<String> answerSets) {

		List<List<OrderedJumpStep>> betterJumps = new ArrayList<>();

		for (String answerSet : answerSets) {
			try {
				List<OrderedJumpStep> jump = RegexResolver.getOrderedJumpSteps(answerSet);
				betterJumps.add(jump);
			} catch (IOException io) {
			}
		}
		return betterJumps;
	}

	/**
	 * Parse all jump's steps into a list of cell. It executes a mapping from
	 * the predicates of the answerset to real cell coordinates
	 *
	 * @param orderedSteps
	 *            predicates of answer set which represent the steps of a jump
	 * @return the parsed ordered steps of a jump
	 */
	private Move parseOrderedJumpSteps(List<OrderedJumpStep> orderedSteps) {
		List<Cell> moveSteps = new ArrayList<>();
		List<Pawn> eatenOpponentPawns = new ArrayList<>();

		Collections.sort(orderedSteps, new Comparator<OrderedJumpStep>() {
			@Override
			public int compare(OrderedJumpStep o1, OrderedJumpStep o2) {
				if (o1.getOrder() < o2.getOrder())
					return -1;
				else
					return 1;
			}
		});
		int counter = 0;
		for (OrderedJumpStep o : orderedSteps) {
			Cell srcCell = reverseMapping.get(o.getSrcNode());
			Cell dstCell = reverseMapping.get(o.getDstNode());

			if (counter == 0) {
				moveSteps.add(srcCell);
				counter++;
			}

			moveSteps.add(dstCell);
			findEatenOpponentsPawn(srcCell, dstCell, eatenOpponentPawns);
		}
		return new ConcreteMove(moveSteps, eatenOpponentPawns);
	}

	private void findEatenOpponentsPawn(Cell srcCell, Cell dstCell, List<Pawn> eatenOpponentPawns) {

		ChessboardItem middleItem = chessboard.getMiddleItem(srcCell, dstCell);
		if (middleItem instanceof Pawn)
			eatenOpponentPawns.add((Pawn) middleItem);
	}

	private StringBuilder generateFactsForChooseJumpModule() {
		int nodeCounter = 0;
		StringBuilder facts = new StringBuilder();

		for (Jump j : jumpingPawns) {
			idJumpingPawns.add(j.getId());
			Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
			if (mapping.get(c) == null) {
				mapping.put(c, nodeCounter);
				reverseMapping.put(nodeCounter, c);
				facts.append("rootNode(" + nodeCounter + "," + j.getId() + ").");
				String statusPawn = "";
				if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhiteDama
						|| chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackDama)
					statusPawn = "king";
				else if (chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof WhitePawn
						|| chessboard.getCell(j.getStartingRow(), j.getStartingCol()) instanceof BlackPawn)
					statusPawn = "man";
				facts.append("pawn(" + nodeCounter + "," + statusPawn + ").");
				nodeCounter++;
			}
		}
		// assignment id to nodes
		for (Jump j : jumps) {
			Cell c = new Cell(j.getStartingRow(), j.getStartingCol());
			if (mapping.get(c) == null) {
				mapping.put(c, nodeCounter);
				reverseMapping.put(nodeCounter, c);
				nodeCounter++;
			}

			c = new Cell(j.getNextRow(), j.getNextCol());

			if (mapping.get(c) == null) {
				mapping.put(c, nodeCounter);
				reverseMapping.put(nodeCounter, c);
				nodeCounter++;
			}
		}
		// creating arch facts
		for (Jump j : jumps) {
			String pawnStatus = j.getJumpedPawnStatus();
			facts.append("arch(" + mapping.get(new Cell(j.getStartingRow(), j.getStartingCol())) + ","
					+ mapping.get(new Cell(j.getNextRow(), j.getNextCol())) + "," + j.getId() + "," + pawnStatus
					+ ").");
		}
		return facts;
	}
}
