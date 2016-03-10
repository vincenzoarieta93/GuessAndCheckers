package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

import game_objects.PawnsColor;

public class Main {

	private static final String ROOT_NAME_REAL_CONF = "realConf";
	private static final String DIRECTORY_PATH_REAL_CONF = "src//configurations";
	private static final String CSV_FILE_NAME = "newOriginalRealConfs.csv";

	private static final String ROOT_NAME_RANDOM_CONF = "randConf";
	private static final String DIRECTORY_PATH_RANDOM_CONF = "src//randomconfigurations";

	private static final String FORMAT = ".txt";
	private static final String SEPARATOR = ",";
	private static final String SPLITTER_CHAR = "#";

	private static PawnsColor colorToMove = PawnsColor.BLACK;

	public static void main(String[] args) {

		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(CSV_FILE_NAME), SEPARATOR.charAt(0));
		} catch (IOException e) {
			e.printStackTrace();
		}

		MainFacade main = new MainFacade();
		int files = new File(DIRECTORY_PATH_RANDOM_CONF).list().length;// src//configurations
		int filesCounter = 0;

		filesCounter = 0;
		while (filesCounter < files) {
			StringBuilder csvRow = new StringBuilder();
			// repeat 5 times the same configuration
			for (int i = 0; i < 5; i++) {
				int j = 0;
				while (j < 2) {// switch white/black to move
					long startExecution = System.nanoTime();

					int evaluatedConfs = main.solve(ROOT_NAME_RANDOM_CONF + (filesCounter + 1) + FORMAT, colorToMove);
					double duration = (System.nanoTime() - startExecution) / 1000000;

					colorToMove = PawnsColor.getOppositeColor(colorToMove);
					csvRow.append(evaluatedConfs + SPLITTER_CHAR + duration);

					if (i < 4 || i == 4 && j == 0)
						csvRow.append(SPLITTER_CHAR);
					j++;
				}
			}
			System.out.println(csvRow.toString());
			writeOnCSVFile(writer, csvRow.toString());
			System.out.println("_____________________");
			filesCounter++;
		}
		closeCSVWriter(writer);
	}

	private static void writeOnCSVFile(CSVWriter writer, String data) {
		String[] entries = data.split(SPLITTER_CHAR);
		writer.writeNext(entries);
	}

	private static void closeCSVWriter(CSVWriter writer) {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}