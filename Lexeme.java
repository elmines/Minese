/*
 * Ethan Mines
 * CS503 - Lusth
 */
class Lexeme {

	public final Type type;

	public Lexeme(Type type) {
		this(type, "");
	}

	public Lexeme(Type type, String value) {
		this.type = type;

		this.strVal = value;
		this.intVal = null;
		this.boolVal = null;
	}

	public Lexeme(Type type, Integer value) {
		this.type = type;

		this.strVal = null;
		this.intVal = value;
		this.boolVal = null;
	}

	public Lexeme(Type type, Boolean value) {
		this.type = type;

		this.strVal = null;
		this.intVal = null;
		this.boolVal = value;
	}

	@Override
	public String toString() {
		String value = this.valueString();
		if (value.length() > 0) return String.format("%s %s", this.type.toString(), value);
		else                    return this.type.toString();
	}

	private final Boolean boolVal;
	private final Integer intVal;
	private final String strVal;

	private String valueString() {
		if (this.intVal != null) return this.intVal.toString();
		else if (this.boolVal != null) return this.boolVal.toString();
		else if (this.strVal != null) return this.strVal;
		else                          return "";
	}
}
