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

	/**
	 * Checks if a board has been solved
	 * 
	 * @param board
	 * @return whether the board is solved
	 */
	public static boolean isSolved(Integer[] board) {
		for (int i = 0; i < board.length; i++) {
			if (i != board[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert 1D representation of board into 2D string representation
	 * 
	 * @param board
	 * @return
	 */
	public static String board2String(Integer[] board) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < board.length; i++) {
			if (i > 0) {
				if (i % 3 == 0) {
					sb.append("\n");
				} else {
					sb.append(" ");
				}
			}
			sb.append(board[i]);
		}
		return sb.toString();
	}

	/**
	 * Finds the index of the empty (0) position on the provided board
	 * 
	 * @param board
	 * @return
	 */
	public static int getEmptyIndex(Integer[] board) {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				return i;
			}
		}

		// Should never happen
		return -1;
	}

	/**
	 * Convert an index in 1D board to coordinates on a 2D board
	 * 
	 * @param index
	 * @return
	 */
	public static int[] index2Coords(int index) {
		int y = (int) ((index + 0.0) / 3.0);
		int x = index % 3;
		return new int[] { x, y };
	}

	/**
	 * Convert coordinates on a 2D board to index on a 1D board
	 * 
	 * @param coords
	 * @return index or -1 if provided coordinates are out of bounds
	 */
	public static int coords2Index(int[] coords) {
		return coords2Index(coords[0], coords[1]);
	}

	/**
	 * Convert coordinates on a 2D board to index on a 1D board
	 * 
	 * @param x
	 * @param y
	 * @return index or -1 if provided coordinates are out of bounds
	 */
	public static int coords2Index(int x, int y) {
		if (x < 0 || x > 2 || y < 0 || y > 2) {
			return -1;
		}
		return (y * 3) + x % 3;
	}

	/**
	 * Copy a board to a new array instance
	 * 
	 * @param board
	 * @return
	 */
	public static Integer[] copyBoard(Integer[] board) {
		Integer[] newBoard = new Integer[board.length];
		for (int i = 0; i < board.length; i++) {
			newBoard[i] = board[i];
		}
		return newBoard;
	}

	/**
	 * Move a specified tile to the empty position on a board
	 * 
	 * @param board
	 * @param empty
	 * @param moving
	 * @return copy of provided board with the swap applied or NULL if the swap ==
	 *         -1 (invalid neighbor)
	 */
	public static Integer[] swapTiles(Integer[] board, int empty, int moving) {
		if (moving == -1) {
			return null;
		}
		Integer[] newBoard = copyBoard(board);
		newBoard[empty] = newBoard[moving];
		newBoard[moving] = 0;
		return newBoard;
	}

	/**
	 * Checks whether provided coordinates are in bounds on a 3x3 board
	 * 
	 * @param coords
	 * @return
	 */
	public static boolean validCoords(int[] coords) {
		for (int i : coords) {
			if (i < 0 || i > 2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether provided coordinates are in bounds on a 3x3 board
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean validCoords(int x, int y) {
		return validCoords(new int[] { x, y });
	}

	/**
	 * Finds all valid neighbor states given a board (all possible next moves)
	 * 
	 * @param board
	 * @return [left,top,right,bottom], neighbor will be null if out of bounds
	 */
	public static Integer[][] getAllNeighborStates(Integer[] board) {
		int empty = getEmptyIndex(board);
		int[] emptyCoords = index2Coords(empty);
		Integer[] leftState = swapTiles(board, empty, coords2Index(emptyCoords[0] - 1, emptyCoords[1]));
		Integer[] topState = swapTiles(board, empty, coords2Index(emptyCoords[0], emptyCoords[1] - 1));
		Integer[] rightState = swapTiles(board, empty, coords2Index(emptyCoords[0] + 1, emptyCoords[1]));
		Integer[] bottomState = swapTiles(board, empty, coords2Index(emptyCoords[0], emptyCoords[1] + 1));
		return new Integer[][] { leftState, topState, rightState, bottomState };

	}
}
