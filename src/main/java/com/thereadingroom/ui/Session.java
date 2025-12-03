package com.thereadingroom.ui;

import com.thereadingroom.model.User;

public class Session {

    private static User currentUser;

    // Store whole User object (Admin or StandardUser)
    public static void setUser(User user) {
        currentUser = user;
    }


    public static int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public static String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
