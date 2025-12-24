# BankSystem API Documentation

This directory contains the complete Javadoc API documentation for the BankSystem project.

## Viewing the Documentation

Open `index.html` in a web browser to view the full documentation:

```bash
# Open in default browser (Linux)
xdg-open index.html

# Or navigate directly
firefox index.html
```

## Documentation Structure

The documentation includes:

### Core Classes
- **BankAccount** - Abstract base class for all account types
- **CheckingAccount** - Account with overdraft protection
- **SavingsAccount** - Interest-earning account with no overdraft

### Supporting Classes
- **Transaction** - Immutable transaction record
- **TransactionType** - Enum defining transaction types
- **CentralBank** - Singleton managing default interest rates

### Exceptions
- **InvalidAmountException** - Thrown for invalid transaction amounts
- **InsufficientFundsException** - Thrown when funds are insufficient

### Application
- **BankApp** - Demo application showing system usage

## Regenerating Documentation

To regenerate the Javadoc after code changes:

```bash
cd /home/erwin/Downloads/Study_AFMS/Playground/UseCases/BankSystem
javadoc -d docs -author -version -private *.java
```

## Documentation Standards

All public APIs are documented with:
- Class-level descriptions
- Method parameter documentation
- Return value descriptions
- Exception documentation
- Usage examples where appropriate
- Version and author information
