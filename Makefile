# Ethan Mines
# CS503 - Lusth

JVM=java
COMP=javac
OPTS=-Xlint

run: TestEnv.class
	$(JVM) TestEnv

TestEnv.class: TestEnv.java Environment.class EnvException.class
	$(COMP) $(OPTS) TestEnv.java

Parser.class: Parser.java Type.class Group.class Lexer.class Lexeme.class \
		 LexException.class SyntaxException.class
	$(COMP) $(OPTS) Parser.java

Environment.class: Environment.java Type.class Lexeme.class EnvException.class
	$(COMP) $(OPTS) Environment.java

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

clean:
	rm *.class
