/*
 * Ethan Mines
 * CS503 - Lusth
 */
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;

public class Evaluator {

	public static Lexeme eval(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		if (tree == null) return Lexeme.literal(Type.NULL, null, -1);

		if (tree.type == Type.NULL) return tree;

		if (tree.type == Type.INTEGER) return tree;
		if (tree.type == Type.STRING) return tree;
		if (tree.type == Type.BOOLEAN) return tree;
		if (tree.type == Type.array) return evalArray(tree, env);
		if (tree.type == Type.anonFunction) return evalLambda(tree, env);

		if (tree.type == Type.IDENTIFIER) return Environment.get(env, tree);

		if (Group.BINARY.contains(tree.type)) return evalBinary(tree, env);
		if (Group.UNARY.contains(tree.type)) return evalUnaryOp(tree, env);

		if (tree.type == Type.RETURNVAL) return tree;
		if (tree.type == Type.returnStatement) {
			return Lexeme.cons(Type.RETURNVAL, null, eval(tree.cdr(), env));
		}
		if (tree.type == Type.condStatement) return evalCond(tree, env);
		if (tree.type == Type.whileStatement) return evalWhile(tree, env);


		if (tree.type == Type.statements) {
			Lexeme stmt = tree.car();
			stmt = eval(stmt, env);
			if (stmt != null && stmt.type == Type.RETURNVAL) return stmt;
			Lexeme following = tree.cdr();
			return eval(following, env);
		}

		if (tree.type == Type.funcCall) {
			return evalFuncCall(tree, env);
		}


		if (tree.type == Type.classDef) return evalClassDef(tree, env);
		if (tree.type == Type.funcDef)  return evalFuncDef(tree, env);
		if (tree.type == Type.varDef)   return evalVarDef(tree, env);


		if (tree.type == Type.defs) {
			eval(tree.car(), env);
			return eval(tree.cdr(), env);
		}

		if (tree.type == Type.program) return eval(tree.cdr(), env);

		throw new EvalException( String.format("Invalid evaluation item %s", tree) );

	}

	private static Lexeme evalClassDef(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme className = tree.car();
		Lexeme statements = class2FuncBody(tree.cdr());

		Lexeme funcBody = Lexeme.cons(Type.funcBody, null, statements);

		Lexeme constructor = Lexeme.cons(Type.funcDef, className, funcBody);

		Lexeme closure = Lexeme.cons(Type.CLOSURE, constructor, env);	
						

		Environment.insert(env, className, closure);

		return closure;

	}
	private static Lexeme class2FuncBody(Lexeme classBody) throws EvalException {
		Lexeme returnStatement = returnThis();

		if (classBody == null) classBody = Lexeme.cons(Type.statements, returnStatement, null);
		else {
			Lexeme definitions = classBody.cdr();
			classBody          = Lexeme.cons(Type.statements, definitions.car(), definitions.cdr());
			
			Lexeme tail = classBody;
			while (tail.cdr() != null) {
				tail.type = Type.statements;
				tail = tail.cdr();
			}
			tail.setRight( Lexeme.cons(Type.statements, returnStatement, null) );

		}
		return classBody;
	}

	private static Lexeme returnThis() {
		return Lexeme.cons(Type.returnStatement, null, Lexeme.literal(Type.IDENTIFIER, "this", -1));
	}

	private static Lexeme evalArray(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		ArrayList<Lexeme> array = new ArrayList<Lexeme>();
		Lexeme elements = tree.cdr();
		while (elements != null) {
			Lexeme expr = eval(elements.car(), env);
			array.add(expr);
			elements = elements.cdr();
		}
		return Lexeme.literal(Type.array, array, -1);
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
			if (evaluated.type == Type.RETURNVAL) return evaluated;
			condition = eval(symCondition, env);
		}

		return Lexeme.literal(Type.NULL, null, -1);

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

		if (op.type == Type.DOT) return evalDot(left, right, env);

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

		if (op.type == Type.AND) return evalAnd(left, right, env);
		if (op.type == Type.OR)  return evalOr(left, right, env);

		throw new EvalException("Invalid binary operator "+op.type);

	}

	private static Lexeme evalDot(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		Lexeme obj = eval(l, env);
		if (obj.type != Type.environment) {
			throw new EvalException("Tried to access a member from type "+obj.type);
		}
		return Environment.get(obj, r);
	}

	private static Lexeme evalAnd(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.BOOLEAN || r.type != Type.BOOLEAN) {
			throw new EvalException("Tried to perform operation "+ Type.AND +
				" on types " + l.type + " and " + r.type);
		}
		boolean result = ((Boolean) l.value()) && ((Boolean) r.value());
		return Lexeme.literal(Type.BOOLEAN, result, -1);
	}
	private static Lexeme evalOr(Lexeme l, Lexeme r, Lexeme env) throws EnvException, EvalException {
		l = eval(l, env);
		r = eval(r, env);
		if (l.type != Type.BOOLEAN || r.type != Type.BOOLEAN) {
			throw new EvalException("Tried to perform operation "+ Type.OR +
				" on types " + l.type + " and " + r.type);
		}
		boolean result = ((Boolean) l.value()) || ((Boolean) r.value());
		return Lexeme.literal(Type.BOOLEAN, result, -1);
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
		if (l.type != r.type) return false;

		Object lVal = l.value(), rVal = r.value();
		if (lVal == null) {
			if (rVal == null) return true;
			else              return false;
		}
		return lVal.equals(rVal);
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
		Lexeme result = eval(r, env);
		if (l.type == Type.DOT) {
			Lexeme obj = eval(l.car(), env);
			Lexeme field = l.cdr();
			Environment.set(obj, field, result);
			return result;
		}

		if (l.type != Type.IDENTIFIER) {
			throw new EvalException("Tried to "+Type.ASSIGN + " to a "+l.type+" rather than an "+Type.IDENTIFIER);
		}

		Environment.set(env, l, result);	
		return result;

	}

	private static Lexeme evalLambda(Lexeme tree, Lexeme env) throws EnvException, EvalException {
		Lexeme funcBody = tree.cdr();
		Lexeme id = Lexeme.literal(Type.IDENTIFIER, "lambda", -1);
		Lexeme funcDef = Lexeme.cons(Type.funcDef, id, funcBody);
		Lexeme closure = Lexeme.cons(Type.CLOSURE, funcDef, env);
		return closure;
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
		Environment.insert(localEnv, Lexeme.literal(Type.IDENTIFIER, "this", -1), localEnv);
		Lexeme body = getBody(closure);

		assert(body.type == Type.statements);

		Lexeme result = eval(body, localEnv);
		if (result.type == Type.RETURNVAL) return result.cdr();
		else if (result.type == Type.NULL)       return result;

		throw new EvalException("Invalid return Lexeme "+result.type);

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
