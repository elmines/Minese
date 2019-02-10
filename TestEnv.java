public class TestEnv {

	public static void main(String[] args) throws EnvException {

		System.out.println("Creating a root environment with variables "+
			"z = 6, y = 5, x = 4...");
		Lexeme a = Environment.newEnvironment();
		Environment.insert(a, "x", 4);
		Environment.insert(a, "y", 5);
		Environment.insert(a, "z", 6);


		System.out.println("Extending the environment with variable x = \"Four\"...");
		Lexeme b = Environment.newScope(a, idList("x"), valList("Four"));

		System.out.println("Displaying the local environment...");
		Environment.displayLocal(b);

		System.out.println("Displaying the full environment...");
		Environment.displayFull(b);

		System.out.printf("Getting the value of x: %s\n", (String) Environment.get(b, "x") );
		System.out.printf("Getting the value of z: %d\n", (Integer) Environment.get(b, "z") );

		System.out.printf("Setting the value of x to \"forty-two\"...\n");
		Environment.set(b, "x", "forty-two");
		System.out.println("Displaying the full environment...");
		Environment.displayFull(b);


		System.out.println("Extending the environment with variable z = \"six\"");
		Lexeme c = Environment.newScope(b, idList("z"), valList("six"));
		System.out.println("Displaying the local environment...");
		Environment.displayLocal(c);
		System.out.println("Inserting a = \"the first letter\"...");
		Environment.insert(c, "a", "the first letter");
		System.out.println("Inserting b = \"the second letter\"...");
		Environment.insert(c, "b", "the second letter");
		System.out.println("Displaying the local environment...");
		Environment.displayLocal(c);

		System.out.println("Setting the value of y to 55, and displaying the full environment...");
		Environment.set(c, "y", 55);
		Environment.displayFull(c);

		try {
			System.out.println("Getting the value of nonexistent variable p...");
			Environment.get(c, "p");
		}
		catch (EnvException e) {
			System.out.println("Caught an exception: "+e.getMessage());
		}

		try {
			System.out.println("Setting value of nonexistent variable q to 79...");
			Environment.set(c, "q", 79);
		}
		catch (EnvException e) {
			System.out.println("Caught an exception: "+e.getMessage());
		}

	}

	private static Lexeme idList(String ...ids) {
		Lexeme node = null; 
		for (String id : ids) {
			Lexeme prev = node;
			Lexeme idName = new Lexeme(Type.IDENTIFIER, id);
			node = new Lexeme(Type.IDNODE, idName, prev);

		}
		return node;
	}

	private static Lexeme valList(Object ...vals) {
		Lexeme node = null;
		for (Object val : vals) {
			Lexeme prev = node;
			Lexeme actVal = new Lexeme(Type.UNKNOWN, val);
			node = new Lexeme(Type.VALNODE, actVal, prev);
		}
		return node;
	}

}
