
package banking;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.*;

import banking.accounts.*;
import banking.exceptions.*;

/**
 * Manages a collection of bank accounts with persistence and scheduled interest
 * services.
 * 
 * <p>
 * This class provides:
 * <ul>
 * <li>Account registration and retrieval</li>
 * <li>Persistent storage using Java serialization</li>
 * <li>Automated interest application via scheduled executor service</li>
 * <li>Thread-safe account management using ConcurrentHashMap</li>
 * </ul>
 * 
 * <p>
 * Thread Safety: This class is thread-safe. Account operations use concurrent
 * collections,
 * and the scheduler runs on a separate thread pool.
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class Bank implements Serializable {
    private final ConcurrentHashMap<String, BankAccount> accounts = new ConcurrentHashMap<String, BankAccount>();
    private final transient ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    /**
     * Opens a new bank account and registers it in the system.
     * 
     * @param account the account to open (must not be null)
     * @throws AccountStatusException if an account with the same account number
     *                                already exists
     */
    public void openAccount(BankAccount account) throws AccountStatusException {
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new AccountStatusException("ERROR: Account already Exists");
        }

        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Retrieves an existing account by its account number.
     * 
     * @param id the account number to retrieve
     * @return the account associated with the given ID
     * @throws AccountStatusException if no account exists with the given ID
     */
    public BankAccount getAccount(String id) throws AccountStatusException {
        if (!accounts.containsKey(id)) {
            throw new AccountStatusException("ERROR: Account does not Exist");
        }
        return accounts.get(id);
    }

    /**
     * Applies interest to all savings accounts in the bank.
     * 
     * <p>
     * This method iterates through all registered accounts and applies interest
     * to those that are instances of {@link SavingsAccount}. Checking accounts
     * and other account types are skipped.
     * 
     * @throws AccountStatusException if interest application fails for any account
     */
    public void payGlobalInterest() throws AccountStatusException {
        for (BankAccount account : accounts.values()) {
            if (account instanceof SavingsAccount) {
                account.applyInterest();
            }
        }
    }

    /**
     * Starts a background service that automatically applies interest to all
     * savings accounts at regular intervals.
     * 
     * <p>
     * The service runs every 10 seconds (simulating nightly batch processing)
     * on a separate thread. The scheduler continues running until
     * {@link #stopServices()} is called.
     * 
     * <p>
     * Note: In a production environment, this would typically run once per day
     * using a cron job or scheduled task.
     */
    public void startInterestService() {
        // Run this task every 10 seconds
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n--- [SYSTEM] RUNNING NIGHTLY INTEREST BATCH ---");
            try {
                payGlobalInterest();
            } catch (AccountStatusException e) {
                System.err.println("Error applying interest: " + e.getMessage());
            }
            System.out.println("-----------------------------------------------");
        }, 10, 10, TimeUnit.SECONDS);

    }

    /**
     * Stops all background services including the interest application scheduler.
     * 
     * <p>
     * This method should be called before the application shuts down to ensure
     * a graceful shutdown of all background threads.
     */
    public void stopServices() {
        scheduler.shutdown();
    }

    /**
     * Persists all bank account data to disk using Java serialization.
     * 
     * <p>
     * The accounts map is serialized to a file named "bank.dat" in the current
     * working directory. This allows account data to be preserved between
     * application runs.
     * 
     * <p>
     * Note: The scheduler is marked as transient and is not serialized.
     */
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("bank.dat"))) {
            out.writeObject(this.accounts);
            out.writeObject(this.users);
            System.out.println("[SYSTEM] Bank data saved to disk.");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save data: " + e.getMessage());
        }
    }

    /**
     * Loads previously saved bank account data from disk.
     * 
     * <p>
     * Attempts to deserialize account data from "bank.dat". If the file doesn't
     * exist, the method returns silently. If the file exists but cannot be read,
     * an error message is printed.
     * 
     * <p>
     * All existing accounts in memory are cleared before loading to ensure
     * consistency with the saved state.
     */
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File("bank.dat");
        if (!file.exists())
            return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ConcurrentHashMap<String, BankAccount> loadedAccounts = (ConcurrentHashMap<String, BankAccount>) in
                    .readObject();

            ConcurrentHashMap<String, User> loadedUsers = (ConcurrentHashMap<String, User>) in
                    .readObject();

            this.accounts.clear();
            this.accounts.putAll(loadedAccounts);

            this.users.clear();
            this.users.putAll(loadedUsers);

            System.out.println("[SYSTEM] Bank data loaded from disk.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERROR] Could not load data: " + e.getMessage());
        }
    }

    public void createNewCustomer(String username, String password, String ownerName, String accountType) throws AccountStatusException, UserStatusException {
        if (users.containsKey(username)) {
            throw new UserStatusException("ERROR: Username already exists");
        }

        String accountID = UUID.randomUUID().toString();

        if (!accounts.containsKey(accountID)) {
            throw new UserStatusException("ERROR: account ID does not exist");
        }

        User newUser = new User(username, password, accountID);
        users.put(username, newUser);

        if (accountType.trim().equalsIgnoreCase("savings")) {
            SavingsAccount newAccount = new SavingsAccount(accountID, ownerName);
            this.openAccount(newAccount); 
        }

        else if (accountType.trim().equalsIgnoreCase("checking")) {
            CheckingAccount newAccount = new CheckingAccount(accountID, ownerName);
            this.openAccount(newAccount);
        }
        
        else {
            throw new AccountStatusException("ERROR: account type is not available");
        }
    }

    public User authenticateUser(String username, String password) throws InvalidCredentialsException {
        if (!users.containsKey(username)) {
            throw new InvalidCredentialsException("ERROR: Username does not exist");
        }

        if (!users.get(username)
                .getPassword()
                .equals(password)) {

            throw new InvalidCredentialsException("ERROR: password is incorrect");
        }

        return users.get(username);
    }
}
