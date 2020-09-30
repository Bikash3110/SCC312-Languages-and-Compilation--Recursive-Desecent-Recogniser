JAVA := $(shell which java)
JAVAC := $(shell which javac)

SOURCES := $(shell ls *.java)
CLASSES := $(SOURCES:.java=.class)
FILTER  := Generate.java SyntaxAnalyser.java

%.class : %.java
	$(JAVAC) $<

.PHONY: clean run package

all: Compiler
	$(info -- Built compiler!)

Compiler: $(CLASSES)
	$(info -- Compiled everything!)

run: Compiler
	$(info -- Running compiler tests...)
	$(JAVA) Compile > output.txt
	$(info -- Done! Check your output.txt for the results)

clean:
	$(info -- Removing all *.txt and *.class files)
	rm -f output.txt res.txt
	rm -f *.class

package: clean run
	$(info -- Removing old package.zip, if present...)
	rm -f package.zip
	$(info -- Building new package.zip)
	zip -r package.zip $(SOURCES) output.txt res.txt $(shell ls *.bat) "Programs Folder" makefile
	$(info -- Done!)

student-package: clean
	$(info -- Removing old student-package.zip, if present...)
	rm -f student-package.zip
	$(info -- Building new student-package.zip)
	zip -r student-package.zip $(filter-out $(FILTER),$(SOURCES)) $(shell ls *.bat) "Programs Folder" program0-example.txt makefile
	$(info -- Done!)
