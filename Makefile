COMP=javac
OPTS=-Xlint

scanner: Scanner.class scriptText.txt
	cp scriptText.txt scanner
	chmod +x scanner

run: test1 test2 test3 test4 test5

test1: scanner
	./scanner test1.min

test2: scanner
	./scanner test2.min

test3: scanner
	./scanner test3.min

test4: scanner
	./scanner test4.min

test5: scanner
	./scanner test5.min

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
	rm *.class scanner
