/*
 * Ethan Mines
 * CS503 - Lusth
 */

import java.io.PushbackInputStream;
import java.io.FileInputStream;

public class Minese {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Parser <source_file>");
			return;
		}
		final String path = args[0];

		Lexeme mainFuncClosure = Lexeme.cons(Type.CLOSURE, Lexeme.literal(Type.IDENTIFIER, "main", -1), null);
		Lexeme mainFuncCall = Lexeme.cons(Type.funcCall, mainFuncClosure, null);


		Lexeme programTree;
		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			programTree = new Parser(in).program();
			Lexeme rootEnv = Environment.newEnvironment();
			Evaluator.eval(programTree, rootEnv);

			Evaluator.evalFuncCall(mainFuncCall, rootEnv);

		}
		catch (Exception e) {
			System.out.println("Caught an exception.");
			System.out.println(e.getMessage());
		}

	}

}
