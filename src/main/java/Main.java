import java.util.Arrays;

public class Main {

	public static void main(String[] args){

		for (int i = 0; i < args.length; i++){
			if (i < args.length-1 && args[i].equals("--name")){
				Globals.teamName = args[i+1];
				i++;
			}
		}

		System.out.println(Globals.teamName);

		Application.runApp();
	}
}
