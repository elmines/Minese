/*
 * Ethan Mines
 * CS503 - Lusth
 */
class Lexeme {

	public final Type type;
	public final int lineNumber;

	/*
	 * Construc a nonterminal lexeme
	 */
	public Lexeme(Type type, Lexeme l, Lexeme r, int lineNumber) {
		this.type = type;
		this.strVal = null;
		this.intVal = null;
		this.boolVal = null;

		this.l = l;
		this.r = r;
		this.lineNumber = lineNumber;

	}
	/*
	 * Construc a nonterminal lexeme, with line number inferred from l and r
	 */
	public Lexeme(Type type, Lexeme l, Lexeme r) {
		this(type, l, r, inferLineNumber(l, r));
	}

	public Lexeme(int lineNumber, Type type) {
		this(lineNumber, type, "");
	}


	public Lexeme(int lineNumber, Type type, String value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = value;
		this.intVal = null;
		this.boolVal = null;
		this.l = null;
		this.r = null;

	}

	public Lexeme(int lineNumber, Type type, Integer value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = null;
		this.intVal = value;
		this.boolVal = null;
		this.l = null;
		this.r = null;

	}

	public Lexeme(int lineNumber, Type type, Boolean value) {
		this.lineNumber = lineNumber;
		this.type = type;

		this.strVal = null;
		this.intVal = null;
		this.boolVal = value;
		this.l = null;
		this.r = null;
	}

	/**
	 * @return A new Lexeme with all of orig's fields, except for its new Type
	 */
	public static Lexeme toUminus(Lexeme orig) {
		return new Lexeme(Type.UMINUS, orig.l, orig.r);
	}

	public Lexeme getLeft() {return this.l;}
	public Lexeme getRight() {return this.r;}
	public void setLeft(Lexeme l) {this.l = l;}
	public void setRight(Lexeme r) {this.r = r;}


	@Override
	public String toString() {
		String value = this.valueString();
		if (value.length() > 0) return String.format("%d: %s %s", this.lineNumber, this.type.toString(), value);
		else                    return String.format("%d: %s", this.lineNumber, this.type.toString());
	}

	private final Boolean boolVal;
	private final Integer intVal;
	private final String strVal;
	private Lexeme l, r;

	private String valueString() {
		if (this.intVal != null) return this.intVal.toString();
		else if (this.boolVal != null) return this.boolVal.toString();
		else if (this.strVal != null) return this.strVal;
		else                          return "";
	}

	private static int inferLineNumber(Lexeme l, Lexeme r) {
		if      (l != null) return l.lineNumber;
		else if (r != null) return r.lineNumber;
		else                return -1;
	}

}
