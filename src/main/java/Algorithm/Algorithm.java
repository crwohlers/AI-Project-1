package Algorithm;

/**
 * Algorithm stuff. Don't really know exactly. Definitely should contain the minimax definition.
 * Board/move evaluation could be here or in Board I think.
 */
public class Algorithm {

	public int terminalEval(){
		return 0;
	}

	public int intermediateEval(){
		return 0;
	}

	/*
	* leftmost branch
	* full explore one node and max depth - 1
	* right one branch at max depth - 1
	* if max level, search until terminal > full explore
	* if min, opposite
	*
	* basically, for each branch, eval terminal nodes
	* until decision reached for upper node. prune others
	*
	*
	*
	* */
}
