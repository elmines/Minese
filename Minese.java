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

		Lexeme mainFuncCall = Lexeme.cons(Type.funcCall, Lexeme.literal(Type.IDENTIFIER, "main", -1), null);


		Lexeme programTree;
		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			programTree = new Parser(in).program();
			Lexeme rootEnv = Environment.newEnvironment();
			Evaluator.eval(programTree, rootEnv);

			Evaluator.evalFuncCall(mainFuncCall, rootEnv);

		}
		catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
