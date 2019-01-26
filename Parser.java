/*
 * Ethan Mines
 * CS503 - Lusth
 */
import java.io.PushbackInputStream;

public class Parser {

	public Parser(PushbackInputStream in) throws LexException {
		this.lexer = new Lexer(in);
		advance();
	}

	public Lexeme program() throws LexException, SyntaxException {
		Lexeme d = def();
		Lexeme p = programPending() ? program() : null;
		return new Lexeme(Type.program, d, p);
	}	
	private boolean programPending(){return defPending();}

	private Lexeme def() throws LexException, SyntaxException {
		if (varDefPending()) return varDef();
		if (funcDefPending()) return funcDef();
		return classDef();
	}
	private boolean defPending() {
		return varDefPending() ||
			funcDefPending() ||
			classDefPending();
	}

	private Lexeme varDef() throws LexException, SyntaxException {
		match(Type.VAR);
		Lexeme id = match(Type.IDENTIFIER);

		//match(Type.EQ);
		Lexeme expr = null;

		match(Type.SEMICOLON);
		return new Lexeme(Type.varDef, id, expr);
	}
	private boolean varDefPending() { return check(Type.VAR); }

	private Lexeme funcDef() throws LexException, SyntaxException {
		return new Lexeme(-1, Type.UNKNOWN);
	}
	private boolean funcDefPending() {return false;}

	private Lexeme classDef() throws LexException, SyntaxException {
		return new Lexeme(-1, Type.UNKNOWN);
	}
	private boolean classDefPending() {return false;}

	private Lexer lexer;
	private Lexeme curr;

	private boolean check(Type t) {return t == this.curr.type;}

	/**
	 * @return The previous lexeme
	 */
	private Lexeme advance() throws LexException {
		Lexeme old = this.curr;
		this.curr = lexer.lex();	
		return old;
	}
	private Lexeme match(Type t) throws LexException, SyntaxException {
		if (!check(t)) {
			String fmt = "Error on line %d: Expected %s, found %s";
			throw new SyntaxException(String.format(fmt, this.curr.lineNumber, this.curr.type, t));
		}
		return advance();
	}

}
