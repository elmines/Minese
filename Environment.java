/*
 * Ethan Mines
 * CS503 - Lusth
 */
public class Environment extends Lexeme {

	public Environment() {
		super(Type.environment,
			new Lexeme(Type.TABLE, null, null),
			null
		);
	}


	/**
	 * @return The inserted value
	 */
	public Object insert(String id, Object value) {

		Lexeme idLeaf = new Lexeme(Type.IDENTIFIER, id);
		Lexeme valLeaf = new Lexeme(value);

		this.car().setCar( new Lexeme(Type.IDNODE,   idLeaf, this.caar()) );
		this.car().setCdr( new Lexeme(Type.VALNODE, valLeaf, this.cdar()) );

		return value;
	}

	public Object get(String id) throws EnvException {
		Lexeme valLeaf = getValLeaf(id);
		return valLeaf.value();
	}

	/**
	 * @return The old value previously there
	 */
	public Object set(String id, Object val) throws EnvException {
		Lexeme valLeaf = getValLeaf(id);
		Object oldVal = valLeaf.value();
		valLeaf.setValue(val);	
		return oldVal;
	}

	private Lexeme getValLeaf(String id) throws EnvException {
		Environment env = this;
		while (env != null) {
			Lexeme ids = env.caar();
			Lexeme vals = env.cdar();

			while (ids != null) {
				String candId = (String) ids.car().value();
				if (id.equals(candId)) return vals.car();

				ids = ids.cdr();
				vals = vals.cdr();
			}
			env = (Environment) env.cdr();
		}
		throw new EnvException("Undefined variable "+id);
	}

	public Environment newScope(Lexeme ids, Lexeme vals) {
		Environment local = new Environment();
		local.setCar( new Lexeme(Type.TABLE, ids, vals) );
		local.setCdr( this );
		return local;
	}

	public void displayLocal() {
		System.out.println(this.toString());
	}

	public void displayFull() {

		String indentation = "";
		Environment env = this;

		while (env != null) {

			String unindented = env.toString();
			String indented = indentation + unindented.replaceAll("\n", "\n"+indentation);
			System.out.println(indented);

			env = (Environment) env.cdr();
			indentation += "\t";
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Lexeme ids = this.caar();
		Lexeme vals = this.cdar();

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

}
