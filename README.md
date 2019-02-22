# Minese
A dynamically-typed, interpreted programming language by Ethan Mines

A program consists of definitions (of variables, functions, and/or classes):

## Executing a program
```
make
./run <program_name>.min
```


## Sample Code
```
//Declare all variables (including formal parameters) with var
var global = 5;

/* Comments are C-style */
class Person {

	var name;              //An instance variable
	var surname = "Smith"; //Default value

	define description() {
		return name + " " + surname; //Concatenate strings with +
	}

}

//For conditional statements, loops, and function bodies,
// you can omit the braces if you've only one statement
define hi() println("Hi");

//An executable program must have a main method
//A set number of command-line arguments can be passed as strings
define main(var arg1, var arg2) {
	var num1 = atoi(arg1); //Use the built-in atoi to convert strings to integers

	//All types are dynamic
	var x = [5, 3, 4, "Minese", 9]; //An array

	//Use the builtins len, get, set, and append to use arrays
	var i = 0;
	while (i < len(x)) {
		println( get(x, i) );   //Use println (or print) to print to standard out
		i = i + 1;
	}
	append(x, "Arrays are dynamic!");
	set(x, 1, 42); //Set the first element to 42

	var num = get(x, 4);
	if      ((num % 15) == 0) { println("Fizzbuzz"); }
	else if ((num % 3 ) == 0)   println("Fizz");
	else if ((num % 5 ) == 0)   println("Buzz");
	else                        println("...");

	//An anonymous function
	var summation = lambda(var accumulator, var operand) return accumulator + operand; ;

	//A local, named function
	define reduce(var values, var reduction) {
		var accumulator = 0;
		var i = 0;
		while ( i < len(values) ) {
			accumulator = reduction(accumulator, get(values, i));
			i = i + 1;
		}
		return accumulator;
	}

	var nums = [68, 42, 21, 11];
	println("The sum of those numbers is ", reduce(nums, summation));

	//Construct an object (constructors are not parameterized)
	var ethan = Person();
	ethan.name = "Ethan";
	ethan.surname = "Mines";
	println( ethan.description() );

}
```
