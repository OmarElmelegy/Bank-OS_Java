package app;

import java.util.Scanner;

import banking.Bank;
import banking.accounts.BankAccount;
import banking.accounts.CheckingAccount;
import banking.accounts.SavingsAccount;
import banking.accounts.User;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;
import banking.exceptions.InvalidCredentialsException;

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
        //myBank.startInterestService();

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
        System.out.println("\n--- Secure Login ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            // 1. Authenticate (Checks password)
            // You need to import banking.accounts.User;
            User user = myBank.authenticateUser(username, password);

            // 2. Retrieve the Linked Account
            String accountId = user.getLinkedAccountId();
            currentAccount = myBank.getAccount(accountId);

            System.out.println("✓ Welcome back, " + currentAccount.getOwnerName());

        } catch (InvalidCredentialsException e) {
            System.out.println("LOGIN FAILED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.println("\n--- New Customer Registration ---");

        // 1. Capture Credentials
        System.out.print("Enter desired Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter your Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Select account type:");
        System.out.println("[1] Savings Account");
        System.out.println("[2] Checking Account");
        System.out.print("Choose: ");
        String typeSelection = scanner.nextLine().trim();

        // 2. Map selection to string
        String accountType = typeSelection.equals("2") ? "checking" : "savings";

        try {
            // 3. Call the POWER METHOD you wrote in Bank.java
            myBank.createNewCustomer(username, password, name, accountType);

            System.out.println("✓ Registration Successful! You may now login.");
            myBank.saveData(); // Save immediately

        } catch (Exception e) {
            System.out.println("REGISTRATION FAILED: " + e.getMessage());
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
}