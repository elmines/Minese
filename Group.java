/*
 * Ethan Mines
 * CS503 - Lusth
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public final class Group {

	/*
	public static final Set<Lexeme> BINARY_OPS = new HashSet<>(Arrays.asList(
		Type.PLUS, Type.MINUS, Type.TIMES, Type.DIV, Type.MOD,
		Type.LT, Type.LTE, Type.NEQ, Type.EQ, Type.GTE, Type.GT,
		Type.ASSIGN, Type.DOT, Type.AND, Type.OR
	));
	*/

	public static final Group BINARY = new Group("binary operator", 
		Type.PLUS, Type.MINUS, Type.TIMES, Type.DIV, Type.MOD,
		Type.LT, Type.LTE, Type.NEQ, Type.EQ, Type.GTE, Type.GT,
		Type.ASSIGN, Type.DOT, Type.AND, Type.OR);

	public boolean contains(Type t) { return terminals.contains(t); }


	public final String name;
	public final Set<Type> terminals;

	private Group(String name, Type... l) {
		this.name = name;
		this.terminals = new HashSet<>(Arrays.asList(l));
	}

}

