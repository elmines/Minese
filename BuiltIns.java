import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public final class BuiltIns {

	public static final Map<String, BuiltIn> funcMap = new HashMap<String, BuiltIn>(){{
		put("print", BuiltIns::print);
		put("println", BuiltIns::println);
		put("endl", BuiltIns::endl);
		put("len", BuiltIns::len);
		put("set", BuiltIns::set);
		put("get", BuiltIns::get);
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



	private static Lexeme get(Lexeme args) throws EvalException {
		Lexeme iterable = args.car();
		Lexeme index =    args.cdr().car();
		if (iterable.type == Type.array) {
			ArrayList<Lexeme> array = (ArrayList<Lexeme>) iterable.value();
			int numIndex = (Integer) index.value();
			return array.get(numIndex);
		}

		throw new EvalException("Cannot access elements from type "+iterable.type);
	}


	private static Lexeme set(Lexeme args) throws EvalException {
		Lexeme iterable = args.car();
		Lexeme index =    args.cdr().car();
		Lexeme value =    args.cdr().cdr().car();
		if (iterable.type == Type.array) {
			ArrayList<Lexeme> array = (ArrayList<Lexeme>) iterable.value();
			int numIndex = (Integer) index.value();
			Lexeme old = array.get(numIndex);
			array.set(numIndex, value);
			return old;
		}

		throw new EvalException("Cannot access elements from type "+iterable.type);
	}


	private static Lexeme len(Lexeme args) throws EvalException {
		Lexeme iterable = args.car();
		if (iterable.type == Type.array) {
			ArrayList<Lexeme> array = (ArrayList<Lexeme>) iterable.value();
			return Lexeme.literal(Type.INTEGER, array.size(), -1);
		}

		throw new EvalException("Cannot calculate length of type "+iterable.type);

	}

}
