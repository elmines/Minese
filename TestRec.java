/*
 * Ethan Mines
 * CS503 - Lusth
 */

import java.io.PushbackInputStream;
import java.io.FileInputStream;


public class TestRec {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: java Parser <source_file>");
			return;
		}
		final String path = args[0];

		boolean legal = true;
		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			Parser p = new Parser(in);
			Lexeme prog = p.program();
		}
		catch (SyntaxException | LexException e) {
			System.out.println(e.getMessage());
			legal = false;
		}
		catch (java.io.FileNotFoundException e) {
			System.out.printf("The file %s does not exist.", path);
			System.out.println();
			System.exit(100);
		}
		catch (java.io.IOException e) {
			System.out.println("An error closing the file occurred.");
			System.exit(101);
		}

		if (legal) System.out.println("legal");
		else {
			System.out.println("illegal");
			System.exit(1);
		}

	}

}
