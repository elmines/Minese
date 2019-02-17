@FunctionalInterface
public interface BuiltIn {

	public Lexeme eval(Lexeme args) throws EvalException;
}
