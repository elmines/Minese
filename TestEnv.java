public class TestEnv {

	public static void main(String[] args) throws EnvException {

		System.out.println("Creating a root environment with variables "+
			"z = 6, y = 5, x = 4...");
		Lexeme a = Environment.newEnvironment();

		Environment.insert(a, Lexeme.literal(Type.IDENTIFIER, "x", -1), Lexeme.literal(Type.INTEGER, 4, -1));
		Environment.insert(a, Lexeme.literal(Type.IDENTIFIER, "y", -1), Lexeme.literal(Type.INTEGER, 5, -1));
		Environment.insert(a, Lexeme.literal(Type.IDENTIFIER, "z", -1), Lexeme.literal(Type.INTEGER, 6, -1));


		System.out.println("Extending the environment with variable x = \"Four\"...");
		Lexeme b = Environment.newScope(a, idList("x"), valList("Four"));

		System.out.println("Displaying the local environment...");
		Environment.displayLocal(b);

		System.out.println("Displaying the full environment...");
		Environment.displayFull(b);

		System.out.printf("Getting the value of x: %s\n",
			(String) Environment.get(b, Lexeme.literal(Type.IDENTIFIER, "x", -1)).value()
		);
		System.out.printf("Getting the value of z: %d\n",
			(Integer) Environment.get(b, Lexeme.literal(Type.IDENTIFIER, "z", -1)).value()
		);

		System.out.printf("Setting the value of x to \"forty-two\"...\n");
		Environment.set(b, Lexeme.literal(Type.IDENTIFIER, "x", -1), Lexeme.literal(Type.STRING, "forty-two", -1));
		System.out.println("Displaying the full environment...");
		Environment.displayFull(b);


		System.out.println("Extending the environment with variable z = \"six\"");
		Lexeme c = Environment.newScope(b, idList("z"), valList("six"));
		System.out.println("Displaying the local environment...");
		Environment.displayLocal(c);
		System.out.println("Inserting a = \"the first letter\"...");
		Environment.insert(c,
			Lexeme.literal(Type.IDENTIFIER, "a", -1), Lexeme.literal(Type.STRING, "the first letter", -1)
		);
		System.out.println("Inserting b = \"the second letter\"...");
		Environment.insert(c,
			Lexeme.literal(Type.IDENTIFIER, "b", -1), Lexeme.literal(Type.STRING, "the second letter", -1)
		);
		System.out.println("Displaying the local environment...");
		Environment.displayLocal(c);

		System.out.println("Setting the value of y to 55, and displaying the full environment...");
		Environment.set(c, Lexeme.literal(Type.IDENTIFIER, "y",-1), Lexeme.literal(Type.INTEGER, 55, -1) );
		Environment.displayFull(c);

		try {
			System.out.println("Getting the value of nonexistent variable p...");
			Environment.get(c, Lexeme.literal(Type.IDENTIFIER, "p", -1));
		}
		catch (EnvException e) {
			System.out.println("Caught an exception: "+e.getMessage());
		}

		try {
			System.out.println("Setting value of nonexistent variable q to 79...");
			Environment.set(c, Lexeme.literal(Type.IDENTIFIER, "q", -1), Lexeme.literal(Type.INTEGER, 79, -1) );
		}
		catch (EnvException e) {
			System.out.println("Caught an exception: "+e.getMessage());
		}

	}

	private static Lexeme idList(String ...ids) {
		Lexeme node = null; 
		for (String id : ids) {
			Lexeme prev = node;
			Lexeme idName = Lexeme.literal(Type.IDENTIFIER, id, -1);
			node = Lexeme.cons(Type.IDNODE, idName, prev);

		}
		return node;
	}

	private static Lexeme valList(Object ...vals) {
		Lexeme node = null;
		for (Object val : vals) {
			Lexeme prev = node;
			Lexeme actVal = Lexeme.literal(Type.UNKNOWN, val, -1);
			node = Lexeme.cons(Type.VALNODE, actVal, prev);
		}
		return node;
	}

}
