/*
 * Ethan Mines
 * CS503 - Lusth
 */
public class Environment {

	public static Lexeme newEnvironment() {
		return Lexeme.cons(Type.environment,
					Lexeme.cons(Type.TABLE, null, null),
					null
		);

	}


	/**
	 * @return The inserted value
	 */
	public static Object insert(Lexeme env, String id, Object val) {
		Lexeme idLeaf = Lexeme.literal(Type.IDENTIFIER, id, -1);
		Lexeme valLeaf = Lexeme.literal(Type.UNKNOWN, val, -1);
		env.car().setCar( Lexeme.cons(Type.IDNODE,   idLeaf, env.caar()) );
		env.car().setCdr( Lexeme.cons(Type.VALNODE, valLeaf, env.cdar()) );
		return val;
	}

	public static Object get(Lexeme env, String id) throws EnvException {
		Lexeme valLeaf = getValLeaf(env, id);
		return valLeaf.value();
	}

	/**
	 * @return The old value previously there
	 */
	public static Object set(Lexeme env, String id, Object val) throws EnvException {
		Lexeme valLeaf = getValLeaf(env, id);
		Object oldVal = valLeaf.value();
		valLeaf.setValue(val);	
		return oldVal;
	}


	public static Lexeme newScope(Lexeme env, Lexeme ids, Lexeme vals) {
		Lexeme local = Environment.newEnvironment();
		local.setCar( Lexeme.cons(Type.TABLE, ids, vals) );
		local.setCdr( env );
		return local;
	}

	public static void displayLocal(Lexeme env) {
		System.out.println(Environment.toString(env));
	}

	public static void displayFull(Lexeme env) {
		String indentation = "";
		while (env != null) {
			String unindented = Environment.toString(env);
			String indented = indentation + unindented.replaceAll("\n", "\n"+indentation);
			System.out.println(indented);
			env = env.cdr();
			indentation += "\t";
		}
	}

	public static String toString(Lexeme env) {
		StringBuilder sb = new StringBuilder();
		Lexeme ids = env.caar();
		Lexeme vals = env.cdar();

		boolean firstLine = true;
		while (ids != null) {
			String id = (String) ids.car().value();
			Object val = vals.car().value();

			if (!firstLine) sb.append("\n");
			sb.append( String.format("%s: %s", id, val.toString()) );

			ids = ids.cdr();
			vals = vals.cdr();
			firstLine = false;
		}
		return sb.toString();
	}

	private static Lexeme getValLeaf(Lexeme env, String id) throws EnvException {
		while (env != null) {
			Lexeme ids = env.caar();
			Lexeme vals = env.cdar();

			while (ids != null) {
				String candId = (String) ids.car().value();
				if (id.equals(candId)) return vals.car();

				ids = ids.cdr();
				vals = vals.cdr();
			}
			env = env.cdr();
		}
		throw new EnvException("Undefined variable "+id);
	}

}
