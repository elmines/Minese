import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public final class BuiltIns {

	public static final Map<String, BuiltIn> funcMap = new HashMap<String, BuiltIn>(){{
		put("atoi", BuiltIns::atoi);
		put("open", BuiltIns::open);
		put("hasNext", BuiltIns::hasNext);
		put("next", BuiltIns::next);
		put("print", BuiltIns::print);
		put("println", BuiltIns::println);
		put("endl", BuiltIns::endl);
		put("len", BuiltIns::len);
		put("set", BuiltIns::set);
		put("get", BuiltIns::get);
		put("append", BuiltIns::append);
	}};

	private static Lexeme atoi(Lexeme args) {
		String token = (String) args.car().value();
		return Lexeme.literal(Type.INTEGER, Integer.parseInt(token), -1);

	}
	private static Lexeme hasNext(Lexeme args) throws EvalException {
		Scanner in = (Scanner) args.car().value();
		return Lexeme.literal(Type.BOOLEAN, in.hasNext(), -1);
	}

	private static Lexeme next(Lexeme args) throws EvalException {
		Scanner in = (Scanner) args.car().value();
		return Lexeme.literal(Type.STRING, in.next(), -1);
	}

	private static Lexeme open(Lexeme args) throws EvalException {
		String path = (String) args.car().value();

		Scanner in;

		try{
			in  = new Scanner(new java.io.File(path));
		} catch (java.io.FileNotFoundException e) {
			throw new EvalException("File "+path+" not found.");
		}

		Lexeme fp = Lexeme.literal(Type.FILE, in, -1);
		return fp;
							
	}

	private static Lexeme print(Lexeme args) {

		StringBuilder sb = new StringBuilder();

		while (args != null) {
			Object val = args.car().value();
			if (val == null) sb.append("null");
			else             sb.append( val.toString() );
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

	private static Lexeme append(Lexeme args) throws EvalException {
		Lexeme sequence = args.car();
		Lexeme element = args.cdr().car();

		if (sequence.type == Type.array) {
			ArrayList<Lexeme> array = (ArrayList<Lexeme>) sequence.value();
			array.add(element);
			return element;
		}
		throw new EvalException("Cannot append elements to type "+sequence.type);
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
