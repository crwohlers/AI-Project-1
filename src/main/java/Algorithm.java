import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Algorithm stuff. Don't really know exactly. Definitely should contain the minimax definition.
 * Board/move evaluation could be here or in Board I think.
 */
public class Algorithm {

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
	 * Heuristic
	 * @param search
	 * @return
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


	public static Board.Edge minimaxFindBest(long moveStartStamp){
		bestEdge = null;
		Board head = Globals.mainBoard.copy();

		int alpha = -Integer.MAX_VALUE;
		int beta = Integer.MAX_VALUE;

		minimaxRecurse(0, head, alpha, beta, true, moveStartStamp);
		if (bestEdge != null){
			return bestEdge;
		}
		else{   //not great
			return head.edgeConnections.values().stream().findFirst().get();
		}
	}

	private static Board.Edge bestEdge = null;

	private static double minimaxRecurse(int depth, Board test, double alpha, double beta, boolean isMaxing, long moveStart){
		if (depth == 3) {
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
				double val = minimaxRecurse(depth+1, next, alpha, beta, false, moveStart);

				double oldbest = best;
				best = Math.max(best, val);

				if (best > oldbest && depth == 0){
					bestEdge = edge;
				}

				alpha = Math.max(alpha, best);

				if (beta <= alpha){
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
				double val = minimaxRecurse(depth+1, next, alpha, beta, true, moveStart);

				best = Math.min(best, val);
				beta = Math.min(beta, best);

				if (beta <= alpha){
					break;
				}

			}

			return best;
		}

	}
}
