import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class FileIO {

	private static final File go = new File(Globals.pathToGo);
	private static final File pass = new File(Globals.pathToPass);
	private static final File end = new File(Globals.pathToEnd);
	private static final File moveFile = new File(Globals.pathToMove);

	/**
	 * Polls the folder for the .go/.pass/end files, returns move data when found.
	 * @return move data
	 */
	public static MoveData waitForTurn(){
		while (true){
			if (end.exists()){
				return new MoveData(0, MoveData.Action.END, "");
			}
			else if (go.exists() || pass.exists()){

				MoveData.Action action = go.exists()? MoveData.Action.GO : MoveData.Action.PASS;

				try (Scanner in = new Scanner(moveFile)){

					if (!in.hasNext()){
						return new MoveData(go.lastModified(), MoveData.Action.FIRST, "");
					}

					in.next();
					String data = in.nextLine().trim();
					return new MoveData(go.lastModified(), action, data);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					sleep(50);  //can change
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * The ref polls every 50ms for the move to finish, we have to wait for that. Do so by providing
	 * the time stamp of the start of the turn and comparing against that.
	 * @param stamp The unix timestamp of the start of the turn
	 */
	public static void waitForEndOfTurn(long stamp){
		while (true){
			if ((go.exists() && go.lastModified() == stamp) || (pass.exists() && pass.lastModified() == stamp)){
				try {
					sleep(50);  //can change
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				break;
			}
		}
	}

	/**
	 * Writes a turn result to a file
	 * @param result data to write
	 */
	public static void writeTurnResult(String result){
		try (FileWriter writer = new FileWriter(moveFile)){
			writer.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Helper class for storing move data conveniently.
	 */
	public static class MoveData {
		public long moveStartStamp;    //millis since epoch
		public Action action;
		public String move;            //read from move_file

		public MoveData(long mod, Action toDo, String data){
			moveStartStamp = mod;
			action = toDo;
			this.move = data;
		}


		enum Action {
			GO, PASS, END, FIRST
		}

	}
}
