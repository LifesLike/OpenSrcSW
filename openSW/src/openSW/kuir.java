package openSW;

public class kuir {

	public static void main(String[] args) {
		
		long beforeTime = System.nanoTime();
		
		if (args[0].equals("-c")) {
			makeCollection mc = new makeCollection(args[1]);
		}
		else if (args[0].equals("-k")) {
			makeKeyword mk = new makeKeyword(args[1]);
		}
		else if (args[0].equals("-i")) {
			indexer ixr = new indexer(args[1]);

		}
		
		long afterTime = System.nanoTime();
		long diff = (afterTime - beforeTime);
		System.out.println(diff);
	}

}
