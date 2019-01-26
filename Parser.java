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

		Lexeme expr = null;
		if (check(Type.ASSIGN)) {
			advance();
			expr = expression();
		}
		match(Type.SEMICOLON);
		return new Lexeme(Type.varDef, id, expr);
	}
	private boolean varDefPending() { return check(Type.VAR); }

	private Lexeme expression() throws LexException, SyntaxException {
		Lexeme u = unary();
		Lexeme op = null;
		if (operatorPending()) {
			op = operator();
			op.setRight(expression());
		}
		return new Lexeme(Type.expression, u, op);
	}
	private boolean expressionPending() {
		return false;
	}

	private Lexeme operator() throws LexException, SyntaxException {
		return match(Group.BINARY);
	}
	private boolean operatorPending() {
		return check(Group.BINARY);
	}

	private Lexeme unary() throws LexException, SyntaxException {

		if (check(Type.MINUS)) {
			Lexeme uMinus = Lexeme.toUminus(advance());
			return new Lexeme(Type.unary, uMinus, unary());
		}
		if (check(Type.NOT))
			return new Lexeme(Type.unary, advance(), unary());


		if (check(Type.OPAREN)) {
			advance();
			Lexeme expr = expression();
			match(Type.CPAREN);
			return expr;
		}

		if (check(Type.IDENTIFIER)) return advance();
		if (check(Type.BOOLEAN))    return advance();
		if (check(Type.INTEGER))    return advance();
		if (check(Type.STRING))     return advance();
		

		throw new SyntaxException(String.format(syntaxFmt, this.curr.lineNumber, "unary", this.curr.type));

	}

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

	/**
	 * @return The matched lexeme
	 */
	private Lexeme match(Type t) throws LexException, SyntaxException {
		if (!check(t)) {
			throw new SyntaxException(String.format(syntaxFmt, this.curr.lineNumber, t, this.curr.type));
		}
		return advance();
	}

	/**
	 * @return The matched lexeme
	 */
	private Lexeme match(Group g) throws LexException, SyntaxException {
		if (!check(g)) {
			String fmt = "Error on line %d: Expected %s, found %s";
			throw new SyntaxException(String.format(fmt, this.curr.lineNumber, g.name, this.curr.type));
		}
		return advance();
	}

	private boolean check(Type t) {return t == this.curr.type;}
	private boolean check(Group g) {return g.contains(this.curr.type);}


	/**
	 * @return The previous lexeme
	 */
	private Lexeme advance() throws LexException {
		Lexeme old = this.curr;
		this.curr = lexer.lex();	
		return old;
	}


	private static final String syntaxFmt = "Error on line %d: Expected %s, found %s";
}
