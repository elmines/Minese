/*
 * Ethan Mines
 * CS503 - Lusth
 */

public enum Type {
	OPAREN, CPAREN, OCURLY, CCURLY, OBRACK, CBRACK,
	PLUS, MINUS, TIMES, DIV, MOD,
	LT, LTE, NEQ, EQ, GTE, GT,
	ASSIGN, DOT,
	AND, OR,
	NOT, UMINUS,
	COMMA, SEMICOLON,
	INTEGER, STRING,
	IF, ELSE, WHILE,
	VAR, DEFINE, CLASS, EXTENDS, RETURN, LAMBDA,
	BOOLEAN,
	IDENTIFIER,
	EOF,
	UNKNOWN,

	//Nonterminals
	program,
	defs,
	subclassDef,
	varDef, funcDef, classDef,

	expression, unary,

	anonFunction,
	array,
	condStatement,
	whileStatement,
	funcCall,
	arrayElement,
	funcBody,
	paramsList,
	exprList,


	block,
	statements,
	returnStatement,


	//Non-lexical types

	environment,
	TABLE,
	CLOSURE,

	IDNODE, VALNODE,
}

