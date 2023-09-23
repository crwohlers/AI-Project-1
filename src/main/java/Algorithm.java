import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Algorithm stuff. Don't really know exactly. Definitely should contain the minimax definition.
 * Board/move evaluation could be here or in Board I think.
 */
public class Algorithm {
	/**
	 * Evaluation of the board. In play, this is the sum of the number of boxes captured and a heuristic value.
	 * In terminal states, this is the number of boxes captured.
	 * @param eval the board to evaluate
	 * @return the value of the board state.
	 */
	public static double evalBoard(Board eval){
		int count = 0;

		for (int x= 0; x < 9; x++){
			for (int y= 0; y < 9; y++){
				if (eval.cells[x][y].capturedBy == 1){
					count++;
				}
			}
		}

		double heuristicVal;
		if (eval.edgeConnections.size() != 0){
			heuristicVal = sortEdges(eval).get(0).weight;
		}
		else{
			heuristicVal = 0;
		}

		return count + heuristicVal;
	}

	/**
	 * Pseudo-heuristic evaluator. Technically, the heuristic values are updated whenever an edge is removed,
	 * but this sorts the edges so the best edge is first in the list.
	 * @param search the board to sort the edges of
	 * @return the edges, sorted by weight, best first
	 */
	private static ArrayList<Board.Edge> sortEdges(Board search) {
		return search.edgeConnections.values().stream().sorted((e1, e2) -> {
			if (e2.weight - e1.weight > 0){
				return 1;
			}
			else if (e2.weight - e1.weight < 0){
				return -1;
			}
			else{
				return 0;
			}
		}).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Parent function of the minimax algorithm. Does not call recursively, controls progressive deepening
	 * and some timing.
	 * @param moveStartStamp the milliseconds since epoch of the start of the turn
	 * @return the best edge to remove this turn
	 */
	public static Board.Edge minimaxFindBest(long moveStartStamp){
		bestEdge = null;
		bestFullRunEdge = null;
		Board head = Globals.mainBoard.copy();

		int alpha = -Integer.MAX_VALUE;
		int beta = Integer.MAX_VALUE;

		int maxDepth = 1;


		long timeSinceStart = Instant.now().toEpochMilli() - moveStartStamp;
		long remainingTime = Globals.time_limit - timeSinceStart;
		long timeForLastDepth = 0;
		long lastStartStamp = Instant.now().toEpochMilli();

		//deepen if there is time for it
		while (remainingTime > 500){
			minimaxRecurse(0, maxDepth, head, alpha, beta, true, moveStartStamp);
			bestFullRunEdge = bestEdge;
			maxDepth++;
			timeForLastDepth = Instant.now().toEpochMilli() - lastStartStamp;
			timeSinceStart = Instant.now().toEpochMilli() - moveStartStamp;
			remainingTime = Globals.time_limit - timeSinceStart;
			lastStartStamp = Instant.now().toEpochMilli();
		}

		if (bestFullRunEdge != null){
			return bestFullRunEdge;
		}
		else{
			if (bestEdge != null){
				return bestEdge;
			}
			else{   //not great, probably should never happen, but safety first
				return head.edgeConnections.values().stream().findFirst().get();
			}
		}
	}


	//class variable to store current best edge. Way easier than returning it somehow
	private static Board.Edge bestEdge = null;
	private static Board.Edge bestFullRunEdge = null;

	/**
	 * Brains of minimax algorithm. Called recursively.
	 *
	 * Side effect: updates bestEdge to the edge with the best value at depth = 0.
	 *
	 * @param depth the current depth of the search
	 * @param maxDepth the maximum depth to search to
	 * @param test the board state to continue from
	 * @param alpha the alpha value for a-b pruning
	 * @param beta the beta value for a-b pruning
	 * @param isMaxing true if the layer is looking for a max value, false otherwise
	 * @param moveStart epoch millisecond stamp of the start of the turn
	 * @return the value found at this node of the search tree
	 */
	private static double minimaxRecurse(int depth, int maxDepth, Board test, double alpha, double beta, boolean isMaxing, long moveStart){
		if (depth == maxDepth) {
			return evalBoard(test);
		}

		if (isMaxing){
			double best = -Integer.MAX_VALUE;

			for(Board.Edge edge:sortEdges(test)){
				if (moveStart + Globals.time_limit - 250 < Instant.now().toEpochMilli()){
					break;
				}
				Board next = test.copy();

				String move = edge.c1[0] + "," + edge.c1[1] + " " + edge.c2[0] + "," + edge.c2[1];

				next.removeConnection(move, 1);
				double moveValue = minimaxRecurse(depth+1, maxDepth, next, alpha, beta, false, moveStart);

				double oldbest = best;
				best = Math.max(best, moveValue);

				if (best > oldbest && depth == 0){
					bestEdge = edge;
				}

				alpha = Math.max(alpha, best);

				if (beta <= alpha){
					//System.out.println("break max");
					break;
				}

			}

			return best;

		}
		else{
			double best = Integer.MAX_VALUE;

			for(Board.Edge edge:sortEdges(test)){
				if (moveStart + Globals.time_limit -250 < Instant.now().toEpochMilli()){
					break;
				}
				Board next = test.copy();
				next.removeConnection(edge.c1[0] + "," + edge.c1[1] + " " + edge.c2[0] + "," + edge.c2[1], 1);
				double moveValue = minimaxRecurse(depth+1, maxDepth, next, alpha, beta, true, moveStart);

				best = Math.min(best, moveValue);
				beta = Math.min(beta, best);

				if (beta <= alpha){
					//System.out.println("break min");
					break;
				}

			}

			return best;
		}

	}
}
