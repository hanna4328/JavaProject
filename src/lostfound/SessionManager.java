package lostfound;

public class SessionManager {
    // We use a static variable to hold the ID of the user currently logged in.
    private static int currentUserId = -1; // -1 means no user is logged in

    public static void login(int userId) {
        currentUserId = userId;
    }

    public static void logout() {
        currentUserId = -1;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}
