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

	public Lexeme(Object value, Lexeme l, Lexeme r) {
		this.lineNumber = -1;
		this.type = Type.UNKNOWN;
		this.val = value;
		this.l = l;
		this.r = r;
	}
	public Lexeme(Object value) {
		this(value, null, null);
	}
	public Lexeme(Type type, Object value) {
		this.lineNumber = -1;
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

	public Lexeme car() {return this.getLeft();}
	public Lexeme cdr() {return this.getRight();}

	public Lexeme caar() {return this.car().car();}
	public Lexeme cdar() {return this.car().cdr();}
	public Lexeme cadr() {return this.cdr().car();}
	public Lexeme cddr() {return this.cdr().cdr();}
	public void setCar(Lexeme lex) {this.setLeft(lex);}
	public void setCdr(Lexeme lex) {this.setRight(lex);}

	public Object value(){return this.val;}
	public Object getValue(){return this.value();}
	public void setValue(Object v){this.val = v;}



	@Override
	public String toString() {
		if (this.val != null) return this.val.toString();
		else                  return this.type.toString();
	}

	public String lexString() {
		String value = this.toString();
		if (value.length() > 0) return String.format("%d: %s %s", this.lineNumber, this.type.toString(), value);
		else                    return String.format("%d: %s", this.lineNumber, this.type.toString());
	}

	private Object val;
	private Lexeme l, r;

	
	private static int inferLineNumber(Lexeme l, Lexeme r) {
		if      (l != null) return l.lineNumber;
		else if (r != null) return r.lineNumber;
		else                return -1;
	}

}
