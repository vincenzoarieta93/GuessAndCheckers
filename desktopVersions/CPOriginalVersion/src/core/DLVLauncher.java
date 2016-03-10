package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DLVLauncher {

	private static DLVLauncher instance = null;

	public static DLVLauncher getInstance() {
		if (instance == null)
			instance = new DLVLauncher();
		return instance;
	}

	public static List<String> launchDLV(String pathExe, String pathLogicProgram, StringBuilder options,
			StringBuilder rules) throws Exception {
		List<String> answerSets = new ArrayList<>();
		String line;
		OutputStream stdin = null;
		InputStream stderr = null;
		InputStream stdout = null;
		String mainString = pathExe + " " + options.toString() + " " + pathLogicProgram;

		// launch EXE and grab stdin/stdout and stderr
		Process process = null;
		long startExecution = System.nanoTime();
		try {
			process = Runtime.getRuntime().exec(mainString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		stdin = process.getOutputStream();
		stderr = process.getErrorStream();
		stdout = process.getInputStream();

		// "write" the parms into stdin
		line = rules.toString();
		stdin.write(line.getBytes());
		stdin.flush();
		stdin.close();

		// clean up if any output in stdout
		BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
		while ((line = brCleanUp.readLine()) != null)
			answerSets.add(line);
		brCleanUp.close();

		// clean up if any output in stderr
		brCleanUp = new BufferedReader(new InputStreamReader(stderr));
		while ((line = brCleanUp.readLine()) != null) {
			System.out.println("[Stderr] " + line);
		}
		brCleanUp.close();
		long endExecution = System.nanoTime();
		// System.out.println("Time elapsed = " + (endExecution -
		// startExecution) / 1000000000 + "seconds");
		return answerSets;
	}
}
