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
	public static Lexeme insert(Lexeme env, Lexeme id, Lexeme val) {

		env.car().setCar( Lexeme.cons(Type.IDNODE,   id, env.caar()) );
		env.car().setCdr( Lexeme.cons(Type.VALNODE, val, env.cdar()) );
		return val;
	}

	public static Lexeme get(Lexeme env, Lexeme id) throws EnvException {
		Lexeme parent = getValParent(env, id);
		return parent.car();
	}


	/**
	 * @return The old value previously there
	 */
	public static Lexeme set(Lexeme env, Lexeme id, Lexeme val) throws EnvException {
		Lexeme parent = getValParent(env, id);
		Lexeme oldVal = parent.car();
		parent.setCar(val);
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
			sb.append( String.format("%s: %s", id, ids.car().toString()) );

			ids = ids.cdr();
			vals = vals.cdr();
			firstLine = false;
		}
		if (sb.length() == 0) return "EMPTY";
		return sb.toString();
	}

	private static Lexeme getValParent(Lexeme env, Lexeme id) throws EnvException {
		while (env != null) {
			Lexeme ids = env.caar();
			Lexeme vals = env.cdar();

			while (ids != null) {
				Lexeme candidate = ids.car();
				if (id.equals(candidate)) return vals;

				ids = ids.cdr();
				vals = vals.cdr();
			}
			env = env.cdr();
		}
		throw new EnvException("Undefined variable "+id);
	}

}
