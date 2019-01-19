import java.io.PushbackInputStream;


public class Lexer {


	public Lexer(PushbackInputStream in) {
		this.in = in;

	}

	public Lexeme lex() {

		char ch;
		try {
			ch = ( (char) in.read() );
		}
		catch(java.io.IOException e) {
			System.err.println("Caught an exception reading a character.");
			e.printStackTrace();
			return new Lexeme(Type.EOF);
		}


		if (ch == -1) return new Lexeme(Type.EOF);

		switch(ch) {
			case '(': return new Lexeme(Type.OPAREN);
			case ')': return new Lexeme(Type.CPAREN);
			case '{': return new Lexeme(Type.OCURLY);
			case '}': return new Lexeme(Type.CCURLY);
			case '[': return new Lexeme(Type.OBRACK);
			case ']': return new Lexeme(Type.CBRACK);
			case '+': return new Lexeme(Type.PLUS);
			case '-': return new Lexeme(Type.MINUS);
			case '*': return new Lexeme(Type.TIMES);
			case '/': return new Lexeme(Type.DIV);
			case '%': return new Lexeme(Type.MOD);
			case '<': return new Lexeme(Type.LT);
			case '>': return new Lexeme(Type.GT);
			case '=': return new Lexeme(Type.ASSIGN);
			case '.': return new Lexeme(Type.DOT);
			case '&': return new Lexeme(Type.AND);
			case '|': return new Lexeme(Type.OR);
			case '!': return new Lexeme(Type.NOT);


		}

		return new Lexeme(Type.EOF);
	}


	private PushbackInputStream in;
}
