package openSW;

public class kuir {

	public static void main(String[] args) {
		
		if (args[0].equals("-c")) {
			makeCollection mc = new makeCollection(args[1]);
		}
		else if (args[0].equals("-k")) {
			makeKeyword mk = new makeKeyword(args[1]);
		}
	}

}
