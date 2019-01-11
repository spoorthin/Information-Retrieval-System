JFLAGS = -g -sourcepath ./src/assignment1/* -classpath :./lib/*
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) ./src/assignment1/Main.java
CLASSES= \
	./src/assignment1/Main.java
default = ./src/assignment1/Main.class
Main.class: ./src/assignment1/Main.java
	$(JC) $(JFLAGS) ./src/assignment1/Main.java
run: Main.class
	java -classpath :./lib/* Main
clean:
	$(RM) *.class
search:
	java -classpath :./lib/* org.apache.lucene.demo.SearchFiles -index ./index/ -query string
