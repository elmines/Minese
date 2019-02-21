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

		Lexeme funcArgs = argsList(args);
		Lexeme mainFuncCall = Lexeme.cons(Type.funcCall, Lexeme.literal(Type.IDENTIFIER, "main", -1), funcArgs);


		Lexeme programTree;
		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			programTree = new Parser(in).program();
			Lexeme rootEnv = Environment.newEnvironment();
			Evaluator.eval(programTree, rootEnv);

			Evaluator.evalFuncCall(mainFuncCall, rootEnv);

		}
		catch (Exception e) {
			System.out.print("Caught an exception: ");
			//System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
			System.exit(1);
		}

	}

	public static Lexeme argsList(String[] args) {
		//First arg-source file
		if (args.length < 2) return null;

		Lexeme root = Lexeme.cons(Type.exprList,
			Lexeme.literal(Type.STRING, args[1], -1),
			null);
		Lexeme parent = root;
		for (int i = 2; i < args.length; ++i) {
			Lexeme arg = Lexeme.cons(Type.exprList,
					Lexeme.literal(Type.STRING, args[i], -1),
					null);
			parent.setRight(arg);
			parent = arg;
		}
		return root;
	}

}
