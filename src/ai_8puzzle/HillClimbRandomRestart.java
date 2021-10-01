package ai_8puzzle;

import java.util.ArrayList;
import java.util.HashSet;

public class HillClimbRandomRestart {
	public static final int FIRST_CHOICE = 0, RANDOM_CHOICE = 1, STEEPEST_CHOICE = 2;
	private static final ArrayList<HashSet<Integer[]>> triedStateList = new ArrayList<>();
	private static HashSet<Integer[]> firstChoiceStates = new HashSet<>(), randomStates = new HashSet<>(),
			steepestStates = new HashSet<>();
	public static final Integer[] GOAL = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public static void main(String[] args) {
		triedStateList.add(firstChoiceStates);
		triedStateList.add(randomStates);
		triedStateList.add(steepestStates);

		if (calcSuccess(RANDOM_CHOICE, GOAL) != 0) {
			throw new RuntimeException(
					String.format("Fitness function broken. Goal state = %.2f", calcSuccess(RANDOM_CHOICE, GOAL)));
		}
		for (HashSet<Integer[]> set : triedStateList) {
			set.clear();
		}

		int TOTAL_RUNS = 100;
		for (int i = 0; i < TOTAL_RUNS; i++) {
			Integer[] board = Utils.createPuzzle();
			RunResult randomResult = hillClimbRandom(new RunResult(RANDOM_CHOICE, board));
			RunResult firstChoiceResult = hillClimbFirstChoice(new RunResult(FIRST_CHOICE, board));
			// Uncomment when steepest ascent method is complete
			// RunResult steepestAscentResult = hillClimbSteepestAscent(new
			// RunResult(STEEPEST_CHOICE, board));

			if (!randomResult.isSolved()) {
				throw new RuntimeException("Not solved - Random");
			}

			System.out.printf("%5d | %5d(%.2f) -> %s\n", randomResult.getRuns(), firstChoiceResult.getRuns(),
					calcSuccess(FIRST_CHOICE, firstChoiceResult.getBoard()), "");
		}

	}

	/**
	 * Calculate average distance to correct position for each tile on the board. I
	 * guess this is called the Manhattan distance?
	 * 
	 * @param board
	 * @return average distance - lower is better
	 */
	public static double calcSuccess(int type, Integer[] board) {
		if (board == null) {
			return 1000;
		}
		double sum = 0;

		if (!Utils.checkSolvable(board)) {
			throw new RuntimeException("BOARD SOMEHOW NOT SOLVABLE");
		}
		for (int i = 0; i < board.length; i++) {
			int[] currCoords = Utils.index2Coords(i);
			int[] destCoords = Utils.index2Coords(board[i]);
			int moveX = Math.abs(destCoords[0] - currCoords[0]);
			int moveY = Math.abs(destCoords[1] - currCoords[1]);
			sum += moveX + moveY;
		}
		return sum + (triedStateList.get(type).contains(board) ? 100 : 0);
	}

	private static double calcSuccess(RunResult result) {
		return calcSuccess(result.type, result.getBoard());
	}

	/**
	 * Uses hill climb with first choice approach to solve 8 puzzle
	 * 
	 * @param node
	 * @return
	 */
	public static RunResult hillClimbFirstChoice(RunResult node) {
		double score = calcSuccess(node);
		if (score == 0) {
			node.complete();
			return node;
		}
		triedStateList.get(node.type).add(node.getBoard());
		Integer[] current = node.getBoard();
		for (int i = 0; i < 4; i++) {
			Integer[] neighbor = Utils.getNeighbor(current, i);
			if (neighbor == null) {
				continue;
			}
			if (triedStateList.get(node.type).contains(neighbor)) {
				continue;
			}
			double newScore = calcSuccess(node.type, neighbor);
			if (newScore < score) {
				current = neighbor;
				break;
			}
		}
		if (current != node.getBoard()) {
			return hillClimbFirstChoice(node.newBranch(current));
		}

		return node;
	}

	/**
	 * Uses hill climb steepest ascent approach to solve 8 puzzle
	 * 
	 * @param node
	 * @return
	 */
	public static RunResult hillClimbSteepestAscent(RunResult node) {
		// FIXME: This function is not complete!!!!
		double score = calcSuccess(node);
		if (score == 0) {
			node.complete();
			return node;
		}
		triedStateList.get(node.type).add(node.getBoard());
		Integer[] current = node.getBoard();
		while (true) {
			for (int i = 0; i < 4; i++) {
				Integer[] neighbor = Utils.getNeighbor(current, i);
				if (neighbor == null) {
					continue;
				}
			}
		}
	}

	/**
	 * Uses hill climb with random restart to solve 8 puzzle
	 * 
	 * @param node
	 * @return
	 */
	public static RunResult hillClimbRandom(RunResult node) {
		double score = calcSuccess(node);
		triedStateList.get(RANDOM_CHOICE).add(node.getBoard());
		if (score == 0) {
			return node.complete();
		}

		Integer[] nextBoard = Utils.getRandomNeighbor(node.getBoard());
		double newScore = calcSuccess(node.type, nextBoard);
		if (newScore <= score) {
			return hillClimbRandom(node.newBranch(nextBoard));
		}
		return hillClimbRandom(node);
	}

}

class RunResult {
	private long runs = 0;
	public final int type;
	private Integer[] board;
	private final long startTime;
	private long endTime = -1;
	private boolean isSolved = false;

	public RunResult(int type, Integer[] board) {
		this.type = type;
		this.board = Utils.copyBoard(board);
		this.startTime = System.currentTimeMillis();
	}

	public RunResult newBranch(Integer[] board) {
		this.board = board;
		runs++;
		return this;
	}

	public boolean isSolved() {
		return Utils.isSolved(board);
	}

	public long getRuns() {
		return !isSolved ? -1 : runs;
	}

	public RunResult complete() {
		if (endTime == -1) {
			endTime = System.currentTimeMillis();
			isSolved = this.isSolved();
		} else {
			throw new IllegalStateException("complete() can only be called once!");
		}
		return this;
	}

	public long runTime() {
		if (endTime == -1) {
			return -1;
		}
		return endTime - startTime;
	}

	public Integer[] getBoard() {
		return board;
	}

	public boolean solvable() {
		return Utils.checkSolvable(board);
	}
}
