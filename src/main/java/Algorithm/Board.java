package Algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Data representation class of the board. Plan is graph structure: boxes are nodes, edges are edges,
 * moves remove edges to subdivide graph.
 */
public class Board {

	public static Box[][] cells = new Box[9][9];
	public static Map<String, Edge> edgeConnections = new HashMap<>();
	static {
		for (int x = 0; x < 9; x++){
			for (int y = 0; y < 9; y++){
				cells[x][y] = new Box();
			}
		}

		for (int x= 0; x < 9; x++){
			for (int y= 0; y < 9; y++){
				//box based coords
				String coord = x + "," + y;

				if (x==0){
					//left
					edgeConnections.put(coord+"w", new Edge(cells[0][y]));

				}

				if (x==8){
					//special right
					edgeConnections.put(coord+"e", new Edge(cells[8][y]));
				}
				else{
					//right
					edgeConnections.put(coord+"e", new Edge(cells[x][y], cells[x+1][y]));
				}

				if (y==0){
					//up only
					edgeConnections.put(coord+"n", new Edge(cells[x][0]));
				}

				if (y==8){
					//special down
					edgeConnections.put(coord+"s", new Edge(cells[x][8]));
				}
				else{
					//down
					edgeConnections.put(coord+"s", new Edge(cells[x][y], cells[x][y+1]));
				}
			}
		}
		//int x = 1; //breakpoint line
	}


	public static String parseMoveToEdgeKey(String move){
		//move is "x,y x,y"
		move = move.replace(',', ' ');

		Scanner in = new Scanner(move);

		int x1 = in.nextInt();
		int y1 = in.nextInt();
		int x2 = in.nextInt();
		int y2 = in.nextInt();

		in.close();

		if (x2 < x1){   //swap, leverage the fact that moves must be 1 long
			x2++;
			x1--;
		}

		if (y2 < y1){
			y2++;
			y1--;
		}

		if (x2 > x1){   //x move
			return x1 + "," + y1 + (y1==0?"n":"s");
		}
		else {          //must be y move
			return x1 + "," + y1 + (x1==0?"w":"e");
		}
	}

	public static void removeConnection(String move){
		String key = parseMoveToEdgeKey(move);
		Edge rm = edgeConnections.get(key);

		Box b1 = rm.b1;

		b1.conns.remove(rm);

		if (rm.b2 != null){
			rm.b2.conns.remove(rm);
		}

		edgeConnections.remove(key);

	}

	/**
	 * Graph node.
	 */
	public static class Box {
		public ArrayList<Edge> conns = new ArrayList<>();

		public Box(){
		}
	}

	public static class Edge {

		public Box b1;
		public Box b2;

		public Edge(Box b){
			b.conns.add(this);
			b1 = b;
		}
		public Edge(Box b1, Box b2){
			b1.conns.add(this);
			b2.conns.add(this);

			this.b1 = b1;
			this.b2 = b2;
		}
	}
}
