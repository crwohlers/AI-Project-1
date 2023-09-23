public class Main {

	public static void main(String[] args){
		boolean argSet = false;
		for (int i = 0; i < args.length; i++){
			if (i < args.length-1 && args[i].equals("--name")){
				Globals.setTeamName(args[i+1]);
				argSet = true;
				break;
			}
		}

		if (!argSet){
				Globals.setTeamName("FlyingSolo");
		}

		//System.out.println(Globals.teamName);

		Application.runApp();
	}
}
