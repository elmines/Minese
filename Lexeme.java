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
		this.val = null;
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

	public Lexeme(int lineNumber, Type type, Object value) {
		this.lineNumber = lineNumber;
		this.type = type;
		this.val = value;
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

	private final Object val;
	private Lexeme l, r;

	private String valueString() {
		if (this.val != null) return this.val.toString();
		else                  return "";
	}

	private static int inferLineNumber(Lexeme l, Lexeme r) {
		if      (l != null) return l.lineNumber;
		else if (r != null) return r.lineNumber;
		else                return -1;
	}

}
