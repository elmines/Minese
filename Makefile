# Ethan Mines
# CS503 - Lusth

JVM=java
COMP=javac
OPTS=-Xlint

run: Minese.class scriptText.txt
	cp scriptText.txt run
	chmod +x run

error1: error1.min
	cat erro1.min
error1x: error1.min run
	./run error1.min
error2: error2.min
	cat error2.min
error2x: error2.min run
	./run error2.min
error3: error3.min
	cat error3.min
error3x: error3.min run
	./run error3.min
error4: error4.min
	cat error4.min
error4x: error4.min run
	./run error4.min
error5: error5.min
	cat error5.min
error5x: error5.min run
	./run error5.min

problem: iAVL
	cat testCase
problemx: iAVL run testCase
	./run iAVL testCase

arrays: arrays.min
	cat arrays.min
arraysx: arrays.min run
	./run arrays.min

conditionals: conditionals.min
	cat conditionals.min
conditionalsx: conditionals.min run
	./run conditionals.min

recursion: recursion.min
	cat recursion.min
recursionx: recursion.min run
	./run recursion.min

iteration: iteration.min
	cat iteration.min
iterationx: iteration.min run
	./run iteration.min

functions: functions.min
	cat functions.min
functionsx: functions.min run
	./run functions.min

lambda: lambda.min
	cat lambda.min
lambdax: lambda.min run
	./run lambda.min
objects: objects.min
	cat objects.min
objectsx: objects.min run
	./run objects.min

grad: grad.min
	cat grad.min
gradx: grad.min run
	./run grad.min

#My extra tests
args: args.min
	cat args.min
argsx: args.min run
	./run args.min hi yo
nulls: nulls.min
	cat nulls.min
nullsx: nulls.min run
	./run nulls.min

Minese.class: Minese.java Evaluator.class Environment.class Lexeme.class Parser.class \
		EvalException.class
	$(COMP) $(OPTS) Minese.java

Evaluator.class: Evaluator.java Lexeme.class Group.class Environment.class \
		BuiltIns.class EvalException.class
	$(COMP) $(OPTS) Evaluator.java

BuiltIns.class: BuiltIns.java Lexeme.class BuiltIn.class 
	$(COMP) $(OPTS) BuiltIns.java
BuiltIn.class: BuiltIn.java Lexeme.class
	$(COMP) $(OPTS) BuiltIn.java

Environment.class: Environment.java Type.class Lexeme.class EnvException.class
	$(COMP) $(OPTS) Environment.java

Parser.class: Parser.java Type.class Group.class Lexer.class Lexeme.class \
		 LexException.class SyntaxException.class
	$(COMP) $(OPTS) Parser.java

Lexer.class: Lexer.java Lexeme.class Type.class LexException.class
	$(COMP) $(OPTS) Lexer.java

Lexeme.class: Lexeme.java Type.class
	$(COMP) $(OPTS) Lexeme.java

Group.class: Group.java Type.class
	$(COMP) $(OPTS) Group.java

Type.class: Type.java
	$(COMP) $(OPTS) Type.java

LexException.class: LexException.java
	$(COMP) $(OPTS) LexException.java

SyntaxException.class: SyntaxException.java
	$(COMP) $(OPTS) SyntaxException.java

EnvException.class: EnvException.java
	$(COMP) $(OPTS) EnvException.java

EvalException.class: EvalException.java
	$(COMP) $(OPTS) EvalException.java

clean:
	rm *.class run
