import Algorithm.Board;

import java.util.Collection;

import static java.lang.Thread.sleep;

public class Application {
	/**
	 * Main Application loop.
	 */
	public static void runApp(){
		while (true){
			FileIO.MoveData data = FileIO.waitForTurn();

			if (data.action == FileIO.MoveData.Action.END) {
				break;
			}
			String result = "";
			if (data.action == FileIO.MoveData.Action.FIRST){
				result = doTurn(data, true);
			}
			else {
				result = doTurn(data, false);
			}

			FileIO.writeTurnResult(result);
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			FileIO.waitForEndOfTurn(data.moveStartStamp);
		}
	}

	/**
	 * Turn logic. Make a call to the Algorithm class for most of it
	 * Remember to add timing logic!
	 * @param data move_file data
	 * @return string to write to the move_file
	 */
	public static String doTurn(FileIO.MoveData data, boolean first){
		String move = "";
		System.out.println("act: " + data.action + " move: " + data.move);

		if (!first && !data.move.equals("0,0 0,0")){
			System.out.println("removing " + data.move);
			Board.removeConnection(data.move);
		}

		if (data.action == FileIO.MoveData.Action.PASS){
			return Globals.teamName + " 0,0 0,0";
		}

		Board.evaluateSections();

		while (move.length() == 0 || !Board.edgeConnections.containsKey(Board.parseMoveToEdgeKey(move))) {

			if (Board.sections.size() > 0){
				Board.Edge line = Board.sections.stream().flatMap(Collection::stream).filter(b->b.conns.size() == 1).findFirst().orElse(Board.sections.get(0).get(0)).conns.get(0);

				move = line.c1[0] + "," + line.c1[1] + " " + line.c2[0] + "," + line.c2[1];
			}
			else{
				int x = (int) (Math.random() * 9);
				int y = (int) (Math.random() * 9);

				int horiz = (int) (Math.random() * 2);

				int x2 = x + (horiz == 1 ? 1 : 0);
				int y2 = y + (horiz == 0 ? 1 : 0);
				move = x + "," + y + " " + x2 + "," + y2;
			}
		}

		Board.removeConnection(move);
		System.out.println(Globals.teamName + " " + move);
		return Globals.teamName + " " + move;
	}
}
