import Algorithm.Board;

import java.util.Arrays;

public class Main {

	public static void main(String[] args){

		for (int i = 0; i < args.length; i++){
			if (i < args.length-1 && args[i].equals("--name")){
				Globals.setTeamName(args[i+1]);
				i++;
			}
		}
		Board.edgeConnections.values();     //load board class, results ignored intentionally
		//System.out.println(Globals.teamName);

		Application.runApp();
	}
}
