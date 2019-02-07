# Minese Environment class

`Environment extends Lexeme` for your convenience (so you can just call `new Environment()`).

## API
- `public Object insert(String id, Object value)`: Insert a new variable with the given id and value, and return the value inserted
- `public Object get(String id)`: Get the value for the given variable
- `public Object set(String id, Object value)`: Set the variable to the new value, and return the old one
- `public Environment newScope(Lexeme ids, Lexeme vals)`: Extend the environment with a new set of variables, and return the newly created local environment
- `public void displayLocal()`: Print the contents of the innermost environment to stdout
- `public void displayFull()`: Print the contents of the environment and all its enclosing environments to stdout
- `public String toString()`: String representation of the local environment
