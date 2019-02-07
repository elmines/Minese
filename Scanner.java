/*
 * Ethan Mines
 * CS503 - Lusth
 */

import java.io.PushbackInputStream;
import java.io.FileInputStream;


public class Scanner {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: java Scanner <source_file>");
			return;
		}
		final String path = args[0];


		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			Lexer lexer = new Lexer(in);

			Lexeme lexeme = lexer.lex();
			while (lexeme.type != Type.EOF) {
				System.out.println(lexeme.lexString());
				lexeme = lexer.lex();
			}
		}
		catch (LexException e) {
			System.out.println(e.getMessage());
		}
		catch (java.io.FileNotFoundException e) {
			System.out.printf("The file %s does not exist.", path);
		}
		catch (java.io.IOException e) {
			System.out.println("An error closing the file occurred.");
		}


	}

}
