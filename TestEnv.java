
public class TestEnv {

	public static void main(String[] args) throws EnvException {

		System.out.println("Creating a root environment with variables "+
			"z = 6, y = 5, x = 4...");
		Environment a = new Environment();
		a.insert("x", 4); a.insert("y", 5); a.insert("z", 6);


		System.out.println("Extending the environment with variable x = \"Four\"...");
		Environment b = a.newScope(idList("x"), valList("Four"));

		System.out.println("Displaying the local environment...");
		b.displayLocal();

		System.out.println("Displaying the full environment...");
		b.displayFull();

		System.out.printf("Getting the value of x: %s\n", (String) b.get("x") );
		System.out.printf("Getting the value of z: %d\n", (Integer) b.get("z") );

		System.out.printf("Setting the value of x to \"forty-two\"...\n");
		b.set("x", "forty-two");
		System.out.println("Displaying the full environment...");
		b.displayFull();

		System.out.println("Inserting the function definition f...");
		b.insert("f", new Lexeme(Type.funcDef));
		System.out.println("Displaying the full environment...");
		b.displayFull();


		System.out.println("Extending the environment with variable z = \"six\"");
		Environment c = b.newScope(idList("z"), valList("six"));
		System.out.println("Displaying the local environment...");
		c.displayLocal();
		System.out.println("Inserting class definition Square...");
		c.insert("Square", new Lexeme(Type.classDef));
		System.out.println("Inserting the array primes...");
		c.insert("primes", new Lexeme(Type.array));
		System.out.println("Displaying the local environment...");
		c.displayLocal();

		System.out.println("Setting the value of y to 55, and displaying the full environment...");
		c.set("y", 55);
		c.displayFull();

		try {
			System.out.println("Getting the value of nonexistent variable p...");
			c.get("p");
		}
		catch (EnvException e) {
			System.out.println("Caught an exception: "+e.getMessage());
		}

		try {
			System.out.println("Setting value of nonexistent variable q to 79...");
			c.set("q", 79);
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
