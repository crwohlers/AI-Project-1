/**
 * Contains project global variable definitions
 */
public class Globals {

	public static String teamName = "TBD";
	public static String pathToMove = "move_file";
	public static String pathToGo = teamName + ".go";
	public static String pathToPass = teamName + ".pass";
	public static String pathToEnd = "end_game";

	public static Board mainBoard = new Board(true);

	public static final int time_limit = 2 * 1000; //ms seems good
	public static final int pass_time_limit = 2000;


	public static void setTeamName(String teamName) {
		Globals.teamName = teamName;
		pathToGo = teamName + ".go";
		pathToPass = teamName + ".pass";
	}
}
