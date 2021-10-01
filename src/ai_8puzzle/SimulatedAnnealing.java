package ai_8puzzle;

import java.util.ArrayList;
import java.util.Arrays;

public class SimulatedAnnealing {

	private final Integer[] goal = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public StateNode runSimulatedAnnealing(StateNode initialState) {
		System.out.println("--------------- STARTING TO SOLVE PUZZLE USING SimulatedAnnealing -------------");
		StateNode currentState = initialState;

		final double temperature = 1000;

		int searchCost = 0;

		while (!Arrays.equals(currentState.getCurrentState(), goal)) {
			ArrayList<StateNode> children = currentState.expandCurrentNode();

			for (StateNode child : children) {

				if (sumOfDistance(child) <= sumOfDistance(currentState)) {
					searchCost++;
					child.setSearchCost(searchCost);
					currentState = child;
				} else { // Accept worse state child with certain probability
					double loss = Math.abs(sumOfDistance(currentState) - sumOfDistance(child));
					double probability = Math.exp(-(loss / temperature));

					// if child is not better than current state then pick child based on probabilyt
					if (Math.random() <= probability) {
						searchCost++;
						child.setSearchCost(searchCost);
						currentState = child;

					}
				}

			}

			int numberOfChildren = children.size();

			int randomChildIndex;
			randomChildIndex = (int) (Math.floor(Math.random() * numberOfChildren));
			currentState = children.get(randomChildIndex);

			// if child is worse than orginal puzzle, pick random chld
			if (sumOfDistance(initialState) > sumOfDistance(children.get(randomChildIndex))) {
				currentState = children.get(randomChildIndex);
			}
		}
		return currentState;

	}

	/**
	 * Heuristic function for the Manhattan distance, i.e. the sum of the dist. from
	 * the tile to the goal. Returns the sum of the distances A.K.A H2
	 */
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
