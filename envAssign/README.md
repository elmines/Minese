# Minese Environment class

## API
	`public static Lexeme newEnvironment()`: Construct a new empty environment
	`public static Lexeme insert(Lexeme env, Lexeme id, Lexeme val)`: Insert a new variable with the given id and value, and return the value inserted
	`public static Lexeme get(Lexeme env, Lexeme id)`: Get the value for the given variable
	`public static Lexeme set(Lexeme env, Lexeme id, Lexeme val)`: Set the variable to the new value, and return the old one
	`public static Lexeme newScope(Lexeme env, Lexeme ids, Lexeme vals)`: Extend the environment with a new set of variables, and return the newly created local environment
	`public static void displayLocal(Lexeme env)`: Print the contents of the innermost environment to stdout
	`public static void displayFull(Lexeme env)`: Print the contents of the environment and all its enclosing environments to stdout
	`public static String toString(Lexeme env)`: String representation of the local environment
