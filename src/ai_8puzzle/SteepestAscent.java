package ai_8puzzle;

import java.util.ArrayList;
import java.util.Arrays;

public class SteepestAscent {

	private final Integer[] goal = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public StateNode runSteepestAscent(StateNode initialState) {
		System.out
				.println("--------------- STARTING TO SOLVE PUZZLE USING Hill Climbing Steepest Ascent -------------");

		// Step 1: Evaluate Initial State
		if (Arrays.equals(initialState.getCurrentState(), goal)) {
			return initialState;
		}

		int searchCost = 0;

		StateNode previousState = null;
		StateNode currentState = initialState;
		// Step 2: Loop until a solution is found or the current state does not change
		while (!currentState.isSolved() && (previousState == null || !previousState.equals(currentState))) {
			ArrayList<StateNode> initialStateChildren = currentState.expandCurrentNode();
			// Step 2.1: Let successor be a state such that any successor of the current
			// state will be better than it (just started with first successor)
			StateNode successor = initialStateChildren.get(0);
			for (int i = 1; i < initialStateChildren.size(); i++) {
				searchCost++;
				// Step 2.2: Apply new operator, generate new state, and compare to successor
				if (sumOfDistance(initialStateChildren.get(i)) < sumOfDistance(successor)) {
					// Step 2.3: If new state is better than successor, make it new successor
					successor = initialStateChildren.get(i);
				}
			}
			// Step 2.4: If successor is goal state, return it
			if (successor.isSolved()) {
				searchCost++;
				successor.setSearchCost(searchCost);
				return successor;
			}
			previousState = currentState;
			// Step 2.5: If successor is better than current state, make successor new
			// current state
			if (sumOfDistance(successor) < sumOfDistance(currentState)) {
				currentState = successor;
			} else {
				System.out.printf("CurrentState: %d | Successor: %d\n", sumOfDistance(currentState),
						sumOfDistance(successor));
			}
			currentState.setSearchCost(searchCost);
		}

		return currentState;
	}

	public int sumOfDistance(StateNode node) {
		int sum = 0;
		for (int i = 0; i < node.getCurrentState().length; ++i) {
			if (node.getCurrentState()[i] == i)
				continue;
			if (node.getCurrentState()[i] == 0)
				continue;
			int row = node.getCurrentState()[i] / 3;
			int col = node.getCurrentState()[i] % 3;
			int goalRow = i / 3;
			int goalCol = i % 3;
			sum += Math.abs(col - goalCol) + Math.abs(row - goalRow);
		}
		return sum;
	}

}
