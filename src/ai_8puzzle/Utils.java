package ai_8puzzle;

import java.util.Arrays;
import java.util.Random;

/**
 * Useful functions for the project
 * 
 * @author Jack Eastman
 *
 */
public class Utils {

	/**
	 * Creates a new unique 8 puzzle that is solvable then will set the initial and
	 * current board state
	 */
	public static Integer[] createPuzzle() {
		boolean canSolve = false;
		Integer[] board = new Integer[9];
		Arrays.fill(board, -1);
		int numFailed = 0;
		int emptyPosition = 0;
		while (!canSolve) {
			Random rand = new Random();
			board = new Integer[9];
			Arrays.fill(board, -1);
			// Fill the board with random values between 0 and 9
			for (int i = 0; i < board.length; ++i) {
				Integer tile = rand.nextInt(9);
				// Check for unique-ness
				while (arrIndexOf(board, tile) != -1) {
					tile = rand.nextInt(9);
				}
				if (tile.equals(0))
					emptyPosition = i;
				board[i] = tile;
			}
			// Runs the Puzzle classes checkSolvable function, returns true if inversions is
			// even
			canSolve = checkSolvable(board);
			if (canSolve == false)
				numFailed++;
		}
		System.out.println("Amount of times failed: " + numFailed);
		System.out.println("Empty Pos: " + emptyPosition);
		return board;
		// setCurrentState(board);
	}

	/**
	 * Helper function for createPuzzle, used to find the index of `searchFor`
	 * within the array `arr`
	 */
	private static int arrIndexOf(Integer[] arr, Integer searchFor) {
		for (int i = 0; i < arr.length; ++i) {
			if (arr[i].equals(searchFor))
				return i;
		}
		return -1;
	}

	/**
	 * Checks to see if a puzzle is solvable. A solvable puzzle has a positive of
	 * inversions. An inversion is when a tile with a greater number on it precedes
	 * a tile with a smaller number Returns true if inversions are even
	 */
	public static boolean checkSolvable(Integer[] board) {
		int inversions = 0;
		for (int i = 0; i < board.length - 1; ++i) {
			for (int j = i + 1; j < board.length; ++j) {
				// Make sure not to count the 0 tile
				if (board[i] != 0 && board[j] != 0 && board[i] > board[j])
					inversions++;
			}
		}
		return inversions % 2 == 0;
	}
}
