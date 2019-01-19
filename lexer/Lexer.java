import java.io.PushbackInputStream;

import java.util.*;

public class Lexer {


	public Lexer(PushbackInputStream in) {
		this.in = in;
		this.lineNumber = 1;
		this.ch = ( (char) -1 );
	}

	public Lexeme lex() {

		this.ch = (char) this.readByte();
		skipWhitespace();


		if (isEOF(this.ch)) return new Lexeme(Type.EOF);

		switch(this.ch) {
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


			default:
				if (Character.isDigit(this.ch)) {
					this.unread(this.ch);
					return lexNumber();
				}

		}

		return new Lexeme(Type.EOF);
	}

	private Lexeme lexNumber() {
		StringBuilder digits = new StringBuilder();
		int next = this.readByte();
		while (Character.isDigit(next)) {
			digits.append((char) next);
			next = this.readByte();
		}
		this.unread(next);

		return new Lexeme(Type.INTEGER, Integer.parseInt(digits.toString()));
	}

	/**
	 * After execution, the input stream points to the first non-whitespace, non-comment byte
	 */
	private void skipWhitespace() {

		while ( Character.isWhitespace(this.ch) || this.ch == '/' ) {
			if (this.ch == '/') {
				char prev = this.ch;
				char next = (char) this.readByte();

				switch(next) {
					case '/': skipLineComment(); break;
					case '*': skipBlockComment(); break;
					default:
						this.ch = prev;
						this.unread(next);
						return;
				}

			}
			else if (this.ch == '\n') ++this.lineNumber;

			this.ch = (char) this.readByte();
		}
	}


	/**
	 * After execution, the input stream is at the first byte after the newline character.
	 * This method does check for carriage returns
	*/
	private void skipLineComment() {
		char next = (char) this.readByte();
		while (next != '\n' && !isEOF(next) ) next = (char) this.readByte();
		if (next == '\n') ++this.lineNumber;
		this.ch = next;
	}

	/**
	 * After execution, the input stream is at the first byte after the comment (assuming the comment is closed)
	 * @return Whether the comment was closed.
	*/
	private boolean skipBlockComment() {

		boolean open = true;
		char next = (char) this.readByte();

		while (!isEOF(next)) {
			if (next == '*') {
				next = (char) this.readByte();
				if (next == '/') {
					this.ch = (char) this.readByte();
					return true;
				}
			}
			else {
				if (next == '\n') ++this.lineNumber;
				next = (char) this.readByte();
			}

		}
		this.ch = next;
		return false;	
	}

	private int readByte() {
		try {
			return in.read();
		}
		catch(java.io.IOException e) {
			System.err.println("Caught an exception reading a character.");
			e.printStackTrace();
			return -1;
		}


	}
	private void unread(int b) {
		try {
			this.in.unread(b);
		}
		catch (java.io.IOException e) {
			System.err.println("Error pushing byte back to input stream");
			System.exit(-1);
		}
	}

	private static boolean isEOF(char c) {return c == -1 || c == 65535;}


	private PushbackInputStream in;
	private char ch;
	private int lineNumber;
}
