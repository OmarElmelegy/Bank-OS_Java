
package banking;

/**
 * Singleton class representing the central bank that sets the default interest
 * rate.
 * 
 * <p>
 * This class uses the Singleton pattern (eager initialization) to ensure only
 * one instance exists. The interest rate set here is used as the default for
 * bank accounts that don't specify their own rate.
 * 
 * <p>
 * Thread Safety: The interest rate field is volatile to ensure visibility
 * across threads.
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class CentralBank {
    private static final CentralBank instance = new CentralBank();

    private volatile double interestRate = 0.05;

    /**
     * Sets the central bank's default interest rate.
     * 
     * @param interestRate the new interest rate (must be between 0.0 and 1.0)
     * @throws IllegalArgumentException if the rate is not between 0 and 1
     */
    public void setInterestRate(double interestRate) {
        if (interestRate < 0 || interestRate > 1.0) {
            throw new IllegalArgumentException("Interest rate must be between 0 and 1");
        }
        this.interestRate = interestRate;
    }

    /**
     * Returns the current default interest rate.
     * 
     * @return the interest rate (between 0.0 and 1.0)
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Private constructor to prevent external instantiation.
     */
    private CentralBank() {
    }

    /**
     * Returns the singleton instance of the central bank.
     * 
     * @return the CentralBank instance
     */
    public static CentralBank getInstance() {
        return instance;
    }
}
