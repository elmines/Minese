/*
 * Ethan Mines
 * CS503 - Lusth
 */
import java.io.PushbackInputStream;

public class Recognizer {

	public Recognizer(PushbackInputStream in) throws LexException {
		this.lexer = new Lexer(in);
		advance();
	}

	public Lexeme program() throws LexException, SyntaxException {
		Lexeme definitions = defPending() ? defs() : null;
		return Lexeme.cons(Type.program, null, definitions);
	}	
	private boolean programPending(){return defPending();}

	//General definitions
	private Lexeme defs() throws LexException, SyntaxException {
		Lexeme head = def();
		Lexeme following = defPending() ? defs() : null;
		return Lexeme.cons(Type.defs, head, following);
	}
	private Lexeme def() throws LexException, SyntaxException {
		if (varDefPending())  return varDef();
		if (funcDefPending()) return funcDef();
		return classDef();
	}
	private boolean defPending() {
		return  varDefPending()  ||
			funcDefPending() ||
			classDefPending();
	}

	//Classes
	private Lexeme classDef() throws LexException, SyntaxException {
		match(Type.CLASS);
		Lexeme className = match(Type.IDENTIFIER);
		Lexeme superclassName = null;
		if (check(Type.EXTENDS)) {
			advance();
			superclassName = match(Type.IDENTIFIER);
		}
		match(Type.OCURLY);
		Lexeme body = program();
		match(Type.CCURLY);
		Lexeme cls = Lexeme.cons(Type.classDef, className, body);

		if (superclassName != null) return Lexeme.cons(Type.subclassDef, superclassName, cls);
		return cls;


	}
	private boolean classDefPending() {return check(Type.CLASS);}

	//Named functions
	private Lexeme funcDef() throws LexException, SyntaxException {
		match(Type.DEFINE);
		Lexeme id = match(Type.IDENTIFIER);
		Lexeme body = funcBody();

		return Lexeme.cons(Type.funcDef, id, body);
	}
	private boolean funcDefPending() {return check(Type.DEFINE);}
	private Lexeme funcBody() throws LexException, SyntaxException {
		match(Type.OPAREN);
		Lexeme params = null;
		if (paramsListPending()) params = paramsList();
		match(Type.CPAREN);
		Lexeme statements = block();
		return Lexeme.cons(Type.funcBody, params, statements);
	}
	private Lexeme paramsList() throws LexException, SyntaxException {
		match(Type.VAR);
		Lexeme param = match(Type.IDENTIFIER);		

		if (check(Type.COMMA)) {
			advance();
			return Lexeme.cons(Type.paramsList, param, paramsList());
		}
		return Lexeme.cons(Type.paramsList, param, null);
	}
	private boolean paramsListPending() {return check(Type.VAR);}



	private Lexeme varDef() throws LexException, SyntaxException {
		match(Type.VAR);
		Lexeme id = match(Type.IDENTIFIER);
		Lexeme expr = null;
		if (check(Type.ASSIGN)) {
			advance();
			expr = expression();
		}
		match(Type.SEMICOLON);
		return Lexeme.cons(Type.varDef, id, expr);
	}
	private boolean varDefPending() { return check(Type.VAR); }

	//Blocks and statements
	private Lexeme block() throws LexException, SyntaxException {
		if (check(Type.OCURLY)) {
			Lexeme ocurly = advance();
			Lexeme stmts = statementsPending() ? statements() : null;
			match(Type.CCURLY);
			Lexeme blk = Lexeme.cons(Type.block, null, stmts);
			return blk;
		}

		return statement();
	}
	private boolean blockPending() { return check(Type.OCURLY) || statementPending(); }
	private Lexeme statements() throws LexException, SyntaxException {
		Lexeme stmt = statement();
		Lexeme following = statementsPending() ? statements() : null;
		return Lexeme.cons(Type.statements, stmt, following);

	}
	private boolean statementsPending() {return statementPending();}
	private Lexeme statement() throws LexException, SyntaxException {
		if (defPending())             return def();
		if (returnStatementPending()) return returnStatement();

		if (condStatementPending())   return condStatement();

		if (whileStatementPending())  return whileStatement();

		if (expressionPending()) {
			Lexeme expr = expression();
			match(Type.SEMICOLON);
			return expr;
		}

		throw new SyntaxException(String.format(syntaxFmt, this.curr.lineNumber, "statement", this.curr.type));
	}
	private boolean statementPending() {
		return  defPending()             ||
			returnStatementPending() ||
			condStatementPending()   ||
			whileStatementPending()  ||
			expressionPending()        ;
	}

	//Selection and iteration
	private Lexeme condStatement() throws LexException, SyntaxException {
		match(Type.IF);
		match(Type.OPAREN);
		Lexeme condition = expression();
		match(Type.CPAREN);
		Lexeme body = block();

		Lexeme alt = null;
		if (check(Type.ELSE)) {
			advance();
			alt = block();
		}
		return Lexeme.cons(Type.condStatement, body, alt);
	}
	private boolean condStatementPending() { return check(Type.IF); }
	private Lexeme whileStatement() throws LexException, SyntaxException {
		match(Type.WHILE);
		match(Type.OPAREN);
		Lexeme condition = expression();
		match(Type.CPAREN);
		Lexeme body = block();
		return Lexeme.cons(Type.whileStatement, condition, body);
	}
	private boolean whileStatementPending() {return check(Type.WHILE);}

	//Return statements
	private Lexeme returnStatement() throws LexException, SyntaxException {
		Lexeme returnKeyword = match(Type.RETURN);
		Lexeme expr = expressionPending() ? expression() : null;
		match(Type.SEMICOLON);
		return Lexeme.cons(Type.returnStatement, null, expr);
	}
	private boolean returnStatementPending() { return check(Type.RETURN); }


	//Expressions
	private Lexeme expression() throws LexException, SyntaxException {
		Lexeme u = unary();
		if (check(Type.OPAREN)) {
			advance();
			Lexeme args = exprList();
			match(Type.CPAREN);
			Lexeme funcCall = Lexeme.cons(Type.funcCall, u, args);

			u = funcCall;
		}
		if (check(Type.OBRACK)) {
			advance();
			Lexeme index = expression();
			match(Type.CBRACK);
			Lexeme element = Lexeme.cons(Type.arrayElement, u, index);

			u = element;
		}


		if (operatorPending()) {
			Lexeme op = operator();
			op.setRight(expression());
			return Lexeme.cons(Type.expression, u, op);
		}
		return u;
	}
	private Lexeme exprList() throws LexException, SyntaxException {
		Lexeme expr = expression();
		Lexeme remaining = null;
		if (check(Type.COMMA)) {
			advance();
			remaining = exprList();
		}
		return Lexeme.cons(Type.exprList, expr, remaining);
	}
	private boolean expressionPending() { return unaryPending(); }
	private Lexeme operator() throws LexException, SyntaxException { return match(Group.BINARY); }
	private boolean operatorPending() { return check(Group.BINARY); }

	//Unaries
	private Lexeme unary() throws LexException, SyntaxException {
		if (check(Type.MINUS)) {
			Lexeme uMinus = Lexeme.toUminus(advance());
			return Lexeme.cons(Type.unary, uMinus, unary());
		}
		if (check(Type.NOT)) {
			Lexeme not = advance();
			return Lexeme.cons(Type.unary, not, unary());
		}
		if (check(Type.OPAREN)) {
			advance();
			Lexeme expr = expression();
			match(Type.CPAREN);
			return expr;
		}
		if (arrayPending())         return array();

		if (anonFunctionPending())  return anonFunction();

		if (check(Type.IDENTIFIER)) return advance();
		if (check(Type.BOOLEAN))    return advance();
		if (check(Type.INTEGER))    return advance();
		if (check(Type.STRING))     return advance();
		

		throw new SyntaxException(String.format(syntaxFmt, this.curr.lineNumber, "unary", this.curr.type));

	}
	private boolean unaryPending() {
		return  check(Type.MINUS)       ||
			check(Type.NOT)         ||
			check(Type.OPAREN)      ||
			arrayPending()          ||
			anonFunctionPending()   ||
			check(Type.IDENTIFIER)  ||
			check(Type.BOOLEAN)     ||
			check(Type.INTEGER)     ||
			check(Type.STRING);
	}
	private Lexeme array() throws LexException, SyntaxException {
		match(Type.OBRACK);
		Lexeme elements = null;
		if (expressionPending()) elements = exprList();
		match(Type.CBRACK);
		return Lexeme.cons(Type.array, null, elements);
	}
	private boolean arrayPending() { return check(Type.OBRACK); }
	private Lexeme anonFunction() throws LexException, SyntaxException {
		Lexeme lambdaKeyword = match(Type.LAMBDA);
		Lexeme body = funcBody();
		return Lexeme.cons(Type.anonFunction, null, body);
	}
	private boolean anonFunctionPending() {return check(Type.LAMBDA);}


	//Input processing
	private Lexer lexer;
	private Lexeme curr;
	private static final String syntaxFmt = "Error on line %d: Expected %s, found %s";

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


}
