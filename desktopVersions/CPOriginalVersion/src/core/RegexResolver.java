package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game_objects.BlackDama;
import game_objects.BlackPawn;
import game_objects.Cell;
import game_objects.ConcreteMove;
import game_objects.Jump;
import game_objects.Move;
import game_objects.OrderedJumpStep;
import game_objects.Pawn;
import game_objects.PawnsColor;
import game_objects.WhiteDama;
import game_objects.WhitePawn;

public class RegexResolver {

	public static String ORIGIN_LABEL = "origin";
	public static String MAN_LABEL = "man";
	public static String KING_LABEL = "king";
	public static String JUMP_LABEL = "jump";
	public static String WHITE_LABEL = PawnsColor.WHITE.getFullLabel();
	public static String BLACK_LABEL = PawnsColor.BLACK.getFullLabel();
	public static String ORDERED_JUMP_STEP_LABEL = "orderedJumpStep";
	public static String MOVE_LABEL = "move";
	public static String CHOOSE_LABEL = "choose";

	public static Pawn getChessboardItem(String expr) throws IOException {
		// pawn(5,7,white,man,16).
		Pattern p = Pattern.compile("pawn\\((\\d),(\\d),(" + WHITE_LABEL + "|" + BLACK_LABEL + "),(" + MAN_LABEL + "|"
				+ KING_LABEL + "),(\\d+).+");
		Matcher m = p.matcher(expr);
		if (m.find()) {
			int row = Integer.parseInt(m.group(1));
			int col = Integer.parseInt(m.group(2));
			int id = Integer.parseInt(m.group(m.groupCount()));

			String color = m.group(3);
			String status = m.group(4);

			if (color.equals(BLACK_LABEL)) {
				if (status.equals(KING_LABEL))
					return new BlackDama(row, col);
				else
					return new BlackPawn(row, col);
			} else {
				if (status.equals(KING_LABEL))
					return new WhiteDama(row, col);
				else
					return new WhitePawn(row, col);
			}

		}
		throw new IOException("unknow fact");
	}

	public static void getJumpingPawnsAndJumps(String answerSet, List<Jump> jumpingPawns, List<Jump> jumps) {
		// {origin(6,4,4,2,10,king,man),origin(6,4,4,6,10,king,man),jump(0,2,2,0,10,king,man),
		// jump(0,2,2,4,10,king,man),...,}
		String[] predicates = answerSet.split("(, |\\}|\\{)");

		for (String predicate : predicates) {
			Pattern p = Pattern.compile("([a-zA-Z]+)\\((\\d),(\\d),(\\d),(\\d),(\\d+),(" + MAN_LABEL + "|" + KING_LABEL
					+ "),(" + MAN_LABEL + "|" + KING_LABEL + ")\\)");
			Matcher m = p.matcher(predicate);

			if (m.find()) {
				if (m.group(1).equals(ORIGIN_LABEL)) {
					jumpingPawns.add(new Jump(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),
							Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)),
							m.group(7), m.group(8)));
				} else if (m.group(1).equals(JUMP_LABEL)) {
					jumps.add(new Jump(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),
							Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)),
							m.group(7), m.group(8)));
				}
			}
		}
	}

	public static List<OrderedJumpStep> getOrderedJumpSteps(String answerSet) throws IOException {

		List<OrderedJumpStep> jump = new ArrayList<>();

		if (answerSet.matches("Best model: \\{.+\\}")) {
			String[] predicates = answerSet.split("(, |\\}|\\{)");

			for (String predicate : predicates) {
				Pattern p = Pattern.compile(ORDERED_JUMP_STEP_LABEL + "\\((\\d+),(\\d+),(\\d+),(\\d+),(\\d+),("
						+ MAN_LABEL + "|" + KING_LABEL + ")\\)");
				Matcher m = p.matcher(predicate);

				if (m.find())
					jump.add(new OrderedJumpStep(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
							Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)),
							m.group(6)));
			}
		} else
			throw new IOException();
		return jump;
	}

	public static Move getMove(String answerset) {
		List<Cell> cells = new ArrayList<>();
		Pattern p = Pattern.compile("\\{" + MOVE_LABEL + "\\((\\d),(\\d),(\\d),(\\d)\\)}");
		Matcher m = p.matcher(answerset);
		if (m.find()) {
			cells.add(new Cell(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
			cells.add(new Cell(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
		}
		return new ConcreteMove(cells, null);
	}

	public static int getIdBestConfiguration(String answerSet) throws IOException {
		// Best model: {choose(0,0,black,man,2), ..., choose(7,7,white,man,2)}
		if (answerSet.matches("Best model: \\{.+\\}")) {
			String[] predicates = answerSet.split("(, |\\}|\\{)");

			for (String predicate : predicates) {
				Pattern p = Pattern.compile(CHOOSE_LABEL + "\\((\\d+),(\\d+),(" + BLACK_LABEL + "|" + WHITE_LABEL
						+ "),(" + MAN_LABEL + "|" + KING_LABEL + "),(\\d+)\\)");
				Matcher m = p.matcher(predicate);

				if (m.find())
					return Integer.parseInt(m.group(5));
			}
		}
		throw new IOException("an error occured while evaluation process");

	}

}
