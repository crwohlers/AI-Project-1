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
			String result = doTurn(data);

			FileIO.writeTurnResult(result);
		}
	}

	/**
	 * Turn logic. Make a call to the Algorithm class for most of it
	 * Remember to add timing logic!
	 * @param data move_file data
	 * @return string to write to the move_file
	 */
	public static String doTurn(FileIO.MoveData data){
		return null;
	}
}
