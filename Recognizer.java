/*
 * Ethan Mines
 * CS503 - Lusth
 */

import java.io.PushbackInputStream;
import java.io.FileInputStream;


public class Recognizer {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: java Parser <source_file>");
			return;
		}
		final String path = args[0];


		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			Parser p = new Parser(in);
			Lexeme prog = p.program();
		}
		catch (SyntaxException | LexException e) {
			System.out.println(e.getMessage());
			System.out.println("illegal");
		}
		catch (java.io.FileNotFoundException e) {
			System.out.printf("The file %s does not exist.", path);
		}
		catch (java.io.IOException e) {
			System.out.println("An error closing the file occurred.");
		}

		System.out.println("legal");

	}

}
