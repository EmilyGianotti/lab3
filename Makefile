# Variables
# SRC_DIR = src
BIN_DIR = bin

# Find all Java source files
# SOURCES = $(shell find $(SRC_DIR) -name "*.java")
SOURCES = $(shell find -name "*.java")

# Create the bin directory if it doesn't exist
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# Build target - compile all Java files
build: $(BIN_DIR)
	javac -d $(BIN_DIR) $(SOURCES)

# Clean target - remove all compiled class files
clean:
	rm -rf $(BIN_DIR)

# PHONY tells make that these are not actual files
.PHONY: build clean
