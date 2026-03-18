# Directories
OUT = out

# Find all java files
SOURCES = $(shell find src -name "*.java")

# Default target - compile and run
all: compile run

# Compile all java files
compile:
	javac -d out $(SOURCES)

# Run the game
run:
	java -cp out:res main.Main

# Clean compiled files
clean:
	rm -rf $(OUT)/*.class