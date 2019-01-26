/*
 * Ethan Mines
 * CS503 - Lusth
 */

public enum Type {
	OPAREN, CPAREN, OCURLY, CCURLY, OBRACK, CBRACK,
	PLUS, MINUS, TIMES, DIV, MOD,
	LT, LTE, NEQ, EQ, GTE, GT,
	ASSIGN, DOT,
	AND, OR, NOT,
	COMMA, SEMICOLON,
	INTEGER, STRING,
	IF, ELSE, WHILE,
	VAR, DEFINE, CLASS, EXTENDS, RETURN,
	BOOLEAN,
	IDENTIFIER,
	EOF,
	UNKNOWN,

	//Nonterminals
	program,
	varDef,
	expression,
	unary
}

