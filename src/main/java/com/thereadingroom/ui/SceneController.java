package com.thereadingroom.ui;

public class SceneController {

    private static void load(String fxmlName) {
        try {
            Main.changeScene(fxmlName);  // Loads /fxml/<name>.fxml
        } catch (Exception e) {
            System.err.println("Failed to load scene: " + fxmlName);
            e.printStackTrace();
        }
    }


    public static void goToRegister() {
        load("register");
    }

    public static void goToLogin() {
        load("login");
    }


    public static void goToAdminDashboard() {
        load("dashboard_admin");
    }

    public static void goToUserDashboard() {
        load("dashboard_user");
    }


    public static void goToAnalytics() {
        load("analytics");
    }

    public static void goToAdminAnalytics() {
        load("admin_analytics");
    }

    public static void goBackFromAnalytics() {
        String role = Session.getRole();
        if ("admin".equalsIgnoreCase(role)) {
            goToAdminDashboard();
        } else {
            goToUserDashboard();
        }
    }


    public static void goToManageUsers() {
        load("manage_users");
    }

    public static void goToManageBooks() {
        load("manage_books");
    }


    public static void goToDiscussions() {
        load("discussions");
    }

    public static void goBackFromDiscussions() {
        String role = Session.getRole();
        if ("admin".equalsIgnoreCase(role)) {
            goToAdminDashboard();
        } else {
            goToUserDashboard();
        }
    }


    public static void goToFriends() {
        load("friends");
    }
}
