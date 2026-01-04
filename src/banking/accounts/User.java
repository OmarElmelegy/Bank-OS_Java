package banking.accounts;

import java.io.Serializable;

public class User implements Serializable{
    private final String username;
    private final String password;
    private final String linkedAccountId;

    public User(String username, String password, String linkedAccountId) {
        this.username = username;
        this.password = password;
        this.linkedAccountId = linkedAccountId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLinkedAccountId() {
        return linkedAccountId;
    }
}
