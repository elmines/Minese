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
		if (Group.UNARY.contains(tree.type)) return evalUnaryOp(tree, env);

		if (tree.type == Type.condStatement) return evalCond(tree, env);
		if (tree.type == Type.whileStatement) return evalWhile(tree, env);


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

	private static Lexeme evalUnaryOp(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme operand = eval(tree.car(), env);

		if (tree.type == Type.UMINUS) {
			if (operand.type != Type.INTEGER)
				throw new EvalException("Tried to apply "+Type.UMINUS + " to "+operand.type);
			return Lexeme.literal(Type.INTEGER, - (Integer) operand.value(), -1);
		}

		if (tree.type == Type.NOT) {
			if (operand.type != Type.BOOLEAN)
				throw new EvalException("Tried to apply "+Type.NOT + " to "+operand.type);
			return Lexeme.literal(Type.BOOLEAN, ! (Boolean) operand.value(), -1);
		}


		throw new EvalException("Invalid unary operation "+tree.type);
	}

	private static Lexeme evalWhile(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme symCondition = tree.car();

		Lexeme condition = eval(symCondition, env);
		if (condition.type != Type.BOOLEAN)
			throw new EnvException("Tried to interpret " + condition.type + " as a "+Type.BOOLEAN);

		Lexeme body = tree.cdr();
		while ( (Boolean) condition.value() ) {
			Lexeme evaluated = eval(body, env);
			condition = eval(symCondition, env);
		}

		return null;

	}

	private static Lexeme evalCond(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme condition = eval(tree.car().car(), env);

		if (condition.type != Type.BOOLEAN)
			throw new EnvException("Tried to interpret " + condition.type + " as a "+Type.BOOLEAN);

		if ( (Boolean) condition.value() ) {
			Lexeme body = tree.car().cdr();
			return eval(body, env);
		}

		Lexeme alt = tree.cdr();
		return eval(alt, env);	
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

		if (op.type == Type.EQ)  return evalEq(left, right, env);
		if (op.type == Type.NEQ) return evalNeq(left, right, env);

		if (op.type == Type.LT)  return evalNumCmp(left, right, env, "<");
		if (op.type == Type.LTE) return evalNumCmp(left, right, env, "<=");
		if (op.type == Type.GTE) return evalNumCmp(left, right, env, ">=");
		if (op.type == Type.GT)  return evalNumCmp(left, right, env, ">");

		if (op.type == Type.ASSIGN) return evalAssign(left, right, env);

		throw new EvalException("Invalid binary operator "+op.type);

	}

	private static Lexeme evalEq(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		return Lexeme.literal(Type.BOOLEAN, rawEq(l, r, env), -1);
	}
	private static Lexeme evalNeq(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		return Lexeme.literal(Type.BOOLEAN, ! rawEq(l, r, env), -1);
	}

	private static boolean rawEq(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		return l.value().equals(r.value());
	}


	private static Lexeme evalNumCmp(Lexeme l, Lexeme r, Lexeme env, String op) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.INTEGER || r.type != Type.INTEGER) {
			throw new EvalException("Tried to perform operation "+ op +
				" on types " + l.type + " and " + r.type);
		}

		Integer lVal = (Integer) l.value(), rVal = (Integer) r.value();
		Boolean result = false;

		if      (op.equals("<") ) result = lVal < rVal;
		else if (op.equals("<=")) result = lVal <= rVal;
		else if (op.equals(">") ) result = lVal > rVal;
		else if (op.equals(">=")) result = lVal >= rVal;
		else throw new EvalException("Invalid operator "+op);

		return Lexeme.literal(Type.BOOLEAN, result, -1);

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