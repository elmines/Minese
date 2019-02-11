/*
 * Ethan Mines
 * CS503 - Lusth
 */

public class Evaluator {


	public static Lexeme eval(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		if (tree == null) return tree;

		if (tree.type == Type.INTEGER) return tree;
		if (tree.type == Type.STRING) return tree;
		if (tree.type == Type.BOOLEAN) return tree;
		if (tree.type == Type.array) return tree;


		if (tree.type == Type.funcDef) return evalFuncDef(tree, env);

		if (tree.type == Type.varDef) return evalVarDef(tree, env);


		if (tree.type == Type.defs) {
			eval(tree.car(), env);
			eval(tree.cdr(), env);
			return null; //FIXME: Doesn't seem right
		}

		if (tree.type == Type.program) return eval(tree.cdr(), env);

		throw new EvalException("Invalid evaluation item.");

	}

	//This is public so our main program can directly call a "main" method in env
	public static Lexeme evalFuncCall(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme closure = Environment.get(env, tree.car());
		Lexeme args = evalArgs(tree.cdr(), env);

		if (isBuiltIn(closure)) return evalBuiltIn(closure, args);

		Lexeme staticEnv = closure.cdr();
		Lexeme formalParams = getParams(closure);

		Lexeme localEnv = Environment.newScope(staticEnv, formalParams, args);
		Lexeme body = getBody(closure);

		return eval(body, localEnv);

	}

	private static Lexeme evalArgs(Lexeme args, Lexeme env) throws EnvException, EvalException {
		if (args == null) return args;

		Lexeme arg = eval(args.car(), env);
		Lexeme remaining = evalArgs(args.cdr(), env);
		return Lexeme.cons(Type.VALNODE, arg, remaining);
	}

	private static Lexeme getBody(Lexeme closure) throws EnvException, EvalException {
		Lexeme funcDef = closure.cdr();
		Lexeme funcBody = funcDef.cdr();
		Lexeme statements = funcBody.cdr();
		return statements;
	}

	private static Lexeme getParams(Lexeme closure) throws EnvException, EvalException {
		Lexeme funcDef = closure.cdr();
		Lexeme funcBody = funcDef.cdr();
		Lexeme params = funcBody.car();
		return params;
	}

	private static boolean isBuiltIn(Lexeme closure) throws EnvException, EvalException {
		String name = (String) closure.car().car().value();
		return name.equals("print");
	}

	private static Lexeme evalBuiltIn(Lexeme closure, Lexeme args) throws EnvException, EvalException {
		String name = (String) closure.car().car().value();

		if ( name.equals("print") ) BuiltIns.print(args);

		return null;
	}

	private static Lexeme evalFuncDef(Lexeme funcDef, Lexeme env) throws EnvException, EvalException {
		Lexeme identifier = funcDef.car();
		Lexeme closure = Lexeme.cons(Type.CLOSURE, funcDef, env);
		Environment.insert(env, identifier, closure);
		return closure;
	}

	private static Lexeme evalVarDef(Lexeme varDef, Lexeme env) throws EnvException, EvalException {
		Lexeme identifier = varDef.car();
		Lexeme value = eval( varDef.cdr() , env);
		Environment.insert(env, identifier, value);
		return identifier;
	}



}
