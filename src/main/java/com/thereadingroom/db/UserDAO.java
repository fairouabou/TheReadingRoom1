package com.thereadingroom.db;

import com.thereadingroom.model.User;
import com.thereadingroom.model.Admin;
import com.thereadingroom.model.StandardUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static class UserDTO {
        public int id;
        public String username;
        public String email;
        public String role;

        public UserDTO(int id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }
    }

    private static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // username already exists
            }

        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public static boolean registerUser(User user) {
        if (userExists(user.getUsername())) {
            return false;
        }

        String sql = "INSERT INTO users(username, password, email, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static User findUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) return null;

                int id = rs.getInt("id");
                String email = rs.getString("email");
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("admin")) {
                    return new Admin(id, username, password, email);
                } else {
                    return new StandardUser(id, username, password, email);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
            return null;
        }
    }

    public static User findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("admin")) {
                    return new Admin(userId, username, password, email);
                } else {
                    return new StandardUser(userId, username, password, email);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<User> findAllUsersDetailed() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("admin")) {
                    users.add(new Admin(id, username, password, email));
                } else {
                    users.add(new StandardUser(id, username, password, email));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return users;
    }


    public static List<UserDTO> findAllUsers() {
        String sql = "SELECT id, username, email, role FROM users";
        List<UserDTO> list = new ArrayList<>();

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new UserDTO(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return list;
    }


    public static String findUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }

        } catch (SQLException e) {
            System.out.println("Error finding username: " + e.getMessage());
        }

        return "Unknown";
    }

    public static boolean updateUserRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating role: " + e.getMessage());
            return false;
        }
    }

    public static int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
