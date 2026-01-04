import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import banking.Bank;
import banking.accounts.BankAccount;
import banking.accounts.CheckingAccount;
import banking.accounts.SavingsAccount;
import banking.accounts.User;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InvalidCredentialsException;
import banking.exceptions.UserStatusException;

/**
 * Test suite for user authentication and registration features.
 */
public class UserAuthenticationTest {

    private Bank bank;

    @BeforeEach
    public void setUp() {
        bank = new Bank();
    }

    @Test
    @DisplayName("Test successful user registration with savings account")
    public void testCreateNewCustomerSavings() throws Exception {
        bank.createNewCustomer("john_doe", "password123", "John Doe", "savings");

        User user = bank.authenticateUser("john_doe", "password123");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john_doe");
        assertThat(user.getLinkedAccountId()).isNotNull();
    }

    @Test
    @DisplayName("Test successful user registration with checking account")
    public void testCreateNewCustomerChecking() throws Exception {
        bank.createNewCustomer("jane_smith", "pass456", "Jane Smith", "checking");

        User user = bank.authenticateUser("jane_smith", "pass456");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("jane_smith");
    }

    @Test
    @DisplayName("Test duplicate username rejection")
    public void testDuplicateUsername() throws Exception {
        bank.createNewCustomer("duplicate", "pass1", "User One", "savings");

        assertThrows(UserStatusException.class, () -> {
            bank.createNewCustomer("duplicate", "pass2", "User Two", "checking");
        });
    }

    @Test
    @DisplayName("Test invalid account type rejection")
    public void testInvalidAccountType() {
        assertThrows(AccountStatusException.class, () -> {
            bank.createNewCustomer("test_user", "pass", "Test User", "invalid");
        });
    }

    @Test
    @DisplayName("Test authentication with correct credentials")
    public void testAuthenticateSuccess() throws Exception {
        bank.createNewCustomer("auth_test", "secret123", "Auth Test", "savings");

        User user = bank.authenticateUser("auth_test", "secret123");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("auth_test");
        assertThat(user.getPassword()).isEqualTo("secret123");
    }

    @Test
    @DisplayName("Test authentication with non-existent username")
    public void testAuthenticateNonExistentUser() {
        assertThrows(InvalidCredentialsException.class, () -> {
            bank.authenticateUser("nonexistent", "password");
        });
    }

    @Test
    @DisplayName("Test authentication with incorrect password")
    public void testAuthenticateWrongPassword() throws Exception {
        bank.createNewCustomer("password_test", "correct_pass", "Pass Test", "checking");

        assertThrows(InvalidCredentialsException.class, () -> {
            bank.authenticateUser("password_test", "wrong_pass");
        });
    }

    @Test
    @DisplayName("Test user linked account can be retrieved")
    public void testUserLinkedAccount() throws Exception {
        bank.createNewCustomer("linked_test", "pass", "Linked Test", "savings");
        User user = bank.authenticateUser("linked_test", "pass");

        BankAccount account = bank.getAccount(user.getLinkedAccountId());
        assertThat(account).isNotNull();
        assertThat(account.getOwnerName()).isEqualTo("Linked Test");
        assertThat(account).isInstanceOf(SavingsAccount.class);
    }

    @Test
    @DisplayName("Test case-insensitive account type (savings)")
    public void testCaseInsensitiveAccountType() throws Exception {
        bank.createNewCustomer("case_test1", "pass", "Case Test 1", "SAVINGS");
        User user = bank.authenticateUser("case_test1", "pass");

        BankAccount account = bank.getAccount(user.getLinkedAccountId());
        assertThat(account).isInstanceOf(SavingsAccount.class);
    }

    @Test
    @DisplayName("Test case-insensitive account type (checking)")
    public void testCaseInsensitiveCheckingType() throws Exception {
        bank.createNewCustomer("case_test2", "pass", "Case Test 2", "ChEcKiNg");
        User user = bank.authenticateUser("case_test2", "pass");

        BankAccount account = bank.getAccount(user.getLinkedAccountId());
        assertThat(account).isInstanceOf(CheckingAccount.class);
    }
}
