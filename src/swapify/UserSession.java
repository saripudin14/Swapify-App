package swapify;

public final class UserSession {

    private static UserSession instance;
    private User loggedInUser;

    private UserSession() {
        // Constructor privat untuk mencegah pembuatan instance dari luar
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public void clearSession() {
        loggedInUser = null;
    }
}