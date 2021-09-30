package ai_8puzzle;

import java.util.HashSet;

public class HillClimbRandomRestart {
	private static HashSet<String> triedStates = new HashSet<>();
	public static final Integer[] GOAL = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	private static double bestScore = 100;
	private static Integer[] bestBoard = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Integer[] board = Utils.createPuzzle();
		System.out.println(Utils.board2String(board));
		for (int i = 0; i < 9; i++) {
			int[] coords = Utils.index2Coords(i);
			System.out.printf("%d -> (%d,%d)\n", i, coords[0], coords[1]);
		}
		if (calcSuccess(GOAL) != 0) {
			throw new RuntimeException(String.format("Fitness function broken. Goal state = %.2f", calcSuccess(GOAL)));
		}
		bestScore = 100;
		bestBoard = null;
		// System.out.println(calcSuccess(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }));
		// System.out.println(calcSuccess(board));
		try {
			System.out.println(hillClimb(board));
		} catch (StackOverflowError soe) {
			System.err.println("Stack Overflow Exception!");
		}

		System.out.printf("Best Board (%.2f):\n%s\n", bestScore,
				bestBoard == null ? "NULL" : Utils.board2String(bestBoard));

	}

	/**
	 * Calculate average distance to correct position for each tile on the board
	 * 
	 * @param board
	 * @return average distance - lower is better
	 */
	public static double calcSuccess(Integer[] board) {
		double sum = 0;

		for (int index = 0; index < board.length; index++) {
			int i = index;
			if (board[i] == 0) {
				i--;
				continue;
			}
			int[] currCoords = Utils.index2Coords(i);
			int[] destCoords = Utils.index2Coords(board[i]);
			double deltaX = Math.pow((currCoords[0] + 0.0) - destCoords[0], 2);
			double deltaY = Math.pow((currCoords[1] + 0.0) - destCoords[1], 2);
			double dist = Math.sqrt(deltaX + deltaY);
			System.out.printf("%d(%d): (%d,%d) -> (%d,%d) = %.2f\n", i, board[i], currCoords[0], currCoords[1],
					destCoords[0], destCoords[1], dist);
			sum += dist;

		}
		double retVal = sum / (board.length - 1.0);
		if (retVal < bestScore) {
			bestScore = retVal;
			bestBoard = Utils.copyBoard(board);
		}
		return retVal;
	}

	public static Integer[] hillClimb(Integer[] board) {
		double success = calcSuccess(board);
		if (success == 0) {
			return board;
		}

		triedStates.add(Utils.board2String(board));
		double bestScore = 100;
		Integer[] bestBoard = null;
		Integer[][] neighbors = Utils.getAllNeighborStates(board);
		int tried = 0;
		for (Integer[] neighbor : neighbors) {
			if (neighbor == null) {
				continue;
			}
			if (triedStates.contains(Utils.board2String(neighbor))) {
				continue;
			}
			double newScore = calcSuccess(hillClimb(neighbor));
			if (newScore <= bestScore) {
				bestScore = newScore;
				bestBoard = Utils.copyBoard(neighbor);
			}
			// triedStates.add(neighbor);
			tried++;
		}
		if (tried == 0 || success < bestScore) {
			return board;
		}
		return hillClimb(bestBoard);

	}

}
