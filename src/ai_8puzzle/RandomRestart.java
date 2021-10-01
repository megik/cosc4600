package ai_8puzzle;

import java.util.ArrayList;
import java.util.Arrays;

public class RandomRestart {

	private final Integer[] goal = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public StateNode runRandomRestart(StateNode initialState) {
		System.out.println("--------------- STARTING TO SOLVE PUZZLE USING Hill Climbing Random Restart -------------");

		StateNode currentState = initialState;
		int searchCost = 0;

		while (!Arrays.equals(currentState.getCurrentState(), goal)) {
			ArrayList<StateNode> children = currentState.expandCurrentNode();

			int numberOfChildren = children.size();
			int randomChildIndex;
			randomChildIndex = (int) (Math.floor(Math.random() * numberOfChildren));
			currentState = children.get(randomChildIndex);

			if (sumOfDistance(currentState) > sumOfDistance(children.get(randomChildIndex))) {
				searchCost++;

				currentState.setSearchCost(searchCost);
				currentState = children.get(randomChildIndex);
			} else {
				searchCost++;
				currentState.setSearchCost(searchCost);
			}

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
