import java.util.List;

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
    /**
     * Main entry point for the banking application demo.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SavingsAccount account = new SavingsAccount("1234", "Conan");
        CheckingAccount account2 = new CheckingAccount("5678", "Merlin");

        // Happy Path
        try {
            account.deposit(500);
            account2.deposit(200);
            account.deposit(500);
            account2.deposit(200);
            account2.withdraw(200);
            account2.deposit(200);
            account.transferTo(account2, 200);

        } catch (InsufficientFundsException e) {
            System.out.println(">> WITHDRAWAL DENIED: " + e.getMessage());

        } catch (InvalidAmountException e) {
            System.out.println(">> INVALID INPUT: " + e.getMessage());
        } catch (AccountStatusException e) {
            System.out.println(">> ACCOUNT STATUS ERROR: " + e.getMessage());
        }

        account.printStatement();
        account2.printStatement();

        List<Transaction> filteredTransactions = account.getTransactionByType(TransactionType.DEPOSIT);
        System.out.println("--- FILTERED TRANSACTIONS: ---");
        for (Transaction transaction : filteredTransactions) {
            System.out.println(transaction);
        }

        CentralBank central = CentralBank.getInstance();
        central.setInterestRate(0.10);

        CentralBank anotherRef = CentralBank.getInstance();
        System.out.println(anotherRef.getInterestRate());

        try {
            account.applyInterest();
        } catch (AccountStatusException e) {
            System.out.println(">> ACCOUNT STATUS ERROR: " + e.getMessage());
        }
        account.printStatement();
    }
}