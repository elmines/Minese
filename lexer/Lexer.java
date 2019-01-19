import java.io.PushbackInputStream;

import java.util.*;

public class Lexer {


	public Lexer(PushbackInputStream in) {
		this.in = in;
		this.lineNumber = 1;
		this.ch = ( (char) -1 );
		this.prevLine = null;
		this.currLine = new StringBuilder();
	}

	public Lexeme lex() throws LexException{

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

			case '<':
			case '>':
			case '!':
			case '=':
				this.unread();
				return lexCmpOperator();

			case '.': return new Lexeme(Type.DOT);
			case '&': return new Lexeme(Type.AND);
			case '|': return new Lexeme(Type.OR);
			case ',': return new Lexeme(Type.COMMA);
			case ';': return new Lexeme(Type.SEMICOLON);


			default:
				if (Character.isDigit(this.ch)) {
					this.unread();
					return lexNumber();
				}
				else if (Character.isLetter(this.ch)) {
					this.unread();
					return lexWord();
				}
				else {
					throwException("Unknown character "+this.ch);
					return new Lexeme(Type.UNKNOWN, new String(new char[]{this.ch}));
				}

		}
	}

	private Lexeme lexCmpOperator() {
		this.readChar();
		char first = this.ch;
		this.readChar();
		char second = this.ch;

		switch (first) {
			case '<':
				if (second == '=') return new Lexeme(Type.LTE);
				else               this.unread(); return new Lexeme(Type.LT);
			case '>':
				if (second == '=') return new Lexeme(Type.GTE);
				else               this.unread(); return new Lexeme(Type.GT);
			case '=':
				if (second == '=') return new Lexeme(Type.EQ);
				else               this.unread(); return new Lexeme(Type.ASSIGN);
			case '!':
				if (second == '=') return new Lexeme(Type.NEQ);
				else               this.unread(); return new Lexeme(Type.NOT);
			default:
				return new Lexeme(Type.UNKNOWN, new String(new char[]{first}));
		}

	}

	private Lexeme lexWord() {

		StringBuilder chars = new StringBuilder();
		this.readChar();
		while (Character.isLetterOrDigit(this.ch)) {
			chars.append(this.ch);
			this.readChar();
		}
		this.unread();

		String word = chars.toString();
		switch (word) {
			case "if": return new Lexeme(Type.IF);
			case "else": return new Lexeme(Type.ELSE);
			case "while": return new Lexeme(Type.WHILE);
			case "var": return new Lexeme(Type.VAR);
			case "define": return new Lexeme(Type.DEFINE);
			case "class": return new Lexeme(Type.CLASS);
			case "extends": return new Lexeme(Type.EXTENDS);
			case "return": return new Lexeme(Type.RETURN);
			case "true": return new Lexeme(Type.BOOLEAN, true);
			case "false": return new Lexeme(Type.BOOLEAN, false);

			default: return new Lexeme(Type.IDENTIFIER, word);
		}
	}

	private Lexeme lexNumber() {
		StringBuilder digits = new StringBuilder();

		readChar();
		while (Character.isDigit(this.ch)) {
			digits.append(this.ch);
			readChar();
		}
		this.unread();

		return new Lexeme(Type.INTEGER, Integer.parseInt(digits.toString()));
	}

	/**
	 * After execution, the input stream points to the first non-whitespace, non-comment byte
	 */
	private void skipWhitespace() throws LexException{

		readChar();
		while ( Character.isWhitespace(this.ch) || this.ch == '/' ) {

			if (this.ch == '/') {
				readChar();

				switch(this.ch) {
					case '/': consumeLine(); break;
					case '*': skipBlockComment(); break;
					default: this.unread(); return;
				}

			}
			else {
				if (this.ch == '\n') nextLine();
				readChar();
			}
		}
	}

	private void nextLine() {
		this.prevLine = this.currLine.toString();
		this.currLine = new StringBuilder();
		++this.lineNumber;
	}


	/**
	 * After execution, this.ch is a newline or EOF.
	 * This does not increment the line counter.
	 * This method does check for carriage returns
	*/
	private void consumeLine() {
		readChar();
		while (this.ch != '\n' && !isEOF(this.ch) ) readChar();
	}

	/**
	 * After execution, this.ch the first byte after the comment (assuming the comment is closed)
	 * @return Whether the comment was closed.
	*/
	private boolean skipBlockComment() throws LexException{

		boolean open = true;
		readChar();

		while (!isEOF(this.ch)) {
			if (this.ch == '*') {
				readChar();
				if (this.ch == '/') {
					readChar();
					return true;
				}
			}
			else {
				if (this.ch == '\n') nextLine();
				readChar();
			}

		}
		throwUnterminatedException("block comment");

		return false;

	}

	private void throwUnterminatedException(String unterminated) throws LexException {
		String fullMessage = "Unterminated " + unterminated + " at line " + this.lineNumber;
		throw new LexException(fullMessage);
	}


	private void throwException(String baseMessage) throws LexException {

		
		int lineNumDigits = new Integer(this.lineNumber).toString().length();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lineNumDigits + 2; ++i) sb.append(' '); //Move past line number
		for (int i = 0; i < this.currLine.length() - 1; ++i) sb.append(' ');
		sb.append('^');

		if (this.ch != '\n') consumeLine(); //Get rest of line for the error message

		String fullMessage;
		if (this.lineNumber > 1) {
			String fmtString = "Lexing error at line %d: %s\n" +
					"%" + lineNumDigits + "d: %s\n" +
					"%" + lineNumDigits + "d: %s\n" +
					"%s";

			fullMessage = String.format(fmtString,
				this.lineNumber, baseMessage,
				this.lineNumber-1, this.prevLine,
				this.lineNumber, this.currLine.toString(),
				sb.toString()
			);
		}
		else {
			String fmtString = "Lexing error at line %d: %s\n" +
					"%" + lineNumDigits + "d: %s\n" +
					"%s";

			fullMessage = String.format(fmtString,
				this.lineNumber, baseMessage,
				this.lineNumber, this.currLine.toString(),
				sb.toString()
			);


		}
		throw new LexException(fullMessage);
	}

	private void readChar() {
		this.ch = (char) this.readByte();
		this.appendChar(this.ch);
	}

	private void appendChar(char c) {
		if (!isEOF(c) && c != '\n') this.currLine.append(c);
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
	private void unread() {
		try {
			this.in.unread(this.ch);
			this.currLine.deleteCharAt(this.currLine.length() - 1);

			if (currLine.length() > 0)
				this.ch = currLine.charAt(this.currLine.length() - 1);
			else
				this.ch = (char) -1;
		}
		catch (java.io.IOException e) {
			System.err.println("Error pushing byte back to input stream");
			System.exit(-1);
		}
	}

	private static boolean isEOF(char c) {return c == -1 || c == 65535;}


	private PushbackInputStream in;
	private char ch;
	public int lineNumber;
	public String prevLine;
	public StringBuilder currLine;
}
