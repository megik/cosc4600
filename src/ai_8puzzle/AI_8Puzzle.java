package ai_8puzzle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AI_8Puzzle {
	public static final int FIRST_CHOICE = 0, RANDOM_RESTART = 1, STEEPEST_ASCENT = 2, SIMULATED_ANNEALING = 3;
	public static final String[] ALGOS = { "First Choice", "Random Restart", "Steepest Ascent", "Simulated Annealing" };
	private static final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy_HH_mm_ss");
	private static int fails = 0;
	private static int totalFailedSearchCost = 0;

	public static void main(String[] args) throws Exception {
		AI_8Puzzle ai = new AI_8Puzzle();
		if (args.length > 0 && args[0].equals("auto")) {
			System.out.println("Starting auto mode...");
			ai.testMode(Integer.parseInt(args[1]));
			System.exit(0);
		}

		System.out.println("8-Puzzle Problem Start");

		ai.randomPuzzle();
	}

	private SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing();
	private RandomRestart randomRestart = new RandomRestart();
	private SteepestAscent steepestAscent = new SteepestAscent();
	private FirstChoice firstChoice = new FirstChoice();

	/**
	 * Generates specified number of random puzzles and uses them for each type of
	 * algorithm
	 * 
	 * @param numRuns number of puzzles to generate
	 * @throws IOException if the report cannot be written
	 */
	public void testMode(int numRuns) throws IOException {
		String dateTime = sdf.format(new Date()).toUpperCase();
		File report = new File(String.format("%d_runs_results_%s.txt", numRuns, dateTime));
		FileOutputStream fos = new FileOutputStream(report);
		PrintStream out = new PrintStream(fos, true);
		out.println("COSC 4600 Assignment #1 Results");
		out.println("Results Generated On: " + dateTime);
		out.println("\n\nUsing Puzzles:");
		Puzzle[] generatedPuzzles = new Puzzle[numRuns];
		for (int i = 0; i < generatedPuzzles.length; i++) {
			generatedPuzzles[i] = new RandomPuzzle();
			out.printf("#%d: %s\n", i,
					generatedPuzzles[i].getInitialStateNode().toString().replace(" ", "").replace("\n", ""));
		}
		for (int algo = 0; algo <= SIMULATED_ANNEALING; algo++) {
			System.out.printf("Starting %s report...\n", ALGOS[algo]);
			out.printf("\n%-10s\n", ALGOS[algo] + " Results");
			out.println("----------------------------------------------------------------------------------");
			ArrayList<SearchData> runtimeData = new ArrayList<>();
			for (int iter = 0; iter < numRuns; iter++) {
				SearchData compute = solveHomeworkOneAlgorithms(algo, generatedPuzzles[iter].getInitialStateNode());
				runtimeData.add(compute);
			}
			out.printf("%-10s | %-12s | %-10s\n", "Puzzle #", "Search Cost", "Total Time");
			int avgCost = 0, avgTime = 0, total = 0;
			int avgFailCost = 0, avgFailTime = 0, totalFail = 0;
			for (int iter = 0; iter < runtimeData.size(); iter++) {
				SearchData entry = runtimeData.get(iter);
				if (entry.depth != -1) {
					total++;
					avgCost += entry.searchCost;
					avgTime += entry.totalTime;
				} else {
					totalFail++;
					avgFailCost += entry.searchCost;
					avgFailTime += entry.totalTime;
				}

				out.println("----------------------------------------------------------------------------------");
				out.printf("%-10s | %-12s | %-10s\n",
						entry.depth == -1 ? String.format("#%d (Fail)", iter) : String.valueOf(iter), entry.searchCost,
						String.valueOf(entry.totalTime) + " ms");
			}
			if (total > 0) {
				out.printf("Average Search Cost (Pass): %d | Average Time (Pass): %d\n", avgCost / total,
						avgTime / total);
			} else {
				out.printf("All %s FAILED.\n", ALGOS[algo]);
			}
			if (totalFail > 0) {
				out.printf("Average Search Cost (Fail): %d | Average Time (Fail): %d\n\n", avgFailCost / totalFail,
						avgFailTime / totalFail);
			} else {
				out.printf("All %s PASSED.\n\n", ALGOS[algo]);
			}

		}
		out.flush();
		out.close();
		System.out.printf("Generated report: %s\n", report.getAbsolutePath());

	}

	public void randomPuzzle() {
		Map<Integer, ArrayList<SearchData>> runtimeData = new TreeMap<>();
		int timesToRun = -1;
		int algo = -1;
		System.out.println("How many times do you want to run this?");
		Scanner kb = new Scanner(System.in);
		while (timesToRun < 1) {
			String num = kb.nextLine();
			try {
				timesToRun = Integer.parseInt(num);
			} catch (NumberFormatException e) {
				System.out.println("That was not a number.");
				timesToRun = -1;
			}
			if (timesToRun < 1)
				System.out.println("How many times do you want to run this? (Must be greater than 0)");
		}
		System.out.println(
				"Which algorithm do you want to use? (0: First Choice | 1: Random Restart | 2: Steepest Ascent | 3: Simulated Annealing)");
		while (algo < 0 || algo > 3) {
			String num = kb.nextLine();
			try {
				algo = Integer.parseInt(num);
			} catch (NumberFormatException e) {
				System.out.println("That was not a number.");
				algo = -1;
			}
			if (algo < 0 || algo > 3) {
				System.out.println(
						"Must be one of the following options - 0: First Choice | 1: Random Restart | 2: Steepest Ascent | 3: Simulated Annealing");
			}
		}
		File random = new File(timesToRun + "_Random_Test_Cases.txt");

		BufferedWriter bw = null;
		try {
			random.createNewFile();
			bw = new BufferedWriter(new FileWriter(random));
		} catch (IOException ex) {

		}
		for (int i = 0; i < timesToRun; ++i) {

			Puzzle puzzle = new RandomPuzzle();
			try {
				bw.write(puzzle.getInitialStateNode().toString().replace(" ", "").replace("\n", ""));
				bw.newLine();
			} catch (IOException ex) {
			}

			SearchData compute = solveHomeworkOneAlgorithms(algo, puzzle.getInitialStateNode());
			if (!runtimeData.containsKey(compute.depth)) {
				runtimeData.put(compute.depth, new ArrayList<>());
			}
			runtimeData.get(compute.depth).add(compute);
		}

		System.out.printf("%-10s | %-12s | %-10s\n", "d", "Search Cost", "Total Time");
		runtimeData.entrySet().stream().forEach((entry) -> {
			int avgCost = 0, avgTime = 0, total = entry.getValue().size();
			for (int i = 0; i < entry.getValue().size(); ++i) {
				SearchData data = entry.getValue().get(i);
				avgCost += data.searchCost;
				avgTime += data.totalTime;
			}
			System.out.println("----------------------------------------------------------------------------------");
			System.out.printf("%-10s | %-12s | %-10s\n",
					entry.getKey() == -1 ? String.format("-1 (%d)", fails) : String.valueOf(entry.getKey()),
					entry.getKey() == -1 ? String.valueOf(totalFailedSearchCost / fails) + " (avg)"
							: String.valueOf(avgCost / total),
					String.valueOf(avgTime / total) + " ms");
		});
		try {
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(AI_8Puzzle.class.getName()).log(Level.SEVERE, null, ex);
		}
		kb.close();

	}

	private SearchData solveHomeworkOneAlgorithms(int algo, StateNode init) {

		System.out.println(init);

		long start1 = System.currentTimeMillis();

		StateNode goalNode = null;
		switch (algo) {
		case FIRST_CHOICE:
			goalNode = firstChoice.runFirstChoice(init);
			break;
		case RANDOM_RESTART:
			goalNode = randomRestart.runRandomRestart(init);
			break;
		case STEEPEST_ASCENT:
			goalNode = steepestAscent.runSteepestAscent(init);
			break;
		case SIMULATED_ANNEALING:
			goalNode = simulatedAnnealing.runSimulatedAnnealing(init);
			break;
		}

		long end1 = System.currentTimeMillis();
		long total1 = end1 - start1;
		if (!goalNode.isSolved()) {
			fails++;
			totalFailedSearchCost += goalNode.getSearchCost();
			System.out.println("No solution found. Search Cost: " + goalNode.getSearchCost());
			return new SearchData(-1, goalNode.getSearchCost(), total1);
		}
		return new SearchData(goalNode.getCost(), goalNode.getSearchCost(), total1);

	}

	/**
	 * A struct to hold all the search data from solving a puzzle. Will be used to
	 * calculate averages
	 */
	private class SearchData {
		public int depth;
		public int searchCost;
		public long totalTime;

		public SearchData(int d, int sCost, long tTime) {
			depth = d;
			searchCost = sCost;
			totalTime = tTime;
		}
	}
}
