# Minese
A dynamically-typed, interpreted programming language by Ethan Mines

## Program Structure
A program consists of definitions (of variables, functions, and/or classes):

```
var global = 5;

/* Comments are C-style */
class Person {

	var name; //An instance variable

	define constructor(var n) {
		name = n;
	}	
}
//A subclass
class Student extends Person {
	var GPA;
	define constructor(var n, var g) {
		super(n);
		GPA = g;	
	}

	//A block needs no braces if it only has one statement
	define toString() return n + ": " + g;

}

define main() {

	var i = 0;
	while (i < 10) {
		if (i % 15 == 0)     print("Fizzbuzz");
		else if (i % 3 == 0) print("Fizz");
		else if (i % 5 == 0) print("Buzz");
		else                 print("...");

		i = i + 1;
	}

	//All types are dynamic
	var x = [5, 3, 4, "Minese", 9]; //An array
	print(x[3] + " is the best language ever!"); //0-based array indexing


	//An anonymous function
	var summation = lambda(var accumulator, var operand) return accumulator + operand; ;

	//A local, named function
	define reduce(var values, var reduction) {
		var accumulator = 0;
		var i = 0;
		while ( i < values.length() ) {
			accumulator = reduction(accumulator, values[i]);
			i = i + 1;
		}
		return accumulator;
	}

	var nums = [68, 42, 21, 11];
	print("The sum of those numbers is " + reduce(nums, reduction));

}
```

## Lexer Test Cases
- test1.min: A more complicated program utilizing most of the program's features
- test2.min: Various conditional statements, and boolean literals
- test3.min: Rather awkward whitespace and comments
- test4.min: Boolean expressions
- test5.min: Program with illegal character $

## Parser Test Cases
- test1.min: A more complicated program utilizing most of the program's features (legal)
- test2.min: Control flow and loops (legal)
- test3.min: Extensive nesting of functions (legal)
- test4.min: A subclass missing the superclass name in its header (illegal)
- test5.min: An arithmetic expression where a comma was used instead of an operator (illegal)
