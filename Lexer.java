/*
 * Ethan Mines
 * CS503 - Lusth
 */
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


		if (isEOF(this.ch)) return Lexeme.symbol(Type.EOF, this.lineNumber);

		switch(this.ch) {
			case '(': return Lexeme.symbol(Type.OPAREN,this.lineNumber);
			case ')': return Lexeme.symbol(Type.CPAREN,this.lineNumber);
			case '{': return Lexeme.symbol(Type.OCURLY,this.lineNumber);
			case '}': return Lexeme.symbol(Type.CCURLY,this.lineNumber);
			case '[': return Lexeme.symbol(Type.OBRACK,this.lineNumber);
			case ']': return Lexeme.symbol(Type.CBRACK,this.lineNumber);
			case '+': return Lexeme.symbol(Type.PLUS,this.lineNumber);
			case '-': return Lexeme.symbol(Type.MINUS,this.lineNumber);
			case '*': return Lexeme.symbol(Type.TIMES,this.lineNumber);
			case '/': return Lexeme.symbol(Type.DIV,this.lineNumber);
			case '%': return Lexeme.symbol(Type.MOD,this.lineNumber);

			case '<':
			case '>':
			case '!':
			case '=':
				this.unread();
				return lexCmpOperator();

			case '.': return Lexeme.symbol(Type.DOT,this.lineNumber);
			case '&': return Lexeme.symbol(Type.AND,this.lineNumber);
			case '|': return Lexeme.symbol(Type.OR,this.lineNumber);
			case ',': return Lexeme.symbol(Type.COMMA,this.lineNumber);
			case ';': return Lexeme.symbol(Type.SEMICOLON,this.lineNumber);
			case '"':
				this.unread();
				return lexString();


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
					return Lexeme.symbol(Type.UNKNOWN, this.lineNumber);
				}

		}
	}

	private Lexeme lexString() throws LexException {
		this.readChar();
		assert this.ch == '"';

		StringBuilder literal = new StringBuilder();
		this.readChar();
		while (this.ch != '"') {
			if (this.ch == '\n') nextLine();
			if (isEOF(this.ch)) throwUnterminatedException("string literal");
			literal.append(this.ch);
			this.readChar();
		}	
		return Lexeme.literal(Type.STRING, literal.toString(), this.lineNumber);
	}

	private Lexeme lexCmpOperator() {
		this.readChar();
		char first = this.ch;
		this.readChar();
		char second = this.ch;

		switch (first) {
			case '<':
				if (second == '=') return Lexeme.symbol(Type.LTE, this.lineNumber);
				else               this.unread(); return Lexeme.symbol(Type.LT, this.lineNumber);
			case '>':
				if (second == '=') return Lexeme.symbol(Type.GTE, this.lineNumber);
				else               this.unread(); return Lexeme.symbol(Type.GT, this.lineNumber);
			case '=':
				if (second == '=') return Lexeme.symbol(Type.EQ, this.lineNumber);
				else               this.unread(); return Lexeme.symbol(Type.ASSIGN, this.lineNumber);
			case '!':
				if (second == '=') return Lexeme.symbol(Type.NEQ, this.lineNumber);
				else               this.unread(); return Lexeme.symbol(Type.NOT, this.lineNumber);
			default:
				return Lexeme.symbol(Type.UNKNOWN, this.lineNumber);
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
			case "if": return Lexeme.symbol(Type.IF, this.lineNumber);
			case "else": return Lexeme.symbol(Type.ELSE, this.lineNumber);
			case "while": return Lexeme.symbol(Type.WHILE, this.lineNumber);
			case "var": return Lexeme.symbol(Type.VAR, this.lineNumber);
			case "define": return Lexeme.symbol(Type.DEFINE, this.lineNumber);
			case "class": return Lexeme.symbol(Type.CLASS, this.lineNumber);
			case "extends": return Lexeme.symbol(Type.EXTENDS, this.lineNumber);
			case "return": return Lexeme.symbol(Type.RETURN, this.lineNumber);
			case "lambda": return Lexeme.symbol(Type.LAMBDA, this.lineNumber);
			case "true": return Lexeme.literal(Type.BOOLEAN, true, this.lineNumber);
			case "false": return Lexeme.literal(Type.BOOLEAN, false, this.lineNumber);
			case "null": return Lexeme.literal(Type.NULL, null, this.lineNumber);

			default: return Lexeme.literal(Type.IDENTIFIER, word, this.lineNumber);
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

		return Lexeme.literal(Type.INTEGER, Integer.parseInt(digits.toString()), this.lineNumber);
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

		if (this.ch != '\n') consumeLine(); //Get rest of line for the error message

		String fullMessage;
		if (this.lineNumber > 1) {
			String fmtString = "Parsing error at line %d: %s\n" +
					"%" + lineNumDigits + "d: %s\n" +
					"%" + lineNumDigits + "d: %s";

			fullMessage = String.format(fmtString,
				this.lineNumber, baseMessage,
				this.lineNumber-1, this.prevLine,
				this.lineNumber, this.currLine.toString()
			);
		}
		else {
			String fmtString = "Parsing error at line %d: %s\n" +
					"%" + lineNumDigits + "d: %s";

			fullMessage = String.format(fmtString,
				this.lineNumber, baseMessage,
				this.lineNumber, this.currLine.toString()
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
