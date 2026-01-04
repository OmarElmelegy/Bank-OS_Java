package app;

import java.util.Scanner;

import banking.Bank;
import banking.accounts.BankAccount;
import banking.accounts.CheckingAccount;
import banking.accounts.SavingsAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;

/**
 * Main application class demonstrating the banking system functionality.
 * 
 * <p>
 * This class provides examples of:
 * <ul>
 * <li>Creating bank accounts</li>
 * <li>Performing deposits and withdrawals</li>
 * <li>Transferring money between accounts</li>
 * <li>Printing account statements</li>
 * <li>Filtering transactions by type</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class BankApp {
    private static Scanner scanner = new Scanner(System.in);
    private static Bank myBank = new Bank();
    private static BankAccount currentAccount = null;

    /**
     * Main entry point for the banking application demo.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Welcome to the Banking System ===\n");

        myBank.loadData();
        myBank.startInterestService();

        // Main menu loop
        while (true) {
            if (currentAccount == null) {
                showMainMenu();
            } else {
                showTransactionMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("[1] Login");
        System.out.println("[2] Create New Account");
        System.out.println("[Q] Quit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                createAccount();
                break;
            case "q":
                shutdown();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void showTransactionMenu() {
        System.out.println("\n=== Account: " + currentAccount.getAccountNumber() + " ===");
        System.out.println("Owner: " + currentAccount.getOwnerName());
        System.out.println("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        System.out.println("\n--- Transaction Menu ---");
        System.out.println("[1] Deposit");
        System.out.println("[2] Withdraw");
        System.out.println("[3] Transfer");
        System.out.println("[4] View Statement");
        System.out.println("[5] Logout");
        System.out.println("[Q] Quit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "1":
                handleDeposit();
                break;
            case "2":
                handleWithdraw();
                break;
            case "3":
                handleTransfer();
                break;
            case "4":
                currentAccount.printStatement();
                break;
            case "5":
                logout();
                break;
            case "q":
                shutdown();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void login() {
        System.out.print("\nEnter account number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            currentAccount = myBank.getAccount(accountNumber);
            if (currentAccount == null) {
                System.out.println("ERROR: Account not found.");
            } else {
                System.out.println("✓ Login successful! Welcome, " + currentAccount.getOwnerName());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Select account type:");
        System.out.println("[1] Savings Account (2% interest)");
        System.out.println("[2] Checking Account (Overdraft protection)");
        System.out.print("Choose: ");
        String type = scanner.nextLine().trim();

        try {
            BankAccount newAccount;
            if (type.equals("1")) {
                newAccount = new SavingsAccount(generateAccountNumber(), name);
            } else if (type.equals("2")) {
                newAccount = new CheckingAccount(generateAccountNumber(), name);
            } else {
                System.out.println("Invalid account type.");
                return;
            }

            myBank.openAccount(newAccount);
            System.out.println("✓ Account created successfully!");
            System.out.println("Your account number is: " + newAccount.getAccountNumber());

            myBank.saveData();
        } catch (AccountStatusException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.print("\nEnter amount to deposit: ");
        String amountStr = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(amountStr);

            if (currentAccount instanceof SavingsAccount) {
                ((SavingsAccount) currentAccount).deposit(amount);
            } else if (currentAccount instanceof CheckingAccount) {
                ((CheckingAccount) currentAccount).deposit(amount);
            }

            System.out.println(
                    "✓ Deposit successful! New balance: $" + String.format("%.2f", currentAccount.getBalance()));
            myBank.saveData();
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid amount format.");
        } catch (InvalidAmountException | AccountStatusException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.print("\nEnter amount to withdraw: ");
        String amountStr = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(amountStr);

            if (currentAccount instanceof SavingsAccount) {
                ((SavingsAccount) currentAccount).withdraw(amount);
            } else if (currentAccount instanceof CheckingAccount) {
                ((CheckingAccount) currentAccount).withdraw(amount);
            }

            System.out.println(
                    "✓ Withdrawal successful! New balance: $" + String.format("%.2f", currentAccount.getBalance()));
            myBank.saveData();
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid amount format.");
        } catch (InvalidAmountException | AccountStatusException | InsufficientFundsException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleTransfer() {
        System.out.print("\nEnter destination account number: ");
        String destAccountNumber = scanner.nextLine().trim();

        try {
            BankAccount destAccount = myBank.getAccount(destAccountNumber);
            if (destAccount == null) {
                System.out.println("ERROR: Destination account not found.");
                return;
            }

            System.out.print("Enter amount to transfer: ");
            String amountStr = scanner.nextLine().trim();
            double amount = Double.parseDouble(amountStr);

            if (currentAccount instanceof SavingsAccount) {
                ((SavingsAccount) currentAccount).transfer(destAccount, amount);
            } else if (currentAccount instanceof CheckingAccount) {
                ((CheckingAccount) currentAccount).transfer(destAccount, amount);
            }

            System.out.println(
                    "✓ Transfer successful! New balance: $" + String.format("%.2f", currentAccount.getBalance()));
            myBank.saveData();
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid amount format.");
        } catch (InvalidAmountException | AccountStatusException | InsufficientFundsException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void logout() {
        System.out.println("✓ Logged out successfully.");
        currentAccount = null;
    }

    private static void shutdown() {
        System.out.println("\n=== Shutting down ===");
        myBank.saveData();
        myBank.stopServices();
        scanner.close();
        System.out.println("✓ Goodbye!");
        System.exit(0);
    }

    private static String generateAccountNumber() {
        return String.valueOf(10000 + (int) (Math.random() * 90000));
    }
}