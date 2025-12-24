# Diagram Images

This directory contains generated PNG images from the PlantUML diagram sources located in `docs/`.

## Generating Diagrams

### Using PlantUML Command Line

```bash
# From project root, generate all diagrams
plantuml docs/*.puml -o ../out/docs

# Generate specific diagram
plantuml docs/class-diagram.puml -o ../out/docs
```

### Using VS Code PlantUML Extension

1. Install the "PlantUML" extension in VS Code
2. Open any `.puml` file from the `docs/` directory
3. Right-click in the editor → "Export Current Diagram"
4. Select PNG format
5. Save to `out/docs/` directory

## Diagram Files

- `class-diagram.png` - Complete system class diagram with all relationships
- `sequence-diagram.png` - Transfer operation flow with rollback mechanism
- `overdraft-sequence.png` - Overdraft fee calculation and application
- `account-lifecycle.png` - Account status state machine

## Requirements

To generate diagrams, you need:
- PlantUML installed (http://plantuml.com)
- Java Runtime Environment (JRE)
- Graphviz (optional, for better layout)

### Installation

```bash
# Ubuntu/Debian
sudo apt-get install plantuml graphviz

# macOS
brew install plantuml graphviz

# Or download PlantUML JAR
wget http://sourceforge.net/projects/plantuml/files/plantuml.jar/download -O plantuml.jar
java -jar plantuml.jar docs/*.puml -o ../out/docs
```
