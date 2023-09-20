import java.util.*;
import java.util.stream.Collectors;

/**
 * Data representation class of the board. Plan is graph structure: boxes are nodes, edges are edges,
 * moves remove edges to subdivide graph.
 */
public class Board {

	public Box[][] cells = new Box[9][9];
	public Map<String, Edge> edgeConnections = new HashMap<>();
	public Set<Edge> removed = new HashSet<>();
	public List<List<Box>> sections = new ArrayList<>();
	public Board(boolean generate) {
		if (generate){
			for (int x = 0; x < 9; x++){
				for (int y = 0; y < 9; y++){
					cells[x][y] = new Box(this);
				}
			}

			this.generateEdges();
			//int x = 1; //breakpoint line
		}
	}

	private void generateEdges(){
		for (int x= 0; x < 9; x++){
			for (int y= 0; y < 9; y++){
				//box based coords: +x is down, +y is right
				String coord = x + "," + y;

				if (x==0){
					//up
					edgeConnections.put(coord+"n", new Edge(cells[0][y], new int[]{0,y}, new int[]{0,y+1}));

				}

				if (x==8){
					//special down
					edgeConnections.put(coord+"s", new Edge(cells[8][y], new int[]{9,y}, new int[]{9,y+1}));
				}
				else{
					//down
					edgeConnections.put(coord+"s", new Edge(cells[x][y], cells[x+1][y], new int[]{x+1,y}, new int[]{x+1,y+1}));
				}

				if (y==0){
					//left
					edgeConnections.put(coord+"w", new Edge(cells[x][0], new int[]{x, 0}, new int[]{x+1,0}));
				}

				if (y==8){
					//special right
					edgeConnections.put(coord+"e", new Edge(cells[x][8], new int[]{x, 9}, new int[]{x+1,9}));
				}
				else{
					//right
					edgeConnections.put(coord+"e", new Edge(cells[x][y], cells[x][y+1], new int[]{x, y+1}, new int[]{x+1,y+1}));
				}
			}
		}
	}

	/**
	 * This can probably be avoided with better coding practices.
	 * Generally, seeks to avoid reversed coords. Also, I wrote the map to label each edge uniquely, so easier to
	 * translate to there.
	 * Thus, this also serves as a translation layer from node coordinates (move_file) to box coordinates (internal).
	 *
	 * @param move move data in the form provide by move_file and the MoveData object
	 * @return a key to Board.edgeConnections to retrieve an edge.
	 */
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

		if (x2 > x1){   //vertical
			return x1 + "," + (y1-(y1==0?0:1)) + (y1==0?"w":"e");
		}
		else {          //horizontal
			return (x1-(x1==0?0:1)) + "," + y1 + (x1==0?"n":"s");
		}
	}


	/**
	 * Do a move on the internal representation of the board. As such, remove one connection.
	 * Then do updates on sections and box weights.
	 *
	 * @param move move string in the form given from move_file or the MoveData object.
	 */
	public void removeConnection(String move, int player){
		String key = parseMoveToEdgeKey(move);
		Edge rm = edgeConnections.get(key);
		rm.removedBy = player;
		removed.add(rm);

		Box b1 = rm.b1;

		b1.conns.remove(rm);
		if (b1.conns.size() == 0){
			b1.capturedBy = player;
		}

		if (rm.b2 != null){
			rm.b2.conns.remove(rm);
			if (rm.b2.conns.size() == 0){
				rm.b2.capturedBy = player;
			}
		}

		edgeConnections.remove(key);
		rm.weight = -Integer.MAX_VALUE;
		evaluateSections();

		b1.updWeight();
		if (rm.b2 != null){
			rm.b2.updWeight();
		}
	}

	/**
	 * Backbone of several things. A "section" is a piece that can be captured in a row.
	 * Generally, these are just a line of boxes that each have 2-3 lines on them already.
	 *
	 * Theoretically should be safe against circular pieces, but maybe more testing needed there.
	 */
	public void evaluateSections() {
		sections.clear();
		List<Box> used = new ArrayList<>();
		int x = 0;
		int y = 0;

		while(y < 9){
			List<Box> section = new ArrayList<>();
			Box head = cells[x][y];

			if (x == 8){
				y++;
				x = 0;
			}
			else{
				x++;
			}

			if (used.contains(head) || (head.conns.size() == 0 || head.conns.size() > 2)){
				continue;   //not interested, check the next box (back to start)
			}
			//head should now be new and necessary to keep track of (adding connection will lead to captures)
			section.add(head);
			used.add(head);

			//connected boxes
			ArrayList<Box> searchQueue = head.conns.stream().flatMap(edge -> edge.connections().stream()).collect(Collectors.toCollection(ArrayList::new));
			//that are not used or do not continue section
			searchQueue.removeAll(used);
			searchQueue.removeIf(b->b.conns.size()>2 || b.conns.size() == 0);

			//not sure what search algorithm this is, hopefully depth-first, not sure it matters
			while (searchQueue.size() > 0){
				Box check = searchQueue.get(0);
				used.add(check);
				section.add(check);
				searchQueue.remove(check);

				//same restrictions as above, now crammed into one line
				searchQueue.addAll(0, check.conns.stream().flatMap(edge -> edge.connections().stream())
											.filter(b->!used.contains(b) && (b.conns.size() == 1 || b.conns.size() ==2)).collect(Collectors.toList()));

			}



			sections.add(section);
			for (Box part : section){
				part.updWeight();
			}
		}

	}


	public Board copy(){
		Board cp = new Board(false);
		for (int x= 0; x < 9; x++){
			for (int y= 0; y < 9; y++){
				cp.cells[x][y] = cells[x][y].copy(cp);
			}
		}
		cp.generateEdges();

		for (Edge e: removed) {
			String move = e.c1[0] + "," + e.c1[1] + " " + e.c2[0] + "," + e.c2[1];
			cp.removeConnection(move, e.removedBy);
		}

		return cp;
	}



	/**
	 * Graph node.
	 */
	public static class Box {
		public Board belongsTo;
		public int capturedBy = -1;
		public ArrayList<Edge> conns = new ArrayList<>();

		public double getWeight() {
			return weight;
		}

		/**
		 * Provides Box weighting. Generally depicts how interested the player should be in that box.
		 * Higher numbers are more interesting.
		 */
		public void updWeight() {

			switch (conns.size()){
				case 0:
					weight = -Integer.MAX_VALUE;
					break;
				case 1:
					weight = belongsTo.sections.stream().filter(s->s.contains(this)).findFirst().get().size();
					break;
				case 2:
					weight = -belongsTo.sections.stream().filter(s->s.contains(this)).findFirst().get().size();
					break;
				case 3:
					weight = .5;
					break;
				default:
					System.out.println("Error: box has weird connections");
			}

			for (Edge conn:conns) {
				conn.updEdgeWeight();
			}


		}

		private double weight = 0;

		public Box(Board belongsTo){
			this.belongsTo = belongsTo;
		}

		public Box copy(Board owner){
			Box cp = new Box(owner);
			cp.weight = getWeight();
			cp.capturedBy = capturedBy;
			return cp;
		}
	}

	/**
	 * Graph Edge
	 */
	public static class Edge {

		public Box b1;
		public Box b2;
		public int [] c1;
		public int [] c2;

		public int removedBy = -1;

		public double weight = 0;

		/**
		 * Provides an edge weighting. Higher numbers are better.
		 * Done by averaging the adjacent box weights. Off of edge are considered to be zero, changeable if necessary.
		 */
		private void updEdgeWeight(){
			if (b2 == null){
				weight = b1.getWeight() / 2;
			}
			else {
				weight = (b1.getWeight() + b2.getWeight()) / 2;
			}
		}

		/**
		 * Two constructors to allow for edges
		 * @param b An edge box
		 * @param c1 coord 1
		 * @param c2 coord 2
		 */
		public Edge(Box b, int [] c1, int [] c2){
			b.conns.add(this);
			b1 = b;

			this.c1 = c1;
			this.c2 = c2;

		}
		public Edge(Box b1, Box b2, int [] c1, int [] c2){
			b1.conns.add(this);
			b2.conns.add(this);

			this.b1 = b1;
			this.b2 = b2;

			this.c1 = c1;
			this.c2 = c2;
		}

		/**
		 * Returns the connections in a way that's easier to use than looking for b1, b2.
		 * @return An arraylist of the 1-2 connections this has.
		 */
		public List<Box> connections(){
			ArrayList<Box> ret = new ArrayList<>();
			ret.add(b1);
			if (b2 != null){
				ret.add(b2);
			}
			return ret;
		}

	}
}
