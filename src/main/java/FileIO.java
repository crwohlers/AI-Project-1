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
	 * Polls the folder for the .go/.pass files, returns move data when found.
	 * @return move data, probably switch to obj for timing info
	 */
	public static MoveData waitForTurn(){
		while (true){
			if (go.exists()){
				Scanner in = null;
				try {
					in = new Scanner(moveFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				String data = in.nextLine();
				in.close();
				return new MoveData(go.lastModified(), MoveData.Action.GO, data);
			}
			else if (pass.exists()){
				return new MoveData(pass.lastModified(), MoveData.Action.PASS, Globals.teamName + "0,0 0,0");
			}
			else if (end.exists()){
				return new MoveData(0, MoveData.Action.END, "");
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
	 * Writes a turn result to a file
	 * @param result data to write
	 */
	public static void writeTurnResult(String result){
		try {
			FileWriter writer = new FileWriter(moveFile);
			writer.write(result);
			writer.close();
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
		public String data;            //read from move_file

		public MoveData(long mod, Action toDo, String data){
			moveStartStamp = mod;
			action = toDo;
			this.data = data;
		}


		enum Action {
			GO, PASS, END
		}

	}
}
