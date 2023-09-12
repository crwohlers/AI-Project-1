public class Application {
	/**
	 * Main Application loop.
	 */
	public static void runApp(){
		while (true){
			String data = FileIO.waitForTurn();

			if (data.equals("end")) {
				break;
			}
			String result = doTurn(data);

			FileIO.writeTurnResult(result);
		}
	}

	/**
	 * Turn logic. Make a call to the Algorithm class for most of it
	 * Remember to add timing logic!
	 * @param data move_file data, maybe switch to obj to add timing data more easily
	 * @return string to write to the move_file
	 */
	public static String doTurn(String data){
		return null;
	}
}
