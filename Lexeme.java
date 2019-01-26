/*
 * Ethan Mines
 * CS503 - Lusth
 */
class Lexeme {

	public final Type type;
	public final int lineNumber;

	public Lexeme(int lineNumber, Type type) {
		this(lineNumber, type, "");
	}

	public Lexeme(int lineNumber, Type type, String value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = value;
		this.intVal = null;
		this.boolVal = null;
	}

	public Lexeme(int lineNumber, Type type, Integer value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = null;
		this.intVal = value;
		this.boolVal = null;
	}

	public Lexeme(int lineNumber, Type type, Boolean value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = null;
		this.intVal = null;
		this.boolVal = value;
	}

	@Override
	public String toString() {
		String value = this.valueString();
		if (value.length() > 0) return String.format("%d: %s %s", this.lineNumber, this.type.toString(), value);
		else                    return String.format("%d: %s", this.lineNumber, this.type.toString());
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
