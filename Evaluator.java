/*
 * Ethan Mines
 * CS503 - Lusth
 */
import java.util.Map;
import java.util.HashMap;

public class Evaluator {

	public static Lexeme eval(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		if (tree == null) return tree;

		if (tree.type == Type.INTEGER) return tree;
		if (tree.type == Type.STRING) return tree;
		if (tree.type == Type.BOOLEAN) return tree;
		if (tree.type == Type.array) return tree;

		if (tree.type == Type.IDENTIFIER) return Environment.get(env, tree);

		if (Group.BINARY.contains(tree.type)) return evalBinary(tree, env);


		if (tree.type == Type.statements) {
			Lexeme stmt = tree.car();
			eval(stmt, env);
			Lexeme following = tree.cdr();
			return eval(following, env);
		}

		if (tree.type == Type.funcCall) {
			return evalFuncCall(tree, env);
		}


		if (tree.type == Type.funcDef) return evalFuncDef(tree, env);
		if (tree.type == Type.varDef) return evalVarDef(tree, env);


		if (tree.type == Type.defs) {
			eval(tree.car(), env);
			return eval(tree.cdr(), env);
		}

		if (tree.type == Type.program) return eval(tree.cdr(), env);

		throw new EvalException( String.format("Invalid evaluation item %s", tree) );

	}

	private static Lexeme evalBinary(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme op = tree;
		Lexeme left  = op.car();
		Lexeme right = op.cdr();

		if (op.type == Type.PLUS) return evalPlus(left, right, env);
		if (op.type == Type.MINUS) return evalMinus(left, right, env);
		if (op.type == Type.TIMES) return evalTimes(left, right, env);
		if (op.type == Type.DIV) return evalDiv(left, right, env);
		if (op.type == Type.MOD) return evalMod(left, right, env);

		if (op.type == Type.ASSIGN) return evalAssign(left, right, env);

		return null;	
	}

	private static Lexeme evalPlus(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+Type.PLUS +
				" on types " + l.type + " and " + r.type);
		}
		Integer a = (Integer) l.value(), b = (Integer) r.value();
		return Lexeme.literal(Type.INTEGER, a + b, -1);
	}
	private static Lexeme evalMinus(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+Type.MINUS +
				" on types " + l.type + " and " + r.type);
		}
		Integer a = (Integer) l.value(), b = (Integer) r.value();
		return Lexeme.literal(Type.INTEGER, a - b, -1);
	}
	private static Lexeme evalTimes(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+Type.TIMES +
				" on types " + l.type + " and " + r.type);
		}
		Integer a = (Integer) l.value(), b = (Integer) r.value();
		return Lexeme.literal(Type.INTEGER, a * b, -1);
	}
	private static Lexeme evalDiv(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+Type.DIV +
				" on types " + l.type + " and " + r.type);
		}
		Integer a = (Integer) l.value(), b = (Integer) r.value();
		return Lexeme.literal(Type.INTEGER, a / b, -1);
	}
	private static Lexeme evalMod(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+Type.MOD +
				" on types " + l.type + " and " + r.type);
		}
		Integer a = (Integer) l.value(), b = (Integer) r.value();
		return Lexeme.literal(Type.INTEGER, a % b, -1);
	}

	private static Lexeme evalAssign(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		if (l.type != Type.IDENTIFIER) {
			throw new EvalException("Tried to "+Type.ASSIGN + " to a "+l.type+" rather than an "+Type.IDENTIFIER);
		}
		r = eval(r, env);

		Environment.set(env, l, r);	
		return r;

	}


	/**
	 * Evaluate a function call.
	 * tree.type should be Type.funcCall (car's type is IDENTIFIER, cdr's type is exprList)
	 * This is public so our main program can directly call a "main" method in env
	 */
	public static Lexeme evalFuncCall(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme identifier = tree.car();
		Lexeme args = evalArgs(tree.cdr(), env);
		if (isBuiltIn(identifier)) return evalBuiltIn(identifier, args);
		Lexeme closure = Environment.get(env, identifier);


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
		Lexeme funcDef = closure.car();
		Lexeme funcBody = funcDef.cdr();
		Lexeme statements = funcBody.cdr();
		return statements;
	}

	private static Lexeme getParams(Lexeme closure) throws EnvException, EvalException {
		Lexeme funcDef = closure.car();
		Lexeme funcBody = funcDef.cdr();
		Lexeme params = funcBody.car();
		return params;
	}

	private static boolean isBuiltIn(Lexeme identifier) throws EnvException, EvalException {
		String name = (String) identifier.value();
		return BuiltIns.funcMap.containsKey(name);
	}

	private static Lexeme evalBuiltIn(Lexeme identifier, Lexeme args) throws EnvException, EvalException {
		String name = (String) identifier.value();
		BuiltIn func = BuiltIns.funcMap.get(name);
		return func.eval(args);
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
