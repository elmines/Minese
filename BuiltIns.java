import java.util.Map;
import java.util.HashMap;

public final class BuiltIns {

	public static final Map<String, BuiltIn> funcMap = new HashMap<String, BuiltIn>(){{
		put("print", BuiltIns::print);
		put("println", BuiltIns::println);
		put("endl", BuiltIns::endl);
	}};

	private static Lexeme print(Lexeme args) {

		StringBuilder sb = new StringBuilder();

		while (args != null) {
			Object val = args.car().value();
			sb.append( val.toString() );
			args = args.cdr();
		}

		System.out.print(sb.toString());

		return null;
	}

	private static Lexeme println(Lexeme args) {
		print(args);
		System.out.print("\n");
		return null;
	}


	private static Lexeme endl(Lexeme args) {
		return Lexeme.literal(Type.STRING, "\n", -1);
	}

}
