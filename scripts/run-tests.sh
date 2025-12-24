#!/bin/bash
# Script to compile and run BankSystem tests with JUnit 5 and Google Truth

# Navigate to project root
cd "$(dirname "$0")/.."

echo "Compiling Java files..."
javac -cp "lib/*:." -d bin *.java tests/*.java

if [ $? -eq 0 ]; then
    echo "Running tests..."
    java -jar lib/junit-platform-console-standalone-1.10.1.jar execute \
        --class-path bin \
        --class-path lib/truth-1.1.5.jar \
        --class-path lib/guava-31.1-jre.jar \
        --class-path lib/failureaccess-1.0.1.jar \
        --class-path lib/checker-qual-3.12.0.jar \
        --class-path lib/error_prone_annotations-2.11.0.jar \
        --class-path lib/j2objc-annotations-1.3.jar \
        --class-path lib/asm-9.2.jar \
        --scan-class-path
else
    echo "Compilation failed!"
    exit 1
fi
