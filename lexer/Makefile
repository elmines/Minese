COMP=javac
OPTS=-Xlint

scanner: Scanner.class

test1: scanner
	java Scanner test1.min

test2: scanner
	java Scanner test2.min

test3: scanner
	java Scanner test3.min

test4: scanner
	java Scanner test4.min

test5: scanner
	java Scanner test5.min

Scanner.class: Scanner.java Lexer.class LexException.class
	$(COMP) $(OPTS) Scanner.java

Lexer.class: Lexer.java Lexeme.class Type.class LexException.class
	$(COMP) $(OPTS) Lexer.java

Lexeme.class: Lexeme.java Type.class
	$(COMP) $(OPTS) Lexeme.java

Type.class: Type.java
	$(COMP) $(OPTS) Type.java

LexException.class: LexException.java
	$(COMP) $(OPTS) LexException.java

clean:
	rm *.class
