package Algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Data representation class of the board. Plan is graph structure: boxes are nodes, edges are edges,
 * moves remove edges to subdivide graph.
 */
public class Board {

	public static Box[][] cells = new Box[9][9];
	public static Map<String, Boolean> edgeConnections = new HashMap<>();
	static {
		for (int x = 0; x < 9; x++){
			for (int y = 0; y < 9; y++){
				cells[x][y] = new Box();
			}
		}


		for (int x = 0; x < 10; x++){
			for (int y = 0; y < 10; y++){
				if (x==0){
					edgeConnections.put(""+x+","+y+" "+(x+1)+","+y, true);
				}
				else if (x == 9){
					edgeConnections.put(x - 1 +","+y+" "+x+","+y, true);
				}
				else{
					edgeConnections.put(""+x+","+y+" "+(x+1)+","+y, true);
					edgeConnections.put(x - 1 +","+y+" "+x+","+y, true);
				}


				if (y==0){
					edgeConnections.put(""+x+","+y+" "+x+","+(y+1), true);
				}
				else if (y == 9){
					edgeConnections.put(""+x+","+(y-1)+" "+x+","+y, true);
				}
				else{
					edgeConnections.put(""+x+","+(y-1)+" "+x+","+y, true);
					edgeConnections.put(""+x+","+y+" "+x+","+(y+1), true);
				}
			}
		}

	}

	public static void removeConnection(String move){
		edgeConnections.put(move, false);
	}

	/**
	 * Graph node.
	 */
	public static class Box {
		public Box(){
		}
	}
}
