package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import core.RegexResolver;
import game_objects.ChessboardImpl;
import game_objects.Move;
import game_objects.Pawn;
import game_objects.PawnsColor;
import prompters.ASPCheckersPrompter;

public class MainFacade {

	private ChessboardImpl chessboard;

	public int solve(String configurationFile, PawnsColor color) {
		this.chessboard = new ChessboardImpl();
		createChessboardFromFile(this.chessboard, configurationFile);
		// this.chessboard.print();
		ASPCheckersPrompter prompter = new ASPCheckersPrompter(chessboard, color, "game_eval_compact_2.txt");
		int evaluatedConfs = prompter.solve();
		Move m = prompter.getSolution();
		// for (Cell c : m.getMoveSteps())
		// System.out.println(c);
		return evaluatedConfs;
	}

	private void createChessboardFromFile(ChessboardImpl chessboard, String configurationFile) {

		try {
			URL url = Main.class.getClassLoader().getResource(configurationFile);

			BufferedReader reader = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					Pawn pawn = RegexResolver.getChessboardItem(line);
					chessboard.setCell(pawn.getPosition().getRow(), pawn.getPosition().getCol(), pawn);
				} catch (IOException e) {
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}