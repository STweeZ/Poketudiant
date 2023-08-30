SRC = src
SOURCES = $(shell find ./src/ -name *.java)
S = java
SC = javac
TARGET = bin
CP = bin
VPATH=bin

run: compile
	@echo ":: Executing..."
	@$(S) -cp $(CP) Main
	./jar.sh

compile: $(SOURCES:.%.java=.%.class)

%.class: %.java
	clear
	@echo ":: Compiling..."
	@echo "Compiling $*.java.."
	@if [ $(shell stat -c %Y $*.java) -lt $(shell stat -c %Y $(shell echo "$*.class" | sed 's/src/bin/g')) ]; then echo ; else $(SC) -sourcepath $(SRC) -cp $(CP) -d $(TARGET) -encoding utf-8 $*.java; fi




clean:
	@rm -R bin/*
