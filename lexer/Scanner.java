import java.io.PushbackInputStream;
import java.io.FileInputStream;


public class Scanner {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: java Scannar <source_file>");
			return;
		}
		final String path = args[0];


		try (PushbackInputStream in = new PushbackInputStream(new FileInputStream(path))) {
			Lexer lexer = new Lexer(in);

			Lexeme lexeme = lexer.lex();
			while (lexeme.type != Type.EOF) {
				System.out.println(lexeme);
				lexeme = lexer.lex();
			}

		}
		catch (java.io.FileNotFoundException e) {
			System.err.printf("The file %s does not exist.", path);
		}
		catch (java.io.IOException e) {
			System.err.println("An error closing the file occurred.");
		}


	}

}
