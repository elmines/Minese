# Ethan Mines
# CS503 - Lusth

COMP=javac
OPTS=-Xlint

recognizer: TestRec.class scriptText.txt
	cp scriptText.txt recognizer
	chmod +x recognizer

test: run

run: test1 test2 test3 test4 test5

test1: recognizer
	./recognizer test1.min

test2: recognizer
	./recognizer test2.min

test3: recognizer
	./recognizer test3.min

test4: recognizer
	-./recognizer test4.min

test5: recognizer
	-./recognizer test5.min

TestRec.class: TestRec.java Lexeme.class Parser.class LexException.class SyntaxException.class
	$(COMP) $(OPTS) TestRec.java

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

clean:
	rm *.class recognizer
