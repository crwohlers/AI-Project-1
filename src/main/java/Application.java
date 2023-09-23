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
		if (!first && !data.move.equals("0,0 0,0")){
			//System.out.println("removing " + data.move);
			Globals.mainBoard.removeConnection(data.move, 0);
		}

		if (data.action == FileIO.MoveData.Action.PASS){
			return Globals.teamName + " 0,0 0,0";
		}

		String move;
		while (true) {
			Board.Edge line = Algorithm.minimaxFindBest(data.moveStartStamp);
			//System.out.println(line.weight);
			move = line.c1[0] + "," + line.c1[1] + " " + line.c2[0] + "," + line.c2[1];
			//System.out.println(Globals.teamName + " " + move);

			if (!Globals.mainBoard.edgeConnections.containsKey(Board.parseMoveToEdgeKey(move))) {
				continue;
			}

			Globals.mainBoard.removeConnection(move, 1);
			break;
		}


		return Globals.teamName + " " + move;
	}
}
